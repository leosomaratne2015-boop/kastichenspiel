package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R

object LevelCatalog {

    val figures: List<CollectibleFigure> = listOf(
        CollectibleFigure(
            id = "fig_01_fips",
            unlockLevel = 1,
            name = "Fips der Fuchs",
            title = "Wald-Entdecker",
            story = "Fips liebt leuchtende Kästchen auf der Sonnenwiese und findet immer den kürzesten Pfad durch das Moos.",
            superpower = "Goldener Pfadfinder",
            rarity = FigureRarity.COMMON,
            primaryColor = Color(0xFFFF7A3D),
            secondaryColor = Color(0xFFFFD166),
            backdropColor = Color(0xFFFFE9D6),
            imageRes = R.drawable.img_fig_fox_1795263869092
        ),
        CollectibleFigure(
            id = "fig_02_benni",
            unlockLevel = 2,
            name = "Benni der Bär",
            title = "Honig-Baumeister",
            story = "Benni stapelt am liebsten warme Honig-Kästchen zu gemütlichen Türmen in seiner Waldhöhle.",
            superpower = "Bärenstarke Geduld",
            rarity = FigureRarity.COMMON,
            primaryColor = Color(0xFFC87D4B),
            secondaryColor = Color(0xFFFFC857),
            backdropColor = Color(0xFFFFF0DB),
            vectorType = VectorFigureType.BEAR
        ),
        CollectibleFigure(
            id = "fig_03_lumi",
            unlockLevel = 3,
            name = "Lumi die Eule",
            title = "Sternen-Seherin",
            story = "Mit ihrer goldenen Brille erkennt Lumi jedes geheime Muster im Sternenhimmel auf den ersten Blick.",
            superpower = "Nachtsicht-Tipp",
            rarity = FigureRarity.RARE,
            primaryColor = Color(0xFF4F7CAC),
            secondaryColor = Color(0xFF9B8AFB),
            backdropColor = Color(0xFFE6EBFF),
            imageRes = R.drawable.img_fig_owl_1795263868252
        ),
        CollectibleFigure(
            id = "fig_04_pingo",
            unlockLevel = 4,
            name = "Pingo der Pinguin",
            title = "Eis-Gleiter",
            story = "Pingo saust elegant über spiegelglatte Eiskästchen und hinterlässt eine glitzernde Schneespur.",
            superpower = "Turbo-Rutschbahn",
            rarity = FigureRarity.COMMON,
            primaryColor = Color(0xFF2B9EB3),
            secondaryColor = Color(0xFFFFB703),
            backdropColor = Color(0xFFDDF5FA),
            vectorType = VectorFigureType.PENGUIN
        ),
        CollectibleFigure(
            id = "fig_05_robby",
            unlockLevel = 5,
            name = "Robby der Roboter",
            title = "Logik-Tüftler",
            story = "Robby piept fröhlich bei jedem Zug und rechnet blitzschnell tausend Kästchen-Wege im Kopf durch.",
            superpower = "Zahnrad-Logik",
            rarity = FigureRarity.RARE,
            primaryColor = Color(0xFF5C7AEA),
            secondaryColor = Color(0xFF4CC9F0),
            backdropColor = Color(0xFFE2ECFF),
            vectorType = VectorFigureType.ROBOT
        ),
        CollectibleFigure(
            id = "fig_06_klara",
            unlockLevel = 6,
            name = "Klara die Katze",
            title = "Spiralen-Tänzerin",
            story = "Klara schnurrt leise, wenn sie flink von Ecke zu Ecke springt und perfekte Spiralen zieht.",
            superpower = "Samtpfoten-Sprung",
            rarity = FigureRarity.RARE,
            primaryColor = Color(0xFFE86F8C),
            secondaryColor = Color(0xFFFFB5C5),
            backdropColor = Color(0xFFFFE5EC),
            vectorType = VectorFigureType.CAT
        ),
        CollectibleFigure(
            id = "fig_07_funki",
            unlockLevel = 7,
            name = "Funki der Drache",
            title = "Smaragd-Hüter",
            story = "Funki hütet die glühenden Korallen-Kästchen auf dem Drachenfels und versprüht winzige Glücksfunken.",
            superpower = "Glitzer-Flügelschlag",
            rarity = FigureRarity.EPIC,
            primaryColor = Color(0xFF00B894),
            secondaryColor = Color(0xFFFF7675),
            backdropColor = Color(0xFFD8F7EE),
            imageRes = R.drawable.img_fig_dragon_1795263868020
        ),
        CollectibleFigure(
            id = "fig_08_tobi",
            unlockLevel = 8,
            name = "Tobi die Schildkröte",
            title = "Insel-Navigator",
            story = "Tobi kennt jede Strömung der Schildkrötenbucht und findet ruhig und sicher um jedes Riff herum.",
            superpower = "Wellen-Kompass",
            rarity = FigureRarity.RARE,
            primaryColor = Color(0xFF2A9D8F),
            secondaryColor = Color(0xFFE9C46A),
            backdropColor = Color(0xFFDCF6F0),
            vectorType = VectorFigureType.TURTLE
        ),
        CollectibleFigure(
            id = "fig_09_flocki",
            unlockLevel = 9,
            name = "Flocki das Einhorn",
            title = "Wolken-Zauberer",
            story = "Wo Flocki über das Spielfeld galoppiert, verwandelt sich jedes Kästchen in eine pastellbunte Wolke.",
            superpower = "Regenbogen-Glanz",
            rarity = FigureRarity.EPIC,
            primaryColor = Color(0xFF9B5DE5),
            secondaryColor = Color(0xFFF15BB5),
            backdropColor = Color(0xFFF3E5FF),
            vectorType = VectorFigureType.UNICORN
        ),
        CollectibleFigure(
            id = "fig_10_milo",
            unlockLevel = 10,
            name = "Milo der Frosch",
            title = "Teich-König",
            story = "Milo hüpft mit seiner winzigen Krone zielsicher von Seerose zu Seerose, ohne je nass zu werden.",
            superpower = "Königs-Hüpfer",
            rarity = FigureRarity.EPIC,
            primaryColor = Color(0xFF52B788),
            secondaryColor = Color(0xFFFFD166),
            backdropColor = Color(0xFFE0F7E9),
            vectorType = VectorFigureType.FROG
        ),
        CollectibleFigure(
            id = "fig_11_sora",
            unlockLevel = 11,
            name = "Sora der Phönix",
            title = "Sonnen-Schwinge",
            story = "Soras goldene Federn bringen selbst die kniffligsten Kästchen-Labyrinthe zum Strahlen.",
            superpower = "Sonnen-Flamme",
            rarity = FigureRarity.LEGENDARY,
            primaryColor = Color(0xFFFF595E),
            secondaryColor = Color(0xFFFFCA3A),
            backdropColor = Color(0xFFFFE6DF),
            vectorType = VectorFigureType.PHOENIX
        ),
        CollectibleFigure(
            id = "fig_12_kosmo",
            unlockLevel = 12,
            name = "Kosmo der Panda",
            title = "Galaxie-Kapitän",
            story = "Kosmo schwebt mit seinem Regenbogen-Stern durch die Galaxie und krönt wahre Kästchen-Meister!",
            superpower = "Sternen-Gravitation",
            rarity = FigureRarity.LEGENDARY,
            primaryColor = Color(0xFF3F37C9),
            secondaryColor = Color(0xFFFFB703),
            backdropColor = Color(0xFFE2E0FF),
            imageRes = R.drawable.img_fig_astro_1795263867894
        )
    )

    private val palettes: List<LevelThemePalette> = listOf(
        LevelThemePalette(
            activeTileColor = Color(0xFFFF7A3D),
            activeTileDark = Color(0xFFD9531E),
            headTileColor = Color(0xFFFFB703),
            pathLineColor = Color(0xFFFFE29A),
            boardAccentBg = Color(0xFFFFEFE3)
        ),
        LevelThemePalette(
            activeTileColor = Color(0xFF00B894),
            activeTileDark = Color(0xFF008A6E),
            headTileColor = Color(0xFF55EFC4),
            pathLineColor = Color(0xFFBDF7E8),
            boardAccentBg = Color(0xFFE3F9F2)
        ),
        LevelThemePalette(
            activeTileColor = Color(0xFF4C6EF5),
            activeTileDark = Color(0xFF364FC7),
            headTileColor = Color(0xFF74C0FC),
            pathLineColor = Color(0xFFD0E2FF),
            boardAccentBg = Color(0xFFE8EEFF)
        ),
        LevelThemePalette(
            activeTileColor = Color(0xFF9B5DE5),
            activeTileDark = Color(0xFF7235BF),
            headTileColor = Color(0xFFF15BB5),
            pathLineColor = Color(0xFFEAD4FF),
            boardAccentBg = Color(0xFFF3E8FF)
        ),
        LevelThemePalette(
            activeTileColor = Color(0xFFE85D75),
            activeTileDark = Color(0xFFC23A52),
            headTileColor = Color(0xFFFFB703),
            pathLineColor = Color(0xFFFFD6DF),
            boardAccentBg = Color(0xFFFFEBF0)
        )
    )

    val handcraftedLevels: List<LevelDefinition> = listOf(
        // Level 1: 3x3 ring (8 playable boxes)
        LevelDefinition(
            levelNumber = 1,
            title = "Sonnenwiese",
            subtitle = "Dein erstes Kästchen-Abenteuer",
            rows = 3,
            cols = 3,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(1, 1)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(1, 2),
                GridPos(2, 2), GridPos(2, 1), GridPos(2, 0), GridPos(1, 0)
            ),
            rewardFigureId = "fig_01_fips",
            palette = palettes[0]
        ),
        // Level 2: 3x3 full snake (9 playable boxes)
        LevelDefinition(
            levelNumber = 2,
            title = "Bärenhöhle",
            subtitle = "Fülle alle neun Kästchen",
            rows = 3,
            cols = 3,
            startPos = GridPos(0, 0),
            walls = emptySet(),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2),
                GridPos(1, 2), GridPos(1, 1), GridPos(1, 0),
                GridPos(2, 0), GridPos(2, 1), GridPos(2, 2)
            ),
            rewardFigureId = "fig_02_benni",
            palette = palettes[1]
        ),
        // Level 3: 3x4 with 1 wall (11 playable boxes)
        LevelDefinition(
            levelNumber = 3,
            title = "Sternenbaum",
            subtitle = "Umrunde den Sternenstein",
            rows = 3,
            cols = 4,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(1, 2)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(1, 0), GridPos(2, 0), GridPos(2, 1),
                GridPos(1, 1), GridPos(0, 1), GridPos(0, 2), GridPos(0, 3),
                GridPos(1, 3), GridPos(2, 3), GridPos(2, 2)
            ),
            rewardFigureId = "fig_03_lumi",
            palette = palettes[2]
        ),
        // Level 4: 4x4 with horizontal center barrier (14 playable boxes)
        LevelDefinition(
            levelNumber = 4,
            title = "Eisscholle",
            subtitle = "Gleite rund ums Eisriff",
            rows = 4,
            cols = 4,
            startPos = GridPos(0, 1),
            walls = setOf(GridPos(1, 1), GridPos(1, 2)),
            solutionPath = listOf(
                GridPos(0, 1), GridPos(0, 0), GridPos(1, 0), GridPos(2, 0),
                GridPos(3, 0), GridPos(3, 1), GridPos(2, 1), GridPos(2, 2),
                GridPos(3, 2), GridPos(3, 3), GridPos(2, 3), GridPos(1, 3),
                GridPos(0, 3), GridPos(0, 2)
            ),
            rewardFigureId = "fig_04_pingo",
            palette = palettes[2]
        ),
        // Level 5: 4x4 S-bends (15 playable boxes)
        LevelDefinition(
            levelNumber = 5,
            title = "Zahnrädchen",
            subtitle = "Zickzack durch die Werkstatt",
            rows = 4,
            cols = 4,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(3, 0)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(1, 1), GridPos(1, 0),
                GridPos(2, 0), GridPos(2, 1), GridPos(3, 1), GridPos(3, 2),
                GridPos(2, 2), GridPos(1, 2), GridPos(0, 2), GridPos(0, 3),
                GridPos(1, 3), GridPos(2, 3), GridPos(3, 3)
            ),
            rewardFigureId = "fig_05_robby",
            palette = palettes[3]
        ),
        // Level 6: 4x4 full spiral (16 playable boxes)
        LevelDefinition(
            levelNumber = 6,
            title = "Samtpfote",
            subtitle = "Die perfekte Spirale",
            rows = 4,
            cols = 4,
            startPos = GridPos(0, 0),
            walls = emptySet(),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(0, 3),
                GridPos(1, 3), GridPos(2, 3), GridPos(3, 3), GridPos(3, 2),
                GridPos(3, 1), GridPos(3, 0), GridPos(2, 0), GridPos(1, 0),
                GridPos(1, 1), GridPos(1, 2), GridPos(2, 2), GridPos(2, 1)
            ),
            rewardFigureId = "fig_06_klara",
            palette = palettes[4]
        ),
        // Level 7: 4x5 with 3 walls (17 playable boxes)
        LevelDefinition(
            levelNumber = 7,
            title = "Drachenfels",
            subtitle = "Finde den Funkenpfad",
            rows = 4,
            cols = 5,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(0, 3), GridPos(1, 3), GridPos(3, 2)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(1, 2),
                GridPos(1, 1), GridPos(1, 0), GridPos(2, 0), GridPos(3, 0),
                GridPos(3, 1), GridPos(2, 1), GridPos(2, 2), GridPos(2, 3),
                GridPos(3, 3), GridPos(3, 4), GridPos(2, 4), GridPos(1, 4),
                GridPos(0, 4)
            ),
            rewardFigureId = "fig_07_funki",
            palette = palettes[1]
        ),
        // Level 8: 5x4 with 2 walls (18 playable boxes)
        LevelDefinition(
            levelNumber = 8,
            title = "Schildkrötenbucht",
            subtitle = "Schwimme ums Korallenriff",
            rows = 5,
            cols = 4,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(1, 1), GridPos(2, 1)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(0, 3),
                GridPos(1, 3), GridPos(1, 2), GridPos(2, 2), GridPos(2, 3),
                GridPos(3, 3), GridPos(4, 3), GridPos(4, 2), GridPos(3, 2),
                GridPos(3, 1), GridPos(4, 1), GridPos(4, 0), GridPos(3, 0),
                GridPos(2, 0), GridPos(1, 0)
            ),
            rewardFigureId = "fig_08_tobi",
            palette = palettes[1]
        ),
        // Level 9: 5x5 with 3 walls (22 playable boxes)
        LevelDefinition(
            levelNumber = 9,
            title = "Regenbogenwolke",
            subtitle = "Über alle Wolkenkästchen",
            rows = 5,
            cols = 5,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(1, 1), GridPos(2, 1), GridPos(3, 1)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(0, 3),
                GridPos(0, 4), GridPos(1, 4), GridPos(2, 4), GridPos(2, 3),
                GridPos(1, 3), GridPos(1, 2), GridPos(2, 2), GridPos(3, 2),
                GridPos(3, 3), GridPos(3, 4), GridPos(4, 4), GridPos(4, 3),
                GridPos(4, 2), GridPos(4, 1), GridPos(4, 0), GridPos(3, 0),
                GridPos(2, 0), GridPos(1, 0)
            ),
            rewardFigureId = "fig_09_flocki",
            palette = palettes[3]
        ),
        // Level 10: 5x5 center start with 4 walls (21 playable boxes)
        LevelDefinition(
            levelNumber = 10,
            title = "Seerosenteich",
            subtitle = "Starte mitten im Teich",
            rows = 5,
            cols = 5,
            startPos = GridPos(2, 2),
            walls = setOf(GridPos(1, 1), GridPos(2, 1), GridPos(0, 3), GridPos(3, 3)),
            solutionPath = listOf(
                GridPos(2, 2), GridPos(1, 2), GridPos(0, 2), GridPos(0, 1),
                GridPos(0, 0), GridPos(1, 0), GridPos(2, 0), GridPos(3, 0),
                GridPos(4, 0), GridPos(4, 1), GridPos(3, 1), GridPos(3, 2),
                GridPos(4, 2), GridPos(4, 3), GridPos(4, 4), GridPos(3, 4),
                GridPos(2, 4), GridPos(2, 3), GridPos(1, 3), GridPos(1, 4),
                GridPos(0, 4)
            ),
            rewardFigureId = "fig_10_milo",
            palette = palettes[1]
        ),
        // Level 11: 5x5 with 2 center walls (23 playable boxes)
        LevelDefinition(
            levelNumber = 11,
            title = "Feuerkrone",
            subtitle = "Der Weg des Phönix",
            rows = 5,
            cols = 5,
            startPos = GridPos(0, 0),
            walls = setOf(GridPos(2, 2), GridPos(3, 2)),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(1, 0), GridPos(2, 0), GridPos(3, 0),
                GridPos(4, 0), GridPos(4, 1), GridPos(4, 2), GridPos(4, 3),
                GridPos(4, 4), GridPos(3, 4), GridPos(3, 3), GridPos(2, 3),
                GridPos(2, 4), GridPos(1, 4), GridPos(0, 4), GridPos(0, 3),
                GridPos(1, 3), GridPos(1, 2), GridPos(0, 2), GridPos(0, 1),
                GridPos(1, 1), GridPos(2, 1), GridPos(3, 1)
            ),
            rewardFigureId = "fig_11_sora",
            palette = palettes[0]
        ),
        // Level 12: 5x5 full board (25 playable boxes)
        LevelDefinition(
            levelNumber = 12,
            title = "Sternen-Galaxie",
            subtitle = "Das große Kästchen-Finale",
            rows = 5,
            cols = 5,
            startPos = GridPos(0, 0),
            walls = emptySet(),
            solutionPath = listOf(
                GridPos(0, 0), GridPos(0, 1), GridPos(0, 2), GridPos(0, 3), GridPos(0, 4),
                GridPos(1, 4), GridPos(1, 3), GridPos(1, 2), GridPos(1, 1), GridPos(1, 0),
                GridPos(2, 0), GridPos(3, 0), GridPos(4, 0), GridPos(4, 1), GridPos(3, 1),
                GridPos(2, 1), GridPos(2, 2), GridPos(3, 2), GridPos(4, 2), GridPos(4, 3),
                GridPos(3, 3), GridPos(2, 3), GridPos(2, 4), GridPos(3, 4), GridPos(4, 4)
            ),
            rewardFigureId = "fig_12_kosmo",
            palette = palettes[2]
        )
    )

    fun figureById(figureId: String?): CollectibleFigure? {
        if (figureId == null) return null
        return figures.firstOrNull { it.id == figureId }
    }

    fun getLevel(levelNumber: Int): LevelDefinition {
        val clamped = levelNumber.coerceIn(1, 999)
        if (clamped <= handcraftedLevels.size) {
            return handcraftedLevels[clamped - 1]
        }
        // Deterministic, guaranteed-solvable Master Levels (13+) via symmetry transforms of 5x5 levels
        val baseTemplates = handcraftedLevels.drop(6) // Levels 7..12
        val templateIndex = (clamped - handcraftedLevels.size - 1) % baseTemplates.size
        val transformMode = ((clamped - handcraftedLevels.size - 1) / baseTemplates.size) % 4
        val base = baseTemplates[templateIndex]

        fun transformPos(p: GridPos): GridPos = when (transformMode) {
            0 -> GridPos(p.row, base.cols - 1 - p.col) // horizontal mirror
            1 -> GridPos(base.rows - 1 - p.row, p.col) // vertical mirror
            2 -> GridPos(base.rows - 1 - p.row, base.cols - 1 - p.col) // 180 rotate
            else -> p
        }

        val transformedPath = if (transformMode == 3) {
            base.solutionPath.reversed().map(::transformPos)
        } else {
            base.solutionPath.map(::transformPos)
        }

        return LevelDefinition(
            levelNumber = clamped,
            title = "Meister-Level $clamped",
            subtitle = "Endlose Kästchen-Herausforderung",
            rows = base.rows,
            cols = base.cols,
            startPos = transformedPath.first(),
            walls = base.walls.map(::transformPos).toSet(),
            solutionPath = transformedPath,
            rewardFigureId = null,
            palette = palettes[clamped % palettes.size]
        )
    }
}
