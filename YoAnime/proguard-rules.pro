-keepattributes Signature
-keepattributes *Annotation*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-printmapping
-printmapping mapping.txt

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okio.Okio.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp.internal.huc.**

-keep class com.squareup.picasso.** { *; }
-keepclasseswithmembers class * {
    @com.squareup.picasso.** *;
}
-keepclassmembers class * {
    @com.squareup.picasso.** *;
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

-dontwarn rx.internal.util.**
-dontwarn java.lang.invoke.*

-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep class android.support.v7.widget.SearchView { *; }

-keep public class org.jsoup.** {
    public *;
}

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn org.w3c.dom.**

-keep class org.mozilla.javascript.** { *; }
-dontwarn org.mozilla.javascript.**

-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**
-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontobfuscate