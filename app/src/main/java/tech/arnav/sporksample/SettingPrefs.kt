package tech.arnav.sporksample

import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("settings")
abstract class SettingPrefs {

    @Pref("dark_mode")
    abstract val darkMode: Boolean
}