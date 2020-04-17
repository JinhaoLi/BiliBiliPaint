package com.jil.paintf.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CanScrollView extends HorizontalScrollView {
    public CanScrollView(Context context) {
        super(context);
    }

    public CanScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *  在自定义控件中如下重写onInterceptTouchEvent就告诉所有父View：不要拦截事件，让我消费！！
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }
}
