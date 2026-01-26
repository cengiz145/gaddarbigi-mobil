# Add project specific ProGuard rules here.

# Compose
-keep class androidx.compose.ui.** { *; }

# R8/ProGuard fixes
-dontwarn javax.annotation.**
-keep class javax.annotation.** { *; }
-dontwarn okio.**
-dontwarn javax.naming.**

# Gson & Data Models
# Keep data classes used for JSON parsing to prevent R8 from renaming fields
-keep class com.example.gaddarquiz.model.** { *; }
-keep class com.example.gaddarquiz.data.** { *; }

# Gson specific
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep resources just in case (though isShrinkResources usually handles it)
# Specifically raw resources if accessed dynamically
-keep class com.example.gaddarquiz.R$raw { *; }
