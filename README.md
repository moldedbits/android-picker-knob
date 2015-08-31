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
compile 'com.github.moldedbits:android-picker-knob:0.2'
```

## Copyright

The MIT License (MIT)

Copyright (c) 2015 moldedbits

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
