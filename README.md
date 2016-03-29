# android-travis-appicon-overlay
Plugin for Android Gradle to automatically overlay the app icon with information about the current build: travis build, git branch name, commit SHA1, etc. (jdk 1.7, no imagemagick)

based on https://github.com/gfx/gradle-android-ribbonizer-plugin and https://github.com/splatte/gradle-android-appiconoverlay

### config sample:
```
appiconoverlay {
    textFormat = '$TRAVIS_BRANCH'
    textColor = [255, 255, 255, 255] // [r, g, b, a]
    textBackColor = [0, 0, 0, 0xA0]
    maxCharsPerLine = 8 // 0 - disable
    maxWidth = 95 // percent
    maxHeightPerLine = 30 // percent

    footerTextFormat = '$TRAVIS_BUILD_NUMBER'
    footerTextColor = [255, 255, 255, 255] // [r, g, b, a]
    footerTextBackColor = [0, 0, 0, 0xA0]
    footerMaxCharsPerLine = 8 // 0 - disable
    footerMaxWidth = 95 // percent
    footerMaxHeightPerLine = 30 // percent

    ignoreRelease = false // ignore buildType.name == "release"
    ignoreBranches "master"
    //ignoreFlavors "deploy", "beta"

    //iconNames "@drawable/my_logo" // extra graphics
}
```
