package org.ithot.android.ui.inter;

public abstract class RangeUIListener extends BaseUIListener {

    private float max;
    private float min;
    private float sum = 100f;

    protected RangeUIListener(float min, float max) {
        this.min = min;
        this.max = max;
    }

    protected RangeUIListener(float min, float max, float sum) {
        this.min = min;
        this.max = max;
        this.sum = sum;
    }


    /**
     * Transformation algorithm @see https://blog.csdn.net/Touch_Dream/article/details/62076236
     * @param value
     * @return
     */
    @Override
    protected float transform(float value) {
        return (int) ((max - min) / sum * value + min);
    }
}
