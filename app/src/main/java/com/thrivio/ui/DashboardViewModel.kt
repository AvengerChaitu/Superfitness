package com.thrivio.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.MealEntity
import com.thrivio.data.local.entity.UserStatsEntity
import com.thrivio.data.local.entity.WorkoutEntity
import com.thrivio.data.repository.GamificationRepository
import com.thrivio.data.repository.NutritionRepository
import com.thrivio.data.repository.StepRepository
import com.thrivio.data.repository.WorkoutRepository
import com.thrivio.gamification.GamificationEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val stepRepo = StepRepository(db)
    val nutritionRepo = NutritionRepository(db)
    val workoutRepo = WorkoutRepository(db)
    val gamificationRepo = GamificationRepository(db)

    val stats: StateFlow<UserStatsEntity> = gamificationRepo.observeStats()
        .map { it ?: UserStatsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStatsEntity())

    val todayMeals: StateFlow<List<MealEntity>> = nutritionRepo.getMealsForDate(
        java.time.LocalDate.now().toString()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentWorkouts: StateFlow<List<WorkoutEntity>> = workoutRepo.getRecentWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaySteps: StateFlow<Int> = stepRepo.getTodaySteps()
        .map { it?.steps ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val xpProgress: StateFlow<Float> = stats.map { s ->
        GamificationEngine.xpProgress(s.totalXp, s.currentLevel)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        viewModelScope.launch {
            gamificationRepo.initNewUser()
        }
    }

    fun awardXp(amount: Int) {
        viewModelScope.launch { gamificationRepo.awardXp(amount) }
    }

    fun logMeal(name: String, mealType: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            nutritionRepo.logMeal(name, mealType, calories, protein, carbs, fat)
            gamificationRepo.awardXp(GamificationEngine.XpEvents.LOG_MEAL)
            gamificationRepo.trackMealLogged()
        }
    }

    fun logWorkout(name: String, durationMinutes: Int, calories: Int) {
        viewModelScope.launch {
            workoutRepo.logWorkout(name, durationMinutes * 60, calories)
            gamificationRepo.awardXp(GamificationEngine.XpEvents.COMPLETE_WORKOUT)
            gamificationRepo.trackWorkout()
        }
    }

    fun logMeditation(minutes: Int) {
        viewModelScope.launch {
            gamificationRepo.awardXp(GamificationEngine.XpEvents.MEDITATE_5_MIN)
            gamificationRepo.trackMeditation(minutes)
        }
    }

    fun syncStepData(steps: Int) {
        viewModelScope.launch {
            gamificationRepo.trackSteps(steps)
            val xp = GamificationEngine.stepsToXp(steps)
            if (xp > 0) gamificationRepo.awardXp(xp)
            gamificationRepo.checkStreak()
        }
    }
}
