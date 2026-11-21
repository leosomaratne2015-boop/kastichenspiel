package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.LevelCatalog
import com.example.data.LevelDefinition
import com.example.data.PlayerProgress
import com.example.data.UnlockedFigure
import com.example.ui.components.CollectibleFigurePortrait
import com.example.ui.theme.StarGold

@Composable
fun LevelMapScreen(
    userDisplayName: String?,
    progress: PlayerProgress,
    unlockedFigures: Map<String, UnlockedFigure>,
    activeLevelNumber: Int,
    onSelectLevel: (Int) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highestUnlocked = progress.highestUnlockedLevel.toInt().coerceAtLeast(1)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .testTag("level_map_list")
        ) {
            // Player Progress Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                                    )
                                )
                            )
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.levels_title),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(
                                        R.string.levels_current_banner,
                                        progress.currentLevel.toInt()
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (!userDisplayName.isNullOrBlank()) {
                                    Text(
                                        text = "Spieler: $userDisplayName",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                                    contentDescription = stringResource(R.string.auth_sign_out),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.auth_sign_out),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatTile(
                                label = stringResource(R.string.levels_stats_completed),
                                value = "${progress.completedLevelsCount}",
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = stringResource(R.string.levels_stats_stars),
                                value = "${progress.totalStars}",
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = stringResource(R.string.levels_stats_figures),
                                value = "${unlockedFigures.size}/${LevelCatalog.figures.size}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Master Level card once player finishes Level 12
            if (highestUnlocked > LevelCatalog.handcraftedLevels.size) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(32.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Meister-Modus freigeschaltet!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Spiele endlose Kästchen-Level weiter.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Button(
                                onClick = { onSelectLevel(highestUnlocked) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("play_master_level_button")
                            ) {
                                Text(
                                    text = stringResource(R.string.levels_play_master, highestUnlocked),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            // All 12 Handcrafted Story Levels
            items(LevelCatalog.handcraftedLevels, key = { it.levelNumber }) { levelDef ->
                val isUnlocked = levelDef.levelNumber <= highestUnlocked
                val figureRecord = levelDef.rewardFigureId?.let { unlockedFigures[it] }
                val isCompleted = figureRecord != null || levelDef.levelNumber < highestUnlocked
                val isCurrent = levelDef.levelNumber == activeLevelNumber

                LevelRowCard(
                    level = levelDef,
                    unlocked = isUnlocked,
                    completed = isCompleted,
                    current = isCurrent,
                    starsEarned = figureRecord?.starsEarned?.toInt() ?: 0,
                    onClick = {
                        if (isUnlocked) onSelectLevel(levelDef.levelNumber)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LevelRowCard(
    level: LevelDefinition,
    unlocked: Boolean,
    completed: Boolean,
    current: Boolean,
    starsEarned: Int,
    onClick: () -> Unit
) {
    val rewardFigure = LevelCatalog.figureById(level.rewardFigureId)
    val borderColor = when {
        current -> MaterialTheme.colorScheme.primary
        completed -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
        else -> Color.Transparent
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (current) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (current) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(22.dp)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable(enabled = unlocked, onClick = onClick)
            .testTag("level_item_${level.levelNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Level Number Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            current -> MaterialTheme.colorScheme.primary
                            completed -> MaterialTheme.colorScheme.secondary
                            unlocked -> level.palette.activeTileColor
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!unlocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = stringResource(R.string.level_status_locked),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${level.levelNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }

            // Level Title & Progress Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = level.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (unlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (current) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = stringResource(R.string.level_status_current),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else if (completed) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = stringResource(R.string.level_status_done),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${level.cols}×${level.rows} Kästchen • ${level.totalPlayableBoxes} Felder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (completed && starsEarned > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (s in 1..3) {
                            Icon(
                                imageVector = if (s <= starsEarned) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                contentDescription = null,
                                tint = if (s <= starsEarned) StarGold else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Reward Sammelfigur Mini-Portrait
            if (rewardFigure != null) {
                CollectibleFigurePortrait(
                    figure = rewardFigure,
                    unlocked = completed,
                    cornerRadiusDp = 14,
                    modifier = Modifier.size(48.dp)
                )
            }

            if (unlocked) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Level spielen",
                    tint = if (current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
