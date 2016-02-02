# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-dontwarn android.support.v4.**

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.v7.**

-keep class com.github.jjobes.** { *; }
-keep interface com.github.jjobes.** { *; }
-dontwarn com.github.jjobes.**

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#sql cipher
-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *; }
-keep interface net.sqlcipher.** { *; }

-keep class com.shephertz.app42.** { *; }
-keep interface com.shephertz.app42.** { *; }
-dontwarn com.shephertz.app42.**

#-keepattributes *Annotation*