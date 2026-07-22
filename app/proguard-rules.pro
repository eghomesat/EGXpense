# Add project-specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/cntec/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Add any project specific keep rules here:

# Firebase rules are usually handled by the library itself, but adding these just in case
-keep class com.google.firebase.** { *; }

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Apache POI rules (may be needed for Excel export)
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class javax.xml.** { *; }
-keep class org.w3c.dom.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn javax.xml.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.commons.compress.**
-dontwarn aQute.bnd.**
-dontwarn com.github.luben.zstd.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn java.awt.**
-dontwarn org.osgi.framework.**
-dontwarn org.osgi.framework.wiring.**
-dontwarn org.tukaani.xz.**

# Kotlin Serialization
-keepattributes *Annotation*, EnclosingMethod, Signature
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName *;
}
