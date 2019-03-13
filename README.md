# Android-UI

[![Build Status](https://img.shields.io/travis/ithot-all/android-ui/master.svg?style=flat-square)](https://travis-ci.org/ithot-all/android-ui)

:fire: A series of Android view controls 
### Interfaces (Required)
```gradle
implementation "org.ithot.android.ui:ui-inter:0.0.1"
```

### Slider
![slider](arts/slider.gif)
## install
```gradle
implementation "org.ithot.android.ui:sliderview:0.0.1"
```
## usage
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
## install
```gradle
implementation "org.ithot.android.ui:sweepview:0.0.1"
```
## usage
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
## install
```gradle
implementation "org.ithot.android.ui:twinview:0.0.1"
```
## usage
```xml
 <org.ithot.android.ui.twin.TwinView
    android:id="@+id/twin_view"
    android:layout_width="150dp"
    android:layout_height="150dp"
    android:layout_centerHorizontal="true"
    android:layout_gravity="center_horizontal"
    app:twin_foreColor="#ffff00" />
```

## MORE
[Example](https://github.com/ithot-all/android-ui/tree/master/example)
