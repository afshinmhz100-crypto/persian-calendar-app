# ProGuard / R8 Configuration for Persian Calendar

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>();
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }

# Moshi & Retrofit
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# App Data Models
-keep class com.example.calendar.data.** { *; }
-keep class com.example.calendar.news.** { *; }
-keep class com.example.calendar.fortune.** { *; }
-keep class com.example.calendar.core.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

