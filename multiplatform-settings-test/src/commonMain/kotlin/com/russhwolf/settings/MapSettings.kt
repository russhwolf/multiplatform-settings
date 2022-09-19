/*
 * Copyright 2020 Russell Wolf
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

package com.russhwolf.settings

@Deprecated(
    message = "MockSettings has been renamed to to MapSettings",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("MapSettings", "com.russhwolf.settings.MapSettings")
)
@Suppress("KDocMissingDocumentation")
public typealias MockSettings = MapSettings

/**
 * A collection of storage-backed key-value data
 *
 * This class allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key.
 *
 * The `MockSettings` implementation is intended for use in unit tests. The mock persisted state is represented by a
 * [Map] and can be injected at construction time.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 *
 * This class can be instantiated by wrapping a [MutableMap] or set of [Pair] entries, or via a [Factory].
 *
 * This implementation is verified against the same test suite as the real platform-specific implementations to ensure
 * it shares the same behavior, assuming the default [mutableMapOf] delegate is used.
 */
public class MapSettings public constructor(private val delegate: MutableMap<String, Any> = mutableMapOf()) :
    ObservableSettings {
    private val listeners = mutableListOf<() -> Any>()
    private fun invokeListeners() = listeners.forEach { it() }

    public constructor(vararg items: Pair<String, Any>) : this(mutableMapOf(*items))

    /**
     * A factory that can produce [Settings] instances.
     *
     * This implementation will use the same backing [Map] if the same `name` parameter is passed to [create].
     *
     * By default the backing maps produced by this `Factory` are created using the [mutableMapOf] function, but this
     * is configurable by changing the [mapFactory] parameter. The [delegateCache] parameter can be used to control the
     * `Map` implementation used by the cache that stores these delegates.
     */
    public class Factory(
        private val mapFactory: () -> MutableMap<String, Any> = ::mutableMapOf,
        private val delegateCache: MutableMap<String?, MutableMap<String, Any>> = mutableMapOf()
    ) : Settings.Factory {

        /**
         * Assigns the values in [delegate] to the cache that will be used to back any [MockSettings] this factory
         * creates named [name]
         */
        public fun setCacheValues(name: String?, delegate: Map<String, Any>) {
            val map = delegateCache.getOrPut(name, mapFactory)
            map.clear()
            map.putAll(delegate)
        }

        /**
         * Assigns the values in [items] to the cache that will be used to back any [MockSettings] this factory
         * creates named [name]
         */
        public fun setCacheValues(name: String?, vararg items: Pair<String, Any>) {
            setCacheValues(name, mapFactory().apply { putAll(items) })
        }

        public override fun create(name: String?): MapSettings {
            val delegate = delegateCache.getOrPut(name, mapFactory)
            return MapSettings(delegate)
        }
    }

    public override val keys: Set<String> get() = delegate.keys
    public override val size: Int get() = delegate.size

    public override fun clear() {
        delegate.clear()
        invokeListeners()
    }

    public override fun remove(key: String) {
        delegate -= key
        invokeListeners()
    }

    public override fun hasKey(key: String): Boolean = key in delegate

    public override fun putInt(key: String, value: Int) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getInt(key: String, defaultValue: Int): Int = delegate[key] as? Int ?: defaultValue

    public override fun getIntOrNull(key: String): Int? = delegate[key] as? Int

    public override fun putLong(key: String, value: Long) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getLong(key: String, defaultValue: Long): Long = delegate[key] as? Long ?: defaultValue

    public override fun getLongOrNull(key: String): Long? = delegate[key] as? Long

    public override fun putString(key: String, value: String) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getString(key: String, defaultValue: String): String = delegate[key] as? String ?: defaultValue

    public override fun getStringOrNull(key: String): String? = delegate[key] as? String

    public override fun putFloat(key: String, value: Float) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getFloat(key: String, defaultValue: Float): Float = delegate[key] as? Float ?: defaultValue

    public override fun getFloatOrNull(key: String): Float? = delegate[key] as? Float

    public override fun putDouble(key: String, value: Double) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getDouble(key: String, defaultValue: Double): Double = delegate[key] as? Double ?: defaultValue

    public override fun getDoubleOrNull(key: String): Double? = delegate[key] as? Double

    public override fun putBoolean(key: String, value: Boolean) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate[key] as? Boolean ?: defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? = delegate[key] as? Boolean

    @Deprecated(
        "Use typed listener methods instead",
        level = DeprecationLevel.WARNING
    )
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        var prev = delegate[key]

        val listener = {
            val current = delegate[key]
            if (prev != current) {
                callback()
                prev = current
            }
        }
        listeners += listener
        return Listener(listeners, listener)
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * In the [MockSettings] implementation this simply wraps a lambda parameter which is being called whenever a
     * mutating API is called. Unlike platform implementations, this listener will NOT be called if the underlying map
     * is mutated by something other than the `MockSettings` instance that originally created the listener.
     */
    public class Listener internal constructor(
        private val listeners: MutableList<() -> Any>,
        private val listener: () -> Unit
    ) : SettingsListener {
        public override fun deactivate() {
            listeners -= listener
        }
    }
}
