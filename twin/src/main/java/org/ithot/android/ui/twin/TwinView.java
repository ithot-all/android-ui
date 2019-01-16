package org.ithot.android.ui.twin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import org.ithot.android.ui.inter.BaseUIListener;

public class TwinView extends View {

    private static final int TYPE_BACK = 0;
    private static final int TYPE_FORE = 1;
    private static final long LONG_PRESS = 500;

    private Bitmap fore;
    private Bitmap back;
    private Bitmap meta;
    private Rect src = new Rect();
    private RectF dst = new RectF();
    private Rect divSrc = new Rect();
    private RectF divDst = new RectF();
    private int width;
    private int height;
    private float progress;
    private float originProgress;
    private float per;
    private float downY;
    private int foreColor;
    private int backColor;
    private BaseUIListener listener;
    private boolean already;
    private boolean unPerformLongClick;
    private Handler handler = new Handler();

    public TwinView(Context context) {
        super(context);
        init(null);
    }

    public void setListener(BaseUIListener listener) {
        this.listener = listener;
    }

    public TwinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TwinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    void init(AttributeSet attrs) {
        if (attrs == null) {
            meta = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default);
            foreColor = Color.CYAN;
            backColor = Color.GRAY;
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TwinView);
            meta = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.TwinView_twin_layer, R.drawable.icon_default));
            foreColor = ta.getColor(R.styleable.TwinView_twin_foreColor, Color.CYAN);
            backColor = ta.getColor(R.styleable.TwinView_twin_backColor, Color.GRAY);
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        per = height / 100f;
    }

    public void setBackLayerColor(int color, boolean draw) {
        back = Bitmap.createBitmap(fore.getWidth(), fore.getHeight(), fore.getConfig());
        Canvas canvas = new Canvas(back);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(fore, 0, 0, paint);
        if (draw) {
            invalidate();
        }
    }

    private void performEvent(float ey) {
        float ratio = (downY - ey) / height * 100f;
        float result = originProgress + ratio;
        if (result < 0f) {
            result = 0f;
        } else if (result > 100f) {
            result = 100f;
        }
        run(result);
        if (null != listener) {
            listener.call(result);
        }
    }

    public void run(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void run(float progress, float min, float max) {
        float unit = (max - min) / 100f;
        this.progress = (progress - min) / unit;
        invalidate();
    }

    public void setForeColor(int color) {
        setColor(color, TYPE_FORE);
    }

    public void setBackColor(int color) {
        setColor(color, TYPE_BACK);
    }

    private void setColor(final int color, final int type) {
        if (already) {
            Bitmap base = Bitmap.createScaledBitmap(meta, width, height, true);
            Bitmap temp = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
            Canvas canvas = new Canvas(temp);
            Paint paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(base, 0, 0, paint);
            if (type == TYPE_BACK) {
                back = temp;
            } else {
                fore = temp;
            }
            invalidate();
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap base = Bitmap.createScaledBitmap(meta, width, height, true);
                    Bitmap temp = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
                    Canvas canvas = new Canvas(temp);
                    Paint paint = new Paint();
                    paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(base, 0, 0, paint);
                    if (type == TYPE_BACK) {
                        back = temp;
                    } else {
                        fore = temp;
                    }
                    invalidate();
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            unPerformLongClick = false;
            performLongClick();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return super.onTouchEvent(event);
        float ey = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = ey;
                originProgress = progress;
                unPerformLongClick = true;
                // click and long click
                handler.removeCallbacks(longClickRunnable);
                handler.postDelayed(longClickRunnable, LONG_PRESS);
                //
                if (listener != null) {
                    listener.start(progress);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                performEvent(ey);
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(longClickRunnable);
                if (Math.abs(ey - downY) < 2) {
                    if (unPerformLongClick) {
                        performClick();
                    }
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initLayer();
        already = true;
        src.set(0, 0, width, height);
        dst.set(0, 0, width, height);
        float offset = progress * per;
        divSrc.set(0, (int) (height - offset), width, height);
        divDst.set(0, height - offset, width, height);
        canvas.drawBitmap(back, src, dst, null);
        canvas.drawBitmap(fore, divSrc, divDst, null);
    }

    private void initLayer() {
        if (back == null) {
            Bitmap base = Bitmap.createScaledBitmap(meta, width, height, true);
            back = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
            Canvas canvas = new Canvas(back);
            Paint paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(backColor, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(base, 0, 0, paint);
        }
        if (fore == null) {
            Bitmap base = Bitmap.createScaledBitmap(meta, width, height, true);
            fore = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
            Canvas canvas = new Canvas(fore);
            Paint paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(foreColor, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(base, 0, 0, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (meta != null) {
            meta.recycle();
            meta = null;
        }
        if (fore != null) {
            fore.recycle();
            fore = null;
        }
        if (back != null) {
            back.recycle();
            back = null;
        }
    }
}
