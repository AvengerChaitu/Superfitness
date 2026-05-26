# Thrivio ProGuard Rules
-keepattributes Signature
-keepattributes *Annotation*

# Supabase / Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan.tennert.supabase.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
