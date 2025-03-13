// app/src/main/java/com/example/whiskeytastingnote/data/model/CategoryData.kt
package com.example.whiskeytastingnote.data.model

/**
 * Object containing whiskey aroma categories and sub-categories
 */
object CategoryData {
    val CATEGORIES = mapOf(
        "향신료" to listOf("허브", "말린과일", "향신료", "매운 향"),
        "피트" to listOf("피트연기", "공장냄새", "해양 향", "플라스틱"),
        "곡물" to listOf("보리", "견과류", "당류", "가죽"),
        "꽃" to listOf("식물", "꽃", "자연"),
        "과일" to listOf("시트러스", "신선한 과일", "말린 과일", "구운 과일", "디저트"),
        "나무" to listOf("나무", "오래된 목재", "바닐라", "스모키 우드", "가죽"),
        "기타" to listOf("견과류 오일", "곡물 기반 제품", "낙농", "미네랄", "메디컬", "금속", "화학적 향")
    )

    val SUB_AROMAS = mapOf(
        // 향신료
        "허브" to listOf("로즈마리", "타임", "바질", "민트", "세이지", "라벤더"),
        "말린과일" to listOf("바나나", "건포도", "살구", "무화과", "대추", "자두"),
        "향신료" to listOf("계피", "팔각", "육두구", "커민", "후추", "정향", "고수씨", "생강"),
        "매운 향" to listOf("칠리", "고추냉이", "머스타드", "겨자씨"),

        // 피트
        "피트연기" to listOf("훈제향", "불에 탄 나무", "숯", "화산재"),
        "공장냄새" to listOf("석탄불", "그을음", "아스팔트", "타르", "소독약 냄새"),
        "해양 향" to listOf("요오드", "해초", "소금", "바닷바람", "젖은 바위"),
        "플라스틱" to listOf("고무", "비닐", "새 타이어", "본드"),

        // 곡물
        "보리" to listOf("시리얼", "오트밀", "빵", "토스트", "뮤즐리"),
        "견과류" to listOf("헤이즐넛", "아몬드", "호두", "피스타치오", "마카다미아"),
        "당류" to listOf("꿀", "설탕", "초콜릿", "카라멜", "마시멜로"),
        "가죽" to listOf("담배잎", "새 가죽", "낡은 서적", "사냥 가방"),

        // 꽃
        "식물" to listOf("고사리", "이끼", "버섯", "낙엽"),
        "꽃" to listOf("장미", "수국", "제비꽃", "아카시아", "오렌지 블러섬", "라일락"),
        "자연" to listOf("깎은 잔디", "축축한 흙", "젖은 나무", "이슬"),

        // 과일
        "시트러스" to listOf("레몬", "자몽", "귤", "오렌지 껍질", "유자"),
        "신선한 과일" to listOf("청사과", "배", "복숭아", "체리", "망고", "파인애플"),
        "말린 과일" to listOf("무화과", "대추야자", "건자두", "건살구", "건포도"),
        "구운 과일" to listOf("구운 사과", "구운 바나나", "카라멜화된 배"),
        "디저트" to listOf("바닐라 아이스크림", "크림브륄레", "마지팬", "팥앙금"),

        // 나무
        "나무" to listOf("오크", "소나무", "삼나무", "너도밤나무", "편백나무"),
        "오래된 목재" to listOf("엔틱 가구", "종이", "서재 향", "와인 배럴"),
        "바닐라" to listOf("코코넛", "바닐라", "카라멜", "버터스카치", "연유"),
        "스모키 우드" to listOf("구운 참나무", "타르", "숯불"),

        // 기타
        "견과류 오일" to listOf("피넛버터", "아몬드 오일", "참기름"),
        "곡물 기반 제품" to listOf("팝콘", "옥수수 가루", "크래커", "크루아상"),
        "낙농" to listOf("버터", "크림", "치즈", "요거트"),
        "미네랄" to listOf("분필", "젖은 돌", "해변 모래", "화산석"),
        "메디컬" to listOf("소독약", "페놀", "본드", "타르", "장작 타는 냄새"),
        "금속" to listOf("동전", "녹슨 철", "피복 벗겨진 전선"),
        "화학적 향" to listOf("아세톤", "본드", "석유", "매니큐어 리무버")
    )

    // Get all available main categories
    fun getMainCategories(): List<String> = CATEGORIES.keys.toList()

    // Get sub-categories for a main category
    fun getSubCategories(mainCategory: String): List<String> = CATEGORIES[mainCategory] ?: emptyList()

    // Get aromas for a sub-category
    fun getAromas(subCategory: String): List<String> = SUB_AROMAS[subCategory] ?: emptyList()
}