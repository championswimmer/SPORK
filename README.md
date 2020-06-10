![](docs/spork.png)

<p align="center"> 
  <b>S</b>hared <b>P</b>references <b>O</b>bject <b>R</b>elations in <b>K</b>otlin 
  <br><br>
  <a href="https://jitpack.io/#tech.arnav/SPORK"><img src="https://jitpack.io/v/tech.arnav/SPORK.svg"></a>
</p>

A Retrofit-inspired ORM for Android's SharedPreferences that turns an annotated interface into a SharedPreference accessor object.

## Why ?

### Old Way of Using SharedPreferences
There are multiple ORMs for SQLite in Android, and they make it very easy to 
treat `tables` and `rows` as `classes` and `objects`. 
But still, there is a lot of local data we store in `SharedPreferences` 
and accessing/modifying them still requires writing a  couple of boilerplate lines - 

```kotlin
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

### The New \*\*AWESOME\*\* way ! 

#### 1. Create your preference as abstract class

_**AppPrefs.kt**_ 

```kotlin
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile

@PreferenceFile("app_prefs")
abstract class AppPrefs {

    @Pref("count")
    abstract var count: Int

    @Pref("foobar")
    abstract var fooBar: String
}
```

#### 2. Access it from your Activity/Service

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

Over time in large long running projects, you might have added prefs earlier, 
that are not used anymore. Your users might be having huge pref files with ages old 
keys not used anymore. But every time you do `getSharedPreferences` the 
entire file is still read into memory. 

While initialising, **SPORK** will automatically remove old keys that are not 
in `AppPrefs.kt` anymore. Thus your `app_prefs.xml` is always an 
up-to-date and pruned implementation of `AppPrefs.kt`.


## Installation 

Add this into your app's build.gradle 

```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'tech.arnav.SPORK:spork-annotations:0.2.3'
    kapt 'tech.arnav.SPORK:spork-compiler:0.2.3'
}
```

#### Using reflection instead 
> NOTE: Reflection has a huge runtime cost, and it is better to use the build time code generation

If you want to use reflection instead of kapt
```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'tech.arnav.SPORK:spork-reflect:0.2.3'
}
```

## How ? 

### What happens under the hood ? 
A very simply concrete class is generated for each abstract class you craete 

The abstract class you create - 
```kotlin
@PreferenceFile("app_prefs")
abstract class AppPrefs {

    @Pref("count")
    abstract var count: Int

    @Pref("foobar")
    abstract var fooBar: String
}
```

The class that is generated 
```kotlin
class AppPrefsImpl(
  context: Context
) : AppPrefs() {
  val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

  override var count: Int
    get() = prefs.getInt("count", 0) ?: 0
    set(value) {
      prefs.edit().putInt("count", value).apply()
    }

  override var fooBar: String
    get() = prefs.getString("foobar", "") ?: ""
    set(value) {
      prefs.edit().putString("foobar", value).apply()
    }
}
```


## TODO List

- [x] Annotation Processor / Codegen support
- [x] remove kotlin-reflect 
- [ ] allow a way to mark default get values if pref not already present
- [ ] allow turning on/off pruning of old pref keys 
