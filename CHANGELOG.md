# Changelog #

## v0.3 *(2019-04-21)* ##
- Update to Kotlin 1.3.30 and Gradle 5.3.1
- Remove redundant `PlatformSettings` expect declarations
- Add macOS platform
- Add experimental Javascript platform
- Add experimental JVM platform
- Refactor experimental listener behavior to separate interface
- Make delegate keys optional

## v0.2 *(2019-01-26)* ##
- Update to Kotlin 1.3.20 and Gradle 5.1
- Convert to `kotlin-multiplatform` gradle plugin
- Fix inconsistent `clear()` behavior (issue #9) 

## v0.1.1 *(2018-11-01)* ##
- Update to Kotlin 1.3.0
- Add experimental change listener

## v0.1 *(2018-09-09)* ##
- Update naming convention and remove -alphaX
- Convert native code to `kotlin-platform-native` gradle plugin
- Don't force java 8 language version
- Fix bug where using different names on iOS didn't work

## v0.1-alpha4 *(2018-08-04)* ##
- Update to Kotlin 1.2.60 and Kotlin/Native 0.8.1

## v0.1-alpha3 *(2018-07-12)* ##
- Update to Kotlin 1.2.50
- Create separate `Settings` interface and `PlatformSettings` expect class
- Remove AppCompat dependency

## v0.1-alpha2 *(2018-06-10)* ##
- Update to Kotlin/Native 0.7.1 and Gradle 4.7
- Add `Factory` interface to create named `Settings` instances

## v0.1-alpha *(2018-05-28)* ##
- Initial release



