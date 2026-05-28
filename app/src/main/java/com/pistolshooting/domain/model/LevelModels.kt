package com.pistolshooting.domain.model

object LevelCatalog {

    val allLevels: List<LevelConfig> = buildList {

        // ── BEGINNER CAREER LEVELS (1-5) ──────────────────────────────────────
        add(LevelConfig(
            levelNumber = 1,
            displayName = "First Shot",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.PRACTICE,
            windEnabled = false,
            maxWindSpeedKmh = 0f,
            windChanges = false,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 10,
            timeLimitSeconds = 0,
            qualifyingScore = 50f,
            goldScore = 80f,
            silverScore = 65f,
            bronzeScore = 50f,
            description = "Welcome to the range. No wind. Static target. Master the basics."
        ))

        add(LevelConfig(
            levelNumber = 2,
            displayName = "Steady Hand",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.PRACTICE,
            windEnabled = false,
            maxWindSpeedKmh = 0f,
            windChanges = false,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 10,
            timeLimitSeconds = 150,
            qualifyingScore = 60f,
            goldScore = 88f,
            silverScore = 75f,
            bronzeScore = 60f,
            description = "Time limit introduced. Focus on controlled breathing."
        ))

        add(LevelConfig(
            levelNumber = 3,
            displayName = "Gentle Breeze",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.PRACTICE,
            windEnabled = true,
            maxWindSpeedKmh = 5f,
            windChanges = false,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 10,
            timeLimitSeconds = 120,
            qualifyingScore = 55f,
            goldScore = 85f,
            silverScore = 70f,
            bronzeScore = 55f,
            description = "Light 5 km/h wind from the east. Learn to compensate."
        ))

        add(LevelConfig(
            levelNumber = 4,
            displayName = "Moving Target",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.PRACTICE,
            windEnabled = false,
            maxWindSpeedKmh = 0f,
            windChanges = false,
            targetMotion = TargetMotionType.HORIZONTAL,
            targetSpeedFactor = 0.3f,
            shotsAllowed = 10,
            timeLimitSeconds = 120,
            qualifyingScore = 50f,
            goldScore = 82f,
            silverScore = 68f,
            bronzeScore = 50f,
            description = "Target moves horizontally. Lead the target and fire at the right moment."
        ))

        add(LevelConfig(
            levelNumber = 5,
            displayName = "Wind & Motion",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 8f,
            windChanges = false,
            targetMotion = TargetMotionType.HORIZONTAL,
            targetSpeedFactor = 0.4f,
            shotsAllowed = 10,
            timeLimitSeconds = 100,
            qualifyingScore = 55f,
            goldScore = 83f,
            silverScore = 70f,
            bronzeScore = 55f,
            description = "Wind and moving target combined. Your first real challenge."
        ))

        // ── AMATEUR LEVELS (6-10) ──────────────────────────────────────────────
        add(LevelConfig(
            levelNumber = 6,
            displayName = "Oscillation",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 10f,
            windChanges = false,
            targetMotion = TargetMotionType.OSCILLATING,
            targetSpeedFactor = 0.5f,
            shotsAllowed = 10,
            timeLimitSeconds = 90,
            qualifyingScore = 58f,
            goldScore = 86f,
            silverScore = 73f,
            bronzeScore = 58f,
            description = "Target oscillates back and forth. Time your shot perfectly."
        ))

        add(LevelConfig(
            levelNumber = 7,
            displayName = "Crosswind",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 15f,
            windChanges = true,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 10,
            timeLimitSeconds = 90,
            qualifyingScore = 60f,
            goldScore = 87f,
            silverScore = 74f,
            bronzeScore = 60f,
            description = "Changing wind conditions. Observe the flag before each shot."
        ))

        add(LevelConfig(
            levelNumber = 8,
            displayName = "25m Introduction",
            shootingMode = ShootingMode.PISTOL_25M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 10f,
            windChanges = false,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 30,
            timeLimitSeconds = 300,
            qualifyingScore = 180f,
            goldScore = 250f,
            silverScore = 220f,
            bronzeScore = 180f,
            description = "First 25m range session. Larger targets but greater wind sensitivity."
        ))

        add(LevelConfig(
            levelNumber = 9,
            displayName = "Vertical Challenge",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 12f,
            windChanges = true,
            targetMotion = TargetMotionType.VERTICAL,
            targetSpeedFactor = 0.6f,
            shotsAllowed = 10,
            timeLimitSeconds = 85,
            qualifyingScore = 62f,
            goldScore = 88f,
            silverScore = 76f,
            bronzeScore = 62f,
            description = "Target moves vertically. Adapt your timing strategy."
        ))

        add(LevelConfig(
            levelNumber = 10,
            displayName = "National Qualifier",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 18f,
            windChanges = true,
            targetMotion = TargetMotionType.HORIZONTAL,
            targetSpeedFactor = 0.7f,
            shotsAllowed = 10,
            timeLimitSeconds = 80,
            qualifyingScore = 65f,
            goldScore = 90f,
            silverScore = 78f,
            bronzeScore = 65f,
            description = "Official national-level simulation. Strong wind and moving target."
        ))

        // ── NATIONAL LEVELS (11-18) ────────────────────────────────────────────
        add(LevelConfig(
            levelNumber = 11,
            displayName = "Figure Eight",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 15f,
            windChanges = true,
            targetMotion = TargetMotionType.FIGURE_EIGHT,
            targetSpeedFactor = 0.5f,
            shotsAllowed = 10,
            timeLimitSeconds = 85,
            qualifyingScore = 65f,
            goldScore = 90f,
            silverScore = 78f,
            bronzeScore = 65f,
            description = "Target traces a figure-8 path. Unpredictable direction changes."
        ))

        add(LevelConfig(
            levelNumber = 12,
            displayName = "50m Debut",
            shootingMode = ShootingMode.PRECISION_50M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 12f,
            windChanges = false,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 60,
            timeLimitSeconds = 0,
            qualifyingScore = 480f,
            goldScore = 550f,
            silverScore = 520f,
            bronzeScore = 480f,
            description = "50-meter range. Significant bullet drop and wind drift. Patience is key."
        ))

        add(LevelConfig(
            levelNumber = 13,
            displayName = "Rapid Fire",
            shootingMode = ShootingMode.PISTOL_25M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 10f,
            windChanges = true,
            targetMotion = TargetMotionType.HORIZONTAL,
            targetSpeedFactor = 0.8f,
            shotsAllowed = 30,
            timeLimitSeconds = 150,
            qualifyingScore = 220f,
            goldScore = 270f,
            silverScore = 248f,
            bronzeScore = 220f,
            description = "Fast moving targets with time pressure. Quick reflexes required."
        ))

        add(LevelConfig(
            levelNumber = 14,
            displayName = "Storm Conditions",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.TOURNAMENT,
            windEnabled = true,
            maxWindSpeedKmh = 25f,
            windChanges = true,
            targetMotion = TargetMotionType.OSCILLATING,
            targetSpeedFactor = 0.7f,
            shotsAllowed = 10,
            timeLimitSeconds = 80,
            qualifyingScore = 63f,
            goldScore = 89f,
            silverScore = 77f,
            bronzeScore = 63f,
            description = "High wind gusts up to 25 km/h. Only the steadiest hands qualify."
        ))

        add(LevelConfig(
            levelNumber = 15,
            displayName = "International Cup",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.CAREER,
            windEnabled = true,
            maxWindSpeedKmh = 20f,
            windChanges = true,
            targetMotion = TargetMotionType.RANDOM,
            targetSpeedFactor = 0.6f,
            shotsAllowed = 10,
            timeLimitSeconds = 75,
            qualifyingScore = 67f,
            goldScore = 92f,
            silverScore = 80f,
            bronzeScore = 67f,
            description = "World Cup simulation. Random target movement and variable wind.",
            unlockLevel = 10
        ))

        // ── OLYMPIC LEVELS (16-20) ─────────────────────────────────────────────
        add(LevelConfig(
            levelNumber = 16,
            displayName = "World Championship",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.CAREER,
            windEnabled = true,
            maxWindSpeedKmh = 22f,
            windChanges = true,
            targetMotion = TargetMotionType.RANDOM,
            targetSpeedFactor = 0.75f,
            shotsAllowed = 10,
            timeLimitSeconds = 75,
            qualifyingScore = 70f,
            goldScore = 94f,
            silverScore = 83f,
            bronzeScore = 70f,
            description = "World Championship conditions. Elite-level precision required.",
            unlockLevel = 15
        ))

        add(LevelConfig(
            levelNumber = 17,
            displayName = "Olympic Qualifier",
            shootingMode = ShootingMode.PRECISION_50M,
            gameMode = GameMode.CAREER,
            windEnabled = true,
            maxWindSpeedKmh = 18f,
            windChanges = true,
            targetMotion = TargetMotionType.STATIC,
            targetSpeedFactor = 0f,
            shotsAllowed = 60,
            timeLimitSeconds = 0,
            qualifyingScore = 540f,
            goldScore = 590f,
            silverScore = 565f,
            bronzeScore = 540f,
            description = "Olympic qualification round. Your career's defining moment.",
            unlockLevel = 15
        ))

        add(LevelConfig(
            levelNumber = 18,
            displayName = "Olympic Final",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.CAREER,
            windEnabled = true,
            maxWindSpeedKmh = 20f,
            windChanges = true,
            targetMotion = TargetMotionType.FIGURE_EIGHT,
            targetSpeedFactor = 0.8f,
            shotsAllowed = 10,
            timeLimitSeconds = 75,
            qualifyingScore = 72f,
            goldScore = 96f,
            silverScore = 85f,
            bronzeScore = 72f,
            description = "The Olympic Final. All eyes are on you. Shoot for gold.",
            unlockLevel = 17
        ))

        add(LevelConfig(
            levelNumber = 19,
            displayName = "Grand Master",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.CHALLENGE,
            windEnabled = true,
            maxWindSpeedKmh = 30f,
            windChanges = true,
            targetMotion = TargetMotionType.RANDOM,
            targetSpeedFactor = 0.9f,
            shotsAllowed = 10,
            timeLimitSeconds = 70,
            qualifyingScore = 75f,
            goldScore = 98f,
            silverScore = 88f,
            bronzeScore = 75f,
            description = "For true champions only. Maximum difficulty. Can you hit 98?",
            unlockLevel = 18
        ))

        add(LevelConfig(
            levelNumber = 20,
            displayName = "Perfection",
            shootingMode = ShootingMode.AIR_PISTOL_10M,
            gameMode = GameMode.CHALLENGE,
            windEnabled = true,
            maxWindSpeedKmh = 35f,
            windChanges = true,
            targetMotion = TargetMotionType.RANDOM,
            targetSpeedFactor = 1f,
            shotsAllowed = 10,
            timeLimitSeconds = 60,
            qualifyingScore = 80f,
            goldScore = 100f,
            silverScore = 92f,
            bronzeScore = 80f,
            description = "The ultimate test. Perfect score is 100. Can you achieve perfection?",
            unlockLevel = 19
        ))
    }

    fun getPracticeLevels(): List<LevelConfig> = allLevels.filter { it.gameMode == GameMode.PRACTICE }
    fun getTournamentLevels(): List<LevelConfig> = allLevels.filter { it.gameMode == GameMode.TOURNAMENT }
    fun getCareerLevels(): List<LevelConfig> = allLevels.filter { it.gameMode == GameMode.CAREER }
    fun getChallengeLevels(): List<LevelConfig> = allLevels.filter { it.gameMode == GameMode.CHALLENGE }
    fun getLevelsForMode(mode: ShootingMode): List<LevelConfig> = allLevels.filter { it.shootingMode == mode }

    fun createPracticeSession(mode: ShootingMode): LevelConfig = LevelConfig(
        levelNumber = 0,
        displayName = "Free Practice — ${mode.displayName}",
        shootingMode = mode,
        gameMode = GameMode.PRACTICE,
        windEnabled = false,
        maxWindSpeedKmh = 0f,
        windChanges = false,
        targetMotion = TargetMotionType.STATIC,
        targetSpeedFactor = 0f,
        shotsAllowed = mode.maxShots,
        timeLimitSeconds = 0,
        qualifyingScore = 0f,
        goldScore = mode.maxShots * 10f,
        silverScore = mode.maxShots * 8.5f,
        bronzeScore = mode.maxShots * 7f,
        description = "Free practice session with no pressure."
    )
}
