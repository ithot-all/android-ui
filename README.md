# android-ui [![Build Status](https://www.travis-ci.org/ithot-all/android-ui.svg?branch=master)](https://www.travis-ci.org/ithot-all/android-ui)
:fire: A series of Android view controls

### Interfaces (Required)
```gradle
implementation "org.ithot.android.ui.inter:listener:0.0.1"
```

### Slider
![slider](arts/slider.gif)
```gradle
implementation "org.ithot.android.ui.slider:slider:0.0.1"
```
```xml
<org.ithot.android.ui.slider.SliderView
    android:id="@+id/silder_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    app:slider_indicatorRadius="12dp"
    app:slider_trackForegroundColor="#8bc34a"
    app:slider_trackHeight="6dp" />
```

### Sweep
![sweep](arts/sweep.gif)
```gradle
implementation "org.ithot.android.ui.sweep:sweep:0.0.1"
```
```xml
<org.ithot.android.ui.sweep.SweepView
    android:id="@+id/sweep_view"
    app:sweep_foregroundColor="@color/colorAccent"
    app:sweep_strokeWidth="5dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### Twin
![twin](arts/twin.gif)
```gradle
implementation "org.ithot.android.ui.twin:twin:0.0.1"
```
```xml
 <org.ithot.android.ui.twin.TwinView
    android:id="@+id/twin_view"
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:layout_centerHorizontal="true"
    android:layout_gravity="center_horizontal"
    app:twin_foreColor="#ffff00" />
```

## usage
[Example](https://github.com/ithot-all/android-ui/tree/master/example)
