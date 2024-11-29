# Changelog #

## Unreleased ##

<!-- Add new release notes here -->

## v1.3.0 *(2024-11-29)* ##

- Update to Kotlin 2.1.0, Gradle 8.11, and Android Gradle Plugin 8.7.2
- Add `wasmWasi` support to `multiplatform-settings-coroutines` and `multiplatform-settings-serialization`.
- Fix an issue in `multiplatform-settings-serialization` where delegates might return wrong values or crash (#217).

## v1.2.0 *(2024-09-01)* ##

- Update to Kotlin 2.0.0, Gradle 8.7, and Android Gradle Plugin 8.3.2
- Add alternative APIs to `multiplatform-settings-serialization` that don't require manually passing a serializer (
  #172). Thanks for contributions by @findusl
- New experimental module `multiplatform-settings-make-observable` to convert non-observable settings to observable by
  wiring in callbacks when platform observability APIs don't exist (#155, #184). Thanks for contributions by @psuzn.
- Add all supported targets to `multiplatform-settings-coroutines` and `multiplatform-settings-serialization`, including
  `wasmJs`.
- Add additional targets to `multiplatform-settings-datastore` based on multiplatform Datastore version 1.1.0
- Add `wasmWasi` support to `multiplatform-settings`, `multiplatform-settings-test`, and
  `multiplatform-settings-make-observable`.
- Add `Stateflow` analogs to existing `Flow` extensions in `multiplatform-settings-coroutines`. (#156)
- Make `SettingsInitializer` public so it's easier to configure custom android.startup initialization or write tests
  when using `multiplatform-settings-no-arg`
- Adjust `KeychainSettings` to avoid duplicated authentication requests (#193). Thanks for contributions by @crysxd
- Modernize gradle and publication config. This shouldn't impact consumers, but please file an issue if you notice
  anything missing.

## v1.1.1 *(2023-11-20)* ##

- Update to Kotlin 1.9.20
- Fix a crash that could happen when using the no-argument `KeychainSettings` constructor (#175)

## v1.1.0 *(2023-10-09)* ##

- Update to Kotlin 1.9.10, Gradle 8.3, and Android Gradle Plugin 8.1.2
- Remove deprecated Kotlin/Native targets
- Add wasm browser target to `multiplatform-settings`, `multiplatform-settings-test`,
  and `multiplatform-settings-no-arg` (issue #142). This uses the same `StorageSettings` implementation as in the
  current js target.
- Use Dispatchers.IO rather than Dispatchers.Default in `multiplatform-settings-coroutines` on platforms where it is
  available (issue #157)
- Add serialization-aware `removeValue()` and `containsValue()` functions to `multiplatform-settings-serialization` (
  issue #81)
- Fix issue with serialization delegates incorrectly falling back to default values (issues #160 and #162)

## v1.0.0 *(2023-01-14)* ##

- First stable release!
- Update to Kotlin 1.8.0
- Fix memory leak in `KeychainSettings`.
- BREAKING: Remove support for legacy Javascript backend.
- Add `watchosDeviceArm64` target to `multiplatform-settings`, `multiplatform-settings-no-arg`,
  and `multiplatform-settings-test`.

## v1.0.0-RC *(2022-10-02)* ##

- Update to Kotlin 1.7.20, Gradle 7.5.1, and Android Gradle Plugin 7.2.2.
- BREAKING: Remove all deprecated API.
- BREAKING: Remove `useFrosenListeners` parameter from `NSUserDefaultsSettings`.
- BREAKING: Factory implementations now return a specific type from `create()` rather than returning `Settings`.

## v1.0.0-alpha01 *(2022-06-02)* ##

- BREAKING: Rename Settings implementations to be based on the underlying API used rather than the platform

| Old name               | New name                  |
|------------------------|---------------------------|
| AndroidSettings        | SharedPreferencesSettings |
| AppleSettings          | NSUserDefaultsSettings    |
| JsSettings             | StorageSettings           |
| JvmPreferencesSettings | PreferencesSettings       |
| JvmPropertiesSettings  | PropertiesSettings        |
| WindowsSettings        | RegistrySettings          |
| MockSettings           | MapSettings               |

- BREAKING: Migrate typed listeners from extension functions to members of ObservableSettings
- BREAKING: Remove default values for defaultValue parameters
- BREAKING: Remove `multiplatform-settings-coroutines-native-mt` module
- Remove `@ExperimentalSettingsApi` from `ObservableSettings`, `SettingsListener`, and related APIs
- Remove `@ExperimentalSettingsImplementation` from JVM implementations
- Add `Factory` implementation for `KeychainSettings`

## v0.9 *(2022-05-01)* ##

- Update to Kotlin 1.6.21 and Gradle 7.3.2
- Update `multiplatform-settings-coroutines` to use coroutines version 1.6.1
- Update `multiplatform-settings-serialization` to use serialization version 1.3.2
- Add Windows support to `multiplatform-settings-no-arg`
- Add JVM support to `multiplatform-settings-datastore`
- Refactor some internals to take advantage of better HMPP support in Kotlin 1.6+
- Fix a crash that could occur on Android 11 and above when clearing data from SharedPreferences while listeners were
  set by AndroidSettings

## v0.8.1 *(2021-09-23)* ##

- Update to Kotlin 1.5.31 and Gradle 7.2
- Update `multiplatform-settings-coroutines` to use coroutines version 1.5.2
- Update `multiplatform-settings-serialization` to use serialization version 1.3.0-RC
- Add missing Apple targets to `multiplatform-settings-coroutines`, `multiplatform-settings-serialization`,
  and `multiplatform-settings-no-arg`

## v0.8 *(2021-08-27)* ##

- Update to Kotlin 1.5.30 and Gradle 7.1
- Add new Apple ARM targets
- Enable hierarchical project model
- Update `multiplatform-settings-coroutines` to use coroutines version 1.5.1
- Update `multiplatform-settings-datastore` to use DataStore release version 1.0.0
- Update `multiplatform-settings-serialization` to use serialization version 1.2.2
- Other dependency version updates

## v0.7.7 *(2021-05-20)* ##

- Fix missing Kotlin 1.5.0 updates
- Update `multiplatform-settings-coroutines` to use coroutines version 1.5.0
- Update `multiplatform-settings-datastore` to use DataStore version 1.0.0-beta01
- Update `multiplatform-settings-serialization` to use serialization version 1.2.1

## v0.7.6 *(2021-04-27)* ##

- Update to Kotlin 1.5.0

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



