// app/src/main/java/com/example/whiskeytastingnote/data/model/WhiskeyCharacter.kt
package com.example.whiskeytastingnote.data.model

/**
 * Data class representing a whiskey character attribute with contrasting properties
 */
data class WhiskeyCharacter(
    val left: String,   // Left extreme (e.g., "드라이")
    val right: String,  // Right extreme (e.g., "오일리")
    val value: Int      // Value between 0-10, with 5 being neutral
)