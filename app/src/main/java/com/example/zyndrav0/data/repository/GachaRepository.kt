package com.example.zyndrav0.data.repository

import com.example.zyndrav0.data.database.dao.GachaInventoryDao
import com.example.zyndrav0.data.database.dao.UserDao
import com.example.zyndrav0.data.database.entities.GachaInventoryItem
import com.example.zyndrav0.model.GachaCatalog
import com.example.zyndrav0.model.GachaItem
import com.example.zyndrav0.model.GachaItemType
import com.example.zyndrav0.model.Rarity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class GachaRepository(
    private val gachaInventoryDao: GachaInventoryDao,
    private val userDao: UserDao
) {
    private val gachaPool: List<GachaItem> = GachaCatalog.defaultPool()

    fun getAllItems(userId: String): Flow<List<GachaInventoryItem>> {
        return gachaInventoryDao.getAllItems(userId)
    }

    fun getItemsByType(userId: String, type: String): Flow<List<GachaInventoryItem>> {
        return gachaInventoryDao.getItemsByType(userId, type)
    }

    fun getEquippedItems(userId: String): Flow<List<GachaInventoryItem>> {
        return gachaInventoryDao.getEquippedItems(userId)
    }

    suspend fun getEquippedItemByType(userId: String, type: String): GachaInventoryItem? {
        return gachaInventoryDao.getEquippedItemByType(userId, type)
    }

    suspend fun equipItem(userId: String, inventoryId: Long, itemType: String) {
        gachaInventoryDao.unequipAllOfType(userId, itemType)
        gachaInventoryDao.equipItem(inventoryId)
    }

    suspend fun pullGacha(userId: String, cost: Int): Pair<GachaItem?, String> {
        val user = userDao.getUserById(userId).first() ?: return Pair(null, "Usuario no encontrado")

        if (user.currency < cost) {
            return Pair(null, "No tienes suficientes monedas")
        }

        val item = performGachaPull()

        if (cost > 0) {
            userDao.updateCurrency(userId, user.currency - cost)
        }

        addToInventory(userId, item)

        return Pair(item, "¡Obtuviste: ${item.name}!")
    }

    suspend fun addToInventory(userId: String, item: GachaItem) {
        val inventoryItem = GachaInventoryItem(
            userId = userId,
            itemId = item.id,
            itemName = item.name,
            itemDescription = item.description,
            itemType = item.itemType.name,
            rarity = item.rarity.name,
            iconEmoji = item.iconEmoji,
            colorHex = item.colorHex,
            fontFamily = item.fontFamily
        )
        gachaInventoryDao.insertItem(inventoryItem)
    }

    private fun performGachaPull(): GachaItem {
        val random = Random.nextDouble()

        val rarity = when {
            random < 0.01 -> Rarity.LR
            random < 0.05 -> Rarity.SSR
            random < 0.20 -> Rarity.SR
            random < 0.50 -> Rarity.R
            else -> Rarity.N
        }

        val itemsOfRarity = gachaPool.filter { it.rarity == rarity }

        return itemsOfRarity.randomOrNull() ?: gachaPool.first { it.rarity == Rarity.N }
    }

    suspend fun getInventoryCount(userId: String): Int {
        return gachaInventoryDao.getInventoryCount(userId)
    }
}
