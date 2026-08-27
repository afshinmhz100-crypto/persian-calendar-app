package com.example.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate

@Composable
fun DateConverterScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val converterState by viewModel.converterState.collectAsStateWithLifecycle()
    var selectedConversionMode by remember { mutableIntStateOf(0) } // 0 = شمسی به میلادی و قمری, 1 = میلادی به شمسی و قمری

    // Persian input fields
    var pYearText by remember { mutableStateOf(converterState.persianYear.toString()) }
    var pYearError by remember { mutableStateOf(false) }
    var pMonth by remember { mutableIntStateOf(converterState.persianMonth) }
    var pDay by remember { mutableIntStateOf(converterState.persianDay) }

    // Gregorian input fields
    var gYearText by remember { mutableStateOf(converterState.gYear.toString()) }
    var gYearError by remember { mutableStateOf(false) }
    var gMonth by remember { mutableIntStateOf(converterState.gMonth) }
    var gDay by remember { mutableIntStateOf(converterState.gDay) }

    val monthScrollState = rememberScrollState()
    val dayScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector (شمسی به میلادی | میلادی به شمسی)
        TabRow(
            selectedTabIndex = selectedConversionMode,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedConversionMode == 0,
                onClick = { selectedConversionMode = 0 },
                text = {
                    Text(
                        text = "خورشیدی به میلادی و قمری",
                        fontWeight = if (selectedConversionMode == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
            Tab(
                selected = selectedConversionMode == 1,
                onClick = { selectedConversionMode = 1 },
                text = {
                    Text(
                        text = "میلادی به خورشیدی و قمری",
                        fontWeight = if (selectedConversionMode == 1) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedConversionMode == 0) {
            // Mode: Persian to Gregorian & Hijri
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "انتخاب تاریخ خورشیدی (شمسی):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Year row with input field and quick +/- buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val currentY = pYearText.toIntOrNull() ?: 1404
                                val newY = (currentY - 1).coerceAtLeast(1)
                                pYearText = newY.toString()
                                pYearError = false
                                viewModel.updateConverterPersianDate(newY, pMonth, pDay)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "کاهش سال")
                        }

                        OutlinedTextField(
                            value = pYearText,
                            onValueChange = { input ->
                                val cleanInput = PersianCalendarHelper.toEnglishDigits(input).filter { it.isDigit() }
                                pYearText = cleanInput
                                if (cleanInput.isBlank()) {
                                    pYearError = true
                                } else {
                                    val y = cleanInput.toIntOrNull()
                                    if (y != null && y in 1..9999) {
                                        pYearError = false
                                        val maxD = PersianCalendarHelper.getDaysInPersianMonth(y, pMonth)
                                        if (pDay > maxD) pDay = maxD
                                        viewModel.updateConverterPersianDate(y, pMonth, pDay)
                                    } else {
                                        pYearError = true
                                    }
                                }
                            },
                            label = { Text("سال خورشیدی") },
                            placeholder = { Text("مثال: 1378 یا 1404") },
                            isError = pYearError,
                            supportingText = if (pYearError) {
                                { Text("لطفاً سال را وارد کنید", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("converter_persian_year"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                val currentY = pYearText.toIntOrNull() ?: 1404
                                val newY = currentY + 1
                                pYearText = newY.toString()
                                pYearError = false
                                viewModel.updateConverterPersianDate(newY, pMonth, pDay)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزایش سال")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Month Selector: Scrollable Row of month pills
                    Text(
                        text = "انتخاب ماه: ${PersianCalendarHelper.getMonthName(pMonth)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(monthScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (m in 1..12) {
                            val isSelected = (m == pMonth)
                            val monthName = PersianCalendarHelper.getMonthName(m)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    )
                                    .clickable {
                                        pMonth = m
                                        val y = pYearText.toIntOrNull() ?: 1404
                                        val maxD = PersianCalendarHelper.getDaysInPersianMonth(y, m)
                                        if (pDay > maxD) pDay = maxD
                                        viewModel.updateConverterPersianDate(y, m, pDay)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = monthName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Day Selector: Scrollable Row of circular day buttons
                    val yVal = pYearText.toIntOrNull() ?: 1404
                    val maxDays = PersianCalendarHelper.getDaysInPersianMonth(yVal, pMonth)
                    Text(
                        text = "انتخاب روز: ${PersianCalendarHelper.toPersianDigits(pDay)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dayScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (d in 1..maxDays) {
                            val isSelected = (d == pDay)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        pDay = d
                                        val y = pYearText.toIntOrNull() ?: 1404
                                        viewModel.updateConverterPersianDate(y, pMonth, d)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = PersianCalendarHelper.toPersianDigits(d),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Mode: Gregorian to Persian & Hijri
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "انتخاب تاریخ میلادی (Gregorian):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Year row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val currentY = gYearText.toIntOrNull() ?: 2025
                                val newY = (currentY - 1).coerceAtLeast(1)
                                gYearText = newY.toString()
                                gYearError = false
                                viewModel.updateConverterGregorianDate(newY, gMonth, gDay)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Year")
                        }

                        OutlinedTextField(
                            value = gYearText,
                            onValueChange = { input ->
                                val cleanInput = input.filter { it.isDigit() }
                                gYearText = cleanInput
                                if (cleanInput.isBlank()) {
                                    gYearError = true
                                } else {
                                    val y = cleanInput.toIntOrNull()
                                    if (y != null && y in 1..9999) {
                                        gYearError = false
                                        viewModel.updateConverterGregorianDate(y, gMonth, gDay)
                                    } else {
                                        gYearError = true
                                    }
                                }
                            },
                            label = { Text("سال میلادی (Year)") },
                            placeholder = { Text("مثال: 1995 یا 2025") },
                            isError = gYearError,
                            supportingText = if (gYearError) {
                                { Text("لطفاً سال میلادی را وارد کنید", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("converter_gregorian_year"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                val currentY = gYearText.toIntOrNull() ?: 2025
                                val newY = currentY + 1
                                gYearText = newY.toString()
                                gYearError = false
                                viewModel.updateConverterGregorianDate(newY, gMonth, gDay)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Year")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gregorian Months: Scrollable Row
                    Text(
                        text = "Select Month: ${PersianCalendarHelper.getGregorianMonthName(gMonth)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(monthScrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (m in 1..12) {
                            val isSelected = (m == gMonth)
                            val monthName = PersianCalendarHelper.getGregorianMonthName(m)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    )
                                    .clickable {
                                        gMonth = m
                                        val y = gYearText.toIntOrNull() ?: 2025
                                        viewModel.updateConverterGregorianDate(y, m, gDay)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = monthName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gregorian Days: Scrollable Row
                    Text(
                        text = "Select Day: $gDay",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dayScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (d in 1..31) {
                            val isSelected = (d == gDay)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.secondary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        gDay = d
                                        val y = gYearText.toIntOrNull() ?: 2025
                                        viewModel.updateConverterGregorianDate(y, gMonth, d)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conversion Result Card with Persian, Gregorian and Hijri dates
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "نتایج کامل تبدیل تقویم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Displays (Persian, Gregorian, Hijri)
                val pDateStr = if (selectedConversionMode == 0) {
                    "${PersianCalendarHelper.toPersianDigits(converterState.persianDay)} ${PersianCalendarHelper.getMonthName(converterState.persianMonth)} ${PersianCalendarHelper.toPersianDigits(converterState.persianYear)}"
                } else {
                    converterState.convertedPersian?.toPersianFormattedString() ?: ""
                }

                val gDateStr = if (selectedConversionMode == 0) {
                    converterState.convertedGregorian?.let { "${it.day} ${it.monthName} ${it.year}" } ?: ""
                } else {
                    "${converterState.gDay} ${PersianCalendarHelper.getGregorianMonthName(converterState.gMonth)} ${converterState.gYear}"
                }

                val hDateStr = converterState.convertedHijri?.toHijriFormattedString() ?: ""

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateItemBanner(
                        title = "خورشیدی (هجری شمسی)",
                        value = pDateStr,
                        badge = "☀️ تقویم ملی",
                        badgeColor = MaterialTheme.colorScheme.primary
                    )
                    DateItemBanner(
                        title = "میلادی (Gregorian)",
                        value = gDateStr,
                        badge = "🌍 تقویم بین‌المللی",
                        badgeColor = MaterialTheme.colorScheme.secondary
                    )
                    DateItemBanner(
                        title = "قمری (هجری قمری)",
                        value = hDateStr,
                        badge = "🌙 تقویم اسلامی",
                        badgeColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Metric boxes for Day of Week & Leap Year
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        title = "روز هفته",
                        value = converterState.calculatedDayOfWeek,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "وضعیت سال خورشیدی",
                        value = if (converterState.isLeapYear) "کبیسه (۳۶۶ روز)" else "عادی (۳۶۵ روز)",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zodiac Constellation + Year Animal Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "برج فلکی و نماد حیوانی سال",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val month = if (selectedConversionMode == 0) converterState.persianMonth else (converterState.convertedPersian?.month ?: 1)
                val year = if (selectedConversionMode == 0) converterState.persianYear else (converterState.convertedPersian?.year ?: 1404)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "برج فلکی ماه",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = PersianCalendarHelper.getZodiacSign(month),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "نماد حیوانی سال",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val (animalName, emoji) = PersianCalendarHelper.getYearAnimal(year)
                            Text(
                                text = "سال $animalName $emoji",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Age & Birthday Candle Card (جایگزین روزهای مانده به پایان سال)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "محاسبه سن و شمع تولد شخص",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Cake,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val bday = converterState.birthdayInfo
                if (bday != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Exact Age
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "سن دقیق شخص",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${PersianCalendarHelper.toPersianDigits(bday.ageYears)} سال",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${PersianCalendarHelper.toPersianDigits(bday.ageMonths)} ماه و ${PersianCalendarHelper.toPersianDigits(bday.ageDays)} روز",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Candle Number to Blow Out
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "عدد شمع روی کیک تولد",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${PersianCalendarHelper.toPersianDigits(bday.candleNumber)} 🎂🕯️",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = if (bday.isBirthdayToday) "🎉 امروز روز تولد است!"
                                    else "${PersianCalendarHelper.toPersianDigits(bday.daysUntilNextBirthday)} روز تا فوت شمع",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
fun DateItemBanner(
    title: String,
    value: String,
    badge: String,
    badgeColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
