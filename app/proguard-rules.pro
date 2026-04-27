# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase_Impl { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Hilt / Dagger (keep generated components)
-keep class dagger.hilt.** { *; }
-keep class hilt_aggregated_deps.** { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules_BindsModule { *; }
-keep class **_HiltModules_KeyModule { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# WorkManager (keeps Worker class names for reflection fallback)
-keep class * extends androidx.work.ListenableWorker { *; }

# Vico (charts)
-keep class com.patrykandpatryk.vico.** { *; }