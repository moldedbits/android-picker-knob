# Picker Knob Widget

Picker widget shaped like a knob.

![picker knob](https://cloud.githubusercontent.com/assets/1011854/9435171/f462047a-4a62-11e5-9aca-1fd36081e3da.png)

Include in your layout as
```
<com.moldedbits.pickerknob.PickerKnob
        android:id="@+id/picker_knob"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:picker_min_value="0"
        app:picker_max_value="50"
        app:picker_text_size="10sp"
        app:picker_dash_gap="10dp"
        app:picker_text_color="@android:color/black"/>
```

## Download

Download via Gradle:

To the repositories, add
```
maven { url "https://jitpack.io" }
```

and to the dependencies, add
```
compile 'com.github.moldedbits:android-picker-knob:0.1'
```
