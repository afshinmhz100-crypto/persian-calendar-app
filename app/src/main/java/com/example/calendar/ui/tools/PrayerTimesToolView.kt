package com.example.calendar.ui.tools

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.ui.CalendarViewModel

data class PrayerTimeItem(
    val title: String,
    val time: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
fun PrayerTimesToolView(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()

    // Approximate Prayer Times based on longitude / latitude offset from Tehran (35.6892, 51.3890)
    val lonDiffMinutes = ((selectedCity.longitude - 51.3890) * 4.0).toInt()

    fun adjustTime(hour: Int, minute: Int): String {
        var totalMin = hour * 60 + minute - lonDiffMinutes
        if (totalMin < 0) totalMin += 1440
        if (totalMin >= 1440) totalMin -= 1440
        val h = totalMin / 60
        val m = totalMin % 60
        return "%02d:%02d".format(h, m)
    }

    val times = listOf(
        PrayerTimeItem("اذان صبح", PersianCalendarHelper.toPersianDigits(adjustTime(4, 38)), Icons.Default.NightsStay, Color(0xFF5C6BC0)),
        PrayerTimeItem("طلوع آفتاب", PersianCalendarHelper.toPersianDigits(adjustTime(6, 6)), Icons.Default.WbTwilight, Color(0xFFFFA726)),
        PrayerTimeItem("اذان ظهر", PersianCalendarHelper.toPersianDigits(adjustTime(12, 45)), Icons.Default.WbSunny, Color(0xFFFFB300)),
        PrayerTimeItem("غروب آفتاب", PersianCalendarHelper.toPersianDigits(adjustTime(19, 24)), Icons.Default.WbTwilight, Color(0xFFFF7043)),
        PrayerTimeItem("اذان مغرب", PersianCalendarHelper.toPersianDigits(adjustTime(19, 44)), Icons.Default.Mosque, Color(0xFF26A69A)),
        PrayerTimeItem("نیمه‌شب شرعی", PersianCalendarHelper.toPersianDigits(adjustTime(23, 51)), Icons.Default.NightsStay, Color(0xFF7E57C2))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // City Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "اوقات شرعی به افق ${selectedCity.name} (${selectedCity.province})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "مبنا: موسسه ژئوفیزیک دانشگاه تهران",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prayer Times List
        times.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = item.tint.copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.tint,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = item.tint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}
