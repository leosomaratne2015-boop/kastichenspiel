package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CollectibleFigure
import com.example.data.GameProgressRepository
import com.example.data.GridPos
import com.example.data.LevelCatalog
import com.example.data.LevelDefinition
import com.example.data.PlayerProgress
import com.example.data.UnlockedFigure
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CloudProgressUiState {
    data object Loading : CloudProgressUiState
    data class Ready(
        val progress: PlayerProgress,
        val unlockedFigures: Map<String, UnlockedFigure>
    ) : CloudProgressUiState

    data class Error(val message: String) : CloudProgressUiState
}

data class UnlockCelebration(
    val completedLevel: Int,
    val nextLevel: Int,
    val unlockedFigure: CollectibleFigure?,
    val newlyUnlocked: Boolean,
    val starsEarned: Int,
    val countdownSeconds: Int
)

data class GameBoardUiState(
    val level: LevelDefinition = LevelCatalog.getLevel(1),
    val path: List<GridPos> = listOf(LevelCatalog.getLevel(1).startPos),
    val moves: Int = 0,
    val hintPos: GridPos? = null,
    val isCompleting: Boolean = false,
    val celebration: UnlockCelebration? = null,
    val successBannerText: String? = null,
    val saveErrorText: String? = null
) {
    val filledCount: Int get() = path.size
    val totalBoxes: Int get() = level.totalPlayableBoxes
    val canUndo: Boolean get() = path.size > 1 && !isCompleting
}

class GameViewModel(
    private val repository: GameProgressRepository,
    private val currentUserId: String
) : ViewModel() {

    private var hasSyncedInitialLevel = false
    private var celebrationJob: Job? = null
    private var bannerJob: Job? = null

    private val _boardState = MutableStateFlow(GameBoardUiState())
    val boardState: StateFlow<GameBoardUiState> = _boardState.asStateFlow()

    val cloudState: StateFlow<CloudProgressUiState> = combine(
        repository.observeProgress(currentUserId),
        repository.observeUnlockedFigures(currentUserId)
    ) { progress, unlockedList ->
        val effectiveProgress = progress ?: PlayerProgress(
            id = GameProgressRepository.PROGRESS_DOC_ID,
            userId = currentUserId,
            currentLevel = 1L,
            highestUnlockedLevel = 1L,
            completedLevelsCount = 0L,
            totalStars = 0L,
            totalMoves = 0L
        )
        val unlockedMap = unlockedList.associateBy { it.figureId }
        if (!hasSyncedInitialLevel) {
            hasSyncedInitialLevel = true
            val savedLevel = effectiveProgress.currentLevel.toInt().coerceIn(1, 999)
            loadLevelInternal(savedLevel)
        }
        CloudProgressUiState.Ready(
            progress = effectiveProgress,
            unlockedFigures = unlockedMap
        ) as CloudProgressUiState
    }.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing player progress", rootError)
        emit(
            CloudProgressUiState.Error(
                rootError.localizedMessage ?: "Spielstand konnte nicht geladen werden."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = CloudProgressUiState.Loading
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialProgress(currentUserId)
        }
    }

    private fun loadLevelInternal(levelNumber: Int) {
        val definition = LevelCatalog.getLevel(levelNumber)
        _boardState.update {
            it.copy(
                level = definition,
                path = listOf(definition.startPos),
                moves = 0,
                hintPos = null,
                isCompleting = false
            )
        }
    }

    fun onTileInteracted(pos: GridPos, isDrag: Boolean) {
        val state = _boardState.value
        if (state.isCompleting || state.celebration != null) return
        val level = state.level
        if (pos.row !in 0 until level.rows || pos.col !in 0 until level.cols) return
        if (pos in level.walls) return

        val currentPath = state.path
        val head = currentPath.last()
        if (pos == head) return

        val existingIndex = currentPath.indexOf(pos)
        if (existingIndex >= 0) {
            // Backtrack when dragging over previous tile or tapping any earlier tile on the path
            if (isDrag) {
                if (existingIndex == currentPath.lastIndex - 1) {
                    _boardState.update {
                        it.copy(
                            path = currentPath.dropLast(1),
                            moves = it.moves + 1,
                            hintPos = null
                        )
                    }
                }
            } else {
                _boardState.update {
                    it.copy(
                        path = currentPath.subList(0, existingIndex + 1),
                        moves = it.moves + 1,
                        hintPos = null
                    )
                }
            }
            return
        }

        if (!head.isAdjacentTo(pos)) return

        val newPath = currentPath + pos
        val newMoves = state.moves + 1
        val completed = newPath.size == level.totalPlayableBoxes

        _boardState.update {
            it.copy(
                path = newPath,
                moves = newMoves,
                hintPos = null,
                isCompleting = completed
            )
        }

        if (completed) {
            handleLevelCompleted(level, newMoves)
        }
    }

    fun undoStep() {
        val state = _boardState.value
        if (!state.canUndo) return
        _boardState.update {
            it.copy(
                path = it.path.dropLast(1),
                moves = it.moves + 1,
                hintPos = null
            )
        }
    }

    fun resetLevel() {
        val state = _boardState.value
        if (state.isCompleting) return
        _boardState.update {
            it.copy(
                path = listOf(it.level.startPos),
                moves = 0,
                hintPos = null
            )
        }
    }

    fun requestHint() {
        val state = _boardState.value
        if (state.isCompleting) return
        val solution = state.level.solutionPath
        val path = state.path

        // Check whether current path matches the prefix of the canonical solution
        val matchesPrefix = path.size < solution.size &&
            path.indices.all { i -> path[i] == solution[i] }

        if (matchesPrefix) {
            _boardState.update { it.copy(hintPos = solution[path.size]) }
        } else {
            // Snap gently back to the longest matching prefix and highlight the next correct tile
            var matchingLen = 1
            while (matchingLen < path.size &&
                matchingLen < solution.size &&
                path[matchingLen] == solution[matchingLen]
            ) {
                matchingLen++
            }
            val nextTarget = solution.getOrNull(matchingLen)
            _boardState.update {
                it.copy(
                    path = path.subList(0, matchingLen),
                    hintPos = nextTarget
                )
            }
        }
    }

    private fun handleLevelCompleted(level: LevelDefinition, moves: Int) {
        val optimalMoves = (level.totalPlayableBoxes - 1).coerceAtLeast(1)
        val stars = when {
            moves <= optimalMoves + 2 -> 3
            moves <= optimalMoves + 6 -> 2
            else -> 1
        }
        val rewardFigure = LevelCatalog.figureById(level.rewardFigureId)
        val nextLevelNumber = (level.levelNumber + 1).coerceAtMost(999)

        // Check current known unlock state so UI feedback is instantaneous
        val alreadyOwnedLocally = (cloudState.value as? CloudProgressUiState.Ready)
            ?.unlockedFigures
            ?.containsKey(level.rewardFigureId) == true

        celebrationJob?.cancel()
        celebrationJob = viewModelScope.launch {
            // Short sparkle pause so the player enjoys seeing the final box light up
            delay(360L)

            val saveResult = repository.completeLevel(
                userId = currentUserId,
                levelNumber = level.levelNumber,
                figureId = level.rewardFigureId,
                moves = moves,
                stars = stars
            )

            val newlyUnlocked = saveResult.getOrNull()?.figureNewlyUnlocked
                ?: (rewardFigure != null && !alreadyOwnedLocally)

            val errorMsg = if (saveResult.isFailure) {
                "Spielstand wird erneut synchronisiert…"
            } else {
                null
            }

            // Immediately advance the underlying board to the next level
            val nextLevelDef = LevelCatalog.getLevel(nextLevelNumber)
            val bannerText = when {
                rewardFigure != null && newlyUnlocked ->
                    "Neue Sammelfigur freigeschaltet: ${rewardFigure.name}!"
                rewardFigure != null ->
                    "Level ${level.levelNumber} gemeistert! Weiter mit Level $nextLevelNumber."
                else ->
                    "Meister-Level ${level.levelNumber} geschafft! Jetzt Level $nextLevelNumber."
            }

            _boardState.update {
                it.copy(
                    level = nextLevelDef,
                    path = listOf(nextLevelDef.startPos),
                    moves = 0,
                    hintPos = null,
                    isCompleting = false,
                    saveErrorText = errorMsg,
                    celebration = UnlockCelebration(
                        completedLevel = level.levelNumber,
                        nextLevel = nextLevelNumber,
                        unlockedFigure = rewardFigure,
                        newlyUnlocked = newlyUnlocked,
                        starsEarned = stars,
                        countdownSeconds = AUTO_ADVANCE_SECONDS
                    )
                )
            }

            // Auto-advance countdown so the game seamlessly transitions to the next level
            for (remaining in (AUTO_ADVANCE_SECONDS - 1) downTo 1) {
                delay(1000L)
                if (_boardState.value.celebration == null) return@launch
                _boardState.update { current ->
                    current.copy(
                        celebration = current.celebration?.copy(countdownSeconds = remaining)
                    )
                }
            }
            delay(1000L)
            dismissCelebration(bannerText)
        }
    }

    fun dismissCelebration(customBanner: String? = null) {
        celebrationJob?.cancel()
        val cel = _boardState.value.celebration
        val banner = customBanner ?: when {
            cel?.unlockedFigure != null && cel.newlyUnlocked ->
                "Neue Sammelfigur freigeschaltet: ${cel.unlockedFigure.name}!"
            cel != null ->
                "Level ${cel.completedLevel} geschafft! Jetzt spielst du Level ${cel.nextLevel}."
            else -> null
        }

        _boardState.update {
            it.copy(
                celebration = null,
                successBannerText = banner
            )
        }

        if (banner != null) {
            bannerJob?.cancel()
            bannerJob = viewModelScope.launch {
                delay(4500L)
                _boardState.update { it.copy(successBannerText = null) }
            }
        }
    }

    fun dismissSuccessBanner() {
        bannerJob?.cancel()
        _boardState.update { it.copy(successBannerText = null) }
    }

    fun dismissSaveError() {
        _boardState.update { it.copy(saveErrorText = null) }
    }

    fun selectLevel(levelNumber: Int) {
        val ready = cloudState.value as? CloudProgressUiState.Ready
        val maxUnlocked = ready?.progress?.highestUnlockedLevel?.toInt() ?: 1
        val target = levelNumber.coerceIn(1, maxUnlocked)
        celebrationJob?.cancel()
        _boardState.update { it.copy(celebration = null) }
        loadLevelInternal(target)
        viewModelScope.launch {
            val result = repository.selectCurrentLevel(currentUserId, target)
            if (result.isFailure) {
                _boardState.update {
                    it.copy(saveErrorText = "Aktuelles Level konnte nicht gespeichert werden.")
                }
            }
        }
    }

    fun selectCompanion(figureId: String) {
        viewModelScope.launch {
            val result = repository.selectCompanion(currentUserId, figureId)
            if (result.isFailure) {
                _boardState.update {
                    it.copy(saveErrorText = "Begleiter konnte nicht gespeichert werden.")
                }
            }
        }
    }

    companion object {
        private const val TAG = "GameViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
        private const val AUTO_ADVANCE_SECONDS = 4
    }
}
