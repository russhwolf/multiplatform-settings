# Multiplatform Settings

This is a Kotlin library for Multiplatform mobile apps, so that common code can persist key-value data. It stores things using SharedPreferences on Android and UserDefaults on iOS. 

## Project Structure
The library logic lives in the modules `library-common`, `library-android`, and `library-ios`. The common module holds expect declarations for the `Settings` class, which can persist values of the `Int`, `Long`, `String`, `Float`, `Double`, and `Boolean` types. It also holds property delegate wrappers, for more declarative syntax in client code. The android and ios modules then hold actual declarations, delegating to `SharedPreferences` or `NSUserDefaults`.

 Unit tests are defined which can be run via `./gradlew test`. There is some platform-specific code here to handle mocking out the actual platform persistence and instead using an in-memory `Map`.

There is also a sample project, consisting of five modules. The first, `sample-common`, holds the code that actually consumes the library. It simply defines a setting of each type as a demo. `sample-android` and `sample-ios` exist for build configuration, to expose `sample-common` to the respective platforms. The Android app lives in `sample-app`, which consumes `sample-android` and defines an Android UI to update and view the settings. Similarly, `sample-xcode` holds the iOS UI, consuming the framework produced by `sample-ios`.

## How to use
This library is currently not published anywhere, so you must build it from source in order to use it.
