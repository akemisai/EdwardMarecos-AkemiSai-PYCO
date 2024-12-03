package com.pyco.app.models

data class ClothingItem(
    val id: String = "",
    val type: ClothingType = ClothingType.TOP, // Using enum
    val name: String = "",
    val imageUrl: String = "",
    val colors: Colors = Colors.BLACK, // Using enum
    val material: Material = Material.COTTON // Using enum
)
