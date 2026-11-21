package com.example.data

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class PlayerProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val currentLevel: Long = 1L,
    val highestUnlockedLevel: Long = 1L,
    val completedLevelsCount: Long = 0L,
    val totalStars: Long = 0L,
    val totalMoves: Long = 0L,
    val selectedCompanionId: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class UnlockedFigure(
    @DocumentId val id: String = "",
    val userId: String = "",
    val figureId: String = "",
    val unlockedAtLevel: Long = 1L,
    val starsEarned: Long = 1L,
    val bestMoves: Long = 1L,
    val timesCompleted: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class FigureRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class VectorFigureType {
    BEAR,
    PENGUIN,
    ROBOT,
    CAT,
    TURTLE,
    UNICORN,
    FROG,
    PHOENIX
}

data class CollectibleFigure(
    val id: String,
    val unlockLevel: Int,
    val name: String,
    val title: String,
    val story: String,
    val superpower: String,
    val rarity: FigureRarity,
    val primaryColor: Color,
    val secondaryColor: Color,
    val backdropColor: Color,
    @DrawableRes val imageRes: Int? = null,
    val vectorType: VectorFigureType? = null
)

data class GridPos(
    val row: Int,
    val col: Int
) {
    fun isAdjacentTo(other: GridPos): Boolean {
        val dr = kotlin.math.abs(row - other.row)
        val dc = kotlin.math.abs(col - other.col)
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1)
    }
}

data class LevelThemePalette(
    val activeTileColor: Color,
    val activeTileDark: Color,
    val headTileColor: Color,
    val pathLineColor: Color,
    val boardAccentBg: Color
)

data class LevelDefinition(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val rows: Int,
    val cols: Int,
    val startPos: GridPos,
    val walls: Set<GridPos>,
    val solutionPath: List<GridPos>,
    val rewardFigureId: String?,
    val palette: LevelThemePalette
) {
    val totalPlayableBoxes: Int
        get() = rows * cols - walls.size
}

data class CompletionSaveResult(
    val figureNewlyUnlocked: Boolean,
    val nextLevel: Int,
    val savedStars: Int
)
