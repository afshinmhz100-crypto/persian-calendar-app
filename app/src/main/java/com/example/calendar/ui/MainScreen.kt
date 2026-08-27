package com.example.calendar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.calendar.data.ThemeMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.ui.components.WeatherBottomSheet
import com.example.calendar.ui.components.ZarinPalSupportModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: CalendarViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val showZarinPalModal by viewModel.showZarinPalModal.collectAsStateWithLifecycle()
    val weatherData by viewModel.weatherState.collectAsStateWithLifecycle()
    var showWeatherSheet by remember { mutableStateOf(false) }
    val today = remember { PersianCalendarHelper.getTodayPersian() }

    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = when (userSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    // Enlarged Weather Quick Button
                    Surface(
                        onClick = { showWeatherSheet = true },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .testTag("top_bar_weather_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = weatherData?.conditionEmoji ?: "🌤️",
                                fontSize = 19.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = weatherData?.getFormattedTemp() ?: "--",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color(0xFFE0F7FA) else Color(0xFF00363D)
                                )
                                Text(
                                    text = weatherData?.cityName ?: "تهران",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = if (isDark) Color(0xFFB0BEC5) else Color(0xFF455A64)
                                )
                            }
                        }
                    }
                },
                title = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "امروز: ${today.toPersianFormattedString()}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.setZarinPalModalVisible(true) },
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("top_bar_donate_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD166)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "حمایت مالی زرین‌پال",
                                tint = Color(0xFFD90429),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val items = listOf(
                    Triple(MainTab.CALENDAR, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
                    Triple(MainTab.FORTUNE, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
                    Triple(MainTab.CONVERTER, Icons.Filled.Widgets, Icons.Outlined.Widgets),
                    Triple(MainTab.NEWS, Icons.Filled.Newspaper, Icons.Outlined.Newspaper),
                    Triple(MainTab.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings)
                )

                items.forEach { (tab, filledIcon, outlinedIcon) ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) filledIcon else outlinedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
                            unselectedIconColor = if (isDark) Color(0xFFADB5BD) else Color(0xFF2B3035),
                            unselectedTextColor = if (isDark) Color(0xFFDEE2E6) else Color(0xFF191C1E)
                        ),
                        modifier = Modifier.testTag("nav_${tab.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_tabs_transition"
            ) { tab ->
                when (tab) {
                    MainTab.CALENDAR -> CalendarScreen(viewModel = viewModel)
                    MainTab.FORTUNE -> FortuneScreen(viewModel = viewModel)
                    MainTab.CONVERTER -> com.example.calendar.ui.tools.ToolsScreen(viewModel = viewModel)
                    MainTab.NEWS -> NewsScreen(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showZarinPalModal) {
        ZarinPalSupportModal(
            onDismiss = { viewModel.setZarinPalModalVisible(false) }
        )
    }

    if (showWeatherSheet) {
        WeatherBottomSheet(
            viewModel = viewModel,
            onDismiss = { showWeatherSheet = false }
        )
    }
}
