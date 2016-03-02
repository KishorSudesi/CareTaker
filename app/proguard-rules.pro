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

#for date time slider
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

#apache commons codec AES
#-dontwarn org.apache.commons.codec.**
#-keep class org.apache.commons.codec.** { *; }
#-keep interface org.apache.commons.codec.** { *; }

#Permission Dispatcher
-dontwarn com.github.hotchemi.**
-keep class com.github.hotchemi.** { *; }
-keep interface com.github.hotchemi.** { *; }

#AES
-dontwarn com.scottyab.**
-keep class com.scottyab.** { *; }
-keep interface com.scottyab.** { *; }

#jackson
#-dontwarn com.fasterxml.jackson.**
#-keep class com.fasterxml.jackson.** { *; }
#-keep interface com.fasterxml.jackson.** { *; }

#gson
#-dontwarn com.google.code.gson.**
#-keep class com.google.code.gson.** { *; }
#-keep interface com.google.code.gson.** { *; }

#glide
-dontwarn com.github.bumptech.glide.**
-keep class com.github.bumptech.glide.** { *; }
-keep interface com.github.bumptech.glide.** { *; }

#davemorrissey
-dontwarn com.davemorrissey.labs.**
-keep class com.davemorrissey.labs.** { *; }
-keep interface com.davemorrissey.labs.** { *; }

#jakewharton
-dontwarn com.jakewharton.**
-keep class com.jakewharton.** { *; }
-keep interface com.jakewharton.** { *; }

#-keepattributes *Annotation*