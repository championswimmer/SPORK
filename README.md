![](docs/spork.png)

<p align="center"> 
  <b>S</b>hared <b>P</b>references <b>O</b>bject <b>R</b>elations in <b>K</b>otlin 
  <br><br>
  <a href="https://jitpack.io/#tech.arnav/SPORK"><img src="https://jitpack.io/v/tech.arnav/SPORK.svg"></a>
</p>

A Retrofit-inspired ORM for Android's SharedPreferences that turns an annotated interface into a SharedPreference accessor object.

## Why ?

There are multiple ORMs for SQLite in Android, and they make it very easy to treat _tables_ and _rows_ as _classes_ and _objects_. But still, there is a lot of local data we store in `SharedPreferences` and accessing/modifying them still requires writing a  couple of boilerplate lines - 

```kt
val prefs = context.getSharedPreferences('app_prefs', Context.MODE_PRIVATE)

// read
val currCount = prefs.getInt('launch_count', 0) 
// ----------------

// write
val editor = prefs.edit()
editor.putInt('launch_count', currCount + 1) 
editor.apply()
// -- or if using android ktx -- 
prefs.edit {
  putInt('launch_count', currCount + 1)
}
```
It is time to reduce all of that. 

## How it works ?

### 1. Create your preference interface

_**AppPrefs.kt**_ 

```kt
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("app_prefs")
interface AppPrefs {

    @Pref("count")
    var count: Int

    @Pref("screen")
    var screenName: String

}
```

### 2. Access it from your Activity/Service

_**MainActivity.kt**_

```kt
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appPrefs = Spork.create(this, AppPrefs::class)
        // write prefs
        appPrefs.screenName = "MAIN" 
        appPrefs.count++
        
        // read prefs
        Toast.makeText(this, "Visit number " + appPrefs.count, Toast.LENGTH_SHORT).show()

    }
}
```

## Other Benefits

Over time in large long running projects, you might have added prefs earlier, that are not used anymore. Your users might be having huge pref files with ages old keys not used anymore. But every time you do `getSharedPreferences` the entire file is still read into memory. 

While initialising, **SPORK** will automatically remove old keys that are not in `AppPrefs.kt` anymore. Thus your `app_prefs.xml` is always an up-to-date and pruned implementation of `AppPrefs.kt`.


## Installation 

Add this into your app's build.gradle 

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'tech.arnav:SPORK:0.1.0'
}

```
