package org.ithot.android.ui.inter;

public abstract class BaseUIListener implements IListener {

    protected abstract float transform(float value);

    public void call(float value) {
        move(transform(value));
    }

    @Override
    public void start(float value) {

    }

    @Override
    public void end(float value) {

    }
}
