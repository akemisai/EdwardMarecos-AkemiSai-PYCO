@file:Suppress("UNCHECKED_CAST")

package com.pyco.app.models

data class ClothingItem(
    val name: String = "",
    val type: String = "", // e.g., "Shirt", "Pants", "Dress"
    val color: String = "",
    val material: String = "",
    val brand: String = "",
    val tags: List<String> = emptyList()
)

// Extension function: Convert ClothingItem to Map<String, Any>
fun ClothingItem.toMap(): Map<String, Any> {
    return mapOf(
        "name" to name,
        "type" to type,
        "color" to color,
        "material" to material,
        "brand" to brand,
        "tags" to tags
    )
}

// Extension function: Convert Map<String, Any> to ClothingItem
fun Map<String, Any>.toClothingItem(): ClothingItem {
    return ClothingItem(
        name = this["name"] as? String ?: "",
        type = this["type"] as? String ?: "",
        color = this["color"] as? String ?: "",
        material = this["material"] as? String ?: "",
        brand = this["brand"] as? String ?: "",
        tags = this["tags"] as? List<String> ?: emptyList()
    )
}
