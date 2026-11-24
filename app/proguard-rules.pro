# OmniRoute ProGuard & R8 Rules

# Preserve domain data models (prevents stripping fields used in JSON/AI communication)
-keep class com.example.omniroute.data.model.** { *; }
-keepclassmembers class com.example.omniroute.data.model.** { *; }

# Preserve repository and engine
-keep class com.example.omniroute.data.repository.** { *; }
-keepclassmembers class com.example.omniroute.data.repository.** { *; }

# Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# General Networking & JSON serialization safety
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Compose Runtime
-keep class androidx.compose.runtime.** { *; }
