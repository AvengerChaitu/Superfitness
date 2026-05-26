package com.thrivio.data.repository

import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.UserStatsEntity
import com.thrivio.gamification.GamificationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GamificationRepository(private val db: AppDatabase) {

    fun observeStats(): Flow<UserStatsEntity?> = db.userStatsDao().observeStats()

    suspend fun getStats(): UserStatsEntity? = db.userStatsDao().getStats()

    suspend fun initNewUser(): UserStatsEntity = withContext(Dispatchers.IO) {
        val existing = db.userStatsDao().getStats()
        if (existing != null) return@withContext existing
        val fresh = UserStatsEntity()
        db.userStatsDao().upsertStats(fresh)
        fresh
    }

    suspend fun awardXp(amount: Int) = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        val newXp = current.totalXp + amount
        val newLevel = GamificationEngine.calculateLevel(newXp)
        db.userStatsDao().upsertStats(current.copy(totalXp = newXp, currentLevel = newLevel))
    }

    suspend fun checkStreak() = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        val today = LocalDate.now().toString()
        if (current.lastActiveDate == today) return@withContext

        val yesterday = LocalDate.now().minusDays(1).toString()
        val newStreak = if (current.lastActiveDate == yesterday) current.streakDays + 1 else 1
        val longest = maxOf(current.longestStreak, newStreak)
        db.userStatsDao().upsertStats(
            current.copy(
                streakDays = newStreak,
                longestStreak = longest,
                lastActiveDate = today
            )
        )
    }

    suspend fun trackSteps(steps: Int) = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        db.userStatsDao().upsertStats(current.copy(stepsToday = steps))
    }

    suspend fun trackMealLogged() = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        db.userStatsDao().upsertStats(current.copy(mealsLoggedToday = current.mealsLoggedToday + 1))
    }

    suspend fun trackWorkout() = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        db.userStatsDao().upsertStats(current.copy(workoutsCompletedToday = current.workoutsCompletedToday + 1))
    }

    suspend fun trackMeditation(minutes: Int) = withContext(Dispatchers.IO) {
        val current = db.userStatsDao().getStats() ?: UserStatsEntity()
        db.userStatsDao().upsertStats(current.copy(meditationMinutesToday = current.meditationMinutesToday + minutes))
    }
}
