# Multiplatform Settings

This is a Kotlin library for Multiplatform apps, so that common code can persist key-value data. It stores things using SharedPreferences on Android and NSUserDefaults on iOS. 

## Adding to your project
Multiplatform Settings is currently published to jcenter, so add that to repositories.

    repositories {
        ...
        jcenter()
    }

Then, simply add the dependency to your common source-set dependencies

    commonMain {
        dependencies {
            ...
            implementation "com.russhwolf:multiplatform-settings:0.4"
        }
    }
    
See also the sample project, which uses this structure.

## Usage

The `Settings` interface has implementations on the Android, iOS (arm64, arm32, and x64), macOS (x64), JVM, and JS platforms. (Note that the JVM and JS implementations are currently marked as experimental.)

The Android implementation is `AndroidSettings`, which wraps `SharedPreferences`.

    val delegate: SharedPreferences = ...
    val settings: Settings = AndroidSettings(delegate)

On iOS or macOS, `AppleSettings` wraps `NSUserDefaults`.

    val delegate: NSUserDefaults = ...
    val settings: Settings = AppleSettings(delegate)
        
Once the `Settings` instance is created, you can store values by calling the various `putXXX()` methods, or their operator shortcuts

    settings.putInt("key", 3)
    settings["key"] = 3
    
You can retrieve stored values via the `getXXX()` methods or their operator shortcuts. If a key is not present, then the supplied default will be returned instead.

    val a: Int = settings.getInt("key")
    val b: Int = settings.getInt("key", defaultValue = -1) 
    val c: Int = settings["key", -1]
    
Nullable methods are also available to avoid the need to use a default value. Instead, `null` will be returned if a key is not present.

    val a: Int? = settings.getIntOrNull("key")
    val b: Int? = settings["key"]
    
The `getXXX()` and `putXXX()` operation for a given key can be wrapped using a property delegate. This has the advantage of ensuring that the key is always accessed with a consistent type.

    val a: Int by settings.int("key")
    val b: Int by settings.int("key", defaultValue = -1)
    
Nullable delegates exists so that absence of a key can be indicated by `null` instead of a default value
    
    val a: Int? by settings.nullableInt("key")
    
The `key` parameter can be omitted for delegates, and the property name will be reflectively used instead.

    val a: Int by settings.int() // internally, key is "a"
    
Existence of a key can be queried
     
    val a: Boolean = settings.hasKey("key")
    val b: Boolean = "key" in settings
     
 Values can also be removed by key
  
    settings.remove("key")
    settings -= "key"
    settings["key"] = null
  
 Finally, all values in a `Settings` instance can be removed
      
    settings.clear()

For the Android, iOS, and macOS platforms, a `Factory` class also exists, so that multiple named `Settings` instances can coexist with the names being controlled from common code.

On Android, this factory needs a `Context` parameter

    val context: Context = ...
    val factory: Settings.Factory = AndroidSettings.Factory(context)
    
On iOS and macOS, the factory can be instantiated without passing any parameter

    val factory: Settings.Factory = AppleSettings.Factory()
    
## Testing

A testing dependency is available to aid in testing code that interacts with this library.

    implementation "com.russhwolf:multiplatform-settings-test:0.4"
    
This includes a `MockSettings` implementation of the `Settings` interface, which is backed by an in-memory `MutableMap` on all platforms.
    
## Experimental API

### Experimental Platforms

Two pure-JVM implementations exist. `JvmPreferencesSettings` wraps `Preferences` and `JvmPropertiesSettings` wraps `Properties`. Their experimental status is marked with the `@ExperimentalJvm` annotation. 

    val delegate: Preferences = ...
    val settings: Settings = JvmPreferencesSettings(delegate)

    val delegate: Properties = ...
    val settings: Settings = JvmPropertiesSettings(delegate)
    
A JS implementation exists which wraps the `Storage` API. Its experimental status is marked with the `@ExperimentalJvm` annotation

    val delegate: Storage = ...
    val settings: Settings = JsSettings(delegate)
    
    val settings: Settings = JsSettings() // use localStorage by default
    
### Listeners

Update listeners are available using an experimental API, only for the `AndroidSettings`, `AppleSettings`, and `JvmPreferencesSettings` implementations. These are marked with the `ObservableSettings` interface, which includes `addListener()` and `removeListener()` methods.

    val settingsListener: SettingsListener = settings.addListener(key) { ... }
    
The `SettingsListener` returned from the call should be used to signal when you're done listening:

    settings.removeListener(settingsListener)
    
This current listener implementation is not designed with any sort of thread-safety so it's recommended to only interact with these APIs from the main thread of your application.

The listener APIs make use of the Kotlin `@Experimental` annotation. All usages must be marked with `@ExperimentalListener` or `@UseExperimental(ExperimentalListener::class)`.

## Project Structure
The library logic lives in the `commonMain`, `androidMain`, and `iosMain` sources. The common source holds the `Settings` interface which exposes apis for persisting values of the `Int`, `Long`, `String`, `Float`, `Double`, and `Boolean` types. The common source also holds property delegate wrappers and other operator functions for cleaner syntax and usage. The android and ios sources then hold implementations, delegating to `SharedPreferences` or `NSUserDefaults`. The macOS platform reads from the same sources as iOS. The experimental JVM and JS implementations reside in the `jvmMain` and `jsMain` sources, respectively

Some unit tests are defined which can be run via `./gradlew test`. These use Robolectric on Android to mock out the android-specific behavior, and use the ios simulator to run the ios tests. The macOS tests run natively on macOS hosts. The experimental JS implementation is configured to run tests with Mocha, and the experimental JVM implementation runs standard junit tests.

There is also a sample project to demonstrate usage, which is configured as a separate IDEA/gradle project in the `sample` directory. It includes a `shared` module with common, android, and ios sources, to demo a shared logic layer consuming the library. The `app-android` module consumes `shared` and provides an Android UI. The `app-ios` directory holds an Xcode project which builds an iOS app in the usual way, consuming a framework produced by `shared`. The `app-tornadofx` module consumes `shared` and produces a TornadoFX UI. The sample project does not currently have implementations on macOS or JS.
 
 The `shared` module includes some simple unit tests in common code to demonstrate manually mocking out the `Settings` interface when testing code that interacts with it.

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
