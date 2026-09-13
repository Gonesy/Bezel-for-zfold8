# Bezel release build

Build: `./gradlew assembleRelease testDebugUnitTest lintRelease --console=plain`
APK: `app/build/outputs/apk/release/app-release.apk`

Release enables R8 code/resource optimization, excludes debug-only tooling and
uses a dedicated production signing key. PNG crunching is disabled to retain
user-supplied artwork exactly. Version 1.0 uses versionCode 2.

Local signing files are outside the repository:
`~/.android/bezel-release/bezel-release.jks` and `signing.properties`.
Back up both securely; future updates require the same signing key. Do not
publish either file or commit signing passwords. A checkout without these files
builds an unsigned release that must be signed before installation.

The previous debug APK uses a different certificate. Switching to this release
requires uninstalling that debug app first; future releases signed with this
key can update in place. Local app settings reset when uninstalling.

Validation is local build, unit tests, Lint and APK signature/resource checks.
No device installation or runtime testing is performed at the user's request.
