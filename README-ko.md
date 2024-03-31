[![Linux Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-linux.yml?branch=main&label=JVM%2FJS%2FAndroid%2FLinux%20Build&logo=Linux&logoColor=black)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-linux.yml)
[![Mac Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-macos.yml?branch=main&label=iOS%2FmacOS%2FtvOS%2FwatchOS%20Build&logo=Apple)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-macos.yml)
[![Windows Build Status](https://img.shields.io/github/actions/workflow/status/russhwolf/multiplatform-settings/build-windows.yml?branch=main&label=Windows%20Build&logo=Windows)](https://github.com/russhwolf/multiplatform-settings/actions/workflows/build-windows.yml)

[![Maven Central](https://img.shields.io/maven-central/v/com.russhwolf/multiplatform-settings?label=Maven%20Central)](https://search.maven.org/artifact/com.russhwolf/multiplatform-settings)

# Multiplatform Settings

공통 코드가 키-값 데이터를 보관할 수 있도록 하는 Multiplatform 앱용 코틀린 라이브러리입니다. 

## 목차

<!-- TODO it's maybe getting time to break this up into separate pages and do a real docs site -->

* [프로젝트에 추가](#프로젝트에-추가)
* [사용법](#사용법)
  * [Settings 인스턴스 생성](#settings-인스턴스-생성)
    * [플랫폼 생성자](#플랫폼-생성자)
    * [팩토리](#팩토리)
    * [No-arg 모듈](#no-arg-모듈)
  * [Settings API](#settings-api)
    * [리스너](#리스너)
    * [테스트](#테스트)
  * [기타 플랫폼들](#기타-플랫폼들)
* [실험적인 API](#실험적인-api)
  * [실험적인 구현](#실험적인-구현)
    * [애플 키체인](#애플-키체인)
  * [직렬화 모듈](#직렬화-모듈)
  * [코루틴 APIs](#코루틴-apis)
    * [DataStore](#datastore)
* [빌드](#빌드)
* [라이센스](#라이센스)

## 프로젝트에 추가

Multiplatform Settings은 현재 Maven Central에 배포되어 있기에 이를 repositories에 추가해야 합니다.

```kotlin
repositories {
    mavenCentral()
    // ...
}
```

그리고 공통 코드셋 dependencies에 의존성을 추가합니다.

```kotlin
commonMain {
    dependencies {
        // ...
        implementation("com.russhwolf:multiplatform-settings:1.1.1")
    }
}
``` 

이 구조를 사용하는 샘플 프로젝트도 참조하세요.

## 사용법

`Settings` 인터페이스는 Android, iOS, macOS, watchOS, tvOS, JS, JVM, 그리고 Windows 플랫폼에서 구현됩니다.

### Settings 인스턴스 생성

멀티플랫폼 코드를 작성할 떄는 같은 데이터 소스를 공유해야 하는 플랫폼별 코드와 상호 운용해야 할 수도 있습니다. 이를 용이하게 하게 위해 모든 `Settings` 구현은 플랫폼 코드에서도 쓸 수 있는 델리게이트 객체를 래핑합니다.

이 델리게이트는 생성자 인수이므로 이미 사용 중일 수 있는 의존성 주입으로 연결될 수 있어야 합니다. 만약 프로젝트에 이러한 작업이 되어있지 않은 경우의 한 방법은 `expect`를 사용하는 것입니다. 예를 들어

```kotlin
expect val settings: Settings

// or
expect fun createSettings(): Settings
```

그런 다음 `actual` 구현들은 플랫폼별 델리게이트을 전달할 수 있습니다.
이러한 델리게이트에 대한 자세한 내용은 아래의 [플랫폼 생성자](#플랫폼-생성자)를 참조하세요.

일부 플랫폼 구현에는 `Factory` 클래스도 포함됩니다. 이를 통해 공통 코드로부터 다수의 `Settings` 객체를 더 쉽게 관리할 수 있게 하거나 델리게이트를 생성할 필요가 없도록 일부 플랫폼별 구성을 자동화합니다. Factory는 여전히 플랫폼 코드로부터 주입되어야 하지만 공통 코드에서 호출할 수 있습니다.

```kotlin
val settings1: Settings = factory.create("my_first_settings")
val settings2: Settings = factory.create("my_other_settings")
```

자세한 내용은 아래 [팩토리](#팩토리)을 참조하세요.

그러나 모든 키-값 논리가 공통 코드의 단일 인스턴스에 존재하는 경우, 이 `Settings` 초기화 방법들은 불편할 수 있습니다.
순수 공통 사용을 더 쉽게 만들기 위해 Multiplatform Setting은 이제 `Settings()` 팩토리 함수를 제공하는 별도의 모듈을 포함하여 다음과 같은 `Settings` 인스턴스를 생성할 수 있습니다.

```kotlin
val settings: Settings = Settings()
```

자세한 내용은 아래 [No-arg 모듈](#no-arg-모듈)을 참조하세요.

#### 플랫폼 생성자

Android 구현은 `SharedPreferences`를 래핑하는 `SharedPreferencesSettings`입니다.

```kotlin
val delegate: SharedPreferences // ...
val settings: Settings = SharedPreferencesSettings(delegate)
```

iOS, macOS, tvOS 혹은 watchOS에서 `NSUserDefaultsSettings`은 `NSUserDefaults`를 래핑합니다.

```kotlin
val delegate: NSUserDefaults // ...
val settings: Settings = NSUserDefaultsSettings(delegate)
```

키체인에 쓰는 `KeychainSettings`을 사용할 수도 있습니다. 서비스명으로 인터프리터되는 문자열을 전달하여 생성합니다.

```kotlin
val serviceName: String // ...
val settings: Settings = KeychainSettings(serviceName)
```

두 개의 JVM 구현이 존재합니다. `PreferencesSettings`은 `Preference`를 래핑하고 `PropertiesSettings`은 `Properties`를 래핑합니다.

```kotlin
val delegate: Preferences // ...
val settings: Settings = PreferencesSettings(delegate)

val delegate: Properties // ...
val settings: Settings = PropertiesSettings(delegate)
```

JS에서는 `StorageSettings`이 `Storage`를 래핑합니다.

```kotlin
val delegate: Storage // ...
val settings: Settings = StorageSettings(delegate)

val settings: Settings = StorageSettings() // use localStorage by default
```

Windows 레지스트리를 래핑하는 Windows 구현 `RegistrySettings`이 있습니다.

```kotlin
val rootKey: String = "SOFTWARE\\..." // Will be interpreted as subkey of HKEY_CURRENT_USER
val settings: Settings = RegistrySettings(rootKey)
```

#### 팩토리

일부 플랫폼의 경우 `Factory` 클래스도 존재해서 여러 개의 이름이 지정된 `Settings` 인스턴스가 공통 코드에서 제어되도록 할 수 있습니다. 

Android에서는 이 팩토리에 `Context` 매개변수가 필요합니다.

```kotlin
val context: Context // ...
val factory: Settings.Factory = SharedPreferencesSettings.Factory(context)
```    

대부분의 다른 플랫폼에서는 매개변수를 전달하지 않고도 팩토리를 인스턴스화할 수 있습니다.

```kotlin
val factory: Settings.Factory = NSUserDefaultsSettings.Factory()
```

만약 공통 코드에서 `Factory` 참조가 있다면, 여러 개의 이름이 다른 `Settings`를 생성하는 데 사용할 수 있습니다.

```kotlin
val settings1: Settings = factory.create("my_first_settings")
val settings2: Settings = factory.create("my_other_settings")
```

기본 `Factory`로는 필요한 작업을 할 수 없는 경우, 직접 구현할 수도 있습니다.

#### No-arg 모듈

플랫폼별 의존성을 전달할 필요 없이 공통에서 `Settings` 인스턴스를 생성하려면 `multiplatform-settings-no-arg` Gradle 의존성을 추가하세요.
`multiplatform-settings-no-arg` Gradle 의존성. 이는 `multiplatform-settings`를 API 의존성으로 내 보내어 기본 의존성을 대체하여 사용할 수 있습니다.

```kotlin
implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
```

그리고 공통 코드에서는 이렇게 쓸 수 있습니다.

```kotlin
val settings: Settings = Settings()
```

이는 생성자가 없는 Settings에 대해 생성자와 유사한 구문을 제공하기 위해 최상위 함수 Settings()를 통해 구현되었습니다.

Android에서는 내부적으로 `PreferenceManager.getDefaultSharedPreferences()`와 같은 기능을 수행합니다. 이는 [`androidx-startup`](https://developer.android.com/jetpack/androidx/releases/startup)을 사용하여 수동으로 `Context` 참조를 전달하지 않고 가져옵니다. 애플 플랫폼에서는 `NSUserDefaults.standardUserDefaults`를 사용하며, JS에서는 `localStorage`를 사용합니다. JVM에서는 `Preferences.userRoot()`를 델리게이트로 사용하는 `Preferences` 구현을 사용하며, Windows에서는 빌드 중인 실행 파일의 이름을 읽고 해당 이름을 사용하여 `HKEY_CURRENT_USER\SOFTWARE`의 하위 키에 쓰입니다.


주목해야 할 점은, 주요 `multiplatform-settings` 모듈은 모든 사용 가능한 Kotlin 플랫폼에 공통 코드를 배포하지만, `multiplatform-settings-no-arg` 모듈은 구체적인 구현이 있는 플랫폼에만 배포된다는 것입니다.

또한, `no-arg` 모듈은 설정을 보다 덜 하기 쉽게 만들어졌지만, 암호화된 구현을 지원하는 플랫폼에서 사용할 수 없거나 테스트 구현을 대체할 수 없는 기능이 많이 빠져 있습니다. 특히 Android 단위 테스트에서는 `Settings()`를 호출할 수 없습니다. 이는 `Context` 참조를 가져오는 데 필요한 내부 구현이 실행되지 않기 때문입니다. (Robolectric을 사용하더라도)

디폴트 설정이 아닌 설정이 필요한 경우 `multiplatform-settings-no-arg`를 사용하지 않는 것이 좋습니다.

### Settings API

`Settings` 인스턴스가 생성되면 다양한 `putXXX()` 메서드 또는 해당 연산자 단축식들을 호출하여 값을 저장할 수 있습니다.

```kotlin
settings.putInt("key", 3)
settings["key"] = 3
```

`getXXX()` 메서드 또는 해당 연산자 단축식을 통해 저장된 값을 검색할 수 있습니다. 키가 존재하지 않으면 기본값이 반환됩니다.

```kotlin
val a: Int = settings.getInt("key")
val b: Int = settings.getInt("key", defaultValue = -1)
val c: Int = settings["key", -1]
```    

기본값을 사용하지 않도록 하기 위해 Nullable 메서드도 사용할 수 있습니다. 키가 존재하지 않으면 `null`이 반환됩니다.

```kotlin
val a: Int? = settings.getIntOrNull("key")
val b: Int? = settings["key"]
```    

특정 키에 대한 `getXXX()` 및 `putXXX()` 작업은 프로퍼티 델리게이트를 사용하여 래핑할 수 있습니다. 이렇게 하면 키가 항상 일관된 유형으로 액세스되도록 보장할 수 있습니다.

```kotlin
val a: Int by settings.int("key")
val b: Int by settings.int("key", defaultValue = -1)
```    

키가 없음을 나타내기 위해 Nullable 대리자가 존재하며, 기본값 대신 `null`이 표시됩니다.

```kotlin    
val a: Int? by settings.nullableInt("key")
```    

델리게이트를 쓰는 경우를 위해 key 매개변수를 생략할 수 있으며, 프로퍼티 속성 이름이 반사적으로 사용됩니다.

```kotlin
val a: Int by settings.int() // internally, key is "a"
```

키의 존재 여부를 확인할 수 있습니다.

```kotlin     
val a: Boolean = settings.hasKey("key")
val b: Boolean = "key" in settings
```

키로 값을 제거할 수도 있습니다.

```kotlin 
settings.remove("key")
settings -= "key"
settings["key"] = null
``` 

마침내, `Settings` 인스턴스의 모든 값이 제거될 수 있습니다.

```kotlin     
settings.clear()
```

키의 Set과 개수를 알아낼 수 있습니다.

```kotlin
val keys: Set<String> = settings.keys
val size: Int = settings.size
```

`NSUserDefaultsSettings` 구현의 경우 일부 항목은 제거할 수 없으므로 `clear()` 호출 후에도 여전히 남아 있을 수 있습니다. 따라서 `size`는 일반적으로 `clear()` 호출 후에 0이 되는 것을 보장하지 않습니다

#### 리스너

일부 구현에서는 업데이트 리스너가 사용 가능합니다. 이들은 `addListener()` 메서드를 포함하는 `ObservableSettings` 인터페이스로 표시됩니다.

```kotlin
val observableSettings: ObservableSettings // ...
val settingsListener: SettingsListener = observableSettings.addIntListener(key) { value: Int -> /* ... */ }
val settingsListener: SettingsListener = observableSettings.addNullableIntListener(key) { value: Int? -> /* ... */ }
```

호출에서 반환된 `SettingsListener`는 리스닝이 완료되었을 때 신호를 보내도록 사용해야 합니다

```kotlin
settingsListener.deactivate()
```    

만약 `SettingsListener`에 강한 참조를 유지하지 않으면 일부 구현에서 가비지 컬렉션 처리되어 업데이트가 중단될 수 있습니다.

#### 테스트

이 라이브러리와 상호작용하는 코드를 테스트하는 데 도움이 되는 테스트 의존성이 제공됩니다.

```kotlin
implementation("com.russhwolf:multiplatform-settings-test:1.1.1")
```    

모든 플랫폼에서 내부 메모리 `MutableMap`을 기반으로 하는 `Settings` 인터페이스의 `MapSettings` 구현이 포함됩니다.

### 기타 플랫폼들

`Settings` 인터페이스는 모든 사용 가능한 플랫폼에 배포됩니다. 디폴트 구현 외에 다른 구현을 원하는 개발자는 자유롭게 자체 구현을 추가할 수 있으며, 해당 구현이 다른 사람들에게 일반적으로 유용할 경우 풀 리퀘스트를 환영합니다. 외부 의존성이 필요한 구현은 핵심 `multiplatform-settings` 모듈의 의존성 없이 유지하기 위해 별도의 gradle 모듈에 배치되어야 합니다.

## 실험적인 API

일부 API는 `@ExperimentalSettingsApi` 또는 `@ExperimentalSettingsImplementation`로 표시되어 나중에 문제가 발생할 수 있는 부분을 강조하며 안정적이라 여겨질 수 없다는 것을 나타냅니다.

### 실험적인 구현

#### 애플 키체인

애플 플랫폼에서의 `KeychainSettings` 구현과 Windows에서의 `RegistrySettings` 구현은 실험적입니다. 이들이 잘 작동하는지 또는 문제가 발생하는지 여부를 알려주시면 실험적인 상태를 제거하는 데 도움이 됩니다.

### 직렬화 모듈

`kotlinx.serialization` 통합을 통해 비원시 데이터를 쉽게 저장할 수 있습니다.

```kotlin
implementation("com.russhwolf:multiplatform-settings-serialization:1.1.1")
```

이는 기본적으로 `Settings` 저장소를 직렬화 형식으로 사용합니다. 따라서 직렬화 가능한 클래스의

```kotlin
@Serializable
class SomeClass(val someProperty: String, anotherProperty: Int)
```

인스턴스를 저장하거나 가져올 수 있습니다.

```kotlin
val someClass: SomeClass
val settings: Settings

// Store values for the properties of someClass in settings
settings.encodeValue(SomeClass.serializer(), "key", someClass)

// Create a new instance of SomeClass based on the data in settings
val newInstance: SomeClass = settings.decodeValue(SomeClass.serializer(), "key", defaultValue)
val nullableNewInstance: SomeClass = settings.decodeValueOrNull(SomeClass.serializer(), "key")
```

직렬화된 값을 제거하려면 `remove()` 대신 `removeValue()`를 사용하세요.

```kotlin
settings.removeValue(SomeClass.serializer(), "key")

// Don't remove if not all expected data is preset
settings.removeValue(SomeClass.serializer(), "key", ignorePartial = true)
```

직렬화된 값의 존재 여부를 확인하려면 `contains()` 대신 `containsValue()`를 사용하세요.

```kotlin
val isPresent = settings.containsValue(SomeClass.serializer(), "key")
```

원시 유형과 유사한 델리게이트 API도 있습니다.

```kotlin
val someClass: SomeClass by settings.serializedValue(SomeClass.serializer(), "someClass", defaultValue)
val nullableSomeClass: SomeClass? by settings.nullableSerializedValue(SomeClass.serializer(), "someClass")
```

사용에는 `@ExperimentalSettingsApi` 및 `@ExperimentalSerializationApi` 주석을 모두 허용해야 합니다.

### 코루틴 APIs

별도의 `multiplatform-settings-coroutines` 의존성에는 다양한 코루틴 API가 포함되어 있습니다.

```kotlin
implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.1")
```

이는 내부적으로 리스너 API를 사용하는 모든 유형에 대한 플로우 확장을 추가합니다.

```kotlin
val observableSettings: ObservableSettings // Only works with ObservableSettings
val flow: Flow<Int> by observableSettings.intFlow("key", defaultValue)
val nullableFlow: Flow<Int?> by observableSettings.intOrNullFlow("key")
```

또한 `Settings`와 유사한 두 가지 새로운 인터페이스가 있습니다. `SuspendSettings`는 `Settings`과 유사하지만 모든 함수가 `suspend`으로 표시되며, `SuspendSettings`를 확장한`FlowSettings`는 위에서 언급한 확장과 유사한 `Flow` 기반 getter도 포함합니다.

```kotlin
val suspendSettings: SuspendSettings // ...
val a: Int = suspendSettings.getInt("key") // This call will suspend

val flowSettings: FlowSettings // ...
val flow: Flow<Int> = flowSettings.getIntFlow("key")
```

이러한 다른 인터페이스 간에 변환하기 위한 API가 제공됩니다. 이를 통해 공통에서 주로 사용할 인터페이스를 선택할 수 있습니다.

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

안드로이드에서 `FlowSettings`의 구현은 `multiplatform-settings-datastore` 의존성에 존재합니다. 이는 [Jetpack DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)를 기반으로 합니다.

```kotlin
implementation("com.russhwolf:multiplatform-settings-datastore:1.1.1")
```

이는 `DataStoreSettings` 클래스를 제공합니다.

```kotlin
val dataStore: DataStore // = ...
val settings: FlowSettings = DataStoreSettings(dataStore)
```

이를 공유 코드에서 사용하려면 다른 `ObservableSettings` 인스턴스를 `FlowSettings`로 변환하면 됩니다. 예를 들어:

```kotlin
// Common
expect val settings: FlowSettings

// Android
actual val settings: FlowSettings = DataStoreSettings(/*...*/)

// iOS
actual val settings: FlowSettings = NSUserDefaultsSettings(/*...*/).toFlowSettings()
```

또는 리스너 지원이 없는 플랫폼도 포함하려면 `SuspendSettings`를 대신 사용할 수 있습니다.

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

## 빌드

이 프로젝트에는 Github Actions를 사용하여 구성된 여러 CI job이 포함되어 있습니다. PR 또는 `main` 브랜치 업데이트 시 빌드는 `build-linux.yml`, `build-macos.yml`, `build-windows.yml`, `validate-gradle-wrapper.yml` 스크립트를 실행합니다. 이는 라이브러리를 빌드하고 Linux, Mac 그리고 Windows 호스트에서 모든 플랫폼에 대한 단위 테스트를 실행합니다. 또한 라이브러리 빌드 아티팩트는 로컬 maven 저장소에 배포되며, 샘플 프로젝트는 구현된 플랫폼에 대해 빌드됩니다. 이렇게 하면 샘플이 라이브러리 업데이트와 동기화되도록 보장됩니다.

수동 트리거에서 실행되는 추가 빌드 스크립트가 `deploy.yml`에 정의되어 있습니다. 이는 모든 플랫폼에 대해 라이브러리를 빌드하고 Maven Central의 staging에 아티팩트를 업로드합니다. 업로드된 아티팩트는 여전히 수동으로 게시해야 합니다.

## 라이센스

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