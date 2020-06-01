package tech.arnav.sporksample

import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("app_prefs")
abstract class AppPrefs {

    @Pref("count")
    abstract var count: Int

    @Pref("foobar")
    abstract var fooBar: String
}