package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.signOutUser
import com.example.data.GameProgressRepository
import com.example.ui.components.UnlockCelebrationOverlay
import com.example.ui.screens.CollectionScreen
import com.example.ui.screens.LevelMapScreen
import com.example.ui.screens.PlayScreen
import com.example.ui.theme.KaestchenWeltTheme
import com.example.viewmodel.CloudProgressUiState
import com.example.viewmodel.GameViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaestchenWeltTheme {
                AppContent()
            }
        }
    }
}

private enum class AppTab(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    PLAY(R.string.nav_play, Icons.Rounded.GridView, Icons.Outlined.GridView, "nav_play"),
    COLLECTION(R.string.nav_collection, Icons.Rounded.CollectionsBookmark, Icons.Outlined.CollectionsBookmark, "nav_collection"),
    LEVELS(R.string.nav_levels, Icons.Rounded.Map, Icons.Outlined.Map, "nav_levels")
}

@Composable
private fun AppContent() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = { currentUser = Firebase.auth.currentUser })
    } else {
        key(user.uid) {
            KaestchenWeltApp(
                currentUserId = user.uid,
                userDisplayName = user.displayName ?: user.email,
                onSignedOut = { currentUser = Firebase.auth.currentUser }
            )
        }
    }
}

@Composable
private fun KaestchenWeltApp(
    currentUserId: String,
    userDisplayName: String?,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: GameViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                GameViewModel(GameProgressRepository(db), currentUserId)
            }
        }
    )

    val cloudState by viewModel.cloudState.collectAsStateWithLifecycle()
    val boardState by viewModel.boardState.collectAsStateWithLifecycle()
    var activeTab by rememberSaveable { mutableStateOf(AppTab.PLAY) }

    // Return to Play tab when pressing system Back from secondary tabs
    BackHandler(enabled = activeTab != AppTab.PLAY) {
        activeTab = AppTab.PLAY
    }

    val performSignOut: () -> Unit = {
        signOutUser(
            credentialManager = credentialManager,
            scope = scope,
            onSignOutComplete = onSignedOut
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar {
                        AppTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { activeTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(modifier = Modifier.fillMaxHeight()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { activeTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = stringResource(tab.labelRes)
                                    )
                                },
                                label = { Text(stringResource(tab.labelRes)) },
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    when (val state = cloudState) {
                        is CloudProgressUiState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(44.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = stringResource(R.string.loading_progress),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        is CloudProgressUiState.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = performSignOut) {
                                    Text(stringResource(R.string.auth_sign_out))
                                }
                            }
                        }

                        is CloudProgressUiState.Ready -> {
                            when (activeTab) {
                                AppTab.PLAY -> PlayScreen(
                                    progress = state.progress,
                                    unlockedFigures = state.unlockedFigures,
                                    boardState = boardState,
                                    onTileInteracted = viewModel::onTileInteracted,
                                    onUndo = viewModel::undoStep,
                                    onReset = viewModel::resetLevel,
                                    onHint = viewModel::requestHint,
                                    onDismissSuccessBanner = viewModel::dismissSuccessBanner,
                                    onDismissSaveError = viewModel::dismissSaveError,
                                    onOpenLevels = { activeTab = AppTab.LEVELS },
                                    onOpenCollection = { activeTab = AppTab.COLLECTION }
                                )

                                AppTab.COLLECTION -> CollectionScreen(
                                    progress = state.progress,
                                    unlockedFigures = state.unlockedFigures,
                                    onSelectCompanion = viewModel::selectCompanion
                                )

                                AppTab.LEVELS -> LevelMapScreen(
                                    userDisplayName = userDisplayName,
                                    progress = state.progress,
                                    unlockedFigures = state.unlockedFigures,
                                    activeLevelNumber = boardState.level.levelNumber,
                                    onSelectLevel = { levelNum ->
                                        viewModel.selectLevel(levelNum)
                                        activeTab = AppTab.PLAY
                                    },
                                    onSignOut = performSignOut
                                )
                            }
                        }
                    }
                }
            }
        }

        // Full-screen celebratory Sammelfigur unlock overlay with auto-advance to next level
        val celebration = boardState.celebration
        if (celebration != null) {
            UnlockCelebrationOverlay(
                celebration = celebration,
                onContinueToNextLevel = {
                    viewModel.dismissCelebration()
                    activeTab = AppTab.PLAY
                },
                onOpenCollection = {
                    viewModel.dismissCelebration()
                    activeTab = AppTab.COLLECTION
                }
            )
        }
    }
}
