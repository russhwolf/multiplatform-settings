[![Build Status](https://dev.azure.com/russhwolf/Multiplatform%20Settings/_apis/build/status/russhwolf.multiplatform-settings?branchName=master)](https://dev.azure.com/russhwolf/Multiplatform%20Settings/_build/latest?definitionId=2&branchName=master)

# Multiplatform Settings

This is a Kotlin library for Multiplatform apps, so that common code can persist key-value data. It stores things using SharedPreferences on Android and NSUserDefaults on iOS. 

## Adding to your project
Multiplatform Settings is currently published to jcenter, so add that to repositories.

```kotlin
repositories {
    // ...
    jcenter()
}
```

Then, simply add the dependency to your common source-set dependencies

```kotlin
commonMain {
    dependencies {
        // ...
        implementation("com.russhwolf:multiplatform-settings:0.5.1")
    }
}
``` 

See also the sample project, which uses this structure.

## Usage

The `Settings` interface has implementations on the Android, iOS, macOS, watchOOS, tvOS, JS, and JVM platforms. (Note that the two JVM implementations are currently marked as experimental.)

The Android implementation is `AndroidSettings`, which wraps `SharedPreferences`.

```kotlin
val delegate: SharedPreferences // ...
val settings: Settings = AndroidSettings(delegate)
```

On iOS, macOS, tvOS, or watchOS, `AppleSettings` wraps `NSUserDefaults`.

```kotlin
val delegate: NSUserDefaults // ...
val settings: Settings = AppleSettings(delegate)
```

On JS, `JsSettings` wraps `Storage`.

```kotlin
val delegate: Storage // ...
val settings: Settings = JsSettings(delegate)

val settings: Settings = JsSettings() // use localStorage by default
```     

Once the `Settings` instance is created, you can store values by calling the various `putXXX()` methods, or their operator shortcuts

```kotlin
settings.putInt("key", 3)
settings["key"] = 3
```

You can retrieve stored values via the `getXXX()` methods or their operator shortcuts. If a key is not present, then the supplied default will be returned instead.

```kotlin
val a: Int = settings.getInt("key")
val b: Int = settings.getInt("key", defaultValue = -1) 
val c: Int = settings["key", -1]
```    

Nullable methods are also available to avoid the need to use a default value. Instead, `null` will be returned if a key is not present.

```kotlin
val a: Int? = settings.getIntOrNull("key")
val b: Int? = settings["key"]
```    

The `getXXX()` and `putXXX()` operation for a given key can be wrapped using a property delegate. This has the advantage of ensuring that the key is always accessed with a consistent type.

```kotlin
val a: Int by settings.int("key")
val b: Int by settings.int("key", defaultValue = -1)
```    

Nullable delegates exists so that absence of a key can be indicated by `null` instead of a default value

```kotlin    
val a: Int? by settings.nullableInt("key")
```    

The `key` parameter can be omitted for delegates, and the property name will be reflectively used instead.

```kotlin
val a: Int by settings.int() // internally, key is "a"
```

Existence of a key can be queried

```kotlin     
val a: Boolean = settings.hasKey("key")
val b: Boolean = "key" in settings
```     

Values can also be removed by key

```kotlin 
settings.remove("key")
settings -= "key"
settings["key"] = null
``` 

Finally, all values in a `Settings` instance can be removed

```kotlin     
settings.clear()
```

For the Android, iOS, and macOS platforms, a `Factory` class also exists, so that multiple named `Settings` instances can coexist with the names being controlled from common code.

On Android, this factory needs a `Context` parameter

```kotlin
val context: Context // ...
val factory: Settings.Factory = AndroidSettings.Factory(context)
```    

On iOS and macOS, the factory can be instantiated without passing any parameter

```kotlin
val factory: Settings.Factory = AppleSettings.Factory()
```    

## Testing

A testing dependency is available to aid in testing code that interacts with this library.

```kotlin
implementation("com.russhwolf:multiplatform-settings-test:0.5.1")
```    

This includes a `MockSettings` implementation of the `Settings` interface, which is backed by an in-memory `MutableMap` on all platforms.

## Other platforms

The `Settings` interface is published to all available platforms. Developers who desire implementations outside of the defaults provided are free to add their own implementations, and welcome to make pull requests if the implementation might be generally useful to others. Note that implementations which require external dependencies should be places in a separate gradle module in order to keep the core `multiplatform-settings` module dependency-free.

## Experimental API

This is a pre-1.0 library based on an experimental framework, so some occasional API breakage may occur. However certain APIs are marked with explicit experimental annotations to highlight areas that might have more risk of API changes or unexpected behavior.

### Experimental Platforms

Two pure-JVM implementations exist. `JvmPreferencesSettings` wraps `Preferences` and `JvmPropertiesSettings` wraps `Properties`. Their experimental status is marked with the `@ExperimentalJvm` annotation. 

```kotlin
val delegate: Preferences // ...
val settings: Settings = JvmPreferencesSettings(delegate)

val delegate: Properties // ...
val settings: Settings = JvmPropertiesSettings(delegate)
```        

### Listeners

Update listeners are available using an experimental API, only for the `AndroidSettings`, `AppleSettings`, and `JvmPreferencesSettings` implementations. These are marked with the `ObservableSettings` interface, which includes `addListener()` and `removeListener()` methods.

```kotlin
val settingsListener: SettingsListener = settings.addListener(key) { /* ... */ }
```    

The `SettingsListener` returned from the call should be used to signal when you're done listening:

```kotlin
settingsListener.deactivate()
```    

This current listener implementation is not designed with any sort of thread-safety so it's recommended to only interact with these APIs from the main thread of your application.

The listener APIs make use of the Kotlin `@ExperimentalListener` annotation.

## Building

The project includes multiple CI jobs configured using Azure pipelines. On PRs or updates to the `master` branch, the script in `azure-pipelines.yml` runs. This builds the library and runs unit tests for all platforms across Linux, Mac, and Windows hosts. In addition, the library build artifacts are deployed to the local maven repository and the sample project is built for the platforms on which it is implemented. This ensures that the sample remains in sync with updates to the library.

An addition pipeline is defined in `azure-pipelines-deploy.yml`, which runs whenever a tag is pushed to the remote. This builds the library for all platforms and uploads artifacts to Bintray. Uploaded artifacts must still be published manually.

## Project Structure
The library logic lives in the `commonMain`, `androidMain`, and `iosMain` sources. The common source holds the `Settings` interface which exposes apis for persisting values of the `Int`, `Long`, `String`, `Float`, `Double`, and `Boolean` types. The common source also holds property delegate wrappers and other operator functions for cleaner syntax and usage. The platform sources then hold implementations, delegating to whichever delegate that platform uses. The macOS platform reads from the same sources as iOS. The experimental JVM and JS implementations reside in the `jvmMain` and `jsMain` sources, respectively

Some unit tests are defined which can be run via `./gradlew test`. These use Robolectric on Android to mock out the android-specific behavior, and use the ios simulator to run the ios tests. The macOS tests run natively on macOS hosts. The experimental JS implementation uses the default test setup for the new JS plugin, and the experimental JVM implementation runs standard junit tests.

There is also a sample project to demonstrate usage, which is configured as a separate IDEA/gradle project in the `sample` directory. It includes a `shared` module with common, and platform-specific sources, to demo a shared logic layer consuming the library. Several gradle modules consume `shared`, including `app-android` for Android, `app-tornadofx` for TornadoFX on the JVM, and `app-browser` for the Javascript browser target. In addition, the `app-ios` directory holds an Xcode project which builds an iOS app in the usual way, consuming a framework produced by `shared`.
 
 The `shared` module includes some simple unit tests in common code to demonstrate using the `MockSettings` implementation to mock out the `Settings` interface when testing code that interacts with it.

## License
        
    Copyright 2018-2019 Russell Wolf
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

#
[![Jetbrains Logo](images/jetbrains.png)](https://www.jetbrains.com/?from=Multiplatform-Settings)

Made with JetBrains tools 
