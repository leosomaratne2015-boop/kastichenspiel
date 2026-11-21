package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.data.CollectibleFigure
import com.example.data.FigureRarity
import com.example.data.VectorFigureType
import com.example.ui.theme.RarityCommon
import com.example.ui.theme.RarityEpic
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.RarityRare

fun rarityColor(rarity: FigureRarity): Color = when (rarity) {
    FigureRarity.COMMON -> RarityCommon
    FigureRarity.RARE -> RarityRare
    FigureRarity.EPIC -> RarityEpic
    FigureRarity.LEGENDARY -> RarityLegendary
}

@Composable
fun CollectibleFigurePortrait(
    figure: CollectibleFigure,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 20
) {
    val shape = RoundedCornerShape(cornerRadiusDp.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (unlocked) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            figure.backdropColor,
                            figure.primaryColor.copy(alpha = 0.22f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!unlocked) {
            // Mysterious silhouette placeholder for locked figures
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width * 0.5f
                val cy = size.height * 0.54f
                val r = size.minDimension * 0.27f
                // Pedestal shadow
                drawOval(
                    color = Color(0x22000000),
                    topLeft = Offset(cx - r * 1.1f, cy + r * 0.85f),
                    size = Size(r * 2.2f, r * 0.45f)
                )
                // Chibi silhouette body + head
                drawCircle(
                    color = Color(0x33231942),
                    radius = r * 0.9f,
                    center = Offset(cx, cy - r * 0.25f)
                )
                drawOval(
                    color = Color(0x33231942),
                    topLeft = Offset(cx - r * 0.75f, cy + r * 0.2f),
                    size = Size(r * 1.5f, r * 0.95f)
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QuestionMark,
                    contentDescription = "Gesperrte Sammelfigur",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            )
        } else if (figure.imageRes != null) {
            Image(
                painter = painterResource(id = figure.imageRes),
                contentDescription = figure.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawToyPedestal(figure.primaryColor)
                when (figure.vectorType) {
                    VectorFigureType.BEAR -> drawBearToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.PENGUIN -> drawPenguinToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.ROBOT -> drawRobotToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.CAT -> drawCatToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.TURTLE -> drawTurtleToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.UNICORN -> drawUnicornToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.FROG -> drawFrogToy(figure.primaryColor, figure.secondaryColor)
                    VectorFigureType.PHOENIX -> drawPhoenixToy(figure.primaryColor, figure.secondaryColor)
                    null -> drawBearToy(figure.primaryColor, figure.secondaryColor)
                }
            }
        }
    }
}

private fun DrawScope.drawToyPedestal(accent: Color) {
    val cx = size.width * 0.5f
    val baseY = size.height * 0.84f
    val w = size.width * 0.64f
    val h = size.height * 0.11f
    drawOval(
        color = accent.copy(alpha = 0.25f),
        topLeft = Offset(cx - w * 0.5f, baseY - h * 0.35f),
        size = Size(w, h)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.75f),
        topLeft = Offset(cx - w * 0.42f, baseY - h * 0.45f),
        size = Size(w * 0.84f, h * 0.72f)
    )
}

private fun DrawScope.drawCuteEyesAndCheeks(cx: Float, eyeY: Float, eyeSpacing: Float, eyeR: Float) {
    val ink = Color(0xFF231942)
    // Blush cheeks
    drawOval(
        color = Color(0x66FF7096),
        topLeft = Offset(cx - eyeSpacing - eyeR * 1.4f, eyeY + eyeR * 0.55f),
        size = Size(eyeR * 2.2f, eyeR * 1.2f)
    )
    drawOval(
        color = Color(0x66FF7096),
        topLeft = Offset(cx + eyeSpacing - eyeR * 0.8f, eyeY + eyeR * 0.55f),
        size = Size(eyeR * 2.2f, eyeR * 1.2f)
    )
    // Glossy eyes
    listOf(cx - eyeSpacing, cx + eyeSpacing).forEach { ex ->
        drawCircle(color = ink, radius = eyeR, center = Offset(ex, eyeY))
        drawCircle(
            color = Color.White,
            radius = eyeR * 0.36f,
            center = Offset(ex - eyeR * 0.28f, eyeY - eyeR * 0.28f)
        )
    }
}

private fun DrawScope.drawHeldCube(cx: Float, cy: Float, boxSize: Float, color: Color) {
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - boxSize / 2f, cy - boxSize / 2f),
        size = Size(boxSize, boxSize),
        cornerRadius = CornerRadius(boxSize * 0.24f, boxSize * 0.24f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.45f),
        topLeft = Offset(cx - boxSize * 0.35f, cy - boxSize * 0.36f),
        size = Size(boxSize * 0.7f, boxSize * 0.22f),
        cornerRadius = CornerRadius(boxSize * 0.12f, boxSize * 0.12f)
    )
}

private fun DrawScope.drawBearToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val r = size.minDimension * 0.26f
    // Ears
    drawCircle(primary, r * 0.38f, Offset(cx - r * 0.72f, cy - r * 0.92f))
    drawCircle(primary, r * 0.38f, Offset(cx + r * 0.72f, cy - r * 0.92f))
    drawCircle(Color(0xFFFFD6BA), r * 0.2f, Offset(cx - r * 0.72f, cy - r * 0.92f))
    drawCircle(Color(0xFFFFD6BA), r * 0.2f, Offset(cx + r * 0.72f, cy - r * 0.92f))
    // Body
    drawOval(primary, Offset(cx - r * 0.72f, cy + r * 0.18f), Size(r * 1.44f, r * 1.15f))
    drawOval(Color(0xFFFFE6CC), Offset(cx - r * 0.45f, cy + r * 0.38f), Size(r * 0.9f, r * 0.72f))
    // Head
    drawCircle(primary, r, Offset(cx, cy - r * 0.24f))
    // Snout
    drawOval(Color(0xFFFFF0DD), Offset(cx - r * 0.38f, cy - r * 0.22f), Size(r * 0.76f, r * 0.52f))
    drawOval(Color(0xFF3A251B), Offset(cx - r * 0.14f, cy - r * 0.15f), Size(r * 0.28f, r * 0.18f))
    drawCuteEyesAndCheeks(cx, cy - r * 0.34f, r * 0.38f, r * 0.12f)
    // Honey block held in paws
    drawHeldCube(cx, cy + r * 0.72f, r * 0.62f, secondary)
}

private fun DrawScope.drawPenguinToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val r = size.minDimension * 0.27f
    val navy = Color(0xFF223354)
    // Flippers
    drawOval(primary, Offset(cx - r * 1.08f, cy + r * 0.05f), Size(r * 0.45f, r * 0.95f))
    drawOval(primary, Offset(cx + r * 0.63f, cy + r * 0.05f), Size(r * 0.45f, r * 0.95f))
    // Body & Head
    drawOval(navy, Offset(cx - r * 0.82f, cy - r * 0.95f), Size(r * 1.64f, r * 2.18f))
    drawOval(Color.White, Offset(cx - r * 0.58f, cy - r * 0.45f), Size(r * 1.16f, r * 1.52f))
    // Scarf
    drawRoundRect(
        color = secondary,
        topLeft = Offset(cx - r * 0.74f, cy + r * 0.02f),
        size = Size(r * 1.48f, r * 0.28f),
        cornerRadius = CornerRadius(r * 0.14f, r * 0.14f)
    )
    // Beak
    val beak = Path().apply {
        moveTo(cx - r * 0.18f, cy - r * 0.25f)
        lineTo(cx + r * 0.18f, cy - r * 0.25f)
        lineTo(cx, cy - r * 0.02f)
        close()
    }
    drawPath(beak, secondary)
    drawCuteEyesAndCheeks(cx, cy - r * 0.42f, r * 0.34f, r * 0.12f)
    drawHeldCube(cx, cy + r * 0.68f, r * 0.54f, primary)
}

private fun DrawScope.drawRobotToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val r = size.minDimension * 0.25f
    // Antenna
    drawLine(
        color = primary,
        start = Offset(cx, cy - r * 1.05f),
        end = Offset(cx, cy - r * 1.45f),
        strokeWidth = r * 0.12f,
        cap = StrokeCap.Round
    )
    drawCircle(Color(0xFFFFB703), r * 0.18f, Offset(cx, cy - r * 1.52f))
    // Body
    drawRoundRect(
        color = primary,
        topLeft = Offset(cx - r * 0.72f, cy + r * 0.14f),
        size = Size(r * 1.44f, r * 1.08f),
        cornerRadius = CornerRadius(r * 0.34f, r * 0.34f)
    )
    // Head
    drawRoundRect(
        color = Color(0xFFEAF2FF),
        topLeft = Offset(cx - r * 0.96f, cy - r * 1.06f),
        size = Size(r * 1.92f, r * 1.18f),
        cornerRadius = CornerRadius(r * 0.42f, r * 0.42f)
    )
    // Visor screen
    drawRoundRect(
        color = Color(0xFF1D2445),
        topLeft = Offset(cx - r * 0.74f, cy - r * 0.84f),
        size = Size(r * 1.48f, r * 0.72f),
        cornerRadius = CornerRadius(r * 0.24f, r * 0.24f)
    )
    // Glowing cyan LED eyes
    drawCircle(secondary, r * 0.16f, Offset(cx - r * 0.32f, cy - r * 0.48f))
    drawCircle(secondary, r * 0.16f, Offset(cx + r * 0.32f, cy - r * 0.48f))
    // Held glowing cube
    drawHeldCube(cx, cy + r * 0.68f, r * 0.58f, Color(0xFFFFB703))
}

private fun DrawScope.drawCatToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    val r = size.minDimension * 0.26f
    // Pointed cat ears
    val leftEar = Path().apply {
        moveTo(cx - r * 0.85f, cy - r * 0.4f)
        lineTo(cx - r * 0.72f, cy - r * 1.32f)
        lineTo(cx - r * 0.18f, cy - r * 0.86f)
        close()
    }
    val rightEar = Path().apply {
        moveTo(cx + r * 0.85f, cy - r * 0.4f)
        lineTo(cx + r * 0.72f, cy - r * 1.32f)
        lineTo(cx + r * 0.18f, cy - r * 0.86f)
        close()
    }
    drawPath(leftEar, primary)
    drawPath(rightEar, primary)
    // Body
    drawOval(primary, Offset(cx - r * 0.68f, cy + r * 0.18f), Size(r * 1.36f, r * 1.08f))
    drawOval(Color.White, Offset(cx - r * 0.4f, cy + r * 0.35f), Size(r * 0.8f, r * 0.7f))
    // Head
    drawOval(primary, Offset(cx - r * 0.98f, cy - r * 1.0f), Size(r * 1.96f, r * 1.48f))
    drawCuteEyesAndCheeks(cx, cy - r * 0.35f, r * 0.38f, r * 0.13f)
    // Tiny nose + collar bell
    drawCircle(Color(0xFFFFD166), r * 0.16f, Offset(cx, cy + r * 0.36f))
    drawHeldCube(cx, cy + r * 0.78f, r * 0.54f, secondary)
}

private fun DrawScope.drawTurtleToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = size.minDimension * 0.27f
    // Shell behind body
    drawCircle(Color(0xFF1E7568), r * 0.92f, Offset(cx, cy + r * 0.22f))
    drawCircle(secondary, r * 0.62f, Offset(cx, cy + r * 0.22f))
    // Head & limbs
    drawCircle(primary, r * 0.74f, Offset(cx, cy - r * 0.46f))
    drawOval(primary, Offset(cx - r * 1.02f, cy + r * 0.24f), Size(r * 0.48f, r * 0.42f))
    drawOval(primary, Offset(cx + r * 0.54f, cy + r * 0.24f), Size(r * 0.48f, r * 0.42f))
    drawCuteEyesAndCheeks(cx, cy - r * 0.52f, r * 0.3f, r * 0.11f)
    drawHeldCube(cx, cy + r * 0.58f, r * 0.54f, Color(0xFFFFB703))
}

private fun DrawScope.drawUnicornToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = size.minDimension * 0.25f
    // Golden horn
    val horn = Path().apply {
        moveTo(cx - r * 0.18f, cy - r * 0.96f)
        lineTo(cx + r * 0.18f, cy - r * 0.96f)
        lineTo(cx, cy - r * 1.62f)
        close()
    }
    drawPath(horn, Color(0xFFFFC83D))
    // Rainbow mane tufts
    drawCircle(secondary, r * 0.36f, Offset(cx + r * 0.62f, cy - r * 0.62f))
    drawCircle(primary, r * 0.36f, Offset(cx + r * 0.72f, cy - r * 0.22f))
    drawCircle(Color(0xFF4CC9F0), r * 0.32f, Offset(cx + r * 0.68f, cy + r * 0.18f))
    // Body & Head
    drawOval(Color.White, Offset(cx - r * 0.68f, cy + r * 0.12f), Size(r * 1.36f, r * 1.12f))
    drawCircle(Color.White, r * 0.92f, Offset(cx, cy - r * 0.26f))
    drawCircle(primary.copy(alpha = 0.15f), r * 0.92f, Offset(cx, cy - r * 0.26f), style = Stroke(4f))
    drawCuteEyesAndCheeks(cx, cy - r * 0.34f, r * 0.36f, r * 0.12f)
    drawHeldCube(cx, cy + r * 0.66f, r * 0.56f, secondary)
}

private fun DrawScope.drawFrogToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = size.minDimension * 0.26f
    // Eye bumps
    drawCircle(primary, r * 0.42f, Offset(cx - r * 0.52f, cy - r * 0.82f))
    drawCircle(primary, r * 0.42f, Offset(cx + r * 0.52f, cy - r * 0.82f))
    // Tiny royal crown between eyes
    val crown = Path().apply {
        moveTo(cx - r * 0.26f, cy - r * 0.92f)
        lineTo(cx - r * 0.3f, cy - r * 1.34f)
        lineTo(cx - r * 0.08f, cy - r * 1.12f)
        lineTo(cx, cy - r * 1.42f)
        lineTo(cx + r * 0.08f, cy - r * 1.12f)
        lineTo(cx + r * 0.3f, cy - r * 1.34f)
        lineTo(cx + r * 0.26f, cy - r * 0.92f)
        close()
    }
    drawPath(crown, secondary)
    // Round frog body + head
    drawOval(primary, Offset(cx - r * 0.84f, cy - r * 0.1f), Size(r * 1.68f, r * 1.32f))
    drawOval(Color(0xFFD8F3DC), Offset(cx - r * 0.56f, cy + r * 0.24f), Size(r * 1.12f, r * 0.78f))
    drawOval(primary, Offset(cx - r * 0.96f, cy - r * 0.82f), Size(r * 1.92f, r * 1.24f))
    // Glossy eyes on bumps
    drawCuteEyesAndCheeks(cx, cy - r * 0.78f, r * 0.52f, r * 0.16f)
    drawHeldCube(cx, cy + r * 0.66f, r * 0.56f, Color(0xFFFF7A3D))
}

private fun DrawScope.drawPhoenixToy(primary: Color, secondary: Color) {
    val cx = size.width * 0.5f
    val cy = size.height * 0.52f
    val r = size.minDimension * 0.26f
    // Fiery wings
    drawOval(secondary, Offset(cx - r * 1.26f, cy - r * 0.16f), Size(r * 0.78f, r * 1.0f))
    drawOval(secondary, Offset(cx + r * 0.48f, cy - r * 0.16f), Size(r * 0.78f, r * 1.0f))
    // Flame crest
    val crest = Path().apply {
        moveTo(cx - r * 0.26f, cy - r * 0.92f)
        lineTo(cx, cy - r * 1.56f)
        lineTo(cx + r * 0.26f, cy - r * 0.92f)
        close()
    }
    drawPath(crest, secondary)
    // Body + Head
    drawOval(primary, Offset(cx - r * 0.74f, cy + r * 0.08f), Size(r * 1.48f, r * 1.15f))
    drawCircle(primary, r * 0.88f, Offset(cx, cy - r * 0.28f))
    drawOval(secondary, Offset(cx - r * 0.44f, cy + r * 0.3f), Size(r * 0.88f, r * 0.72f))
    // Beak
    val beak = Path().apply {
        moveTo(cx - r * 0.18f, cy - r * 0.22f)
        lineTo(cx + r * 0.18f, cy - r * 0.22f)
        lineTo(cx, cy + r * 0.02f)
        close()
    }
    drawPath(beak, Color(0xFFFFD166))
    drawCuteEyesAndCheeks(cx, cy - r * 0.38f, r * 0.34f, r * 0.12f)
    drawHeldCube(cx, cy + r * 0.66f, r * 0.56f, Color(0xFF9B5DE5))
}
