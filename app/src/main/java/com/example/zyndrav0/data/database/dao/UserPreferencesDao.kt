package com.example.zyndrav0.data.database.dao

import androidx.room.*
import com.example.zyndrav0.data.database.entities.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getPreferences(userId: String): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences)

    @Update
    suspend fun updatePreferences(preferences: UserPreferences)

    @Query("UPDATE user_preferences SET equippedBubbleId = :bubbleId WHERE userId = :userId")
    suspend fun updateEquippedBubble(userId: String, bubbleId: Int?)

    @Query("UPDATE user_preferences SET equippedIconId = :iconId WHERE userId = :userId")
    suspend fun updateEquippedIcon(userId: String, iconId: Int?)

    @Query("UPDATE user_preferences SET equippedBackgroundId = :backgroundId WHERE userId = :userId")
    suspend fun updateEquippedBackground(userId: String, backgroundId: Int?)

    @Query("UPDATE user_preferences SET equippedAnimationId = :animationId WHERE userId = :userId")
    suspend fun updateEquippedAnimation(userId: String, animationId: Int?)

    @Query("UPDATE user_preferences SET isDarkMode = :isDark WHERE userId = :userId")
    suspend fun updateDarkMode(userId: String, isDark: Boolean)

    @Delete
    suspend fun deletePreferences(preferences: UserPreferences)
}
