package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.FigureRarity
import com.example.ui.theme.StarGold
import com.example.viewmodel.UnlockCelebration
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun UnlockCelebrationOverlay(
    celebration: UnlockCelebration,
    onContinueToNextLevel: () -> Unit,
    onOpenCollection: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "confetti")
    val confettiPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_phase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB8191528))
            .padding(24.dp)
            .testTag("unlock_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Animated celebratory confetti particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colors = listOf(
                Color(0xFFFF6B35),
                Color(0xFFFFB703),
                Color(0xFF00B894),
                Color(0xFF9B5DE5),
                Color(0xFF4CC9F0),
                Color(0xFFF15BB5)
            )
            for (i in 0 until 28) {
                val seedX = ((i * 73) % 100) / 100f
                val seedY = ((i * 41) % 100) / 100f
                val y = ((seedY + confettiPhase) % 1f) * size.height
                val x = seedX * size.width + sin((confettiPhase * 6.28f + i).toDouble()).toFloat() * 18f
                val r = 6f + (i % 4) * 3f
                drawCircle(
                    color = colors[i % colors.size].copy(alpha = 0.85f),
                    radius = r,
                    center = Offset(x, y)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Completed Level Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.level_complete_title, celebration.completedLevel),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Stars earned
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (s in 1..3) {
                        val filled = s <= celebration.starsEarned
                        Icon(
                            imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = null,
                            tint = if (filled) StarGold else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                // Unlock headline
                Text(
                    text = if (celebration.newlyUnlocked && celebration.unlockedFigure != null) {
                        stringResource(R.string.figure_unlocked_headline)
                    } else {
                        stringResource(R.string.figure_replay_headline)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // Unlocked Collectible Figure Showcase
                val fig = celebration.unlockedFigure
                if (fig != null) {
                    Box(
                        modifier = Modifier.size(164.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Soft rotating sunburst halo behind figure
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val maxR = size.minDimension * 0.5f
                            for (ray in 0 until 12) {
                                val angle = (ray * 30f + confettiPhase * 360f) * Math.PI / 180.0
                                drawLine(
                                    color = fig.primaryColor.copy(alpha = 0.22f),
                                    start = Offset(cx, cy),
                                    end = Offset(
                                        cx + (maxR * cos(angle)).toFloat(),
                                        cy + (maxR * sin(angle)).toFloat()
                                    ),
                                    strokeWidth = 10f
                                )
                            }
                        }
                        CollectibleFigurePortrait(
                            figure = fig,
                            unlocked = true,
                            cornerRadiusDp = 28,
                            modifier = Modifier.size(144.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = fig.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = rarityColor(fig.rarity).copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = rarityLabel(fig.rarity),
                                style = MaterialTheme.typography.labelMedium,
                                color = rarityColor(fig.rarity),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = fig.story,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Fantastisch gelöst! Du bist ein echter Kästchen-Meister.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Auto-advance indicator to the next level
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.auto_advance_label,
                            celebration.nextLevel,
                            celebration.countdownSeconds
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    LinearProgressIndicator(
                        progress = { (celebration.countdownSeconds / 4f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }

                // Immediate Action Buttons
                Button(
                    onClick = onContinueToNextLevel,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_level_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.btn_next_level, celebration.nextLevel),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (fig != null) {
                    OutlinedButton(
                        onClick = onOpenCollection,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("celebration_collection_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CollectionsBookmark,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.btn_view_collection),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rarityLabel(rarity: FigureRarity): String = when (rarity) {
    FigureRarity.COMMON -> stringResource(R.string.rarity_common)
    FigureRarity.RARE -> stringResource(R.string.rarity_rare)
    FigureRarity.EPIC -> stringResource(R.string.rarity_epic)
    FigureRarity.LEGENDARY -> stringResource(R.string.rarity_legendary)
}
