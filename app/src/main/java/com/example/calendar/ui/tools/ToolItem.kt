package com.example.calendar.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolId(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val iconVector: ImageVector,
    val gradientColors: List<Color>,
    val badgeText: String? = null
) {
    DATE_CONVERTER(
        title = "تبدیل تاریخ و سن",
        subtitle = "شمسی، میلادی، قمری",
        iconEmoji = "📅",
        iconVector = Icons.Default.CalendarMonth,
        gradientColors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0)),
        badgeText = "دقیق"
    ),
    UNIT_CONVERTER(
        title = "تبدیل واحدها",
        subtitle = "وزن، طول، حجم، دما",
        iconEmoji = "⚖️",
        iconVector = Icons.Default.Scale,
        gradientColors = listOf(Color(0xFF00897B), Color(0xFF00695C)),
        badgeText = "جامع"
    ),
    HEALTH_ASSESSMENT(
        title = "پایش سلامت و BMI",
        subtitle = "شاخص بدنی و کالری",
        iconEmoji = "🩺",
        iconVector = Icons.Default.FitnessCenter,
        gradientColors = listOf(Color(0xFFE53935), Color(0xFFC62828)),
        badgeText = "جدید"
    ),
    CURRENCY_CONVERTER(
        title = "قیمت و مبدل ارز",
        subtitle = "دلار، یورو، طلا و سکه",
        iconEmoji = "💰",
        iconVector = Icons.Default.CurrencyExchange,
        gradientColors = listOf(Color(0xFFF57C00), Color(0xFFE65100)),
        badgeText = "لحظه‌ای"
    ),
    PRAYER_TIMES(
        title = "اوقات شرعی",
        subtitle = "اذان و طلوع شهرها",
        iconEmoji = "🕌",
        iconVector = Icons.Default.Mosque,
        gradientColors = listOf(Color(0xFF43A047), Color(0xFF2E7D32)),
        badgeText = "دقیق"
    ),
    TIMER_STOPWATCH(
        title = "زمان‌سنج و تایمر",
        subtitle = "کرونومتر و شمارش",
        iconEmoji = "⏱️",
        iconVector = Icons.Default.Timer,
        gradientColors = listOf(Color(0xFF6D4C41), Color(0xFF4E342E))
    ),
    ESTEKHAREH(
        title = "استخاره با قرآن",
        subtitle = "تفأل قرآنی با معنی",
        iconEmoji = "📖",
        iconVector = Icons.Default.MenuBook,
        gradientColors = listOf(Color(0xFF3949AB), Color(0xFF283593))
    )
}
