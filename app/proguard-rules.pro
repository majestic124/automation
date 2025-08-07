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

# Сохраняем все основные Android компоненты
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

# Сохраняем все ваши основные классы (замените на ваш пакет)
-keep class com.automation.** { *; }

# Базовые правила для Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Правила для Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.** *;
}

# Сохраняем все модели данных
-keep class **.models.** { *; }
-keep class **.dto.** { *; }
-keep class **.entity.** { *; }

# Сохраняем все Parcelable/Serializable
-keep class * implements android.os.Parcelable
-keep class * implements java.io.Serializable

# Сохраняем все аннотации
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# Сохраняем информацию для отладки
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature

# Правила для retrofit и сетевых запросов
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }

# Сохраняем enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Сохраняем все конструкторы по умолчанию
-keepclassmembers class * {
    public <init>();
}

# Базовая оптимизация
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Если используете R8, можно добавить
-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

# Правила для корутин
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Правила для JSON сериализации
-keepattributes Signature
-keepattributes *Annotation*
#
#-dontobfuscate  # Отключает обфускацию
#-optimizations !code/allocation/variable  # Оставляет оптимизации