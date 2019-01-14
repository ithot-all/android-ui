package org.ithot.anroid.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ithot.android.ui.inter.UIListener;
import org.ithot.android.ui.slider.SliderView;
import org.ithot.android.ui.sweep.SweepView;
import org.ithot.android.ui.twin.TwinView;

public class ExampleActivity extends Activity {

    private static final String TAG = "[xampleActivity]";

    TwinView twinView;
    SliderView sliderView;
    SweepView sweepView;
    TextView tvTwin;
    TextView tvSlider;
    TextView tvSweep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        twinView = findViewById(R.id.twin_view);
        sliderView = findViewById(R.id.silder_view);
        sweepView = findViewById(R.id.sweep_view);

        tvTwin = findViewById(R.id.tv_twin);
        tvSlider = findViewById(R.id.tv_slider);
        tvSweep = findViewById(R.id.tv_sweep);

        twinView.setListener(new UIListener() {
            @Override
            public void move(float value) {
                tvTwin.setText(value + "%");
                debug("[twinView] " + value);
            }
        });

        sliderView.setListener(new UIListener() {
            @Override
            public void move(float value) {
                tvSlider.setText(value + "%");
                debug("[sliderView] " + value);
            }

            @Override
            public void start(float value) {
                tvSlider.setText(value + "%");
            }

            @Override
            public void end(float value) {
                tvSlider.setText(value + "%");
            }
        });

        sweepView.setListener(new UIListener() {
            @Override
            public void move(float value) {
                tvSweep.setText(value + "%");
                debug("[sweepView] " + value);
            }
        });

        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweepView.go(50);
            }
        });
    }

    protected void debug(Object m) {
        Log.d(TAG, m + "");
    }
}
