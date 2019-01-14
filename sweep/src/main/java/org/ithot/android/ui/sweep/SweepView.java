package org.ithot.android.ui.sweep;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.ithot.android.ui.inter.BaseUIListener;

public class SweepView extends View {

    private static final String ANIMATE_PROPERTY = "progress";
    private static final int LONG_PRESS_TIMER = 500;

    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private RectF ovalRectF;
    private float progress;
    private double ovalIndent;
    private Class<? extends TimeInterpolator> interpolator;
    private int animateDuration;
    private int startAngle;
    private int sweepAngle;
    private BaseUIListener listener;
    private ObjectAnimator animator;
    private Handler handler = new Handler();
    private boolean unPerformLongClick;
    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            performLongClick();
            unPerformLongClick = false;
        }
    };

    public SweepView(Context context) {
        super(context);
        init(null);
    }

    public SweepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SweepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) {
                    listener.start(progress);
                }
                unPerformLongClick = true;
                handler.removeCallbacks(longClickRunnable);
                handler.postDelayed(longClickRunnable, LONG_PRESS_TIMER);
                supply(event);
                break;
            case MotionEvent.ACTION_MOVE:
                supply(event);
                break;
            case MotionEvent.ACTION_UP:
                supply(event);
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

    /**
     * @param event
     * @deprecated
     */
    private void supply(MotionEvent event) {
        /*
        float width = ovalRectF.width();
        float height = ovalRectF.height();
        float halfW = width / 2f;
        float halfH = height / 2f;
        float centerX = ovalRectF.centerX();
        float centerY = ovalRectF.centerY();
        float x = event.getX();
        float y = event.getY();
        double distance = Math.sqrt(Math.pow(y - centerY, 2) + Math.pow(x - centerX, 2));
        if (distance > halfW + foregroundPaint.getStrokeWidth() || distance < halfW - foregroundPaint.getStrokeWidth() * 5) {
            return;
        }
        double angle = Math.abs(Math.toDegrees(Math.atan((y - centerY) / (x - centerX))));
        if (x >= 0 && x <= halfW) {
            if (y >= 0 && y < halfH) {
                angle = 90 + angle;
            } else if (y >= halfH && y < height) {
                angle = 90 - angle;
            }
        } else if (x > halfW && x <= width) {
            if (y >= 0 && y < halfH) {
                angle = 270 - angle;
            } else if (y >= halfH && y < height) {
                angle = 270 + angle;
            }
        }
        float real = startAngle - 90;
        if (angle > real + sweepAngle || angle < real) {
            return;
        }
        setProgress(Math.round(100f * (angle - real) / sweepAngle));
         */
    }

    void init(AttributeSet attrs) {
        backgroundPaint = new Paint();
        foregroundPaint = new Paint();
        ovalRectF = new RectF();
        float strokeWidth;
        Paint.Cap cap = null;
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SweepView);
            strokeWidth = ta.getDimension(R.styleable.SweepView_sweep_strokeWidth, 12);
            startAngle = 90 + ta.getInteger(R.styleable.SweepView_sweep_startAngle, 0);
            sweepAngle = ta.getInteger(R.styleable.SweepView_sweep_sweepAngle, 360);
            backgroundPaint.setColor(ta.getColor(R.styleable.SweepView_sweep_backgroundColor, Color.GRAY));
            foregroundPaint.setColor(ta.getColor(R.styleable.SweepView_sweep_foregroundColor, Color.CYAN));
            animateDuration = ta.getInteger(R.styleable.SweepView_sweep_animateDuration, 2000);
            boolean shadow = ta.getBoolean(R.styleable.SweepView_sweep_shadowEnable, false);
            if (shadow) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                backgroundPaint.setShadowLayer(ta.getInt(R.styleable.SweepView_sweep_shadowRadius, 8), 0, 0,
                        ta.getColor(R.styleable.SweepView_sweep_shadowColor, Color.BLACK));
            } else {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            ovalIndent = Math.ceil((strokeWidth) / 2f + ta.getInt(R.styleable.SweepView_sweep_shadowRadius, 8));
            switch (ta.getInteger(R.styleable.SweepView_sweep_animateType, 0)) {
                case 0:
                    interpolator = LinearInterpolator.class;
                    break;
                case 1:
                    interpolator = AccelerateInterpolator.class;
                    break;
                case 2:
                    interpolator = DecelerateInterpolator.class;
                    break;
            }
            switch (ta.getInt(R.styleable.SweepView_sweep_strokeCap, 0)) {
                case 0:
                    cap = Paint.Cap.ROUND;
                    break;
                case 1:
                    cap = Paint.Cap.BUTT;
                    break;
                case 2:
                    cap = Paint.Cap.SQUARE;
                    break;
            }
            ta.recycle();
        } else {
            cap = Paint.Cap.ROUND;
            strokeWidth = 12;
            startAngle = 180;
            sweepAngle = 360;
            backgroundPaint.setColor(Color.GRAY);
            foregroundPaint.setColor(Color.CYAN);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ovalIndent = Math.ceil((strokeWidth) / 2f);
            interpolator = LinearInterpolator.class;
            animateDuration = 2000;
        }

        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeCap(cap);

        foregroundPaint.setAntiAlias(true);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
        foregroundPaint.setStrokeCap(cap);
    }

    public float getProgress() {
        return progress;
    }

    public void setListener(BaseUIListener listener) {
        this.listener = listener;
    }

    public void setProgress(float progress) {
        if (progress > 100f) progress = 100f;
        if (progress < 0) progress = 0;
        this.progress = progress;
        if (listener != null) {
            listener.call(this.progress);
        }
        invalidate();
    }

    public void setInterpolator(Class<? extends TimeInterpolator> interpolator) {
        this.interpolator = interpolator;
    }

    public void go(float progress, boolean animate) {
        if (this.progress == progress) return;
        if (animate) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
                animator = null;
            }
            float max = Math.max(this.progress, progress);
            float min = Math.min(this.progress, progress);
            animator = ObjectAnimator.ofFloat(this,
                    ANIMATE_PROPERTY, this.progress, progress);
            animator.setDuration((long) ((this.animateDuration * (max - min)) / 100f));
            try {
                animator.setInterpolator(interpolator.newInstance());
            } catch (Exception ignored) {
            }
            animator.start();
        } else {
            setProgress(progress);
        }
    }

    public void go(float progress) {
        go(progress, true);
    }

    public void go(float progress, float min, float max) {
        float unit = (max - min) / 100f;
        go((progress - min) / unit);
    }

    public void go(float progress, float min, float max, boolean animate) {
        float unit = (max - min) / 100f;
        go((progress - min) / unit, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(ovalRectF, startAngle, sweepAngle, false, backgroundPaint);
        canvas.drawArc(ovalRectF, startAngle, degree(), false, foregroundPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int min = Math.min(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        setMeasuredDimension(min, min);
        float left = (float) (ovalIndent + getPaddingLeft());
        float right = (float) (getMeasuredWidth() - ovalIndent - getPaddingRight());
        float top = (float) (ovalIndent + getPaddingTop());
        float bottom = (float) (getMeasuredHeight() - ovalIndent - getPaddingBottom());
        ovalRectF.set(left, top, right, bottom);
    }

    private float degree() {
        return (sweepAngle / 100f * this.progress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
