package com.pistolshooting.domain.model

// ─── Weapon Upgrades ─────────────────────────────────────────────────────────

data class WeaponUpgrade(
    val name: String,
    val description: String,
    val accuracyBonus: Float = 0f,
    val recoilReduction: Float = 0f,
    val stabilityBonus: Float = 0f,
    val cost: Int,
    val unlocked: Boolean = false
)

// ─── Weapon Types (realistic Olympic pistols) ─────────────────────────────────

enum class WeaponType(
    val displayName: String,
    val manufacturer: String,
    val model: String,
    val description: String,
    val caliber: String,
    val accuracy: Float,      // 0..1
    val recoil: Float,        // 0..1 (higher = more recoil)
    val stability: Float,     // 0..1 (higher = less affected by wind/fatigue)
    val weightKg: Float,
    val fireRateRpm: Float,
    val unlockLevel: Int,
    val cost: Int,
    val compatibleModes: List<ShootingMode>
) {
    MORINI_162E(
        displayName = "Air Pistol",
        manufacturer = "Morini",
        model = "CM 162E",
        description = "Olympic-grade electronic air pistol. Preferred by Olympic champions worldwide.",
        caliber = ".177 (4.5mm)",
        accuracy = 0.92f,
        recoil = 0.05f,
        stability = 0.88f,
        weightKg = 0.91f,
        fireRateRpm = 30f,
        unlockLevel = 1,
        cost = 0,
        compatibleModes = listOf(ShootingMode.AIR_PISTOL_10M)
    ),
    STEYR_LP10(
        displayName = "LP10 Air Pistol",
        manufacturer = "Steyr",
        model = "LP10",
        description = "World record-setting air pistol with exceptional balance and precision.",
        caliber = ".177 (4.5mm)",
        accuracy = 0.95f,
        recoil = 0.04f,
        stability = 0.91f,
        weightKg = 0.95f,
        fireRateRpm = 25f,
        unlockLevel = 8,
        cost = 800,
        compatibleModes = listOf(ShootingMode.AIR_PISTOL_10M)
    ),
    PARDINI_SP(
        displayName = "Sport Pistol",
        manufacturer = "Pardini",
        model = "SP .22 LR",
        description = "The benchmark for 25m sport pistol. Adjustable trigger and perfect balance.",
        caliber = ".22 LR",
        accuracy = 0.88f,
        recoil = 0.22f,
        stability = 0.78f,
        weightKg = 1.06f,
        fireRateRpm = 60f,
        unlockLevel = 5,
        cost = 600,
        compatibleModes = listOf(ShootingMode.PISTOL_25M)
    ),
    HAMMERLI_208(
        displayName = "Match Pistol",
        manufacturer = "Hämmerli",
        model = "208 S",
        description = "Classic Swiss precision pistol. Legendary reliability and accuracy.",
        caliber = ".22 LR",
        accuracy = 0.90f,
        recoil = 0.18f,
        stability = 0.82f,
        weightKg = 1.0f,
        fireRateRpm = 50f,
        unlockLevel = 12,
        cost = 1000,
        compatibleModes = listOf(ShootingMode.PISTOL_25M, ShootingMode.PRECISION_50M)
    ),
    HAMMERLI_XESSE(
        displayName = "Free Pistol",
        manufacturer = "Hämmerli",
        model = "X-Esse SF",
        description = "50m precision pistol with micro-adjustable trigger. Near-zero recoil.",
        caliber = ".22 LR",
        accuracy = 0.96f,
        recoil = 0.12f,
        stability = 0.93f,
        weightKg = 1.25f,
        fireRateRpm = 20f,
        unlockLevel = 18,
        cost = 1500,
        compatibleModes = listOf(ShootingMode.PRECISION_50M)
    ),
    SOB_MIGUEZ(
        displayName = "Olympic Free Pistol",
        manufacturer = "Soб Miguez",
        model = "Freedom",
        description = "The ultimate free pistol. 600g trigger, orthopedic grip, match-tuned.",
        caliber = ".22 LR",
        accuracy = 0.98f,
        recoil = 0.08f,
        stability = 0.95f,
        weightKg = 1.28f,
        fireRateRpm = 15f,
        unlockLevel = 25,
        cost = 2500,
        compatibleModes = listOf(ShootingMode.PRECISION_50M)
    );

    val upgrades: List<WeaponUpgrade>
        get() = listOf(
            WeaponUpgrade("Precision Barrel", "Tighter tolerances for improved accuracy", accuracyBonus = 0.03f, cost = 200),
            WeaponUpgrade("Match Trigger", "Adjustable 2-stage trigger for crisp release", stabilityBonus = 0.04f, cost = 300),
            WeaponUpgrade("Orthopedic Grip", "Custom grip reduces hand movement", stabilityBonus = 0.05f, cost = 350),
            WeaponUpgrade("Muzzle Weight", "Front weight improves aim stability", stabilityBonus = 0.04f, recoilReduction = 0.03f, cost = 150),
            WeaponUpgrade("Optical Sight", "High-precision rear sight system", accuracyBonus = 0.04f, cost = 400)
        )

    fun effectiveAccuracy(upgrades: List<String>): Float {
        return (accuracy + this.upgrades
            .filter { it.name in upgrades }
            .sumOf { it.accuracyBonus.toDouble() }.toFloat()).coerceAtMost(0.99f)
    }

    fun effectiveStability(upgrades: List<String>): Float {
        return (stability + this.upgrades
            .filter { it.name in upgrades }
            .sumOf { it.stabilityBonus.toDouble() }.toFloat()).coerceAtMost(0.99f)
    }
}
