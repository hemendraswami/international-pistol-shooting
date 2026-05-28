-keep class com.pistolshooting.domain.model.** { *; }
-keep class com.pistolshooting.data.local.entity.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn kotlin.reflect.jvm.internal.**
