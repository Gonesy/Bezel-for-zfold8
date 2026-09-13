# Bezel (for zfold8)

**Language: [中文](README.md) | English**

A native Android utility that adds device frames to screenshots from the Samsung Galaxy Z Fold 8. Built with Kotlin and Jetpack Compose.

## Download and install

Visit the [Releases page](https://github.com/Gonesy/Bezel-for-zfold8/releases/latest), download the `.apk` file, and open it on your phone to install.

- Requires Android 12 or later.
- Current version: 1.0.
- If you previously installed a debug build, uninstall it before installing the release build. Uninstalling clears the app's local settings.

## Features

- Select a screenshot or image from your gallery.
- Automatically choose the inner or cover screen frame based on the image ratio, with a manual selection option.
- Choose from lavender, graphite, and cream device frames.
- Adjust the background color and save the framed image to your gallery.

## How to use

1. Open Bezel and tap **Pick Image** to choose an image.
2. Select the screen type, frame color, and background color on the editor screen.
3. Tap **Save** in the upper-right corner, then find the finished image in your gallery.

## Run from source

Open the project in a version of Android Studio that supports Android Gradle Plugin 9.4.0. Install Android SDK 37, complete the Gradle sync, and click Run. The Gradle daemon uses JDK 25 as configured in `gradle/gradle-daemon-jvm.properties`.

You can also run the following command in the project directory:

```bash
./gradlew assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

See [docs/release.md](docs/release.md) for release build and signing instructions. Signing keys are not included in this repository; use your own signing configuration to build a release APK.
