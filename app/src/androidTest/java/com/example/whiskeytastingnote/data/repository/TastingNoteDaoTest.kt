package com.example.whiskeytastingnote.data.repository
import java.util.Date
data class TastingNote(
    val id: Long = 0,
    val name: String = "",
    val date: Date = Date(),
    val abv: Float? = null,
    val score: Int? = null,
    val color: Float = 1.4f,
    val image: String? = null,
    val aroma: Map<String, Float> = mapOf(
        "향신료" to 0f, "피트" to 0f, "곡물" to 0f, "꽃" to 0f,
        "과일" to 0f, "나무" to 0f, "기타" to 0f
    ),
    val selectedAromas: List<AromaSelection> = emptyList(),
    val palate: Map<String, Float> = mapOf(
        "단맛" to 5f, "신맛" to 5f, "쓴맛" to 5f,
        "지방맛" to 5f, "짠맛" to 5f, "감칠맛" to 5f
    ),
    val retronasal: Map<String, Float> = mapOf(
        "향신료" to 0f, "피트" to 0f, "곡물" to 0f, "꽃" to 0f,
        "과일" to 0f, "나무" to 0f, "기타" to 0f
    ),
    val selectedRetronasalAromas: List<AromaSelection> = emptyList(),
    val characters: List<CharacterSlider> = listOf(
        CharacterSlider("드라이", "오일리", 5),
        CharacterSlider("가벼운", "무거운", 5),
        CharacterSlider("순한", "매운", 5),
        CharacterSlider("부드러운", "강렬한", 5)
    ),
    val comments: Comments = Comments(),
    val created: Long = System.currentTimeMillis(),
    val modified: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

data class AromaSelection(
    val category: String,
    val subCategory: String,
    val name: String
)

data class CharacterSlider(
    val left: String,
    val right: String,
    val value: Int
)

data class Comments(
    val nose: String = "",
    val palate: String = "",
    val finish: String = "",
    val overall: String = ""
)