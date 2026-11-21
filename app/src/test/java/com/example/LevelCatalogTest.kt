package com.example

import com.example.data.LevelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelCatalogTest {

    @Test
    fun allHandcraftedAndMasterLevelsHaveValidHamiltonianSolutionPaths() {
        val levelsToCheck = (1..20).map { LevelCatalog.getLevel(it) }
        for (level in levelsToCheck) {
            assertEquals(
                "Level ${level.levelNumber} path must start at startPos",
                level.startPos,
                level.solutionPath.first()
            )
            assertEquals(
                "Level ${level.levelNumber} path length must equal total playable boxes",
                level.totalPlayableBoxes,
                level.solutionPath.size
            )
            assertEquals(
                "Level ${level.levelNumber} path must contain unique cells",
                level.solutionPath.size,
                level.solutionPath.toSet().size
            )
            for (pos in level.solutionPath) {
                assertTrue(
                    "Level ${level.levelNumber} pos $pos out of bounds",
                    pos.row in 0 until level.rows && pos.col in 0 until level.cols
                )
                assertTrue(
                    "Level ${level.levelNumber} pos $pos overlaps a wall",
                    pos !in level.walls
                )
            }
            for (i in 0 until level.solutionPath.lastIndex) {
                val a = level.solutionPath[i]
                val b = level.solutionPath[i + 1]
                assertTrue(
                    "Level ${level.levelNumber} step $i ($a -> $b) is not adjacent",
                    a.isAdjacentTo(b)
                )
            }
        }
    }

    @Test
    fun allHandcraftedLevelsMapToUniqueCollectibleFigures() {
        val ids = LevelCatalog.handcraftedLevels.mapNotNull { it.rewardFigureId }
        assertEquals(12, ids.size)
        assertEquals(12, ids.toSet().size)
        for (id in ids) {
            assertNotNull("Missing figure definition for $id", LevelCatalog.figureById(id))
        }
    }
}
