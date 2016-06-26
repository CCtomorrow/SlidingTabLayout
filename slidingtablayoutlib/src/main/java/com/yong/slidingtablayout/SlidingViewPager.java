package com.yong.slidingtablayout;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Project:</b> com.yong.slidingtablayout.widget <br>
 * <b>Create Date:</b> 2016/6/21 <br>
 * <b>Author:</b> qingyong <br>
 * <b>Address:</b> 995722643@qq.com <br>
 * <b>Description:</b> CustomViewPager <br>
 * 解决两个问题：
 * 1.第一次不调用OnPageChangeListener
 * 2.可以设置不允许滑动
 */
public class SlidingViewPager extends ViewPager {

    private List<OnPageChangeListener> mListeners;

    private boolean isFirstLayout = false;

    private boolean enableScroll = true;

    public SlidingViewPager(Context context) {
        super(context);
    }

    public SlidingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!enableScroll) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!enableScroll) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        super.addOnPageChangeListener(onPageChangeListener);
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(onPageChangeListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (isFirstLayout && null != mListeners && !mListeners.isEmpty()) {
            for (int i = 0, z = mListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onPageSelected(getCurrentItem());
                }
            }
        }
        isFirstLayout = false;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        isFirstLayout = true;
    }

    public boolean enableScrollable() {
        return enableScroll;
    }

    public void setEnableScrollable(boolean isScrollable) {
        this.enableScroll = isScrollable;
    }

}
