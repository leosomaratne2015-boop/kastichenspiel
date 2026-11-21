package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.data.GridPos
import com.example.data.LevelDefinition
import com.example.ui.theme.StarGold
import com.example.ui.theme.TileEmptyBevel
import com.example.ui.theme.TileEmptySurface
import com.example.ui.theme.TileHintGlow
import com.example.ui.theme.TileWallHighlight
import com.example.ui.theme.TileWallSurface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun KaestchenBoard(
    level: LevelDefinition,
    path: List<GridPos>,
    hintPos: GridPos?,
    isCompleting: Boolean,
    onTileInteracted: (GridPos, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentLevel by rememberUpdatedState(level)
    val currentPath by rememberUpdatedState(path)
    val currentCallback by rememberUpdatedState(onTileInteracted)

    val infiniteTransition = rememberInfiniteTransition(label = "board_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    var lastDraggedTile by remember(level.levelNumber) { mutableStateOf<GridPos?>(null) }

    fun offsetToGridPos(offset: Offset, size: IntSize, rows: Int, cols: Int): GridPos? {
        if (size.width <= 0 || size.height <= 0) return null
        val cellW = size.width.toFloat() / cols
        val cellH = size.height.toFloat() / rows
        val col = (offset.x / cellW).toInt()
        val row = (offset.y / cellH).toInt()
        if (row !in 0 until rows || col !in 0 until cols) return null
        return GridPos(row, col)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 460.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardAspect = level.cols.toFloat() / level.rows.toFloat()
        val boardBg = level.palette.boardAccentBg

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(boardAspect)
                .shadow(10.dp, RoundedCornerShape(28.dp), spotColor = level.palette.activeTileDark.copy(alpha = 0.28f))
                .clip(RoundedCornerShape(28.dp))
                .background(boardBg)
                .padding(12.dp)
                .testTag("kaestchen_board")
                .semantics {
                    contentDescription = "Kästchen-Spielfeld Level ${level.levelNumber}, ${path.size} von ${level.totalPlayableBoxes} Kästchen gefüllt"
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(level.levelNumber) {
                        detectTapGestures { offset ->
                            val pos = offsetToGridPos(offset, size, currentLevel.rows, currentLevel.cols)
                            if (pos != null) {
                                val beforeSize = currentPath.size
                                currentCallback(pos, false)
                                if (pos != currentPath.lastOrNull() && pos !in currentLevel.walls && beforeSize >= 1) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        }
                    }
                    .pointerInput(level.levelNumber) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                lastDraggedTile = null
                                val pos = offsetToGridPos(startOffset, size, currentLevel.rows, currentLevel.cols)
                                if (pos != null) {
                                    lastDraggedTile = pos
                                    currentCallback(pos, true)
                                }
                            },
                            onDragEnd = { lastDraggedTile = null },
                            onDragCancel = { lastDraggedTile = null },
                            onDrag = { change, _ ->
                                change.consume()
                                val pos = offsetToGridPos(change.position, size, currentLevel.rows, currentLevel.cols)
                                if (pos != null && pos != lastDraggedTile) {
                                    lastDraggedTile = pos
                                    currentCallback(pos, true)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        )
                    }
            ) {
                val rows = level.rows
                val cols = level.cols
                val cellW = size.width / cols
                val cellH = size.height / rows
                val gap = min(cellW, cellH) * 0.11f
                val corner = min(cellW, cellH) * 0.24f
                val bevelH = min(cellW, cellH) * 0.09f

                val pathIndexMap = path.withIndex().associate { it.value to it.index }
                val headPos = path.lastOrNull()

                // 1. Draw all Kästchen base tiles
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val pos = GridPos(r, c)
                        val left = c * cellW + gap * 0.5f
                        val top = r * cellH + gap * 0.5f
                        val w = cellW - gap
                        val h = cellH - gap
                        val isWall = pos in level.walls
                        val pathIdx = pathIndexMap[pos]
                        val isHead = pos == headPos
                        val isHint = pos == hintPos

                        if (isWall) {
                            drawWallBox(left, top, w, h, corner, bevelH)
                        } else if (pathIdx != null) {
                            val fillColor = when {
                                isCompleting -> StarGold
                                isHead -> level.palette.headTileColor
                                else -> level.palette.activeTileColor
                            }
                            val bevelColor = when {
                                isCompleting -> Color(0xFFD68C00)
                                isHead -> level.palette.activeTileDark
                                else -> level.palette.activeTileDark
                            }
                            drawFilledBox(left, top, w, h, corner, bevelH, fillColor, bevelColor)
                        } else {
                            drawEmptyBox(left, top, w, h, corner, bevelH, isHint, pulse)
                        }
                    }
                }

                // 2. Draw smooth glowing path bridge connecting consecutive Kästchen
                if (path.size >= 2) {
                    val strokeW = min(cellW, cellH) * 0.24f
                    for (i in 0 until path.lastIndex) {
                        val a = path[i]
                        val b = path[i + 1]
                        val start = Offset((a.col + 0.5f) * cellW, (a.row + 0.5f) * cellH - bevelH * 0.35f)
                        val end = Offset((b.col + 0.5f) * cellW, (b.row + 0.5f) * cellH - bevelH * 0.35f)
                        drawLine(
                            color = level.palette.pathLineColor.copy(alpha = 0.85f),
                            start = start,
                            end = end,
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. Draw start star marker and animated head orb
                val startCenter = Offset(
                    (level.startPos.col + 0.5f) * cellW,
                    (level.startPos.row + 0.5f) * cellH - bevelH * 0.35f
                )
                drawTinyStar(startCenter, min(cellW, cellH) * 0.16f, Color.White)

                if (headPos != null) {
                    val headCenter = Offset(
                        (headPos.col + 0.5f) * cellW,
                        (headPos.row + 0.5f) * cellH - bevelH * 0.35f
                    )
                    val orbR = min(cellW, cellH) * 0.18f * pulse
                    drawCircle(
                        color = Color.White.copy(alpha = 0.38f),
                        radius = orbR * 1.45f,
                        center = headCenter
                    )
                    drawCircle(
                        color = Color.White,
                        radius = orbR,
                        center = headCenter
                    )
                    drawCircle(
                        color = level.palette.activeTileDark,
                        radius = orbR * 0.45f,
                        center = headCenter
                    )
                }

                // 4. Subtle neighbor indicator dots on immediately reachable empty Kästchen
                if (headPos != null && !isCompleting) {
                    val dirs = listOf(
                        GridPos(headPos.row - 1, headPos.col),
                        GridPos(headPos.row + 1, headPos.col),
                        GridPos(headPos.row, headPos.col - 1),
                        GridPos(headPos.row, headPos.col + 1)
                    )
                    dirs.forEach { n ->
                        if (n.row in 0 until rows &&
                            n.col in 0 until cols &&
                            n !in level.walls &&
                            n !in pathIndexMap
                        ) {
                            val nc = Offset(
                                (n.col + 0.5f) * cellW,
                                (n.row + 0.5f) * cellH - bevelH * 0.35f
                            )
                            drawCircle(
                                color = level.palette.activeTileColor.copy(alpha = 0.42f),
                                radius = min(cellW, cellH) * 0.085f,
                                center = nc
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawEmptyBox(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    corner: Float,
    bevelH: Float,
    isHint: Boolean,
    pulse: Float
) {
    // Bottom 3D bevel
    drawRoundRect(
        color = TileEmptyBevel,
        topLeft = Offset(left, top + bevelH * 0.6f),
        size = Size(w, h - bevelH * 0.6f),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Top tactile face
    drawRoundRect(
        color = if (isHint) TileHintGlow.copy(alpha = 0.55f) else TileEmptySurface,
        topLeft = Offset(left, top),
        size = Size(w, h - bevelH),
        cornerRadius = CornerRadius(corner, corner)
    )
    if (isHint) {
        drawRoundRect(
            color = StarGold,
            topLeft = Offset(left + 3f, top + 3f),
            size = Size(w - 6f, h - bevelH - 6f),
            cornerRadius = CornerRadius(corner * 0.9f, corner * 0.9f),
            style = Stroke(width = 6f * pulse)
        )
    }
}

private fun DrawScope.drawFilledBox(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    corner: Float,
    bevelH: Float,
    fillColor: Color,
    bevelColor: Color
) {
    drawRoundRect(
        color = bevelColor,
        topLeft = Offset(left, top + bevelH * 0.6f),
        size = Size(w, h - bevelH * 0.6f),
        cornerRadius = CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = fillColor,
        topLeft = Offset(left, top),
        size = Size(w, h - bevelH),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Top glossy sheen
    drawRoundRect(
        color = Color.White.copy(alpha = 0.25f),
        topLeft = Offset(left + w * 0.14f, top + h * 0.08f),
        size = Size(w * 0.72f, h * 0.16f),
        cornerRadius = CornerRadius(corner * 0.5f, corner * 0.5f)
    )
}

private fun DrawScope.drawWallBox(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    corner: Float,
    bevelH: Float
) {
    drawRoundRect(
        color = Color(0xFF231B36),
        topLeft = Offset(left + w * 0.05f, top + bevelH * 0.7f),
        size = Size(w * 0.9f, h - bevelH * 0.7f),
        cornerRadius = CornerRadius(corner, corner)
    )
    drawRoundRect(
        color = TileWallSurface,
        topLeft = Offset(left + w * 0.05f, top + h * 0.04f),
        size = Size(w * 0.9f, h - bevelH - h * 0.04f),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Decorative pebble dots
    drawCircle(
        color = TileWallHighlight,
        radius = min(w, h) * 0.09f,
        center = Offset(left + w * 0.36f, top + h * 0.38f)
    )
    drawCircle(
        color = TileWallHighlight.copy(alpha = 0.7f),
        radius = min(w, h) * 0.06f,
        center = Offset(left + w * 0.62f, top + h * 0.54f)
    )
}

private fun DrawScope.drawTinyStar(center: Offset, outerRadius: Float, color: Color) {
    val innerRadius = outerRadius * 0.45f
    val path = androidx.compose.ui.graphics.Path()
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (i * 36.0 - 90.0) * PI / 180.0
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color)
}
