# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\androidStudio2\sdk/tools/proguard/proguard-android.txt
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

#-keep class com.google.android.gms.* { ; }
#-keep interface com.google.android.gms.* { ; }
#-keep  { ; }
#Warning:com.squareup.picasso.OkHttpDownloader: can't find referenced class com.squareup.okhttp.OkHttpClient
-keepclassmembers class webit.android.shanti {
   public *;
}

-keep class com.squareup.picasso.Picasso.* { *; }
#-keepclassmembers com.squareup.picasso.Picasso.* {
#   public *;
#}
#-keep public class <MyClass>


