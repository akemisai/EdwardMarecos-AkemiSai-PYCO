package com.pyco.app.models

data class ClothingItem(
    val id: String = "",                                // unique ID for the item
    val name: String = "",                              // name of the item
    val type: ClothingType = ClothingType.TOP,          // stores as string via enum name
    val imageUrl: String = "",                          // URL to image
    val colour: Colors = Colors.BLACK,                  // single color
    val material: Material = Material.COTTON,           // single material
    val tags: List<String> = emptyList()                // tags for the item
)
