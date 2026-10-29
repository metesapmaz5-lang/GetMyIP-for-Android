package org.parabellumlinuxproject.getmyip.settings

import android.content.Context
import org.parabellumlinuxproject.getmyip.core.IfconfigCoProvider
import org.parabellumlinuxproject.getmyip.core.IpLeakProvider
import org.parabellumlinuxproject.getmyip.core.IpProvider
import org.parabellumlinuxproject.getmyip.core.Providers

class AppSettings(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var providerId: String
        get() = prefs.getString(KEY_PROVIDER, PROVIDER_IPLEAK) ?: PROVIDER_IPLEAK
        set(value) {
            prefs.edit().putString(KEY_PROVIDER, value).apply()
        }

    var customUrl: String
        get() = prefs.getString(KEY_CUSTOM_URL, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_CUSTOM_URL, value).apply()
        }

    var notifyOnChange: Boolean
        get() = prefs.getBoolean(KEY_NOTIFY, true)
        set(value) {
            prefs.edit().putBoolean(KEY_NOTIFY, value).apply()
        }

    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) {
            prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()
        }

    fun provider(): IpProvider = when (providerId) {
        PROVIDER_IFCONFIG -> IfconfigCoProvider
        PROVIDER_CUSTOM -> Providers.custom(customUrl) ?: IpLeakProvider
        else -> IpLeakProvider
    }

    companion object {
        const val PROVIDER_IPLEAK = "ipleak"
        const val PROVIDER_IFCONFIG = "ifconfig"
        const val PROVIDER_CUSTOM = "custom"
        private const val KEY_PROVIDER = "provider"
        private const val KEY_CUSTOM_URL = "custom_url"
        private const val KEY_NOTIFY = "notify_on_change"
        private const val KEY_ONBOARDING = "onboarding_done"
    }
}
