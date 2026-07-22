# Walkthrough - Play Store Release Preparation

I have prepared the project for Google Play Store submission by configuring the release build and verifying that an Android App Bundle (.aab) can be generated.

## Changes Made

### 1. Build Configuration
- **Enabled Minification:** Updated `app/build.gradle.kts` to enable R8 minification (`isMinifyEnabled = true`) and resource shrinking (`isShrinkResources = true`) for the release build.
- **Created ProGuard Rules:** Added `app/proguard-rules.pro` with necessary rules to support **Firebase**, **Room**, **Kotlin Serialization**, and **Apache POI** (Excel export).

### 2. Build Verification
- Successfully ran `./gradlew app:bundleRelease`.
- Generated an **unsigned** release bundle: `app/build/outputs/bundle/release/app-release.aab`.

## Next Steps for You

Since I cannot create your private signing key for you, please follow these steps to generate the **signed** version for the Play Store:

1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**
2. Select **Android App Bundle** and click **Next**.
3. Click **Create new...** to create a new keystore file (save this file and the password very carefully!).
4. Follow the prompts to fill in your key information.
5. Select **release** as the build variant.
6. Click **Finish**.

The signed `.aab` file will be located in the folder you specified (usually `app/release/`). This is the file you upload to the Google Play Console.

> [!CAUTION]
> **Keep your Keystore safe!** If you lose your `.jks` file or forget the password, you will not be able to update your app on the Play Store in the future.
