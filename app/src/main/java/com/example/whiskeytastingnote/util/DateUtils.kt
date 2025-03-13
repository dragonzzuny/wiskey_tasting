// app/src/main/java/com/example/whiskeytastingnote/util/DateUtils.kt
package com.example.whiskeytastingnote.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility class for date operations
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
    private val fullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

    /**
     * Format a date as a string
     */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    /**
     * Format a date with time as a string
     */
    fun formatDateTime(date: Date): String {
        return "${dateFormat.format(date)} ${timeFormat.format(date)}"
    }

    /**
     * Format a date in full format
     */
    fun formatFull(date: Date): String {
        return fullFormat.format(date)
    }

    /**
     * Get a friendly representation of time elapsed
     */
    fun getTimeAgo(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24

        return when {
            diffInDays > 30 -> formatDate(date)
            diffInDays > 0 -> "${diffInDays}일 전"
            diffInHours > 0 -> "${diffInHours}시간 전"
            diffInMinutes > 0 -> "${diffInMinutes}분 전"
            else -> "방금 전"
        }
    }
}