package com.example.calendar.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.PersianCalendarHelper
import com.example.calendar.core.PersianDate

import android.content.Intent
import android.provider.CalendarContract
import java.util.Calendar

data class CategoryOption(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEventBottomSheet(
    initialDate: PersianDate,
    onDismiss: () -> Unit,
    onAddEvent: (
        title: String,
        description: String,
        persianDate: PersianDate,
        category: String,
        timeHour: Int,
        timeMinute: Int,
        isReminderEnabled: Boolean,
        priority: String,
        colorHex: String
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDay by remember { mutableIntStateOf(initialDate.day) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.month) }
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }

    val categories = listOf(
        CategoryOption("یادداشت", Icons.Default.Bookmark, Color(0xFF007A87)),
        CategoryOption("تولد", Icons.Default.Cake, Color(0xFFE63946)),
        CategoryOption("رویداد مهم", Icons.Default.Star, Color(0xFFF77F00)),
        CategoryOption("کاری", Icons.Default.Work, Color(0xFF2A9D8F)),
        CategoryOption("جلسه", Icons.Default.Event, Color(0xFF457B9D))
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val priorities = listOf("کم", "متوسط", "زیاد")
    var selectedPriority by remember { mutableStateOf("متوسط") }

    var isReminderEnabled by remember { mutableStateOf(true) }
    var syncWithSystemCalendar by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var hasSpecificTime by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "انصراف")
                }
                Text(
                    text = "افزودن یادداشت و رویداد جدید",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${PersianCalendarHelper.toPersianDigits(selectedDay)} ${PersianCalendarHelper.getMonthName(selectedMonth)} ${PersianCalendarHelper.toPersianDigits(selectedYear)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "تاریخ انتخابی:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان رویداد یا یادداشت") },
                placeholder = { Text("مثال: تولد سارا، جلسه کاری، خرید...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("event_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("توضیحات تکمیلی (اختیاری)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Picker
            Text(
                text = "دسته‌بندی:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory.name == cat.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else cat.color
                            )
                        },
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    priorities.forEach { p ->
                        val isSelected = selectedPriority == p
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedPriority = p }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = p,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Text(
                    text = "اولویت:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder & Time Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = { isReminderEnabled = it }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("فعال‌سازی اعلان یادآوری")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sync with Android System Calendar Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = syncWithSystemCalendar,
                    onCheckedChange = { syncWithSystemCalendar = it }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("همگام‌سازی با تقویم اصلی گوشی", style = MaterialTheme.typography.bodyMedium)
                        Text("افزودن به Google Calendar و تقویم سیستم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "لطفاً عنوان رویداد را وارد کنید", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val date = PersianDate(selectedYear, selectedMonth, selectedDay)
                    onAddEvent(
                        title.trim(),
                        description.trim(),
                        date,
                        selectedCategory.name,
                        if (hasSpecificTime) selectedHour else -1,
                        if (hasSpecificTime) selectedMinute else -1,
                        isReminderEnabled,
                        selectedPriority,
                        String.format("#%06X", 0xFFFFFF and selectedCategory.color.hashCode())
                    )

                    // If sync with system calendar is enabled, fire CalendarContract intent
                    if (syncWithSystemCalendar) {
                        try {
                            val greg = PersianCalendarHelper.persianToGregorian(date.year, date.month, date.day)
                            val cal = Calendar.getInstance().apply {
                                set(greg.year, greg.month - 1, greg.day, if (hasSpecificTime) selectedHour else 9, if (hasSpecificTime) selectedMinute else 0)
                            }
                            val intent = Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, title.trim())
                                .putExtra(CalendarContract.Events.DESCRIPTION, description.trim())
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.timeInMillis)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignored if calendar app not present
                        }
                    }

                    Toast.makeText(context, "رویداد با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_event_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ذخیره در تقویم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
