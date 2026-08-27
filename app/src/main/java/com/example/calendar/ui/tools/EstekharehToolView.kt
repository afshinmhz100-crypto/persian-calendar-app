package com.example.calendar.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.PersianCalendarHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class EstekharehItem(
    val id: Int,
    val surah: String,
    val ayahNumber: Int,
    val ayahArabic: String,
    val ayahPersian: String,
    val resultType: EstekharehResultType,
    val interpretation: String
)

enum class EstekharehResultType(val title: String, val colorHex: Long, val emoji: String) {
    VERY_GOOD("بسیار خوب", 0xFF2E7D32, "✨"),
    GOOD("خوب", 0xFF43A047, "✅"),
    MEDIUM("میانه (با احتیاط)", 0xFFF57C00, "⚖️"),
    BAD("بد (صبر و پرهیز)", 0xFFD32F2F, "🛑")
}

object EstekharehRepository {
    private val items = listOf(
        EstekharehItem(
            1, "سوره بقره", 286,
            "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ",
            "خداوند هیچ کس را جز به اندازه توانش تکلیف نمی‌کند...",
            EstekharehResultType.VERY_GOOD,
            "بسیار خوب و مبارک است. با توکل بر خداوند متعال اقدام نمایید، سختی‌های ابتدایی برطرف خواهد شد و به گشایش و برکت فراوان ختم می‌شود."
        ),
        EstekharehItem(
            2, "سوره آل‌عمران", 159,
            "فَإِذَا عَزَمْتَ فَتَوَكَّلْ عَلَى اللَّهِ ۚ إِنَّ اللَّهَ يُحِبُّ الْمُتَوَكِّلِينَ",
            "و هنگامی که تصمیم گرفتی، بر خدا توکل کن؛ زیرا خدا توکل‌کنندگان را دوست دارد.",
            EstekharehResultType.VERY_GOOD,
            "بسیار خوب است. تردید را کنار بگذارید و با مشورت و توکل کار را به انجام رسانید."
        ),
        EstekharehItem(
            3, "سوره یوسف", 86,
            "قَالَ إِنَّمَا أَشْكُو بَثِّي وَحُزْنِي إِلَى اللَّهِ وَأَعْلَمُ مِنَ اللَّهِ مَا لَا تَعْلَمُونَ",
            "گفت: من اندوه و حزن خود را تنها به خدا می‌گویم و از خدا چیزهایی می‌دانم که شما نمی‌دانید.",
            EstekharehResultType.MEDIUM,
            "میانه است. در حال حاضر موانع و ابهاماتی وجود دارد. مدتی صبر کرده و با افراد خبره مشورت بیشتری کنید."
        ),
        EstekharehItem(
            4, "سوره توبه", 51,
            "قُلْ لَنْ يُصِيبَنَا إِلَّا مَا كَتَبَ اللَّهُ لَنَا هُوَ مَوْلَانَا ۚ وَعَلَى اللَّهِ فَلْيَتَوَكَّلِ الْمُؤْمِنُونَ",
            "بگو: هرگز چیزی به ما نمی‌رسد مگر آنچه خدا برای ما مقدر کرده است...",
            EstekharehResultType.GOOD,
            "خوب است. البته نیازمند شکیبایی، دعا و تدبیر است؛ منفعت آن در بلندمدت آشکار می‌شود."
        ),
        EstekharehItem(
            5, "سوره اسراء", 11,
            "وَيَدْعُ الْإِنْسَانُ بِالشَّرِّ دُعَاءَهُ بِالْخَيْرِ ۖ وَكَانَ الْإِنْسَانُ عَجُولًا",
            "انسان همان گونه که نیکی را می‌طلبد، بدی را نیز می‌طلبد؛ و انسان همواره شتاب‌زده است.",
            EstekharehResultType.BAD,
            "بد است. ظاهری فریبنده دارد اما در باطن ممکن است باعث پشیمانی و خسارت مادی یا معنوی شود. پرهیز نمایید یا با صدقه به تأخیر اندازید."
        ),
        EstekharehItem(
            6, "سوره طه", 46,
            "قَالَ لَا تَخَافَا ۖ إِنَّنِي مَعَكُمَا أَسْمَعُ وَأَرَىٰ",
            "فرمود: نترسید! من همراه شما هستم؛ می‌شنوم و می‌بینم.",
            EstekharehResultType.VERY_GOOD,
            "بسیار خوب است. نصرت و پشتیبانی الهی شامل حال شما خواهد بود، بی‌دلهره اقدام نمایید."
        )
    )

    fun getRandom(): EstekharehItem = items.random()
}

@Composable
fun EstekharehToolView(
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var currentItem by remember { mutableStateOf<EstekharehItem?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Hero Instructions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📖 استخاره با قرآن کریم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "آداب: سه بار سوره توحید (قل هو الله احد) و سه بار صلوات قرائت نموده و نیت قلبی فرمایید.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Action Button
        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    delay(700)
                    currentItem = EstekharehRepository.getRandom()
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (currentItem != null) Icons.Default.Refresh else Icons.Default.AutoAwesome,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentItem != null) "استخاره مجدد" else "✨ نیت کنید و استخاره بگیرید",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("در حال ورق زدن و تفأل به کلام‌الله مجید...")
                }
            }
        } else if (currentItem != null) {
            val item = currentItem!!
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Result Badge
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(item.resultType.colorHex).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.resultType.emoji} نتیجه: ${item.resultType.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(item.resultType.colorHex)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Surah & Ayah
                        Text(
                            text = "${item.surah} - آیه شریفه ${PersianCalendarHelper.toPersianDigits(item.ayahNumber.toString())}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Arabic Verse
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "« ${item.ayahArabic} »",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Translation
                        Text(
                            text = item.ayahPersian,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interpretation
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💡 تعبیر و راهنمای استخاره:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.interpretation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    lineHeight = 22.sp
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
