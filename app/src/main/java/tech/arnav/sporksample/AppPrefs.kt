package tech.arnav.sporksample

import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("app_prefs")
interface AppPrefs {

    @Pref("count")
    var count: Int

    @Pref("foobar")
    var fooBar: String

}