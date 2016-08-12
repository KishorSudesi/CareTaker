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
#-dontwarn net.sqlcipher.**
#-keep class net.sqlcipher.** { *; }
#-keep interface net.sqlcipher.** { *; }

-keep class com.shephertz.app42.** { *; }
-keep interface com.shephertz.app42.** { *; }
-dontwarn com.shephertz.app42.**

#apache commons codec AES
#-dontwarn org.apache.commons.codec.**
#-keep class org.apache.commons.codec.** { *; }
#-keep interface org.apache.commons.codec.** { *; }

#Permission Dispatcher
#-dontwarn com.github.hotchemi.**
#-keep class com.github.hotchemi.** { *; }
#-keep interface com.github.hotchemi.** { *; }

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
#-dontwarn com.github.bumptech.glide.**
#-keep class com.github.bumptech.glide.** { *; }
#-keep interface com.github.bumptech.glide.** { *; }

#davemorrissey
#-dontwarn com.davemorrissey.labs.**
#-keep class com.davemorrissey.labs.** { *; }
#-keep interface com.davemorrissey.labs.** { *; }

#jakewharton
#-dontwarn com.jakewharton.**
#-keep class com.jakewharton.** { *; }
#-keep interface com.jakewharton.** { *; }

#09.03.21016 10:30
#dagger
#-dontwarn com.google.dagger.**
#-keep class com.google.dagger.** { *; }
#-keep interface com.google.dagger.** { *; }

#-keep class **$$ModuleAdapter { *; }
#-keepnames class **$$InjectAdapter { *; }

#-keepclassmembers class * {
#    @javax.inject.Inject <fields>;
#    @javax.inject.Inject <init>(...);
#}

#ignore duplicate libraries
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

#butterknife
#-dontwarn butterknife.internal.**
#-keep class **$$ViewInjector { *; }
#-keepnames class * { @butterknife.InjectView *;}

-keepattributes *Annotation*
#-dontwarn
-adaptclassstrings
-repackageclasses 'obfuscated'

#-keepattributes InnerClasses,Signature

#google play services

-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keepclassmembers class com.google.android.gms.** {
    *;
 }
-dontwarn com.google.android.gms.*

 -keep class * extends java.util.ListResourceBundle {
     protected Object[][] getContents();
 }

 -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
     public static final *** NULL;
 }

 -keepnames @com.google.android.gms.common.annotation.KeepName class *
 -keepclassmembernames class * {
     @com.google.android.gms.common.annotation.KeepName *;
 }

 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }

  #Glide
  -dontwarn com.bumptech.glide.**
  -keep public class * implements com.bumptech.glide.module.GlideModule
  -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
  }

#glide circle
-dontwarn jp.wasabeef.**
-keep class com.jp.wasabeef.** { *; }
-keep interface jp.wasabeef.** { *; }

#sql cipher
#-dontwarn net.sqlcipher.**
#-keep class net.sqlcipher.** { *; }
#-keep interface net.sqlcipher.** { *; }

#permission helper
-dontwarn com.ayz4sci.androidfactory.**
-keep class com.ayz4sci.androidfactory.** { *; }
-keep interface com.ayz4sci.androidfactory.** { *; }