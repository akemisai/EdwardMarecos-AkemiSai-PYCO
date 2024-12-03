package com.pyco.app.models

data class ClothingItem(
    val id: String = "",
    val type: String = "", // "top", "bottom", "shoe", "accessory"
    val name: String = "",
    val imageUrl: String = "",
    val color: String = "",
    val material: String = ""
)
