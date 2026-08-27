package com.example.calendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.core.CalendarDayModel
import com.example.calendar.core.CalendarOccasion
import com.example.calendar.core.OccasionType
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate
import com.example.calendar.data.CalendarEventEntity
import com.example.calendar.data.ThemeMode
import com.example.calendar.ui.components.AddEventBottomSheet
import com.example.ui.theme.HolidayRed
import com.example.ui.theme.HolidayRedLight

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val currentYear by viewModel.currentYear.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val monthDays by viewModel.monthDays.collectAsStateWithLifecycle()
    val selectedDayEvents by viewModel.selectedDayEvents.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    val isDark = when (userSettings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    var showAddEventSheet by remember { mutableStateOf(false) }

    val today = remember { PersianCalendarHelper.getTodayPersian() }
    val isViewingTodayMonth = (currentYear == today.year && currentMonth == today.month)

    var totalDragX by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Month Header Controls
            item {
                Spacer(modifier = Modifier.height(6.dp))
                MonthHeader(
                    year = currentYear,
                    month = currentMonth,
                    isViewingToday = isViewingTodayMonth,
                    isDark = isDark,
                    onNext = { viewModel.nextMonth() },
                    onPrev = { viewModel.prevMonth() },
                    onJumpToday = { viewModel.jumpToToday() }
                )
            }

            // Days of Week Header & Month Grid (Supports horizontal swipe to switch months)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(currentYear, currentMonth) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (totalDragX > 45f) {
                                        // In RTL, dragging to right moves to next month
                                        viewModel.nextMonth()
                                    } else if (totalDragX < -45f) {
                                        viewModel.prevMonth()
                                    }
                                    totalDragX = 0f
                                },
                                onDragCancel = { totalDragX = 0f },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    totalDragX += dragAmount
                                }
                            )
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
                        // Week Days Names (ش, ی, د, س, چ, پ, ج)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PersianCalendarHelper.WEEK_DAYS_SHORT.forEachIndexed { index, dayName ->
                                val isFriday = index == 6
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFriday) {
                                        if (isDark) Color(0xFFFF6B6B) else HolidayRed
                                    } else {
                                        if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF111416)
                                    },
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Calendar Grid Days - Perfectly Square Day Cells
                        val rows = monthDays.chunked(7)
                        rows.forEach { week ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                week.forEach { dayModel ->
                                    val isSelected = (dayModel.persianDate == selectedDate)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(1.5.dp)
                                    ) {
                                        DayCell(
                                            dayModel = dayModel,
                                            isSelected = isSelected,
                                            showGregorian = userSettings.showGregorianInGrid,
                                            isDark = isDark,
                                            onClick = {
                                                viewModel.selectDate(dayModel.persianDate)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Header & Occasion Details
            item {
                SelectedDayDetailsCard(
                    selectedDate = selectedDate,
                    occasions = PersianCalendarHelper.getOccasionsForPersianDate(
                        selectedDate.year,
                        selectedDate.month,
                        selectedDate.day
                    ),
                    isDark = isDark,
                    onAddClick = { showAddEventSheet = true }
                )
            }

            // User Events for this day
            if (selectedDayEvents.isNotEmpty()) {
                item {
                    Text(
                        text = "یادداشت‌ها و رویدادهای این روز (${PersianCalendarHelper.toPersianDigits(selectedDayEvents.size)})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                items(selectedDayEvents, key = { it.id }) { event ->
                    EventItemCard(
                        event = event,
                        onToggleComplete = { viewModel.toggleEventCompletion(event) },
                        onDelete = { viewModel.deleteEvent(event) }
                    )
                }
            } else {
                item {
                    EmptyEventsPlaceholder(
                        onAddClick = { showAddEventSheet = true }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar & FAB
            }
        }

        // Floating Action Button to Add Event
        FloatingActionButton(
            onClick = { showAddEventSheet = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .testTag("add_event_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن رویداد")
        }
    }

    if (showAddEventSheet) {
        AddEventBottomSheet(
            initialDate = selectedDate,
            onDismiss = { showAddEventSheet = false },
            onAddEvent = { title, desc, date, category, hour, min, reminder, priority, color ->
                viewModel.addEvent(title, desc, date, category, hour, min, reminder, priority, color)
            }
        )
    }
}

@Composable
fun MonthHeader(
    year: Int,
    month: Int,
    isViewingToday: Boolean,
    isDark: Boolean,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onJumpToday: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev Month Button (Right arrow in RTL)
        IconButton(
            onClick = onPrev,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.85f))
                .size(40.dp)
                .testTag("prev_month_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "ماه قبل",
                tint = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF111416)
            )
        }

        // Month & Year Title and Today chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${PersianCalendarHelper.getMonthName(month)} ${PersianCalendarHelper.toPersianDigits(year)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF111416)
                )
                Text(
                    text = PersianCalendarHelper.getZodiacSign(month),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!isViewingToday) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onJumpToday() }
                ) {
                    Text(
                        text = "امروز",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Next Month Button (Left arrow in RTL)
        IconButton(
            onClick = onNext,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.85f))
                .size(40.dp)
                .testTag("next_month_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "ماه بعد",
                tint = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF111416)
            )
        }
    }
}

@Composable
fun DayCell(
    dayModel: CalendarDayModel,
    isSelected: Boolean,
    showGregorian: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val isFriday = dayModel.dayOfWeekIndex == 6
    val isHoliday = dayModel.isHoliday

    val cellBackground = when {
        isSelected -> MaterialTheme.colorScheme.primary
        dayModel.isToday -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.2f else 0.14f)
        isHoliday && dayModel.isCurrentMonth -> {
            if (isDark) Color(0xFF3E1216) else Color(0xFFFFECEE)
        }
        else -> Color.Transparent
    }

    val primaryTextColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !dayModel.isCurrentMonth -> if (isDark) Color(0xFF757D82) else Color(0xFF8E9599)
        isHoliday || isFriday -> if (isDark) Color(0xFFFF6B6B) else HolidayRed
        dayModel.isToday -> if (isDark) Color(0xFF80DEEA) else Color(0xFF006874)
        else -> if (isDark) Color(0xFFF1F3F4) else Color(0xFF111416)
    }

    val secondaryTextColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f)
        !dayModel.isCurrentMonth -> if (isDark) Color(0xFF5E6569) else Color(0xFFA5AAAE)
        isHoliday || isFriday -> if (isDark) Color(0xFFFF8A80) else HolidayRed.copy(alpha = 0.9f)
        dayModel.isToday -> if (isDark) Color(0xFF80DEEA).copy(alpha = 0.9f) else Color(0xFF004D56)
        else -> if (isDark) Color(0xFFC0C7CB) else Color(0xFF32383C)
    }

    // Perfectly square day cell (aspectRatio 1.0f)
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .clip(RoundedCornerShape(10.dp))
            .background(cellBackground)
            .then(
                if (dayModel.isToday && !isSelected) {
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                } else Modifier
            )
            .clickable { onClick() }
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Persian Day Number (bold, prominent)
            Text(
                text = PersianCalendarHelper.toPersianDigits(dayModel.persianDate.day),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (dayModel.isToday || isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = primaryTextColor,
                fontSize = 15.sp,
                lineHeight = 16.sp
            )

            // Sub-line: Hijri in Persian digits and Gregorian in English digits on ONE single horizontal line
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 1.dp)
            ) {
                // Hijri date in Persian digits (pure number, e.g. ۲۳)
                Text(
                    text = PersianCalendarHelper.toPersianDigits(dayModel.hijriDate.day),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 9.sp
                )

                if (showGregorian) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryTextColor.copy(alpha = 0.6f),
                        fontSize = 7.sp,
                        lineHeight = 8.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    // Gregorian date in English digits (e.g. 23)
                    Text(
                        text = dayModel.gregorianDate.day.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryTextColor.copy(alpha = 0.85f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 9.sp
                    )
                }
            }

            // Dot indicators for events / holidays
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 1.dp)
            ) {
                if (dayModel.eventCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(4.5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                    )
                }
                if (isHoliday && dayModel.isCurrentMonth) {
                    if (dayModel.eventCount > 0) Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isDark) Color(0xFFFF6B6B) else HolidayRed)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedDayDetailsCard(
    selectedDate: PersianDate,
    occasions: List<CalendarOccasion>,
    isDark: Boolean,
    onAddClick: () -> Unit
) {
    val gDate = remember(selectedDate) {
        PersianCalendarHelper.persianToGregorian(selectedDate.year, selectedDate.month, selectedDate.day)
    }
    val hDate = remember(selectedDate) {
        PersianCalendarHelper.persianToHijri(selectedDate.year, selectedDate.month, selectedDate.day)
    }
    val dayOfWeekName = remember(selectedDate) {
        PersianCalendarHelper.getDayOfWeekName(selectedDate)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.5f else 0.65f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Info: Solar, Gregorian, Hijri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${gDate.day} ${gDate.monthName} ${gDate.year}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF4A5557)
                    )
                    Text(
                        text = hDate.toHijriFormattedString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$dayOfWeekName ${selectedDate.toPersianFormattedString()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF111416)
                    )
                    Text(
                        text = PersianCalendarHelper.getZodiacSign(selectedDate.month),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Holidays & Occasions List with specialized color coding (Celebrations/Births = Green, Martyrdom/Deaths = Red, Global = Blue)
            if (occasions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                occasions.forEach { occasion ->
                    val (badgeBg, badgeText, itemBg, labelText, textColor) = when (occasion.occasionType) {
                        OccasionType.CELEBRATION -> {
                            Tuple5(
                                if (isDark) Color(0xFF1B5E20) else Color(0xFF2E7D32),
                                Color.White,
                                if (isDark) Color(0xFF122E15) else Color(0xFFE8F5E9),
                                "ولادت / جشن 🌸",
                                if (isDark) Color(0xFF81C784) else Color(0xFF1B5E20)
                            )
                        }
                        OccasionType.MARTYRDOM -> {
                            Tuple5(
                                if (isDark) Color(0xFF8B0000) else Color(0xFFD90429),
                                Color.White,
                                if (isDark) Color(0xFF381014) else Color(0xFFFFEBEE),
                                "شهادت / رحلت 🖤",
                                if (isDark) Color(0xFFFF8A80) else Color(0xFFB71C1C)
                            )
                        }
                        OccasionType.OFFICIAL_HOLIDAY -> {
                            Tuple5(
                                if (isDark) Color(0xFFB71C1C) else HolidayRed,
                                Color.White,
                                if (isDark) Color(0xFF3E1216) else Color(0xFFFFECEE),
                                "تعطیل رسمی 🔴",
                                if (isDark) Color(0xFFFF8A80) else HolidayRed
                            )
                        }
                        OccasionType.GLOBAL -> {
                            Tuple5(
                                if (isDark) Color(0xFF0277BD) else Color(0xFF0288D1),
                                Color.White,
                                if (isDark) Color(0xFF0D2538) else Color(0xFFE1F5FE),
                                "رویداد جهانی 🌍",
                                if (isDark) Color(0xFF81D4FA) else Color(0xFF01579B)
                            )
                        }
                        OccasionType.NATIONAL -> {
                            Tuple5(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.3f else 0.45f),
                                "مناسبت ملی 🇮🇷",
                                if (isDark) Color(0xFFECECEC) else Color(0xFF111416)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(itemBg)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge with small icon and clear spacing
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeBg
                        ) {
                            Text(
                                text = if (occasion.isOfficialHoliday && occasion.occasionType != OccasionType.MARTYRDOM && occasion.occasionType != OccasionType.CELEBRATION) "تعطیل رسمی" else labelText,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeText,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = occasion.title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (occasion.isOfficialHoliday || occasion.occasionType == OccasionType.CELEBRATION || occasion.occasionType == OccasionType.MARTYRDOM) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

@Composable
fun EventItemCard(
    event: CalendarEventEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = if (event.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "تکمیل",
                        tint = if (event.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }

                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = event.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (event.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right
                        )
                    }

                    if (event.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyEventsPlaceholder(
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "برای افزودن یادداشت یا یادآوری تولد ضربه بزنید",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
