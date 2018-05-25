# Multiplatform Settings

This is a Kotlin library for Multiplatform mobile apps, so that common code can persist key-value data. It stores things using SharedPreferences on Android and UserDefaults on iOS. 

## Adding to your project
In your `kotlin-platform-common` module, add the dependency

    implementation "com.russhwolf:multiplatform-settings-common:0.1"
    
In your `kotlin-platform-android` module, add an `expectedBy` dependency on the common module as well as the dependency

    implementation "com.russhwolf:multiplatform-settings-android:0.1"
    
In your `konan` module, add an `expectedBy` dependency on the common module as well as separate artifacts for the targets `ios_arm64` (phyiscal device) and `ios_x64` (emulator). The syntax here is not particularly well-documented, but here's an example to illustrate. Assume you want to expose a framework named `MyKotlinFramework` to your ios project.


    konanArtifacts {
        framework('MyKotlinFramework_ios_arm64', targets: ['ios_arm64']) {
            enableMultiplatform true
            artifactName 'MyKotlinFramework'
            dependencies {
                artifactMyKotlinFramework_ios_arm64 "com.russhwolf:multiplatform-settings-ios_arm64:0.1"
            }
        }
        framework('MyKotlinFramework_ios_x64', targets: ['ios_x64']) {
            enableMultiplatform true
            artifactName 'MyKotlinFramework'
            dependencies {
                artifactMyKotlinFramework_ios_x64 "com.russhwolf:multiplatform-settings-ios_x64:0.1"
            }
        }
    }

See also the sample project, which uses this structure.

## Usage

A `Settings` instance must be created in a platform-specific way. On Android, pass a `SharedPreferences` instance, like
 
    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    val settings: Settings = Settings(preferences)
    
On iOS, it's enough to directly call

    val settings: Settings = Settings()
    
although optionally a `NSUserDefaults` implementation can be passed by wrapping it in a `NSUserDefaultsWrapper` like so:

    val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults
    val wrapper: NSUserDefaultsWrapper = NSUserDefaultsWrapper(userDefaults)
    val settings: Settings = Settings(wrapper)
    
Once created, you can store values by calling the various `putXXX()` methods, or their operator shortcuts

    settings.putInt("key", 3)
    settings["key"] = 3
    
You can retrieve stored values via the `getXXX()` methods or their operator shortcuts

    val a = settings.getInt("key")
    val b = settings.getInt("key", defaultValue = -1) 
    val c = settings["key", -1]
    
The `getXXX()` and `putXXX()` operation for a given key can be wrapped using a property delegate. This has the advantage of ensuring that the key is always accessed with a consistent type.

    val a by settings.int("key")
    val b by settings.int("key", defaultValue = -1)
    
Existence of a key can be queried
     
    settings.hasKey("key")
    "key" in settings
     
 Values can also be removed by key
  
    settings.remove("key")
    settings -= "key"  
  
 Finally, all values in a `Settings` instance can be removed
      
    settings.clear()

## Project Structure
The library logic lives in the modules `library-common`, `library-android`, and `library-ios`. The common module holds `expect` declarations for the `Settings` class, which can persist values of the `Int`, `Long`, `String`, `Float`, `Double`, and `Boolean` types. It also holds property delegate wrappers and other operator functions for cleaner syntax and usage. The android and ios modules then hold `actual` declarations, delegating to `SharedPreferences` or `NSUserDefaults`.

 Unit tests are defined which can be run via `./gradlew test`. There is some platform-specific code here to handle mocking out the actual platform persistence and instead using an in-memory `Map`.

There is also a sample project, consisting of five modules. The first, `sample-common`, holds the code that actually consumes the library. It simply defines a setting of each type as a demo. `sample-android` and `sample-ios` exist for build configuration, to expose `sample-common` to the respective platforms. The Android app lives in `sample-app`, which consumes `sample-android` and defines an Android UI to update and view the settings. Similarly, `sample-xcode` holds the iOS UI, consuming the framework produced by `sample-ios`.
    

## License
        
    Copyright 2018 Russell Wolf
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
