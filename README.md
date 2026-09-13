# Bezel (for zfold8)

为 Samsung Galaxy Z Fold 8 截图添加手机边框的原生 Android 小工具，使用 Kotlin + Jetpack Compose 开发。

## 下载与安装

前往 [Releases 下载页](https://github.com/Gonesy/Bezel-for-zfold8/releases/latest)，下载 `.apk` 文件，在手机上打开安装。

- 系统要求：Android 12 或更高版本。
- 当前版本：1.0。
- 若之前安装过调试版，需先卸载调试版再安装正式版；卸载会清除本地应用设置。

## 可以做什么

- 从相册选择截图或图片。
- 根据图片比例自动选择内屏或外屏边框，也可以手动切换。
- 选择薰衣草紫、石墨黑、奶油色三种边框。
- 调整背景颜色，将带边框的图片保存到相册。

## 使用方法

1. 打开 Bezel，点击 **Pick Image** 选择图片。
2. 在编辑页选择屏幕类型、边框颜色和背景颜色。
3. 点击右上角 **Save**，等待保存完成后到相册查看。

## 从源码运行

用支持本项目 Android Gradle Plugin 9.4.0 的 Android Studio 打开项目，安装 Android SDK 37，完成 Gradle 同步后点击 Run。Gradle 守护进程使用 JDK 25，配置见 `gradle/gradle-daemon-jvm.properties`。

也可以在项目目录执行：

```bash
./gradlew assembleDebug
```

调试 APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`。

正式版构建与签名说明见 [docs/release.md](docs/release.md)。签名密钥不包含在仓库中；自行构建正式版需要自己的签名配置。
