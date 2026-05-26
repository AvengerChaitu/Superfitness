package com.thrivio.gamification

object GamificationEngine {

    fun xpForLevel(level: Int): Long = (100 * level * 1.5f).toLong()

    fun totalXpForLevel(level: Int): Long = (1..level).sumOf { xpForLevel(it) }

    fun calculateLevel(totalXp: Int): Int {
        var level = 1
        while (totalXpForLevel(level) <= totalXp) level++
        return (level - 1).coerceAtLeast(1)
    }

    fun xpProgress(totalXp: Int, level: Int): Float {
        val currentLevelXp = totalXpForLevel(level - 1).toInt()
        val nextLevelXp = totalXpForLevel(level).toInt()
        val progress = totalXp - currentLevelXp
        val needed = nextLevelXp - currentLevelXp
        return if (needed > 0) progress.toFloat() / needed else 1f
    }

    object XpEvents {
        const val STEP_GOAL_COMPLETED = 50
        const val EXTRA_STEP_PER_K = 5
        const val LOG_MEAL = 25
        const val COMPLETE_WORKOUT = 50
        const val MEDITATE_5_MIN = 30
        const val LOG_MOOD = 10
        const val STREAK_BONUS_BASE = 20
    }

    fun streakBonus(streakDays: Int): Int = XpEvents.STREAK_BONUS_BASE * streakDays

    fun stepsToXp(steps: Int): Int {
        var xp = 0
        if (steps >= 8000) xp += XpEvents.STEP_GOAL_COMPLETED
        if (steps > 8000) {
            val extraK = (steps - 8000) / 1000
            xp += extraK.coerceAtMost(5) * XpEvents.EXTRA_STEP_PER_K
        }
        return xp
    }
}
