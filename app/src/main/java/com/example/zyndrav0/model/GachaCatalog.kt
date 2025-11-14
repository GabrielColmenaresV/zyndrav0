package com.example.zyndrav0.model

/**
 * Catálogo centralizado de ítems disponibles en el gacha.
 * Mantener esta lista en un solo lugar evita desajustes entre capas.
 * Ingregrar cosmeticos para perfil como el marco y parra el fondo.
 */
object GachaCatalog {

    fun defaultPool(): List<GachaItem> {
        return listOf(
            GachaItem(
                id = 1,
                name = "Burbuja Clásica",
                description = "La burbuja de chat de toda la vida.",
                rarity = Rarity.N,
                itemType = GachaItemType.CHAT_BUBBLE,
                iconEmoji = "💬",
                colorHex = "#2196F3",
                fontFamily = "sans"
            ),
            GachaItem(
                id = 2,
                name = "Burbuja Roja",
                description = "Una burbuja con un toque cálido.",
                rarity = Rarity.R,
                itemType = GachaItemType.CHAT_BUBBLE,
                iconEmoji = "🔴",
                colorHex = "#F44336",
                fontFamily = "sans"
            ),
            GachaItem(
                id = 3,
                name = "Burbuja Cósmica",
                description = "Brilla como las estrellas.",
                rarity = Rarity.SR,
                itemType = GachaItemType.CHAT_BUBBLE,
                iconEmoji = "🌌",
                colorHex = "#FFD700",
                fontFamily = "serif"
            ),
            GachaItem(
                id = 4,
                name = "Burbuja de Dragón",
                description = "Inspirada en leyendas orientales.",
                rarity = Rarity.SSR,
                itemType = GachaItemType.CHAT_BUBBLE,
                iconEmoji = "🐲",
                colorHex = "#673AB7",
                fontFamily = "mono"
            ),
            GachaItem(
                id = 5,
                name = "Burbuja Legendaria",
                description = "Forjada al inicio de los tiempos.",
                rarity = Rarity.LR,
                itemType = GachaItemType.CHAT_BUBBLE,
                iconEmoji = "👑",
                colorHex = "#FF6F00",
                fontFamily = "cursive"
            ),
            GachaItem(
                id = 6,
                name = "Marco Básico",
                description = "Un marco simple para tu avatar.",
                rarity = Rarity.R,
                itemType = GachaItemType.AVATAR_FRAME,
                iconEmoji = "🖼️"
            ),
            GachaItem(
                id = 7,
                name = "Marco de Fuego",
                description = "Arde con pasión.",
                rarity = Rarity.SR,
                itemType = GachaItemType.AVATAR_FRAME,
                iconEmoji = "🔥"
            ),
            GachaItem(
                id = 8,
                name = "Llave de Historia",
                description = "Desbloquea un capítulo especial.",
                rarity = Rarity.SSR,
                itemType = GachaItemType.STORY_KEY,
                iconEmoji = "🗝️"
            ),
            GachaItem(
                id = 9,
                name = "Tipografía Retro",
                description = "Texto estilo máquina de escribir.",
                rarity = Rarity.R,
                itemType = GachaItemType.THEME,
                iconEmoji = "⌨️",
                fontFamily = "mono"
            ),
            GachaItem(
                id = 10,
                name = "Tipografía Cómic",
                description = "Letras relajadas y redondeadas.",
                rarity = Rarity.SR,
                itemType = GachaItemType.THEME,
                iconEmoji = "🗯️",
                fontFamily = "cursive",
                colorHex = "#FF80AB"
            )
        )
    }
}

