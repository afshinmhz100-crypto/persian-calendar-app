package com.example.calendar.ui

import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.data.AppThemeStyle
import com.example.calendar.data.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    // Accordion expandable states for settings sections
    var isThemeExpanded by remember { mutableStateOf(false) }
    var isFontExpanded by remember { mutableStateOf(false) }
    var isNotificationsExpanded by remember { mutableStateOf(false) }
    var isCalendarOptionsExpanded by remember { mutableStateOf(false) }
    var isPermissionsExpanded by remember { mutableStateOf(false) }
    var isAboutExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // ZarinPal Support CTA Card with Center-Aligned Texts
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setZarinPalModalVisible(true) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E7)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFD166))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFECEE),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "حمایت",
                                tint = Color(0xFFD90429),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "در صورت رضایت از اپ از ما حمایت کنید",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8A5A00),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "اتصال به درگاه امن زرین‌پال • ۵۰٪ صرف کمک به بیماران نیازمند می‌شود",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B4700),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Section 1: Collapsible Themes & Dark/Light Mode
        item {
            ExpandableSettingsCard(
                title = "شخصی‌سازی و تم",
                subtitle = "حالت تیره/روشن و انتخاب پالت رنگی",
                icon = Icons.Default.Palette,
                isExpanded = isThemeExpanded,
                onToggle = { isThemeExpanded = !isThemeExpanded }
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "حالت نور و تاریکی:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isModeSelected = userSettings.themeMode == mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isModeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setThemeMode(mode) }
                            ) {
                                Text(
                                    text = when (mode) {
                                        ThemeMode.LIGHT -> "☀️ روشن"
                                        ThemeMode.DARK -> "🌙 تاریک"
                                        ThemeMode.SYSTEM -> "⚙️ خودکار"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isModeSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "پالت رنگی برنامه:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AppThemeStyle.values().forEach { theme ->
                        val isSelected = userSettings.theme == theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                )
                                .clickable { viewModel.setAppTheme(theme) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                ) {}
                            } else {
                                Spacer(modifier = Modifier.size(10.dp))
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = theme.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = theme.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Collapsible Font Size Scaling
        item {
            ExpandableSettingsCard(
                title = "اندازه قلم و متون",
                subtitle = "تغییر مقیاس فونت و اندازه اعداد تقویم",
                icon = Icons.Default.FormatSize,
                isExpanded = isFontExpanded,
                onToggle = { isFontExpanded = !isFontExpanded }
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "اندازه متن‌ها و نوشته‌های تقویم را تنظیم کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val currentScale = userSettings.fontSizeScale
                    val presets = listOf(
                        Triple("کوچک", 0.85f, "۸۵٪"),
                        Triple("استاندارد", 1.0f, "۱۰۰٪"),
                        Triple("بزرگ", 1.15f, "۱۱۵٪"),
                        Triple("خیلی بزرگ", 1.30f, "۱۳۰٪")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { (label, scaleVal, percentStr) ->
                            val isSelected = Math.abs(currentScale - scaleVal) < 0.04f
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setFontSizeScale(scaleVal) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = percentStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "A+",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Slider(
                            value = currentScale,
                            onValueChange = { viewModel.setFontSizeScale(it) },
                            valueRange = 0.80f..1.35f,
                            steps = 10,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .testTag("font_size_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "A-",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "پیش‌نمایش قلم وزیرمتن: سه‌شنبه ۲۴ مرداد ۱۴۰۵",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Collapsible Notifications Preferences
        item {
            ExpandableSettingsCard(
                title = "تنظیمات اعلان‌ها و نوار وضعیت",
                subtitle = "اعلان تقویم در نوار وضعیت، یادآوری تولدها و رویدادها",
                icon = Icons.Default.Notifications,
                isExpanded = isNotificationsExpanded,
                onToggle = { isNotificationsExpanded = !isNotificationsExpanded }
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Daily persistent status bar notification
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = userSettings.enableDailyCalendarNotification,
                            onCheckedChange = { viewModel.toggleDailyCalendarNotification(it) }
                        )
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("اعلان دائمی تقویم در نوار وضعیت", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("نمایش عدد روز و تاریخ کامل در نوار اعلان بالای گوشی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (userSettings.enableDailyCalendarNotification) {
                        // Sub-options for daily notification
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            // Weather in notif
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = userSettings.showWeatherInNotification,
                                    onCheckedChange = { viewModel.toggleShowWeatherInNotification(it) }
                                )
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                                    Text("نمایش آب و هوا در اعلان", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text("نمایش دما و وضعیت هوا در نوار اعلان", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Sub-dates in notif
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = userSettings.showSubDatesInNotification,
                                    onCheckedChange = { viewModel.toggleShowSubDatesInNotification(it) }
                                )
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                                    Text("نمایش تاریخ میلادی و قمری در اعلان", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text("نمایش تاریخ‌های معادل در نوار اعلان", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Occasions in notif
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = userSettings.showOccasionsInNotification,
                                    onCheckedChange = { viewModel.toggleShowOccasionsInNotification(it) }
                                )
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                                    Text("نمایش مناسبت‌های روز در اعلان باز شده", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text("نمایش تعطیلات و مناسبت‌ها در اعلان تقویم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Refresh / Test notification button
                            OutlinedButton(
                                onClick = {
                                    viewModel.refreshCalendarNotification()
                                    Toast.makeText(context, "اعلان تقویم بروزرسانی شد", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("بروزرسانی و تست اعلان نوار وضعیت", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // Birthday notifications
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = userSettings.enableBirthdayNotifications,
                            onCheckedChange = { viewModel.toggleBirthdayNotifications(it) }
                        )
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("اعلان یادآوری تولدها و سالگردها", style = MaterialTheme.typography.bodyMedium)
                            Text("یادآوری زادروز دوستان و اقوام در روز مقرر", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Event Reminders
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = userSettings.enableEventReminders,
                            onCheckedChange = { viewModel.toggleEventReminders(it) }
                        )
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("اعلان یادداشت‌ها و جلسات", style = MaterialTheme.typography.bodyMedium)
                            Text("هشدار سر ساعت برای کارهای مهم و جلسات", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Holiday Reminders
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = userSettings.enableHolidayNotifications,
                            onCheckedChange = { viewModel.toggleHolidayNotifications(it) }
                        )
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text("اعلان تعطیلات و مناسبت‌های رسمی", style = MaterialTheme.typography.bodyMedium)
                            Text("نمایش مناسبت‌های ویژه خورشیدی و جهانی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Section 4: Collapsible Calendar Display Options
        item {
            ExpandableSettingsCard(
                title = "تنظیمات نمایش تقویم",
                subtitle = "گزینه‌های نمایش تاریخ میلادی و قمری",
                icon = Icons.Default.Visibility,
                isExpanded = isCalendarOptionsExpanded,
                onToggle = { isCalendarOptionsExpanded = !isCalendarOptionsExpanded }
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = userSettings.showGregorianInGrid,
                            onCheckedChange = { viewModel.toggleShowGregorianInGrid(it) }
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text("نمایش عدد تاریخ میلادی در خانه تقویم", style = MaterialTheme.typography.bodyMedium)
                            Text("نوشتن عدد میلادی در کنار روز خورشیدی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Section 5: Collapsible Calendar Permissions & Device
        item {
            ExpandableSettingsCard(
                title = "دسترسی‌ها و تقویم دستگاه",
                subtitle = "هماهنگی با تقویم پیش‌فرض اندروید",
                icon = Icons.Default.Event,
                isExpanded = isPermissionsExpanded,
                onToggle = { isPermissionsExpanded = !isPermissionsExpanded }
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "با اعطای دسترسی، رویدادها و یادداشت‌های شما با تقویم اصلی گوشی (Google Calendar) هماهنگ می‌شوند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).setData(CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build())
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "اپلیکیشن تقویم پیش‌فرض یافت نشد", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Event, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("مشاهده رویدادها در تقویم سیستمی")
                        }
                    }
                }
            }
        }

        // Section 6: Collapsible About App
        item {
            ExpandableSettingsCard(
                title = "درباره برنامه",
                subtitle = "نسخه ۱.۰.۲ و مشخصات",
                icon = Icons.Default.Info,
                isExpanded = isAboutExpanded,
                onToggle = { isAboutExpanded = !isAboutExpanded }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تقویم پارسی 1405",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "نسخه ۱.۰.۲ (تابستان ۱۴۰۵)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تقویم هوشمند با مناسبت‌ها، اوقات شرعی، استخاره، ابزارهای کاربردی، ویجت هوشمند و اعلان اختصاصی وضعیت",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

/**
 * Reusable Expandable Accordion Card for Settings Sections
 */
@Composable
fun ExpandableSettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "expand_arrow_rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row (Clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggle() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand/Collapse Chevron Icon
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "بستن" else "باز کردن",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )

                // Title, Subtitle and Icon on Right (RTL)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Animated Collapsible Content Body
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                content()
            }
        }
    }
}
