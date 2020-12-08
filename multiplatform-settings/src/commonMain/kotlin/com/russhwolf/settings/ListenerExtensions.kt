package com.russhwolf.settings

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getInt] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addIntListener(
    key: String,
    defaultValue: Int = 0,
    crossinline callback: (Int) -> Unit
): SettingsListener =
    addListener(key) { callback(getInt(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getIntOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addIntOrNullListener(
    key: String,
    crossinline callback: (Int?) -> Unit
): SettingsListener =
    addListener(key) { callback(getIntOrNull(key)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getLong] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addLongListener(
    key: String,
    defaultValue: Long = 0L,
    crossinline callback: (Long) -> Unit
): SettingsListener =
    addListener(key) { callback(getLong(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getLongOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addLongOrNullListener(
    key: String,
    crossinline callback: (Long?) -> Unit
): SettingsListener =
    addListener(key) { callback(getLongOrNull(key)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getString] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addStringListener(
    key: String,
    defaultValue: String = "",
    crossinline callback: (String) -> Unit
): SettingsListener =
    addListener(key) { callback(getString(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getStringOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addStringOrNullListener(
    key: String,
    crossinline callback: (String?) -> Unit
): SettingsListener =
    addListener(key) { callback(getStringOrNull(key)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getFloat] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addFloatListener(
    key: String,
    defaultValue: Float = 0f,
    crossinline callback: (Float) -> Unit
): SettingsListener =
    addListener(key) { callback(getFloat(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getFloatOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addFloatOrNullListener(
    key: String,
    crossinline callback: (Float?) -> Unit
): SettingsListener =
    addListener(key) { callback(getFloatOrNull(key)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getDouble] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addDoubleListener(
    key: String,
    defaultValue: Double = 0.0,
    crossinline callback: (Double) -> Unit
): SettingsListener =
    addListener(key) { callback(getDouble(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getDoubleOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addDoubleOrNullListener(
    key: String,
    crossinline callback: (Double?) -> Unit
): SettingsListener =
    addListener(key) { callback(getDoubleOrNull(key)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getBoolean] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addBooleanListener(
    key: String,
    defaultValue: Boolean = false,
    crossinline callback: (Boolean) -> Unit
): SettingsListener =
    addListener(key) { callback(getBoolean(key, defaultValue)) }

/**
 * Acts like [ObservableSettings.addListener], but passes the result of [Settings.getBooleanOrNull] to the callback as a
 * convenience.
 */
@ExperimentalSettingsApi
public inline fun ObservableSettings.addBooleanOrNullListener(
    key: String,
    crossinline callback: (Boolean?) -> Unit
): SettingsListener =
    addListener(key) { callback(getBooleanOrNull(key)) }
