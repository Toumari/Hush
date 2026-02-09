# Hush ProGuard Rules

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Play Billing
-keep class com.android.vending.billing.** { *; }

# Keep AdMob
-keep class com.google.android.gms.ads.** { *; }
