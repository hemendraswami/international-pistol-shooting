package com.pistolshooting.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages all game audio using Android's SoundPool for low-latency effects.
 * Falls back gracefully when audio resources are unavailable.
 */
@Singleton
class GameAudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<SoundType, Int>()
    private var isLoaded = false
    private var masterVolume = 1f
    private var sfxVolume = 1f
    private var ambientVolume = 0.4f
    private var ambientStreamId = -1

    enum class SoundType {
        PISTOL_FIRE,
        BULLET_IMPACT_BULLSEYE,
        BULLET_IMPACT_RING,
        BULLET_MISS,
        WIND_AMBIENT,
        AUDIENCE_AMBIENT,
        MEDAL_GOLD,
        MEDAL_SILVER,
        MEDAL_BRONZE,
        UI_CLICK,
        BREATH_HOLD,
        BREATH_RELEASE,
        COUNTDOWN_TICK
    }

    fun initialize() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) isLoaded = true
        }

        // Sound IDs will be loaded from res/raw when assets are added.
        // For now we register placeholders — the game handles missing sounds gracefully.
        loadSounds()
    }

    private fun loadSounds() {
        // Sounds are loaded from res/raw/*.ogg
        // Entries here map SoundType to resource names for future asset integration.
        // When assets are added, replace with: soundIds[SoundType.X] = soundPool?.load(context, R.raw.sound_name, 1) ?: -1
    }

    fun playSound(type: SoundType, volumeScale: Float = 1f) {
        val soundId = soundIds[type] ?: return
        if (soundId <= 0) return
        val vol = masterVolume * sfxVolume * volumeScale
        soundPool?.play(soundId, vol, vol, 1, 0, 1f)
    }

    fun playGunshot(weaponRecoil: Float) {
        // Heavier recoil = louder, slightly lower pitch
        val pitch = 1f - weaponRecoil * 0.15f
        val vol = masterVolume * sfxVolume * (0.7f + weaponRecoil * 0.3f)
        val soundId = soundIds[SoundType.PISTOL_FIRE] ?: return
        if (soundId <= 0) return
        soundPool?.play(soundId, vol, vol, 2, 0, pitch)
    }

    fun playImpact(score: Float) {
        val type = when {
            score >= 10f -> SoundType.BULLET_IMPACT_BULLSEYE
            score >= 7f -> SoundType.BULLET_IMPACT_RING
            score > 0f -> SoundType.BULLET_IMPACT_RING
            else -> SoundType.BULLET_MISS
        }
        playSound(type)
    }

    fun startAmbient(withAudience: Boolean) {
        val type = if (withAudience) SoundType.AUDIENCE_AMBIENT else SoundType.WIND_AMBIENT
        val soundId = soundIds[type] ?: return
        if (soundId <= 0) return
        val vol = masterVolume * ambientVolume
        ambientStreamId = soundPool?.play(soundId, vol, vol, 0, -1, 1f) ?: -1
    }

    fun stopAmbient() {
        if (ambientStreamId > 0) {
            soundPool?.stop(ambientStreamId)
            ambientStreamId = -1
        }
    }

    fun setVolumes(master: Float, sfx: Float, ambient: Float) {
        masterVolume = master.coerceIn(0f, 1f)
        sfxVolume = sfx.coerceIn(0f, 1f)
        ambientVolume = ambient.coerceIn(0f, 1f)
    }

    fun release() {
        stopAmbient()
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
