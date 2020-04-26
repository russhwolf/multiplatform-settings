[![Build Status](https://img.shields.io/azure-devops/build/russhwolf/038fde2a-0787-46e9-aca3-f0ab32d0a534/2/master?job=General&label=JVM%2FJS%2FAndroid%2FLinux&logo=Kotlin)](https://dev.azure.com/russhwolf/Multiplatform%20Settings/_build/latest?definitionId=2&branchName=master) [![Build Status](https://img.shields.io/azure-devops/build/russhwolf/038fde2a-0787-46e9-aca3-f0ab32d0a534/2/master?job=Mac&label=iOS%2FmacOS%2FtvOS%2FwatchOS&logo=Apple)](https://dev.azure.com/russhwolf/Multiplatform%20Settings/_build/latest?definitionId=2&branchName=master) [![Build Status](https://img.shields.io/azure-devops/build/russhwolf/038fde2a-0787-46e9-aca3-f0ab32d0a534/2/master?job=Windows&label=Windows&logo=Windows)](https://dev.azure.com/russhwolf/Multiplatform%20Settings/_build/latest?definitionId=2&branchName=master)

# Multiplatform Settings

This is a Kotlin library for Multiplatform apps, so that common code can persist key-value data.

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
        implementation("com.russhwolf:multiplatform-settings:0.6")
    }
}
``` 

See also the sample project, which uses this structure.

## Usage

The `Settings` interface has implementations on the Android, iOS, macOS, watchOOS, tvOS, JS, JVM, and Windows platforms. (Note that the two JVM implementations and the Windows implementation are currently marked as experimental.)

### Creating a Settings instance

When writing multiplatform code, you might need to interoperate with platform-specific code which needs to share the same data-source. To facilitate this, all `Settings` implementations wrap a delegate object which you could also use in your platform code.

Since that delegate is a constructor argument, it should be possible to connect it via any dependency-injection strategy you might already be using. If your project doesn't have such a system already in place, one strategy is to use `expect` declarations, for example
 
```kotlin
expect val settings: Settings
expect fun createSettings(): Settings
```

Then the `actual` implementations can pass the platform-specific delegates. See [Platform constructors](#platform-constructors) below for more details on these delegates.

Some platform implementations also include `Factory` classes. These make it easier to manage multiple named `Settings` objects from common code, or to automate some platform-specific configuration so that delegates don't need to be created manually. The factory still needs to be injected from platform code, but then from common you can call 

```kotlin
val settings1: Settings = factory.create("my_first_settings")
val settings2: Settings = factory.create("my_other_settings")
```

See [Factories](#factories) below for more details.

However, if all of your key-value logic exists in a single instance in common code, these ways of instantiation `Settings` can be inconvenient. To make pure-common usage easier, Multiplatform Settings now includes a separate module which provides an `invoke()` operator on the companion object, so that you can create a `Settings` instance like

```kotlin
val settings: Settings = Settings()
```

See [No-arg module](#no-arg-module) below for more details.

#### Platform constructors

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

#### Factories

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

#### No-arg module

To create a `Settings` instance from common without needing to pass platform-specific dependencies, add the `multiplatform-settings-no-arg` gradle dependency. This exports `multiplatform-settings` as an API dependency, so you can use it as a replacement for that default dependency.

```kotlin
implementation("com.russhwolf:multiplatform-settings-no-arg:0.6")
```    

Then from common code, you can write

```kotlin
val settings: Settings = Settings()
```

This is implemented via an extension function `operator fun Settings.Companion.invoke()` to provide constructor-like syntax even though `Settings` has no constructor.

On Android, this delegates to the equivalent of `PreferenceManager.getDefaultSharedPreferences()` internally. It makes use of a content-provider to get a context reference without needing to pass one manually.
 
On Apple platforms, it uses `NSUserDefaults.standardUserDefaults`. On JS, it uses `localStorage`. On JVM, it uses the `JvmPreferences` implementation with `Preferences.userRoot()` as a delegate.

Note that while the main `multiplatform-settings` module publishes common code to all available Kotlin platforms, the `multiplatform-settings-no-arg` module only publishes to platforms which have concrete implementations.

### Settings API

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

The set of keys and amount of entries can be retrieved

```kotlin
val keys: Set<String> = settings.keys
val size: Int = settings.size
```
Note that for the `AppleSettings` implementation, some entries are unremovable and therefore may still be present after a `clear()` call. Thus, `size` is not generally guaranteed to be zero after a `clear()`.

## Testing

A testing dependency is available to aid in testing code that interacts with this library.

```kotlin
implementation("com.russhwolf:multiplatform-settings-test:0.6")
```    

This includes a `MockSettings` implementation of the `Settings` interface, which is backed by an in-memory `MutableMap` on all platforms.

## Other platforms

The `Settings` interface is published to all available platforms. Developers who desire implementations outside of the defaults provided are free to add their own implementations, and welcome to make pull requests if the implementation might be generally useful to others. Note that implementations which require external dependencies should be places in a separate gradle module in order to keep the core `multiplatform-settings` module dependency-free.

## Experimental API

This is a pre-1.0 library based on an experimental framework, so some occasional API breakage may occur. However certain APIs are marked with explicit experimental annotations to highlight areas that might have more risk of API changes or unexpected behavior.

### Experimental Platforms

#### JVM

Two pure-JVM implementations exist. `JvmPreferencesSettings` wraps `Preferences` and `JvmPropertiesSettings` wraps `Properties`. Their experimental status is marked with the `@ExperimentalJvm` annotation. 

```kotlin
val delegate: Preferences // ...
val settings: Settings = JvmPreferencesSettings(delegate)

val delegate: Properties // ...
val settings: Settings = JvmPropertiesSettings(delegate)
```

#### Windows

There is a Windows implementation `WindowsSettings` which wraps the Windows registry. Its experimental status is marked with `@ExperimentalWindows`.

```kotlin
val rootKey: String = "SOFTWARE\\..." // Will be interpreted as subkey of HKEY_CURRENT_USER
val settings: Settings = WindowsSettings(rootKey)
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

On Apple platforms, the `AppleSettings` listeners are designed to work within the Kotlin/Native threading model. If all interaction with the class is on a single thread, then nothing will be frozen. In multithreaded usage, the `AppleSettings` can be configured to freeze listeners, making it safe to set listeners when the class might be used across threads.

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
        
    Copyright 2018-2020 Russell Wolf
    
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
