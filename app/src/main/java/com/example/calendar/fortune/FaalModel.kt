package com.example.calendar.fortune

data class FaalItem(
    val id: Int,
    val title: String,
    val poemVerses: List<String>, // Couple of verses from Hafez
    val interpretation: String,   // تعبیر فال
    val advice: String,           // توصیه و پند حافظ
    val luckyNumber: Int,
    val mood: String              // حس و حال فال (مثبت، امیدبخش، احتیاط، مژده)
)
