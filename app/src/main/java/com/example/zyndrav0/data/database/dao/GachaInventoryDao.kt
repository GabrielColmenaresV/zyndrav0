package com.example.zyndrav0.data.database.dao

import androidx.room.*
import com.example.zyndrav0.data.database.entities.GachaInventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GachaInventoryDao {
    @Query("SELECT * FROM gacha_inventory WHERE userId = :userId ORDER BY acquiredAt DESC")
    fun getAllItems(userId: String): Flow<List<GachaInventoryItem>>

    @Query("SELECT * FROM gacha_inventory WHERE userId = :userId AND itemType = :type")
    fun getItemsByType(userId: String, type: String): Flow<List<GachaInventoryItem>>

    @Query("SELECT * FROM gacha_inventory WHERE userId = :userId AND isEquipped = 1")
    fun getEquippedItems(userId: String): Flow<List<GachaInventoryItem>>

    @Query("SELECT * FROM gacha_inventory WHERE userId = :userId AND itemType = :type AND isEquipped = 1 LIMIT 1")
    suspend fun getEquippedItemByType(userId: String, type: String): GachaInventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GachaInventoryItem): Long

    @Update
    suspend fun updateItem(item: GachaInventoryItem)

    @Query("UPDATE gacha_inventory SET isEquipped = 0 WHERE userId = :userId AND itemType = :type")
    suspend fun unequipAllOfType(userId: String, type: String)

    @Query("UPDATE gacha_inventory SET isEquipped = 1 WHERE inventoryId = :inventoryId")
    suspend fun equipItem(inventoryId: Long)

    @Delete
    suspend fun deleteItem(item: GachaInventoryItem)

    @Query("SELECT COUNT(*) FROM gacha_inventory WHERE userId = :userId")
    suspend fun getInventoryCount(userId: String): Int
}
