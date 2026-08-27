package com.example.calendar.ui.tools

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.PersianCalendarHelper
import kotlinx.coroutines.delay

@Composable
fun TimerStopwatchToolView(
    modifier: Modifier = Modifier
) {
    var isTimerMode by remember { mutableStateOf(false) }

    // Stopwatch states
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTimeMillis by remember { mutableLongStateOf(0L) }
    val laps = remember { mutableStateListOf<Long>() }

    // Countdown Timer states
    var timerRunning by remember { mutableStateOf(false) }
    var timerRemainingSeconds by remember { mutableIntStateOf(300) } // 5 minutes default
    var initialTimerSeconds by remember { mutableIntStateOf(300) }

    // Stopwatch tick effect
    LaunchedEffect(isRunning) {
        if (isRunning) {
            val startTime = System.currentTimeMillis() - elapsedTimeMillis
            while (isRunning) {
                elapsedTimeMillis = System.currentTimeMillis() - startTime
                delay(30)
            }
        }
    }

    // Countdown Timer tick effect
    LaunchedEffect(timerRunning, timerRemainingSeconds) {
        if (timerRunning && timerRemainingSeconds > 0) {
            delay(1000)
            timerRemainingSeconds--
            if (timerRemainingSeconds <= 0) {
                timerRunning = false
            }
        }
    }

    fun formatMillis(millis: Long): String {
        val totalSec = millis / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        val ms = (millis % 1000) / 10
        return "%02d:%02d.%02d".format(min, sec, ms)
    }

    fun formatSeconds(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "%02d:%02d".format(m, s)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !isTimerMode,
                onClick = { isTimerMode = false },
                label = { Text("⏱️ کرونومتر (زمان‌سنج)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = isTimerMode,
                onClick = { isTimerMode = true },
                label = { Text("⏳ تایمر معکوس", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!isTimerMode) {
            // STOPWATCH VIEW
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = PersianCalendarHelper.toPersianDigits(formatMillis(elapsedTimeMillis)),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset
                        IconButton(
                            onClick = {
                                isRunning = false
                                elapsedTimeMillis = 0L
                                laps.clear()
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "ریست", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Play/Pause
                        Button(
                            onClick = { isRunning = !isRunning },
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRunning) "توقف" else "شروع",
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Lap
                        IconButton(
                            onClick = {
                                if (isRunning) {
                                    laps.add(0, elapsedTimeMillis)
                                }
                            },
                            enabled = isRunning
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = "ثبت دور", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            if (laps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🏁 دورهای ثبت‌شده (Laps):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        laps.forEachIndexed { index, lapTime ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("دور ${PersianCalendarHelper.toPersianDigits((laps.size - index).toString())}")
                                Text(
                                    PersianCalendarHelper.toPersianDigits(formatMillis(lapTime)),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // COUNTDOWN TIMER VIEW
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = PersianCalendarHelper.toPersianDigits(formatSeconds(timerRemainingSeconds)),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Timer duration buttons (1m, 3m, 5m, 10m, 15m)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(60 to "۱ دقیقه", 180 to "۳ دقیقه", 300 to "۵ دقیقه", 600 to "۱۰ دقیقه", 900 to "۱۵ دقیقه").forEach { (sec, label) ->
                            FilterChip(
                                selected = initialTimerSeconds == sec,
                                onClick = {
                                    timerRunning = false
                                    initialTimerSeconds = sec
                                    timerRemainingSeconds = sec
                                },
                                label = { Text(label, fontSize = 10.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                timerRunning = false
                                timerRemainingSeconds = initialTimerSeconds
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "ریست تایمر", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { timerRunning = !timerRunning },
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (timerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (timerRunning) "توقف" else "شروع",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}
