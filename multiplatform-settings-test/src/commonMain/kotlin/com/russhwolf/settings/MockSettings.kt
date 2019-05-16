package com.russhwolf.settings

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
 */
@UseExperimental(ExperimentalListener::class)
public class MockSettings public constructor(private val delegate: MutableMap<String, Any> = mutableMapOf()) :
    ListenableSettings {
    private val listeners = mutableListOf<() -> Any>()
    private fun invokeListeners() = listeners.forEach { it() }

    public constructor(vararg items: Pair<String, Any>) : this(mutableMapOf(*items))

    /**
     * A factory that can produce [Settings] instances.
     *
     * This implementation will use the same backing [Map] if the same `name` parameter is passed to [create].
     */
    public class Factory : Settings.Factory {
        private val delegateCache = mutableMapOf<String?, MutableMap<String, Any>>()

        /**
         * Assigns the values in [delegate] to the cache that will be used to back any [MockSettings] this factory
         * creates named [name]
         */
        public fun setCacheValues(name: String?, delegate: Map<String, Any>) {
            val map = delegateCache.getOrPut(name) { mutableMapOf() }
            map.clear()
            map.putAll(delegate)
        }

        /**
         * Assigns the values in [items] to the cache that will be used to back any [MockSettings] this factory
         * creates named [name]
         */
        public fun setCacheValues(name: String?, vararg items: Pair<String, Any>) {
            setCacheValues(name, mutableMapOf(*items))
        }

        public override fun create(name: String?): Settings {
            val delegate = delegateCache.getOrPut(name) { mutableMapOf() }
            return MockSettings(delegate)
        }
    }

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

    public override fun putLong(key: String, value: Long) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getLong(key: String, defaultValue: Long): Long = delegate[key] as? Long ?: defaultValue

    public override fun putString(key: String, value: String) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getString(key: String, defaultValue: String): String = delegate[key] as? String ?: defaultValue

    public override fun putFloat(key: String, value: Float) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getFloat(key: String, defaultValue: Float): Float = delegate[key] as? Float ?: defaultValue

    public override fun putDouble(key: String, value: Double) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getDouble(key: String, defaultValue: Double): Double = delegate[key] as? Double ?: defaultValue

    public override fun putBoolean(key: String, value: Boolean) {
        delegate[key] = value
        invokeListeners()
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate[key] as? Boolean ?: defaultValue

    @ExperimentalListener
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val cache = Listener.Cache(delegate[key])

        val listener = {
            val prev = cache.value
            val current = delegate[key]
            if (prev != current) {
                callback()
                cache.value = current
            }
        }
        listeners += listener
        return Listener(listener)
    }

    @ExperimentalListener
    public override fun removeListener(listener: SettingsListener) {
        val platformListener = listener as? Listener ?: return
        listeners -= platformListener.delegate
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * In the [MockSettings] implementation this simply wraps a lambda parameter which is being called whenever the
     * underlying map changes.
     */
    @ExperimentalListener
    public class Listener internal constructor(
        internal val delegate: () -> Unit
    ) : SettingsListener {
        internal class Cache(var value: Any?)
    }
}
