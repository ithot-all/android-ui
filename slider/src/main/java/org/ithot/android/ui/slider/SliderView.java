package org.ithot.android.ui.slider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.ithot.android.ui.inter.BaseUIListener;

public class SliderView extends View {

    private static final int LONG_PRESS = 500;

    private Handler handler = new Handler();
    private boolean unPerformLongClick;
    private Paint.Cap seekCap = Paint.Cap.ROUND;
    private BaseUIListener listener;

    private float trackStartX;
    private float trackStandardY;
    private float trackBackgroundEndX;
    private float trackForegroundOffsetX;
    private float indicatorX;

    private Paint indicatorPaint;
    private boolean indicatorShadowEnable;
    private float indicatorShadowRadius;
    private float indicatorRadius;
    private int indicatorColor;
    private int indicatorShadowColor;

    private Paint trackBackgroundPaint;
    private Paint trackForegroundPaint;
    private boolean trackShadowEnable;
    private int trackForegroundColor;

    private float trackShadowRadius;
    private float trackHeight;
    private int trackBackgroundColor;
    private int trackShadowColor;

    private float progress;

    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            unPerformLongClick = false;
            performLongClick();
        }
    };

    private SliderView(Context context) {
        this(context, (AttributeSet) null, 0);
        initial(null);
    }

    public SliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initial(attrs);
    }

    public SliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial(attrs);
    }

    private void initial(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SliderView);
            indicatorRadius = ta.getDimension(R.styleable.SliderView_slider_indicatorRadius, 30);
            indicatorColor = ta.getColor(R.styleable.SliderView_slider_indicatorColor, Color.WHITE);
            indicatorShadowEnable = ta.getBoolean(R.styleable.SliderView_slider_indicatorShadowEnable, true);
            indicatorShadowColor = ta.getColor(R.styleable.SliderView_slider_indicatorShadowColor, Color.BLACK);
            indicatorShadowRadius = ta.getDimension(R.styleable.SliderView_slider_indicatorShadowRadius, 6);
            trackHeight = ta.getDimension(R.styleable.SliderView_slider_trackHeight, 18);
            trackBackgroundColor = ta.getColor(R.styleable.SliderView_slider_trackBackgroundColor, Color.GRAY);
            trackForegroundColor = ta.getColor(R.styleable.SliderView_slider_trackForegroundColor, Color.CYAN);
            trackShadowEnable = ta.getBoolean(R.styleable.SliderView_slider_trackShadowEnable, true);
            trackShadowColor = ta.getColor(R.styleable.SliderView_slider_trackShadowColor, Color.BLACK);
            trackShadowRadius = (int) ta.getDimension(R.styleable.SliderView_slider_trackShadowRadius, 6);
            ta.recycle();
        } else {
            indicatorRadius = 30;
            indicatorColor = Color.WHITE;
            indicatorShadowEnable = true;
            indicatorShadowColor = Color.BLACK;
            indicatorShadowRadius = 6;
            trackHeight = 18;
            trackBackgroundColor = Color.GRAY;
            trackForegroundColor = Color.CYAN;
            trackShadowEnable = true;
            trackShadowColor = Color.BLACK;
            trackShadowRadius = 6;
        }
        if (indicatorShadowEnable || trackShadowEnable) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        trackBackgroundPaint = new Paint();
        trackBackgroundPaint.setColor(trackBackgroundColor);
        trackBackgroundPaint.setAntiAlias(true);
        trackBackgroundPaint.setStrokeCap(seekCap);
        trackBackgroundPaint.setStrokeWidth(trackHeight);
        if (trackShadowEnable) {
            trackBackgroundPaint.setShadowLayer(trackShadowRadius, 0, 0, trackShadowColor);
        }
        trackBackgroundPaint.setStyle(Paint.Style.STROKE);

        trackForegroundPaint = new Paint();
        trackForegroundPaint.setColor(trackForegroundColor);
        trackForegroundPaint.setAntiAlias(true);
        trackForegroundPaint.setStrokeCap(seekCap);
        trackForegroundPaint.setStrokeWidth(trackHeight);
        trackForegroundPaint.setStyle(Paint.Style.STROKE);

        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setStyle(Paint.Style.FILL);
        if (indicatorShadowEnable) {
            indicatorPaint.setShadowLayer(indicatorShadowRadius, 0, 0, indicatorShadowColor);
        }
    }

    private void setupCoordinate() {
        trackStandardY = getMeasuredHeight() / 2f;
        indicatorX = indicatorRadius + indicatorShadowRadius;
        trackStartX = indicatorX;
        trackBackgroundEndX = getMeasuredWidth() - trackStartX;
        trackForegroundOffsetX = trackStartX;
        float each = (trackBackgroundEndX - trackStartX) / 100f * progress;
        indicatorX += each;
        trackForegroundOffsetX += each;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        float indicator = indicatorRadius * 2 + indicatorShadowRadius * 2;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int) Math.max(height, indicator));
        setupCoordinate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(trackStartX, trackStandardY, trackBackgroundEndX, trackStandardY, trackBackgroundPaint);
        canvas.drawLine(trackStartX, trackStandardY, trackForegroundOffsetX, trackStandardY, trackForegroundPaint);
        canvas.drawCircle(indicatorX, trackStandardY, indicatorRadius, indicatorPaint);
    }

    public void init(int progress) {
        this.progress = progress;
        setupCoordinate();
        invalidate();
    }

    public void init(int progress, int min, int max) {
        float unit = (max - min) / 100f;
        this.progress = (progress - min) / unit;
        setupCoordinate();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return super.onTouchEvent(event);
        float ex = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                moveIndicator(ex);
                if (listener != null) {
                    listener.start(progress);
                }
                unPerformLongClick = true;
                handler.removeCallbacks(longClickRunnable);
                handler.postDelayed(longClickRunnable, LONG_PRESS);
                break;
            case MotionEvent.ACTION_MOVE:
                moveIndicator(ex);
                if (listener != null) {
                    listener.call(progress);
                }
                break;
            case MotionEvent.ACTION_UP:
                moveIndicator(ex);
                handler.removeCallbacks(longClickRunnable);
                if (unPerformLongClick) {
                    performClick();
                }
                if (listener != null) {
                    listener.end(progress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(longClickRunnable);
                break;
        }
        return true;
    }

    private void moveIndicator(float ex) {
        if (ex < trackStartX - trackShadowRadius / 2f) {
            ex = trackStartX - trackShadowRadius / 2f;
        }
        if (ex > trackBackgroundEndX + trackShadowRadius / 2f) {
            ex = trackBackgroundEndX + trackShadowRadius / 2f;
        }
        indicatorX = ex;
        trackForegroundOffsetX = ex;
        float val = (ex - trackStartX) / (trackBackgroundEndX - trackStartX) * 100f;
        if (val < 0) val = 0;
        if (val > 100) val = 100;
        progress = val;
        invalidate();
    }

    public void setListener(BaseUIListener listener) {
        this.listener = listener;
    }

    public void setIndicatorColor(int color) {
        indicatorColor = color;
        indicatorPaint.setColor(indicatorColor);
        invalidate();
    }

    public void setTrackForegroundColor(int color) {
        trackForegroundColor = color;
        trackForegroundPaint.setColor(this.trackForegroundColor);
        invalidate();
    }

    public void setTrackBackgroundColor(int color) {
        trackBackgroundColor = color;
        trackBackgroundPaint.setColor(this.trackBackgroundColor);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}