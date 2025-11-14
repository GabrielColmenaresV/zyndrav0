package com.example.zyndrav0.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_inventory")
data class GachaInventoryItem(
    @PrimaryKey(autoGenerate = true) val inventoryId: Long = 0,
    val userId: String,
    val itemId: Int,
    val itemName: String,
    val itemDescription: String,
    val itemType: String,
    val rarity: String,
    val acquiredAt: Long = System.currentTimeMillis(),
    val isEquipped: Boolean = false,
    val iconEmoji: String? = null,
    val colorHex: String? = null,
    val fontFamily: String? = null
)
