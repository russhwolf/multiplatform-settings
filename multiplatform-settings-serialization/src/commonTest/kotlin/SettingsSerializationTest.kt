/*
 * Copyright 2019 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

// TODO Add a test case with a non-empty SerializersModule?
@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class SettingsSerializationTest {

    @Test
    fun serialize() {
        val foo = Foo("hello", 43110)

        val settings: Settings = MapSettings()

        settings.encodeValue(Foo.serializer(), "foo", foo)
        settings.encodeValue(String.serializer(), "herp", "derp")
        settings.encodeValue(ListSerializer(Int.serializer()), "list", listOf(1, 2, 3))

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals("derp", settings.getString("herp"))
        assertEquals(1, settings.getInt("list.0"))
        assertEquals(2, settings.getInt("list.1"))
        assertEquals(3, settings.getInt("list.2"))
        assertEquals(3, settings.getInt("list.size"))
        assertEquals(7, settings.size)
    }

    @Test
    fun deserialize_defaults() {
        val settings: Settings = MapSettings(
            "foo.bar" to "hello",
            "foo.baz" to 43110,
            "herp" to "derp",
            "list.0" to 1,
            "list.1" to 2,
            "list.2" to 3,
            "list.size" to 3,
        )

        val foo = settings.decodeValue(Foo.serializer(), "foo", Foo("goodbye"))
        val herp = settings.decodeValue(String.serializer(), "herp", "")
        val list = settings.decodeValue(ListSerializer(Int.serializer()), "list", listOf())

        assertEquals("hello", foo.bar)
        assertEquals(43110, foo.baz)
        assertEquals("derp", herp)
        assertEquals(listOf(1, 2, 3), list)
    }

    @Test
    fun deserialize_defaults_empty() {
        val settings: Settings = MapSettings()

        val foo = settings.decodeValue(Foo.serializer(), "foo", Foo("goodbye"))

        assertEquals("goodbye", foo.bar)
        assertEquals(42, foo.baz)
    }


    @Test
    fun deserialize_nullable_defaults() {
        val settings: Settings = MapSettings(
            "foo.bar" to "hello",
            "foo.baz" to 43110,
            "herp" to "derp",
            "list.0" to 1,
            "list.1" to 2,
            "list.2" to 3,
            "list.size" to 3,
        )

        val foo = settings.decodeValueOrNull(Foo.serializer(), "foo")
        val herp = settings.decodeValueOrNull(String.serializer(), "herp")
        val list = settings.decodeValueOrNull(ListSerializer(Int.serializer()), "list")

        assertEquals("hello", foo?.bar)
        assertEquals(43110, foo?.baz)
        assertEquals("derp", herp)
        assertEquals(listOf(1, 2, 3), list)
    }

    @Test
    fun deserialize_nullable_defaults_empty() {
        val settings: Settings = MapSettings()

        val foo = settings.decodeValueOrNull(Foo.serializer(), "foo")

        assertNull(foo)
    }

    @Test
    fun delegate() {
        val settings: Settings = MapSettings()
        val delegate = settings.serializedValue(Foo.serializer(), "foo", Foo("goodbye"))
        var foo: Foo by delegate
        assertEquals(Foo("goodbye", 42), foo)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hello", 43110)

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals(2, settings.size)

        foo = Foo("hi", 41)

        assertEquals("hi", settings.getString("foo.bar"))
        assertEquals(41, settings.getInt("foo.baz"))
        assertEquals(2, settings.size)

        val foo2: Foo by delegate
        assertEquals(foo, foo2)
    }

    @Test
    fun delegate_nullable() {
        val settings: Settings = MapSettings()
        val delegate = settings.nullableSerializedValue(Foo.serializer(), "foo")
        var foo: Foo? by delegate
        assertNull(foo)

        foo = Foo("hello", 43110)

        assertEquals(true, settings.getBooleanOrNull("foo?"))
        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals(3, settings.size)

        val foo2: Foo? by delegate
        assertEquals(foo, foo2)

        @Suppress("UNUSED_VALUE")
        foo = null

        assertEquals(false, settings.getBooleanOrNull("foo?"))
        assertEquals(3, settings.size) // TODO turns out we don't actually clear out the other the keys here
    }

    @Test
    fun delegate_keyless() {
        val settings: Settings = MapSettings()
        val delegate = settings.serializedValue(Foo.serializer(), defaultValue = Foo("goodbye"))
        var foo: Foo by delegate
        assertEquals(Foo("goodbye", 42), foo)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hello", 43110)

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals(2, settings.size)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hi", 41)

        assertEquals("hi", settings.getString("foo.bar"))
        assertEquals(41, settings.getInt("foo.baz"))
        assertEquals(2, settings.size)

        val foo2: Foo by delegate
        assertEquals(Foo("goodbye", 42), foo2)
    }

    @Test
    fun delegate_nullable_keyless() {
        val settings: Settings = MapSettings()
        val delegate = settings.nullableSerializedValue(Foo.serializer())
        var foo: Foo? by delegate
        assertNull(foo)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hello", 43110)

        assertEquals(true, settings.getBooleanOrNull("foo?"))
        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals(3, settings.size)

        val foo2: Foo? by delegate
        assertNull(foo2)

        @Suppress("UNUSED_VALUE")
        foo = null

        assertEquals(false, settings.getBooleanOrNull("foo?"))
        assertEquals(3, settings.size) // TODO turns out we don't actually clear out the other the keys here
    }

    @Test
    fun allTypes() {
        val settings: Settings = MapSettings()
        val testClass = TestClass(
            boolean = true,
            byte = 1,
            char = '2',
            double = 3.0,
            enum = TestEnum.A,
            float = 4f,
            int = 5,
            long = 6L,
            short = 7,
            string = "8",
            unit = Unit,
            list = listOf("foo", "bar", "baz"),
            map = mapOf("one" to 1, "two" to 2, "three" to 3),
            nested = TestClassNullable()
        )

        settings.encodeValue(TestClass.serializer(), "testClass", testClass)

        assertFalse("testClass?" in settings)
        assertEquals(true, settings.getBoolean("testClass.boolean"))
        assertFalse("testClass.boolean?" in settings)
        assertEquals(1, settings.getInt("testClass.byte"))
        assertFalse("testClass.byte?" in settings)
        assertEquals('2'.code, settings.getInt("testClass.char"))
        assertFalse("testClass.char?" in settings)
        assertEquals(3.0, settings.getDouble("testClass.double"))
        assertFalse("testClass.double?" in settings)
        assertEquals(TestEnum.A.ordinal, settings.getInt("testClass.enum"))
        assertFalse("testClass.enum?" in settings)
        assertEquals(4f, settings.getFloat("testClass.float"))
        assertFalse("testClass.float?" in settings)
        assertEquals(5, settings.getInt("testClass.int"))
        assertFalse("testClass.int?" in settings)
        assertEquals(6L, settings.getLong("testClass.long"))
        assertFalse("testClass.long?" in settings)
        assertEquals(7, settings.getInt("testClass.short"))
        assertFalse("testClass.short?" in settings)
        assertEquals("8", settings.getString("testClass.string"))
        assertFalse("testClass.string?" in settings)
        assertFalse("testClass.unit?" in settings)

        assertEquals("foo", settings.getString("testClass.list.0"))
        assertEquals("bar", settings.getString("testClass.list.1"))
        assertEquals("baz", settings.getString("testClass.list.2"))
        assertEquals(3, settings.getInt("testClass.list.size"))
        assertFalse("testClass.list?" in settings)

        assertEquals("one", settings.getString("testClass.map.0"))
        assertEquals(1, settings.getInt("testClass.map.1"))
        assertEquals("two", settings.getString("testClass.map.2"))
        assertEquals(2, settings.getInt("testClass.map.3"))
        assertEquals("three", settings.getString("testClass.map.4"))
        assertEquals(3, settings.getInt("testClass.map.5"))
        assertEquals(3, settings.getInt("testClass.map.size"))
        assertFalse("testClass.map?" in settings)

        assertFalse("testClass.nested?" in settings)
        assertFalse("testClass.nested.boolean" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.boolean?"))
        assertFalse("testClass.nested.byte" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.byte?"))
        assertFalse("testClass.nested.char" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.char?"))
        assertFalse("testClass.nested.double" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.double?"))
        assertFalse("testClass.nested.enum" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.enum?"))
        assertFalse("testClass.nested.float" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.float?"))
        assertFalse("testClass.nested.int" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.int?"))
        assertFalse("testClass.nested.long" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.long?"))
        assertFalse("testClass.nested.short" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.short?"))
        assertFalse("testClass.nested.string" in settings)
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.string?"))
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.unit?"))
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.list?"))
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.map?"))
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.nested?"))

        assertEquals(35, settings.size)

        assertEquals(
            testClass,
            settings.decodeValue(
                TestClass.serializer(),
                "testClass",
                TestClass(
                    false,
                    0,
                    0.toChar(),
                    0.0,
                    TestEnum.B,
                    0f,
                    0,
                    0,
                    0,
                    "",
                    Unit,
                    emptyList(),
                    emptyMap(),
                    TestClassNullable()
                )
            )
        )
    }

    @Test
    fun allTypesNullable() {
        val settings: Settings = MapSettings()
        val testClass = TestClassNullable(
            boolean = true,
            byte = 1,
            char = '2',
            double = 3.0,
            enum = TestEnum.A,
            float = 4f,
            int = 5,
            long = 6L,
            short = 7,
            string = "8",
            unit = Unit,
            list = listOf("foo", "bar", "baz"),
            map = mapOf("one" to 1, "two" to 2, "three" to 3),
            nested = null
        )

        settings.encodeValue(TestClassNullable.serializer().nullable, "testClass", testClass)

        assertEquals(true, settings.getBoolean("testClass?"))
        assertEquals(true, settings.getBoolean("testClass.boolean"))
        assertEquals(true, settings.getBooleanOrNull("testClass.boolean?"))
        assertEquals(1, settings.getInt("testClass.byte"))
        assertEquals(true, settings.getBooleanOrNull("testClass.byte?"))
        assertEquals('2'.code, settings.getInt("testClass.char"))
        assertEquals(true, settings.getBooleanOrNull("testClass.char?"))
        assertEquals(3.0, settings.getDouble("testClass.double"))
        assertEquals(true, settings.getBooleanOrNull("testClass.double?"))
        assertEquals(TestEnum.A.ordinal, settings.getInt("testClass.enum"))
        assertEquals(true, settings.getBooleanOrNull("testClass.enum?"))
        assertEquals(4f, settings.getFloat("testClass.float"))
        assertEquals(true, settings.getBooleanOrNull("testClass.float?"))
        assertEquals(5, settings.getInt("testClass.int"))
        assertEquals(true, settings.getBooleanOrNull("testClass.int?"))
        assertEquals(6L, settings.getLong("testClass.long"))
        assertEquals(true, settings.getBooleanOrNull("testClass.long?"))
        assertEquals(7, settings.getInt("testClass.short"))
        assertEquals(true, settings.getBooleanOrNull("testClass.short?"))
        assertEquals("8", settings.getString("testClass.string"))
        assertEquals(true, settings.getBooleanOrNull("testClass.string?"))
        assertEquals(true, settings.getBooleanOrNull("testClass.unit?"))

        assertEquals("foo", settings.getString("testClass.list.0"))
        assertEquals("bar", settings.getString("testClass.list.1"))
        assertEquals("baz", settings.getString("testClass.list.2"))
        assertEquals(3, settings.getInt("testClass.list.size"))
        assertEquals(true, settings.getBooleanOrNull("testClass.list?"))

        assertEquals("one", settings.getString("testClass.map.0"))
        assertEquals(1, settings.getInt("testClass.map.1"))
        assertEquals("two", settings.getString("testClass.map.2"))
        assertEquals(2, settings.getInt("testClass.map.3"))
        assertEquals("three", settings.getString("testClass.map.4"))
        assertEquals(3, settings.getInt("testClass.map.5"))
        assertEquals(3, settings.getInt("testClass.map.size"))
        assertEquals(true, settings.getBooleanOrNull("testClass.map?"))

        assertEquals(false, settings.getBooleanOrNull("testClass.nested?"))
        assertFalse("testClass.nested.boolean" in settings)
        assertFalse("testClass.nested.boolean?" in settings)
        assertFalse("testClass.nested.byte" in settings)
        assertFalse("testClass.nested.byte?" in settings)
        assertFalse("testClass.nested.char" in settings)
        assertFalse("testClass.nested.char?" in settings)
        assertFalse("testClass.nested.double" in settings)
        assertFalse("testClass.nested.double?" in settings)
        assertFalse("testClass.nested.enum" in settings)
        assertFalse("testClass.nested.enum?" in settings)
        assertFalse("testClass.nested.float" in settings)
        assertFalse("testClass.nested.float?" in settings)
        assertFalse("testClass.nested.int" in settings)
        assertFalse("testClass.nested.int?" in settings)
        assertFalse("testClass.nested.long" in settings)
        assertFalse("testClass.nested.long?" in settings)
        assertFalse("testClass.nested.short" in settings)
        assertFalse("testClass.nested.short?" in settings)
        assertFalse("testClass.nested.string" in settings)
        assertFalse("testClass.nested.string?" in settings)
        assertFalse("testClass.nested.unit?" in settings)
        assertFalse("testClass.nested.list?" in settings)
        assertFalse("testClass.nested.map?" in settings)
        assertFalse("testClass.nested.nested?" in settings)

        assertEquals(36, settings.size)

        assertEquals(
            testClass,
            settings.decodeValue(TestClassNullable.serializer().nullable, "testClass", TestClassNullable())
        )
    }

    @Test
    fun docsExample() {

        val defaultUser = User("Asdf")

        fun Settings.saveUser(user: User) {
            if (user.nickname != null) putString("user.nickname", user.nickname) else remove("user.nickname")
            putBoolean("user.nickname?", user.nickname != null)
        }

        fun Settings.loadUser(): User {
            return User(
                nickname = when (getBooleanOrNull("user.nickname?")) {
                    true -> getStringOrNull("user.nickname") ?: return defaultUser
                    false -> null
                    null -> return defaultUser
                }
            )
        }

        fun Settings.loadUserOrNull(): User? {
            return User(
                nickname = when (getBooleanOrNull("user.nickname?")) {
                    true -> getStringOrNull("user.nickname") ?: return null
                    false -> null
                    null -> return null
                }
            )
        }

        val settings = MapSettings()

        settings.clear()
        assertEquals(defaultUser, settings.loadUser())
        assertEquals(defaultUser, settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(null, settings.loadUserOrNull())
        assertEquals(null, settings.decodeValueOrNull(User.serializer(), "user"))

        settings.encodeValue(User.serializer(), "user", User("Qwerty"))
        assertEquals(User("Qwerty"), settings.loadUser())
        assertEquals(User("Qwerty"), settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(User("Qwerty"), settings.loadUserOrNull())
        assertEquals(User("Qwerty"), settings.decodeValueOrNull(User.serializer(), "user"))
        assertEquals("Qwerty", settings.getStringOrNull("user.nickname"))
        assertEquals(true, settings.getBooleanOrNull("user.nickname?"))

        settings.encodeValue(User.serializer(), "user", User(null))
        assertEquals(User(null), settings.loadUser())
        assertEquals(User(null), settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(User(null), settings.loadUserOrNull())
        assertEquals(User(null), settings.decodeValueOrNull(User.serializer(), "user"))
        assertEquals(null, settings.getStringOrNull("user.nickname"))
        assertEquals(false, settings.getBooleanOrNull("user.nickname?"))

        settings.clear()

        settings.saveUser(User("Qwerty"))
        assertEquals(User("Qwerty"), settings.loadUser())
        assertEquals(User("Qwerty"), settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(User("Qwerty"), settings.loadUserOrNull())
        assertEquals(User("Qwerty"), settings.decodeValueOrNull(User.serializer(), "user"))
        assertEquals("Qwerty", settings.getStringOrNull("user.nickname"))
        assertEquals(true, settings.getBooleanOrNull("user.nickname?"))

        settings.saveUser(User(null))
        assertEquals(User(null), settings.loadUser())
        assertEquals(User(null), settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(User(null), settings.loadUserOrNull())
        assertEquals(User(null), settings.decodeValueOrNull(User.serializer(), "user"))
        assertEquals(null, settings.getStringOrNull("user.nickname"))
        assertEquals(false, settings.getBooleanOrNull("user.nickname?"))

        settings.clear()
        settings.putBoolean("user.nickname?", true)
        assertEquals(defaultUser, settings.loadUser())
        assertEquals(defaultUser, settings.decodeValue(User.serializer(), "user", defaultUser))
        assertEquals(null, settings.loadUserOrNull())
        assertEquals(null, settings.decodeValueOrNull(User.serializer(), "user"))
    }
}

@Serializable
data class User(val nickname: String?)

@Serializable
data class Foo(val bar: String, val baz: Int = 42)

@Serializable
data class TestClass(
    val boolean: Boolean,
    val byte: Byte,
    val char: Char,
    val double: Double,
    val enum: TestEnum,
    val float: Float,
    val int: Int,
    val long: Long,
    val short: Short,
    val string: String,
    val unit: Unit,
    val list: List<String>,
    val map: Map<String, Int>,
    val nested: TestClassNullable
)


@Serializable
data class TestClassNullable(
    val boolean: Boolean? = null,
    val byte: Byte? = null,
    val char: Char? = null,
    val double: Double? = null,
    val enum: TestEnum? = null,
    val float: Float? = null,
    val int: Int? = null,
    val long: Long? = null,
    val short: Short? = null,
    val string: String? = null,
    val unit: Unit? = null,
    val list: List<String>? = null,
    val map: Map<String, Int>? = null,
    val nested: TestClassNullable? = null
)

@Suppress("unused")
@Serializable
enum class TestEnum { A, B }
