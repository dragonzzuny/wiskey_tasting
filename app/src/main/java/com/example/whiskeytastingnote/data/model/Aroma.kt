package com.example.whiskeytastingnote.data.model

/**
 * Data class representing a whiskey aroma
 */
data class Aroma(
    val category: String,       // Main category like "향신료", "과일"
    val subCategory: String,    // Sub-category like "허브", "시트러스"
    val name: String            // Specific aroma like "로즈마리", "레몬"
)