# Changelog #

## v0.7.5 *(2021-04-25)* ##

- Update to Kotlin 1.4.32, Gradle 7.0, and Android Gradle Plugin 4.1.2
- Update `multiplatform-settings-coroutines` to use coroutines version 1.4.3
  - Don't use `strictly` for `multiplatform-settings-coroutines-native-mt` dependency declaration
- Update `multiplatform-settings-datastore` to use DataStore version 1.0.0-beta01
- Add `distinctUntilChanged()` operator to `multiplatform-settings-datastore` flows

## v0.7.4 *(2021-03-14)* ##

- Update to Kotlin 1.4.31
- Update `multiplatform-settings-datastore` to use DataStore version 1.0.0-alpha08
- Add `CoroutineDispatcher` parameter to `Settings.toSuspendSettings()` and `ObservableSettings().toFlowSettings()`
  extension functions in `multiplatform-settings-coroutines` module

## v0.7.3 *(2021-02-20)* ##

- Fix remaining crash in `KeychainSettings.clear()` (issue #79)
- Update `multiplatform-settings-serialization` to use kotlinx-serialization version 1.1.0

## v0.7.2 *(2021-02-13)* ##

- Update to Kotlin 1.4.30
- Fix crash when making changes to items in `KeychainSettings` that were saved in a previous application launch (issue
  #79)

## v0.7.1 *(2021-01-17)* ##

- Update `multiplatform-settings-datastore` to use DataStore version 1.0.0-alpha06

## v0.7 *(2020-12-26)* ##

- Kotlin 1.4.21 and other dependency updates
- New typed update listeners
  - `addIntListener { int: Int -> ... }` in addition to `addListener { ... }`
- New `KeychainSettings` stores data in the Apple Keychain
- New `multiplatform-settings-serialization` module with APIs to store structured data in `Settings`
  via `kotlinx-serialization`
- New `multiplatform-settings-coroutines` module with new coroutine-based interfaces `SuspendSettings` and `FlowSettings
  - Also released as `multiplatform-settings-coroutines-native-mt` for use with the `native-mt` branch of coroutines
  - New `multiplatform-settings-datastore` module with a `DataStoreSettings` implementation of `FlowSettings` based on
    Jetpack DataStore.
- Consolidate experimental annotations into `@ExperimentalSettingsImplementation` and `@ExperimentalSettingsApi`

## v0.6.3 *(2020-11-02)* ##

- Kotlin 1.4.10 for real this time
- Optional `commit` parameter in `AndroidSettings` constructor, which will tell it to use `commit()` instead
  of `apply()` when making changes.
- Deprecate `Settings.Companion.invoke()` in the no-arg dependency and replace with `Settings()` factory function.
- Use `androidx.startup` in no-arg dependency instead of a custom `ContentProvider`.

## v0.6.2 *(2020-09-10)* ##

- ~~Kotlin 1.4.10~~ (Erroneously still built with 1.4.0)

## v0.6.1 *(2020-08-16)* ##

- Kotlin 1.4.0, Gradle 6.6, and Android Gradle Plugin 4.0.1
- Support for both legacy and IR compiler modes in Kotlin/JS

## v0.6 *(2020-04-26)* ##

- Kotlin 1.3.72 and Android Gradle Plugin 3.6.3
- Add `multiplatform-settings-no-arg` module, including common `Settings()` function for easy default configuration
- Experimental `WindowsSettings` implementation via registry
- Improved thread-safety for AppleSettings update listeners
- New `keys` and `size` members on `Settings`.
- Add workaround for potential native name collisions (KT-36721)

## v0.5.1 *(2020-03-03)* ##
- Update to Kotlin 1.3.70
- Update to Android Gradle Plugin 3.6.1
- Convert @Experimental usage to @RequiresOptIn

## v0.5 *(2019-12-15)* ##
- Update to Kotlin 1.3.61
- Update to Android Gradle Plugin 3.5.3
- Breaking changes:
    - `Long`-based APIs were previously being backed by `Int`s on 32-bit Apple targets (eg `iosArm32`), which means they were limited to `Int` values. This has been replaced with using `String`-backed storage that will respect the full range of `Long` values.
    - Apple artifacts have been renamed to use default target names. This should only have impact for consumers who depended on platform-specific artifacts instead of using Gradle Metadata and depending only on the `multiplatform-settings` artifact.
- Deprecate `Settings.removeListener()` and replace with `SettingsListener.deactivate()`
- Deprecate `@ExperimentalJs` annotation. Javascript now has the same stability as the rest of the library.
- Deploy common code to all available platforms. This enables users to add implementations on platforms not included by default.
- Add JS browser target to sample project
- Remove incorrect logic attempting to run `JvmPreferencesSettings.Listener` updates on the main thread. This listener implementation updates on an internally-managed background thread. The behavior has not changed but is now documented.
- Build script refactors. This should be largely invisible but please file issues if anything is inconsistent.

## v0.4.1 *(2019-11-19)* ##
- Update to Kotlin 1.3.60

## v0.4 *(2019-10-04)* ##
- Add new `JvmPreferencesSettings` implementation for JVM target, using `Preferences` APIs.
    - To continue with the existing `Properties` implementation, use `JvmPropertiesSettings`. This implementation may be deprecated and removed in the future.
- Add optional persistence callback in `JvmPropertiesSettings`
- Add iosArm32 support.
- Add new nullable get APIs (eg `Settings.getIntOrNull(key)`)
- Add JVM sample

## v0.3.3 *(2019-08-22)* ##
- Update to Kotlin 1.3.50
- Convert build scripts to Kotlin

## v0.3.2 *(2019-06-23)* ##
- Update to Kotlin 1.3.40
- Use new automated JS configuration
- Reduce `@Experimental` annotations to warning status

## v0.3.1 *(2019-05-19)* ##
- Add `multiplatform-settings-test` dependency with `MockSettings` implementation
- Update to Kotlin 1.3.31 and Gradle 5.4.1
- Rename `ListenableSettings` to `ObservableSettings`

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



