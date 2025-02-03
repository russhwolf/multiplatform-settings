[![Linux Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-linux.yml?branch=main&label=JVM%2FJS%2FAndroid%2FLinux%20Build&logo=Linux&logoColor=black)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-linux.yml)
[![Mac Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-macos.yml?branch=main&label=iOS%2FmacOS%2FtvOS%2FwatchOS%20Build&logo=Apple)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-macos.yml)
[![Windows Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-windows.yml?branch=main&label=Windows%20Build&logo=Windows)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-windows.yml)

[![Maven Central](https://img.shields.io/maven-central/v/com.russhwolf/multiplatform-settings?label=Maven%20Central)](https://search.maven.org/artifact/com.russhwolf/multiplatform-settings)

# Multiplatform Settings

This is a Kotlin library for Multiplatform apps, so that common code can persist key-value data.

A [Korean translation](https://github.com/wooram-yang/multiplatform-settings/blob/feature/add_ko_readme_file/README-ko.md)
of this readme is available separately, maintained by @wooram-yang

## Table of contents

<!-- TODO it's maybe getting time to break this up into separate pages and do a real docs site -->

* [Usage](#usage)
  + [Implementation Summary](#implementation-summary)
  + [Creating a Settings instance](#creating-a-settings-instance)
    - [Platform constructors](#platform-constructors)
    - [Factories](#factories)
    - [No-arg module](#no-arg-module)
  + [Settings API](#settings-api)
    - [Listeners](#listeners)
    - [Testing](#testing)
  + [Other platforms](#other-platforms)
* [Experimental API](#experimental-api)
  + [Experimental Implementations](#experimental-implementations)
  + [Serialization module](#serialization-module)
  + [Coroutine APIs](#coroutine-apis)
    - [DataStore](#datastore)
  + [Make-Observable module](#make-observable-module)
* [Adding to your project](#adding-to-your-project)
* [Building](#building)
* [License](#license)

## Usage

The `Settings` interface has implementations on the Android, iOS, macOS, watchOS, tvOS, JS, WasmJS, JVM, and Windows
platforms.

### Implementation Summary

The following table shows the names of implementing classes and what platforms they're available on.

| Class                                   | Backing API                         | Platforms                      |
|-----------------------------------------|-------------------------------------|--------------------------------|
| `KeychainSettings`<sup>2</sup>          | Apple Keychain                      | iOS, macOS, watchOS, tvOS      |
| `NSUserDefaultsSettings`<sup>1</sup>    | User Defaults                       | iOS, macOS, watchOS, tvOS      |
| `PreferencesSettings`<sup>1</sup>       | `java.util.prefs.Preferences`       | JVM                            |
| `PropertiesSettings`                    | `java.util.Properties`              | JVM                            |
| `SharedPreferencesSettings`<sup>1</sup> | `android.content.SharedPreferences` | Android                        |
| `StorageSettings`                       | Web Storage (localStorage)          | JS, WasmJS                     |
| `RegistrySettings`<sup>2</sup>          | Windows Registry                    | MingwX64                       |
| `DataStoreSettings`<sup>3</sup>         | `androidx.datastore.core.DataStore` | Android, JVM, Native           |
| `MapSettings`<sup>1,4</sup>             | `kotlin.collections.MutableMap`     | All platforms                  |

<sup>
<sup>1</sup> Implements <code>ObservableSettings</code> interface<br/>
<sup>2</sup> Implementation is considered experimental<br/>
<sup>3</sup> Implements <code>SuspendSettings</code> and <code>FlowSettings</code> rather than <code>Settings</code> or <code>ObservableSettings</code><br/>
<sup>4</sup> <code>MapSettings</code> is intended for use in unit tests and will not persist data to storage
</sup>

### Creating a Settings instance

When writing multiplatform code, you might need to interoperate with platform-specific code which needs to share the
same data-source. To facilitate this, all `Settings` implementations wrap a delegate object which you could also use in
your platform code.

Since that delegate is a constructor argument, it should be possible to connect it via any dependency-injection strategy
you might already be using. If your project doesn't have such a system already in place, one strategy is to use `expect`
declarations, for example

```kotlin
expect val settings: Settings

// or
expect fun createSettings(): Settings
```

Then the `actual` implementations can pass the platform-specific delegates.
See [Platform constructors](#platform-constructors) below for more details on these delegates.

Some platform implementations also include `Factory` classes. These make it easier to manage multiple named `Settings`
objects from common code, or to automate some platform-specific configuration so that delegates don't need to be created
manually. The factory still needs to be injected from platform code, but then from common you can call

```kotlin
val settings1: Settings = factory.create("my_first_settings")
val settings2: Settings = factory.create("my_other_settings")
```

See [Factories](#factories) below for more details.

However, if all of your key-value logic exists in a single instance in common code, these ways of
instantiation `Settings` can be inconvenient. To make pure-common usage easier, Multiplatform Settings now includes a
separate module which provides a `Settings()` factory function, so that you can create a `Settings` instance like

```kotlin
val settings: Settings = Settings()
```

See [No-arg module](#no-arg-module) below for more details.

#### Platform constructors

The Android implementation is `SharedPreferencesSettings`, which wraps `SharedPreferences`.

```kotlin
val delegate: SharedPreferences // ...
val settings: Settings = SharedPreferencesSettings(delegate)
```

On iOS, macOS, tvOS, or watchOS, `NSUserDefaultsSettings` wraps `NSUserDefaults`.

```kotlin
val delegate: NSUserDefaults // ...
val settings: Settings = NSUserDefaultsSettings(delegate)
```

You can also use `KeychainSettings` which writes to the Keychain. Construct it by passing a String which will be
interpreted as a service name.

```kotlin
val serviceName: String // ...
val settings: Settings = KeychainSettings(serviceName)
```

Two JVM implementations exist. `PreferencesSettings` wraps `Preference`s and `PropertiesSettings` wraps `Properties`.

```kotlin
val delegate: Preferences // ...
val settings: Settings = PreferencesSettings(delegate)

val delegate: Properties // ...
val settings: Settings = PropertiesSettings(delegate)
```

On JS and WasmJS, `StorageSettings` wraps `Storage`.

```kotlin
val delegate: Storage // ...
val settings: Settings = StorageSettings(delegate)

val settings: Settings = StorageSettings() // use localStorage by default
```

There is a Windows implementation `RegistrySettings` which wraps the Windows registry.

```kotlin
val rootKey: String = "SOFTWARE\\..." // Will be interpreted as subkey of HKEY_CURRENT_USER
val settings: Settings = RegistrySettings(rootKey)
```

#### Factories

For some platforms, a `Factory` class also exists, so that multiple named `Settings` instances can coexist with the
names being controlled from common code.

On Android, this factory needs a `Context` parameter

```kotlin
val context: Context // ...
val factory: Settings.Factory = SharedPreferencesSettings.Factory(context)
```    

On most other platforms, the factory can be instantiated without passing any parameter

```kotlin
val factory: Settings.Factory = NSUserDefaultsSettings.Factory()
```

If you have a `Factory` reference from your common code, then you can use it to create multiple `Settings` with
different names.

```kotlin
val settings1: Settings = factory.create("my_first_settings")
val settings2: Settings = factory.create("my_other_settings")
```

If the default `Factory`s don't do what you need, you can also implement your own.

#### No-arg module

To create a `Settings` instance from common without needing to pass platform-specific dependencies, add
the `multiplatform-settings-no-arg` gradle dependency. This exports `multiplatform-settings` as an API dependency, so
you can use it as a replacement for that default dependency.

```kotlin
implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
```

Then from common code, you can write

```kotlin
val settings: Settings = Settings()
```

This is implemented via a top-level function `Settings()` to provide constructor-like
syntax even though `Settings` has no constructor.

On Android, this delegates to the equivalent of `PreferenceManager.getDefaultSharedPreferences()` internally. It makes
use of [`androidx-startup`](https://developer.android.com/jetpack/androidx/releases/startup) to get a `Context`
reference without needing to pass one manually. On Apple platforms, it uses `NSUserDefaults.standardUserDefaults`. On 
JS, it uses `localStorage`. On JVM, it uses the `Preferences` implementation with `Preferences.userRoot()` as a 
delegate. On Windows, it reads the name of the executable being built and writes to a subkey of 
`HKEY_CURRENT_USER\SOFTWARE` using that name.

Note that while the main `multiplatform-settings` module publishes common code to all available Kotlin platforms,
the `multiplatform-settings-no-arg` module only publishes to platforms which have concrete implementations.

Note also that the `no-arg` module is there to make getting started easier with less configuration, but there are plenty
of things it doesn't provide, such as the ability to use an encrypted implementation on platforms that support it, or
the ability to substitute a test implementation. Notably, you can't call `Settings()` from an Android unit test because
the internals that allow it to get a `Context` reference won't run (not even if you use Robolectric).

If you need a non-default setup you likely are better off not using `multiplatform-settings-no-arg`.

### Settings API

Once the `Settings` instance is created, you can store values by calling the various `putXXX()` methods, or their
operator shortcuts

```kotlin
settings.putInt("key", 3)
settings["key"] = 3
```

You can retrieve stored values via the `getXXX()` methods or their operator shortcuts. If a key is not present, then the
supplied default will be returned instead.

```kotlin
val a: Int = settings.getInt("key")
val b: Int = settings.getInt("key", defaultValue = -1)
val c: Int = settings["key", -1]
```    

Nullable methods are also available to avoid the need to use a default value. Instead, `null` will be returned if a key
is not present.

```kotlin
val a: Int? = settings.getIntOrNull("key")
val b: Int? = settings["key"]
```    

The `getXXX()` and `putXXX()` operation for a given key can be wrapped using a property delegate. This has the advantage
of ensuring that the key is always accessed with a consistent type.

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

Note that for the `NSUserDefaultsSettings` implementation, some entries are unremovable and therefore may still be
present after a `clear()` call. Thus, `size` is not generally guaranteed to be zero after a `clear()`.

#### Listeners

Update listeners are available for some implementations. These are marked
with the `ObservableSettings` interface, which includes an `addListener()` method.

```kotlin
val observableSettings: ObservableSettings // ...
val settingsListener: SettingsListener = observableSettings.addIntListener(key) { value: Int -> /* ... */ }
val settingsListener: SettingsListener = observableSettings.addNullableIntListener(key) { value: Int? -> /* ... */ }
```

The `SettingsListener` returned from the call should be used to signal when you're done listening:

```kotlin
settingsListener.deactivate()
```    

If you don't hold a strong reference to the `SettingsListener`, it's possible in some implementations that it will be
garbage-collected and stop sending updates.

#### Testing

A testing dependency is available to aid in testing code that interacts with this library.

```kotlin
implementation("com.russhwolf:multiplatform-settings-test:1.3.0")
```    

This includes a `MapSettings` implementation of the `Settings` interface, which is backed by an in-memory `MutableMap`
on all platforms.

### Other platforms

The `Settings` interface is published to all available platforms. Developers who desire implementations outside of the
defaults provided are free to add their own implementations, and are welcome to make pull requests if the implementation
might be generally useful to others. Note that implementations which require external dependencies should be places in a
separate gradle module in order to keep the core `multiplatform-settings` module dependency-free.

## Experimental API

Certain APIs are marked with `@ExperimentalSettingsApi` or `@ExperimentalSettingsImplementation` to highlight areas that
may have the potential to break in the future and should not be considered stable to depend on.

### Experimental Implementations

The `KeychainSettings` implementation on Apple platforms and the `RegistrySettings` implementation on Windows are
considered experimental. Feel free to reach out if they're working well for you, or if you encounter any issues with
them, to help remove that experimental status.

### Serialization module

A `kotlinx-serialization` integration exists so it's easier to save non-primitive data

```kotlin
implementation("com.russhwolf:multiplatform-settings-serialization:1.3.0")
```

This essentially uses the `Settings` store as a serialization format. Thus for a serializable class

```kotlin
@Serializable
class SomeClass(val someProperty: String, anotherProperty: Int)
```

an instance can be stored or retrieved

```kotlin
val someClass: SomeClass
val settings: Settings

// Store values for the properties of someClass in settings
settings.encodeValue(SomeClass.serializer(), "key", someClass)

// Create a new instance of SomeClass based on the data in settings
val newInstance: SomeClass = settings.decodeValue(SomeClass.serializer(), "key", defaultValue)
val nullableNewInstance: SomeClass = settings.decodeValueOrNull(SomeClass.serializer(), "key")
```

To remove a serialized value, use `removeValue()` rather than `remove()`

```kotlin
settings.removeValue(SomeClass.serializer(), "key")

// Don't remove if not all expected data is preset
settings.removeValue(SomeClass.serializer(), "key", ignorePartial = true)
```

To check for the existance of a serialized value, use `containsValue()` rather than `contains()`.

```kotlin
val isPresent = settings.containsValue(SomeClass.serializer(), "key")
```

There's also a delegate API, similar to that for primitives

```kotlin
val someClass: SomeClass by settings.serializedValue(SomeClass.serializer(), "someClass", defaultValue)
val nullableSomeClass: SomeClass? by settings.nullableSerializedValue(SomeClass.serializer(), "someClass")
```

All APIs also have variants that infer a serializer implicitly rather than taking one as a parameter. These APIs throw
if the class is not serializable.

```kotlin
settings.encodeValue("key", someClass)
val newInstance: SomeClass = settings.decodeValue("key", defaultValue)
val nullableNewInstance: SomeClass = settings.decodeValueOrNull("key")
// etc
```

Usage requires accepting both the `@ExperimentalSettingsApi` and `@ExperimentalSerializationApi` annotations.

### Coroutine APIs

A separate `multiplatform-settings-coroutines` dependency includes various coroutine APIs.

```kotlin
implementation("com.russhwolf:multiplatform-settings-coroutines:1.3.0")
```

This adds flow extensions for all types which use the listener APIs internally.

```kotlin
val observableSettings: ObservableSettings // Only works with ObservableSettings

val flow: Flow<Int> by observableSettings.getIntFlow("key", defaultValue)
val nullableFlow: Flow<Int?> by observableSettings.getIntOrNullFlow("key")
```

There are also `StateFlow` extensions, which require a coroutine scope.

```kotlin
val observableSettings: ObservableSettings // Only works with ObservableSettings
val coroutineScope: CoroutineScope

val stateFlow: StateFlow<Int> by observableSettings.getIntStateFlow("key", defaultValue)
val nullableStateFlow: StateFlow<Int?> by observableSettings.getIntOrNullStateFlow("key")
```

In addition, there are two new `Settings`-like interfaces: `SuspendSettings`, which looks similar to `Settings` but all
functions are marked `suspend`, and `FlowSettings` which extends `SuspendSettings` to also include `Flow`-based getters
similar to the extensions mentioned above.

```kotlin
val suspendSettings: SuspendSettings // ...
val a: Int = suspendSettings.getInt("key") // This call will suspend

val flowSettings: FlowSettings // ...
val flow: Flow<Int> = flowSettings.getIntFlow("key")
```

There are APIs provided to convert between these different interfaces so that you can select one to use primarily from
common.

```kotlin
val settings: Settings // ...
val suspendSettings: SuspendSettings = settings.toSuspendSettings()

val observableSettings: ObservableSettings // ...
val flowSettings: FlowSettings = observableSettings.toFlowSettings()

// Wrap suspend calls in runBlocking
val blockingSettings: Settings = suspendSettings.toBlockingSettings()
val blockingSettings: ObservableSettings = flowSettings.toBlockingObservableSettings()
```

#### DataStore

An implementation of `FlowSettings` exists in the `multiplatform-settings-datastore` dependency, based
on [Jetpack DataStore](https://developer.android.com/jetpack/androidx/releases/datastore). Because DataStore is now a
multiplatform library, starting in version 1.2.0, this module is available on all platforms where DataStore is
available, rather than being limited to Android and JVM.

```kotlin
implementation("com.russhwolf:multiplatform-settings-datastore:1.3.0")
```

This provides a `DataStoreSettings` class

```kotlin
val dataStore: DataStore // = ...
val settings: FlowSettings = DataStoreSettings(dataStore)
```

You can use this in shared code by converting other `ObservableSettings` instances to `FlowSettings`. For example:

```kotlin
// Common
expect val settings: FlowSettings

// Android
actual val settings: FlowSettings = DataStoreSettings(/*...*/)

// iOS
actual val settings: FlowSettings = NSUserDefaultsSettings(/*...*/).toFlowSettings()
```

Or, if you also include platforms without listener support, you can use `SuspendSettings` instead.

```kotlin
// Common
expect val settings: SuspendSettings

// Android
actual val settings: SuspendSettings = DataStoreSettings(/*...*/)

// iOS
actual val settings: SuspendSettings = NSUserDefaultsSettings(/*...*/).toSuspendSettings()

// JS
actual val settings: SuspendSettings = StorageSettings().toSuspendSettings()
```

### Make-Observable module

The experimental `multiplatform-settings-make-observable` module adds an extension function `Settings.makeObservable()`
in common code which converts a `Settings` instance to `ObservableSettings` by directly wiring in callbacks rather than
native observability methods.

```kotlin
val settings: Settings // = ...
val observableSettings: ObservableSettings = settings.makeObservable()
```

This has the advantage of enabling observability on platforms which don't have an observable implementation. It has the
disadvantage that updates will only be delivered to the same instance where changes were made.

## Adding to your project

Multiplatform Settings is currently published to Maven Central, so add that to repositories.

```kotlin
repositories {
  mavenCentral()
  // ...
}
```

Then, simply add the dependency to your common source-set dependencies

```kotlin
commonMain {
  dependencies {
    // ...
    implementation("com.russhwolf:multiplatform-settings:1.3.0")
  }
}
``` 

See also the sample project, which uses this structure.

## Building

The project includes multiple CI jobs configured using Github Actions. On PRs or updates to the `main` branch, the build
will run the scripts in `build-linux.yml`, `build-macos.yml`, `build-windows.yml`, and `validate-gradle-wrapper.yml`.
These builds the library and runs unit tests for all platforms across Linux, Mac, and Windows hosts. In addition, the
library build artifacts are deployed to the local maven repository and the sample project is built for the platforms on
which it is implemented. This ensures that the sample remains in sync with updates to the library.

An addition build script is defined in `deploy.yml`, which runs on a manual trigger. This builds the library for all
platforms and uploads artifacts to staging on Maven Central. Uploaded artifacts must still be published manually

## License

    Copyright 2018-2023 Russell Wolf
    
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
