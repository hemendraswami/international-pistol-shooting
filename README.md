# International Pistol Shooting

A professional Olympic-style pistol shooting simulation for Android, built with Kotlin + Jetpack Compose.

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 35 (API level 35)
- Gradle 8.7

### Open in Android Studio
1. Launch Android Studio
2. **File → Open** → select the `InternationalPistolShooting/` folder
3. Wait for Gradle sync to complete
4. Connect a device or start an emulator (API 26+, landscape orientation)
5. Press **Run** (▶)

### First Build
Gradle will automatically download all dependencies. This may take a few minutes.

---

## Architecture

```
com.pistolshooting/
├── di/                     Hilt dependency injection modules
├── data/
│   ├── local/              Room database (AppDatabase, DAOs, entities)
│   └── repository/         Repository implementations
├── domain/
│   ├── model/              Core game data models (GameState, Vec2, etc.)
│   ├── repository/         Repository interfaces
│   └── usecase/            Business logic use cases
├── game/
│   ├── engine/             GameEngine — owns the game loop & sub-systems
│   ├── physics/            BulletPhysics, CrosshairController, WindSimulator, TargetMotionController
│   ├── scoring/            ScoringEngine (ISSF decimal scoring)
│   ├── level/              LevelManager, LevelCatalog (20 levels)
│   └── audio/              GameAudioManager (SoundPool)
└── presentation/
    ├── navigation/          Compose NavGraph + Screen sealed class
    ├── theme/               Dark professional theme, colors, typography
    ├── components/          GameCanvas, HUD, WindIndicator, ShotResultPopup
    └── screens/
        ├── splash/          Animated splash screen
        ├── home/            Main menu with player card
        ├── modeselect/      Range selection + level list
        ├── game/            The game screen + ViewModel (game loop here)
        ├── results/         Post-game results
        ├── profile/         Athlete profile + skill upgrades
        ├── weapons/         Weapon selection + purchase
        └── career/          Career mode level progression
```

### Architectural Pattern
- **MVVM + Clean Architecture**: ViewModels consume domain use cases, which depend on repository interfaces
- **StateFlow**: All UI state flows down from ViewModels as `StateFlow<UiState>`
- **Hilt**: Dependency injection throughout — GameEngine is a singleton, correctly scoped
- **Room**: Persists `PlayerProgressEntity` (skills, XP, coins) and `GameSessionEntity` (history)
- **Coroutines**: Game loop runs in `viewModelScope` at ~60fps via `delay(16L)`

---

## Game Physics

### Crosshair Sway
Multi-frequency sinusoidal movement that models real hand tremor:
- **3 overlapping frequencies** (0.45 Hz, 0.73 Hz, 1.1 Hz) with random phase offsets → non-repeating, organic movement
- **Breath stabilization**: holding breath reduces sway amplitude (best at 30–65% of hold duration, then increases from oxygen deprivation)
- **Wind bias**: steady crosswind drift + gust variation
- **Fatigue**: sway amplitude grows by up to 60% over a session

### Bullet Physics (BulletPhysics.kt)
```
wind_drift = 0.5 * travel_time² / bullet_speed * wind_speed * drift_factor
gravity_drop = 9.8 * travel_time² * 0.5 * mode.gravityDropFactor
impact = aim_point + wind_drift + gravity_drop + weapon_spread_noise
```
- 10m: travel time ≈ 57ms, minimal drift
- 25m: travel time ≈ 66ms, moderate drift
- 50m: travel time ≈ 111ms, significant drop + drift

### ISSF Scoring (ScoringEngine.kt)
Official ISSF ring specifications per mode:
- 10m Air Pistol: 10-ring = 11.5mm ∅, ring spacing = 16mm
- 25m/50m: 10-ring = 50mm ∅, ring spacing = 50mm

Decimal scoring: within the 10-ring, position is quantized to 0.1 increments (10.0–10.9).

---

## Shooting Modes

| Mode | Distance | Shots | Target ∅ | Time |
|------|----------|-------|-----------|------|
| 10m Air Pistol | 10m | 10 | 170mm | 75s |
| 25m Sport Pistol | 25m | 30 | 500mm | 210s |
| 50m Free Pistol | 50m | 60 | 500mm | Free |

---

## Weapons

| Weapon | Accuracy | Stability | Range | Cost |
|--------|----------|-----------|-------|------|
| Morini CM 162E | 92% | 88% | 10m | Free |
| Steyr LP10 | 95% | 91% | 10m | 800 🪙 |
| Pardini SP .22 | 88% | 78% | 25m | 600 🪙 |
| Hämmerli 208 S | 90% | 82% | 25m/50m | 1000 🪙 |
| Hämmerli X-Esse | 96% | 93% | 50m | 1500 🪙 |
| Soб Miguez Freedom | 98% | 95% | 50m | 2500 🪙 |

---

## Levels

20 levels across 4 modes:
- **Practice** (L1–2): No wind, static targets, learn the basics
- **Tournament** (L3–15): Progressive wind + moving targets + time pressure  
- **Career** (L15–18): World Cup / Olympic simulation conditions
- **Challenge** (L19–20): Maximum difficulty — for elite shooters only

---

## Player Progression

5 upgradeable skills (each costs 200 🪙, max 100%):
- **Stability**: Reduces crosshair sway amplitude
- **Focus**: Reduces wind effect on aim
- **Reflex**: Faster target tracking response
- **Precision**: Smaller bullet spread on impact
- **Breath Control**: Longer breath hold duration

XP is earned each round: `(score × 2) + (bullseyes × 25)`. Every 500 × level XP = level up.

---

## Adding Audio Assets

Place `.ogg` files in `app/src/main/res/raw/` with these names:
- `pistol_fire.ogg` — gunshot sound
- `bullet_impact_bullseye.ogg` — solid center hit
- `bullet_impact_ring.ogg` — ring hit
- `bullet_miss.ogg` — miss/ricochet
- `wind_ambient.ogg` — wind background loop
- `audience_ambient.ogg` — crowd/tournament atmosphere
- `medal_gold.ogg`, `medal_silver.ogg`, `medal_bronze.ogg` — medal fanfare
- `ui_click.ogg` — button click

Then in `GameAudioManager.loadSounds()`, replace the placeholder comments with:
```kotlin
soundIds[SoundType.PISTOL_FIRE] = soundPool?.load(context, R.raw.pistol_fire, 1) ?: -1
// ... etc
```

---

## Performance Notes

- Canvas rendering targets **60fps** via `delay(16L)` in the game loop coroutine
- Physics update is capped at 50ms delta-time to prevent tunneling on slow frames
- Room queries run on IO dispatcher via `suspend` functions
- `StateFlow` with `WhileSubscribed(5_000)` avoids unnecessary upstream collection
- All drawing is on the Compose render thread — no custom GL needed for this style

---

## Future Multiplayer Extension Points

The architecture is designed for future online multiplayer:
- `GameState` is a pure immutable data class → easy to serialize
- `GameEngine` is isolated from UI → can be driven by network events
- `GameRepository` interface → swap `GameRepositoryImpl` for a network-backed version
- `LevelConfig` is data-driven → levels can be fetched from a server

---

## License

© 2024 International Pistol Shooting. All rights reserved.
