package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GameProgressRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
        check(uid == expectedUserId) { "Signed-in user changed; refusing cross-user Firestore operation." }
        return uid
    }

    private fun progressDocRef(uid: String) =
        db.collection("users").document(uid).collection("progress").document(PROGRESS_DOC_ID)

    private fun figuresCollection(uid: String) =
        db.collection("users").document(uid).collection("unlockedFigures")

    fun observeProgress(userId: String): Flow<PlayerProgress?> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/progress/$PROGRESS_DOC_ID"
        emitAll(
            progressDocRef(uid)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            PlayerProgress::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    fun observeUnlockedFigures(userId: String): Flow<List<UnlockedFigure>> = flow {
        val uid = requireUserId(userId)
        val path = "users/$uid/unlockedFigures"
        emitAll(
            figuresCollection(uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        UnlockedFigure::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureInitialProgress(userId: String): Result<Unit> {
        var docPath = "users/$userId/progress/$PROGRESS_DOC_ID"
        return try {
            val uid = requireUserId(userId)
            val docRef = progressDocRef(uid)
            docPath = docRef.path
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "currentLevel" to 1L,
                    "highestUnlockedLevel" to 1L,
                    "completedLevelsCount" to 0L,
                    "totalStars" to 0L,
                    "totalMoves" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun completeLevel(
        userId: String,
        levelNumber: Int,
        figureId: String?,
        moves: Int,
        stars: Int
    ): Result<CompletionSaveResult> {
        val boundedLevel = levelNumber.coerceIn(1, 998)
        val nextLevel = (boundedLevel + 1).coerceIn(1, 999)
        val boundedMoves = moves.coerceIn(1, 999)
        val boundedStars = stars.coerceIn(1, 3)

        var currentPath = "users/$userId/progress/$PROGRESS_DOC_ID"
        return try {
            val uid = requireUserId(userId)
            var figureNewlyUnlocked = false
            var starDelta = 0L

            if (!figureId.isNullOrBlank()) {
                val figRef = figuresCollection(uid).document(figureId)
                currentPath = figRef.path
                val existingFigSnap = figRef.get().await()
                if (!existingFigSnap.exists()) {
                    figureNewlyUnlocked = true
                    starDelta = boundedStars.toLong()
                    val createFigPayload = mapOf(
                        "userId" to uid,
                        "figureId" to figureId,
                        "unlockedAtLevel" to boundedLevel.toLong(),
                        "starsEarned" to boundedStars.toLong(),
                        "bestMoves" to boundedMoves.toLong(),
                        "timesCompleted" to 1L,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    figRef.set(createFigPayload).await()
                } else {
                    val existingFig = existingFigSnap.toObject(
                        UnlockedFigure::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                    val prevStars = (existingFig?.starsEarned ?: 1L).coerceIn(1L, 3L)
                    val prevBestMoves = (existingFig?.bestMoves ?: boundedMoves.toLong()).coerceIn(1L, 999L)
                    val prevTimes = (existingFig?.timesCompleted ?: 1L).coerceIn(1L, 9998L)

                    val newStars = maxOf(prevStars, boundedStars.toLong())
                    val newBestMoves = minOf(prevBestMoves, boundedMoves.toLong())
                    starDelta = (newStars - prevStars).coerceAtLeast(0L)

                    val updateFigPayload = mapOf(
                        "starsEarned" to newStars,
                        "bestMoves" to newBestMoves,
                        "timesCompleted" to (prevTimes + 1L),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    figRef.update(updateFigPayload).await()
                }
            } else {
                // Endless master level bonus star tracking
                starDelta = boundedStars.toLong()
            }

            val progRef = progressDocRef(uid)
            currentPath = progRef.path
            val progSnap = progRef.get().await()

            if (!progSnap.exists()) {
                val createProgPayload = mutableMapOf<String, Any>(
                    "userId" to uid,
                    "currentLevel" to nextLevel.toLong(),
                    "highestUnlockedLevel" to nextLevel.toLong(),
                    "completedLevelsCount" to 1L,
                    "totalStars" to boundedStars.toLong(),
                    "totalMoves" to boundedMoves.toLong(),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (!figureId.isNullOrBlank()) {
                    createProgPayload["selectedCompanionId"] = figureId
                }
                progRef.set(createProgPayload).await()
            } else {
                val existingProg = progSnap.toObject(
                    PlayerProgress::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                val prevHighest = (existingProg?.highestUnlockedLevel ?: 1L).coerceIn(1L, 999L)
                val newHighest = maxOf(prevHighest, nextLevel.toLong()).coerceIn(1L, 999L)
                val isFirstTimeBeatingThisLevel = boundedLevel.toLong() >= prevHighest || figureNewlyUnlocked
                val prevCompleted = (existingProg?.completedLevelsCount ?: 0L).coerceIn(0L, 998L)
                val newCompleted = if (isFirstTimeBeatingThisLevel) {
                    (prevCompleted + 1L).coerceAtMost(999L)
                } else {
                    prevCompleted
                }
                val newTotalStars = ((existingProg?.totalStars ?: 0L) + starDelta).coerceIn(0L, 9999L)
                val newTotalMoves = ((existingProg?.totalMoves ?: 0L) + boundedMoves.toLong()).coerceIn(0L, 999999L)

                val updateProgPayload = mutableMapOf<String, Any>(
                    "currentLevel" to nextLevel.toLong(),
                    "highestUnlockedLevel" to newHighest,
                    "completedLevelsCount" to newCompleted,
                    "totalStars" to newTotalStars,
                    "totalMoves" to newTotalMoves,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                // Automatically set first unlocked collectible as active companion if none chosen yet
                if (existingProg?.selectedCompanionId.isNullOrBlank() && !figureId.isNullOrBlank()) {
                    updateProgPayload["selectedCompanionId"] = figureId
                }
                progRef.update(updateProgPayload).await()
            }

            Result.success(
                CompletionSaveResult(
                    figureNewlyUnlocked = figureNewlyUnlocked,
                    nextLevel = nextLevel,
                    savedStars = boundedStars
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, currentPath)
            Result.failure(e)
        }
    }

    suspend fun selectCurrentLevel(userId: String, levelNumber: Int): Result<Unit> {
        val boundedLevel = levelNumber.coerceIn(1, 999)
        var docPath = "users/$userId/progress/$PROGRESS_DOC_ID"
        return try {
            val uid = requireUserId(userId)
            val docRef = progressDocRef(uid)
            docPath = docRef.path
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val existing = snapshot.toObject(
                    PlayerProgress::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                val highest = (existing?.highestUnlockedLevel ?: 1L).toInt().coerceIn(1, 999)
                val safeTarget = boundedLevel.coerceAtMost(highest)
                docRef.update(
                    mapOf(
                        "currentLevel" to safeTarget.toLong(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun selectCompanion(userId: String, figureId: String): Result<Unit> {
        var docPath = "users/$userId/progress/$PROGRESS_DOC_ID"
        return try {
            val uid = requireUserId(userId)
            require(figureId.matches(Regex("^[a-zA-Z0-9_\\-]+$")) && figureId.length <= 64) {
                "Invalid figureId format"
            }
            val docRef = progressDocRef(uid)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "selectedCompanionId" to figureId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    companion object {
        const val PROGRESS_DOC_ID = "main"
    }
}
