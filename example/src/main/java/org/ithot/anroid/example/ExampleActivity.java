package org.ithot.anroid.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;

import org.ithot.android.ui.TwinLayerView;
import org.ithot.android.ui.inter.UIListener;

public class ExampleActivity extends Activity {

    private static final String TAG = "[ExampleActivity]";

    TwinLayerView tlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        tlView = findViewById(R.id.tl_view);
        tlView.run(50);
        tlView.setForeColor(Color.YELLOW);
        tlView.setListener(new UIListener() {
            @Override
            public void move(float value) {
                debug(value);
            }
        });
    }

    protected void debug(Object m) {
        Log.d(TAG, m + "");
    }
}
