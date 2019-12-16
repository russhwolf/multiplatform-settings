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

import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

// TODO Add a test case with a non-empty SerializersModule?
@OptIn(ExperimentalSerializationApi::class)
class SettingsSerializationTest {

    @Test
    fun serialize() {
        val foo = Foo("hello", 43110)

        val settings: Settings = MockSettings()

        settings.serializeValue(Foo.serializer(), "foo", foo)
        settings.serializeValue(String.serializer(), "herp", "derp")

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))
        assertEquals("derp", settings.getString("herp"))
    }

    @Test
    fun deserialize() {
        val settings: Settings = MockSettings("foo.bar" to "hello", "foo.baz" to 43110, "herp" to "derp")

        val foo = settings.deserializeValue(Foo.serializer(), "foo")
        val herp = settings.deserializeValue(String.serializer(), "herp")

        assertEquals("hello", foo.bar)
        assertEquals(43110, foo.baz)
        assertEquals("derp", herp)
    }

    @Test
    fun deserialize_empty() {
        val settings: Settings = MockSettings()

        val foo = settings.deserializeValue(Foo.serializer(), "foo")

        assertEquals("", foo.bar)
        assertEquals(0, foo.baz)
    }

    @Test
    fun delegate() {
        val settings: Settings = MockSettings()
        val delegate = settings.serializationDelegate(Foo.serializer(), "foo")
        var foo: Foo by delegate
        assertEquals(Foo("", 0), foo)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hello", 43110)

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))

        foo = Foo("hi", 41)

        assertEquals("hi", settings.getString("foo.bar"))
        assertEquals(41, settings.getInt("foo.baz"))

        val foo2: Foo by delegate
        assertEquals(foo, foo2)
    }

    @Test
    fun delegate_keyless() {
        val settings: Settings = MockSettings()
        val delegate = settings.serializationDelegate(Foo.serializer())
        var foo: Foo by delegate
        assertEquals(Foo("", 0), foo)

        @Suppress("UNUSED_VALUE")
        foo = Foo("hello", 43110)

        assertEquals("hello", settings.getString("foo.bar"))
        assertEquals(43110, settings.getInt("foo.baz"))

        @Suppress("UNUSED_VALUE")
        foo = Foo("hi", 41)

        assertEquals("hi", settings.getString("foo.bar"))
        assertEquals(41, settings.getInt("foo.baz"))

        val foo2: Foo by delegate
        assertEquals(Foo("", 0), foo2)
    }

    @Test
    fun allTypes() {
        val settings: Settings = MockSettings()
        val testClass: TestClass? = TestClass(
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
            nested = TestClass()
        )

        settings.serializeValue(TestClass.serializer().nullable, "testClass", testClass)

        assertEquals(true, settings.getBoolean("testClass?"))
        assertEquals(true, settings.getBoolean("testClass.boolean"))
        assertEquals(true, settings.getBooleanOrNull("testClass.boolean?"))
        assertEquals(1, settings.getInt("testClass.byte"))
        assertEquals(true, settings.getBooleanOrNull("testClass.byte?"))
        assertEquals('2'.toInt(), settings.getInt("testClass.char"))
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

        assertEquals(true, settings.getBooleanOrNull("testClass.nested?"))
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
        assertEquals(1, settings.keys.count { it.startsWith("testClass.nested.list") })
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.list?"))
        assertEquals(1, settings.keys.count { it.startsWith("testClass.nested.map") })
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.map?"))
        assertEquals(false, settings.getBooleanOrNull("testClass.nested.nested?"))

        assertEquals(testClass, settings.deserializeValue(TestClass.serializer().nullable, "testClass"))
    }

    @Test
    fun docsExample() {
        @Serializable
        data class MyClass(val myProperty: Int?)

        val settings = MockSettings()

        settings.serializeValue(MyClass.serializer(), "myClass", MyClass(42))
        assertEquals(42, settings.getIntOrNull("myClass.myProperty"))
        assertEquals(true, settings.getBooleanOrNull("myClass.myProperty?"))
        assertEquals(MyClass(42), settings.deserializeValue(MyClass.serializer(), "myClass"))


        settings.serializeValue(MyClass.serializer(), "myClass", MyClass(null))
        assertFalse("myClass.myProperty" in settings)
        assertEquals(false, settings.getBooleanOrNull("myClass.myProperty?"))
        assertEquals(MyClass(null), settings.deserializeValue(MyClass.serializer(), "myClass"))
    }
}

@Serializable
data class Foo(val bar: String, val baz: Int)

@Serializable
data class TestClass(
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
    val nested: TestClass? = null
)

@Suppress("unused")
@Serializable
enum class TestEnum { A, B }
