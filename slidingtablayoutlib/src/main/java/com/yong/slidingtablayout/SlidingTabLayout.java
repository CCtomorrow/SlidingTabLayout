/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yong.slidingtablayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p/>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p/>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p/>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
public class SlidingTabLayout extends HorizontalScrollView {

    /**
     * 调用者自定义的Tab的Layout
     */
    protected int mTabViewLayoutId = 0;
    /**
     * 调用者自定义的Tab的Layout里面的TextView的资源ID
     */
    protected int mTabViewTextViewId = 0;

    /**
     * tab较多的时候，在滑动的时候如果前面一个tab看不见了，给一个这样的距离，让第一个tab还有这个距离可见
     */
    protected int mTabTitleOffset = 0;

    /**
     * 在Tab不能占满全屏的时候是否平分，默认false
     */
    protected boolean mTabDistributeColEvenly = false;

    /**
     * 是否平滑的滑动，默认true
     */
    protected boolean mTabViewPagerSmoothScroll = true;

    /**
     * 文字的大小颜色
     */
    protected int mTabTextTypeface = 0;
    protected int mTabTextSize = 12;
    protected ColorStateList mTabTextColor = ColorStateList.valueOf(Color.BLACK);

    /**
     * TabTopBorderColor和高度
     */
    protected int mTabTopBorderColor;
    protected int mTabTopBorderHeight = 0;

    /**
     * TabBottomBorderColor和高度
     */
    protected int mTabBottomBorderColor;
    protected int mTabBottomBorderHeight = 0;

    /**
     * Top TabIndicatorColor和高度
     */
    protected int mTabTopIndicatorColor = Color.BLUE;
    protected int mTabTopIndicatorHeight = 0;

    /**
     * TabIndicatorColor距离两边的一个距离
     */
    protected int mTabIndicatorPadding = 0;

    /**
     * Bottom TabIndicatorColor和高度
     */
    protected int mTabBottomIndicatorColor = Color.BLUE;
    protected int mTabBottomIndicatorHeight = 3;

    /**
     * Text Indicator颜色高度和把文字包裹之后的富裕的padding
     */
    protected int mTabTextIndicatorColor = Color.DKGRAY;
    protected int mTabTextIndicatorStroke = 0;
    protected int mTabTextIndicatorPaddding = 3;

    /**
     * TabDividerColor和宽度
     */
    protected int mTabDividerColor = Color.DKGRAY;
    protected int mTabDividerStroke = 0;
    protected float mTabDividerHeightRatio = 0.5F;

    /**
     * tab里面TextView的padding
     */
    protected int mTabTextPadding = 8;
    protected int mTabTextPaddingLeft = mTabTextPadding;
    protected int mTabTextPaddingTop = mTabTextPadding;
    protected int mTabTextPaddingRight = mTabTextPadding;
    protected int mTabTextPaddingBottom = mTabTextPadding;

    /**
     * ViewPager
     */
    protected ViewPager mViewPager;

    /**
     * TextView的ContentDescriptions
     */
    protected SparseArray<String> mContentDescriptions = new SparseArray<>();

    /**
     * 监听，使用SlidingTabLayout了
     * 使用
     * {@link #addOnPageChangeListener(ViewPager.OnPageChangeListener)}
     * 替换
     * {@link ViewPager#addOnPageChangeListener(ViewPager.OnPageChangeListener)}
     */
    protected List<ViewPager.OnPageChangeListener> mViewPagerPageChangeListeners = new ArrayList<>();

    /**
     * SlidingTabLayout 里面的LinerLayout，这里放置TextView，以及画一些其他的东西
     */
    protected final SlidingTabStrip mTabStrip;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 不需要 Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);
        // style初始化
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YSlidingTabLayout, defStyle, 0);
        initStyle(a);
        a.recycle();

        mTabStrip = new SlidingTabStrip(context);
        // 添加LinerLayout
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    protected void initStyle(TypedArray a) {
        // 最后一个参数可以设置一个style，为这些属性设置默认值，这个参数就是style的名字
        // 倒数第二个参数它是一个引用类型的属性，指向一个style，并且在当前的theme中进行设置
        // 优先级defStyleAttr高，也就是第三个参数高

        mTabTitleOffset = a.getDimensionPixelOffset(R.styleable.YSlidingTabLayout_tab_title_offset,
                dp2px(mTabTitleOffset));
        mTabDistributeColEvenly = a.getBoolean(R.styleable.YSlidingTabLayout_tab_distribut_col_evenly,
                mTabDistributeColEvenly);
        mTabViewPagerSmoothScroll = a.getBoolean(R.styleable.YSlidingTabLayout_tab_view_pager_smooth_scroll,
                mTabViewPagerSmoothScroll);

        // TextView
        mTabTextTypeface = a.getInt(R.styleable.YSlidingTabLayout_tab_text_type_face, 0);
        mTabTextSize = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_size,
                sp2px(mTabTextSize));
        mTabTextColor = a.hasValue(R.styleable.YSlidingTabLayout_tab_text_color) ?
                a.getColorStateList(R.styleable.YSlidingTabLayout_tab_text_color) : mTabTextColor;

        // Border
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data;
        mTabTopBorderColor = a.getColor(R.styleable.YSlidingTabLayout_tab_top_border_color,
                themeForegroundColor);
        mTabTopBorderHeight = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_top_border_height,
                dp2px(mTabTopBorderHeight));
        mTabBottomBorderColor = a.getColor(R.styleable.YSlidingTabLayout_tab_bottom_border_color,
                themeForegroundColor);
        mTabBottomBorderHeight = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_bottom_border_height,
                dp2px(mTabBottomBorderHeight));

        // Indicator
        mTabTopIndicatorColor = a.getColor(R.styleable.YSlidingTabLayout_tab_top_indicator_color,
                mTabTopIndicatorColor);
        mTabTopIndicatorHeight = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_top_indicator_height,
                dp2px(mTabTopIndicatorHeight));
        mTabIndicatorPadding = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_indicator_padding,
                dp2px(mTabIndicatorPadding));
        mTabBottomIndicatorColor = a.getColor(R.styleable.YSlidingTabLayout_tab_bottom_indicator_color,
                mTabBottomIndicatorColor);
        mTabBottomIndicatorHeight = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_bottom_indicator_height,
                dp2px(mTabBottomIndicatorHeight));

        // TextView的Indicator
        mTabTextIndicatorColor = a.getColor(R.styleable.YSlidingTabLayout_tab_text_indicator_color,
                mTabTextIndicatorColor);
        mTabTextIndicatorStroke = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_indicator_stroke,
                dp2px(mTabTextIndicatorStroke));
        mTabTextIndicatorPaddding = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_indicator_padding,
                dp2px(mTabTextIndicatorPaddding));

        // Divider
        mTabDividerColor = a.getColor(R.styleable.YSlidingTabLayout_tab_divider_color,
                mTabDividerColor);
        mTabDividerStroke = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_divider_stroke,
                dp2px(mTabDividerStroke));
        mTabDividerHeightRatio = a.getFloat(R.styleable.YSlidingTabLayout_tab_divider_heiht_ratio, mTabDividerHeightRatio);

        // padding
        mTabTextPadding = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_padding,
                dp2px(mTabTextPadding));
        mTabTextPaddingLeft = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_padding_left,
                mTabTextPadding);
        mTabTextPaddingTop = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_padding_top,
                mTabTextPadding);
        mTabTextPaddingRight = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_padding_right,
                mTabTextPadding);
        mTabTextPaddingBottom = a.getDimensionPixelSize(R.styleable.YSlidingTabLayout_tab_text_padding_bottom,
                mTabTextPadding);
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
        Typeface typeface;
        if (mTabTextTypeface == 1) {
            typeface = Typeface.DEFAULT_BOLD;
        } else if (mTabTextTypeface == 2) {
            typeface = Typeface.SANS_SERIF;
        } else if (mTabTextTypeface == 3) {
            typeface = Typeface.SERIF;
        } else if (mTabTextTypeface == 4) {
            typeface = Typeface.MONOSPACE;
        } else {
            typeface = Typeface.DEFAULT;
        }
        textView.setTypeface(typeface);
        textView.setTextColor(mTabTextColor);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }*/

        textView.setPadding(mTabTextPaddingLeft, mTabTextPaddingTop,
                mTabTextPaddingRight, mTabTextPaddingBottom);

        return textView;
    }

    /**
     * populate tabs
     */
    protected void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0 && mTabViewTextViewId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId,
                        mTabStrip, false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            // tab没有占满全屏的时候如果为true会平分
            if (mTabDistributeColEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            if (tabTitleView != null) tabTitleView.setText(adapter.getPageTitle(i));

            tabView.setOnClickListener(tabClickListener);

            String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            }

            mTabStrip.addView(tabView);

            if (!SlidingViewPager.class.isInstance(mViewPager)) {
                if (i == mViewPager.getCurrentItem()) {
                    tabView.setSelected(true);
                }
            }

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!SlidingViewPager.class.isInstance(mViewPager)) {
            if (mViewPager != null) {
                scrollToTab(mViewPager.getCurrentItem(), 0);
            }
        }
    }

    /**
     * 滑动到对应位置
     *
     * @param tabIndex       tabIndex
     * @param positionOffset 应该偏移的距离
     */
    protected void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTabTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    public class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        protected int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null) ? (int) (positionOffset * selectedTitle.getWidth()) : 0;
            scrollToTab(position, extraOffset);

            for (int i = 0, z = mViewPagerPageChangeListeners.size(); i < z; i++) {
                ViewPager.OnPageChangeListener listener = mViewPagerPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            for (int i = 0, z = mViewPagerPageChangeListeners.size(); i < z; i++) {
                ViewPager.OnPageChangeListener listener = mViewPagerPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);
            }

            for (int i = 0, z = mViewPagerPageChangeListeners.size(); i < z; i++) {
                ViewPager.OnPageChangeListener listener = mViewPagerPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }
        }

    }

    /**
     * Tab的点击切换
     */
    protected class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i, mTabViewPagerSmoothScroll);
                    return;
                }
            }
        }
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

    }

    public class SlidingTabStrip extends LinearLayout {

        private final Paint mRectPaint;
        private final Paint mLinePaint;

        private int mSelectedPosition;

        private float mSelectionOffset;

        private SlidingTabLayout.TabColorizer mCustomTabColorizer;

        private final SimpleTabColorizer mDefaultTabColorizer;

        SlidingTabStrip(Context context) {
            this(context, null);
        }

        SlidingTabStrip(Context context, AttributeSet attrs) {
            super(context, attrs);
            setWillNotDraw(false);

            mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            mDefaultTabColorizer = new SimpleTabColorizer();
            mDefaultTabColorizer.setIndicatorColors(mTabBottomIndicatorColor);

        }

        void setCustomTabColorizer(SlidingTabLayout.TabColorizer customTabColorizer) {
            mCustomTabColorizer = customTabColorizer;
            invalidate();
        }

        void setSelectedIndicatorColors(int... colors) {
            // Make sure that the custom colorizer is removed
            mCustomTabColorizer = null;
            mDefaultTabColorizer.setIndicatorColors(colors);
            invalidate();
        }

        void onViewPagerPageChanged(int position, float positionOffset) {
            mSelectedPosition = position;
            mSelectionOffset = positionOffset;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final int height = getHeight();
            final int childCount = getChildCount();

            final SlidingTabLayout.TabColorizer tabColorizer =
                    mCustomTabColorizer != null
                            ? mCustomTabColorizer
                            : mDefaultTabColorizer;

            // 顶部Bordor
            if (mTabTopBorderHeight > 0) {
                mRectPaint.setColor(mTabTopBorderColor);
                // 顶部border
                canvas.drawRect(0, 0, getWidth(), mTabTopBorderHeight,
                        mRectPaint);
            }
            // 底部border
            if (mTabBottomBorderHeight > 0) {
                mRectPaint.setColor(mTabBottomBorderColor);
                canvas.drawRect(0, height - mTabBottomBorderHeight,
                        getWidth(), height, mRectPaint);
            }

            // TabIndicator
            // Thick colored underline below the current selection
            if (childCount > 0) {
                View selectedTitleView = getChildAt(mSelectedPosition);
                int left = selectedTitleView.getLeft();
                int right = selectedTitleView.getRight();
                int color = tabColorizer.getIndicatorColor(mSelectedPosition);

                if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                    int nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1);
                    if (color != nextColor) {
                        color = blendColors(nextColor, color, mSelectionOffset);
                    }

                    // Draw the selection partway between the tabs
                    View nextTitleView = getChildAt(mSelectedPosition + 1);
                    left = (int) (mSelectionOffset * nextTitleView.getLeft() +
                            (1.0F - mSelectionOffset) * left);
                    right = (int) (mSelectionOffset * nextTitleView.getRight() +
                            (1.0F - mSelectionOffset) * right);
                }

                if (mTabTopIndicatorHeight > 0) {
                    mRectPaint.setColor(mTabTopIndicatorColor);
                    // 顶部indicator
                    canvas.drawRect(left + mTabIndicatorPadding,
                            0,
                            right - mTabIndicatorPadding,
                            mTabTopIndicatorHeight,
                            mRectPaint);
                }

                if (mTabBottomIndicatorHeight > 0) {
                    mRectPaint.setColor(color);
                    // 底部indicator
                    canvas.drawRect(left + mTabIndicatorPadding,
                            height - mTabBottomIndicatorHeight,
                            right - mTabIndicatorPadding,
                            height,
                            mRectPaint);
                }

                if (mTabTextIndicatorStroke > 0 && TextView.class.isInstance(selectedTitleView)) {
                    TextView titleTextView = (TextView) selectedTitleView;
                    TextPaint paint = titleTextView.getPaint();
                    Paint.FontMetrics metrics = paint.getFontMetrics();
                    int textHeight = (int) (metrics.bottom - metrics.top);
                    int textWidth = (int) paint.measureText(titleTextView.getText().toString());

                    // 文字的顶部的Indicator
                    mLinePaint.setStrokeWidth(mTabTextIndicatorStroke);
                    mLinePaint.setColor(mTabTextIndicatorColor);
                    canvas.drawLine(left + selectedTitleView.getWidth() / 2 - textWidth / 2 - mTabTextIndicatorPaddding,
                            getHeight() / 2 - textHeight / 2 - mTabTextIndicatorPaddding,
                            left + selectedTitleView.getWidth() / 2 + textWidth / 2 + mTabTextIndicatorPaddding,
                            getHeight() / 2 - textHeight / 2 - mTabTextIndicatorPaddding,
                            mLinePaint
                    );

                    // 文字的底部的Indicator
                    canvas.drawLine(left + selectedTitleView.getWidth() / 2 - textWidth / 2 - mTabTextIndicatorPaddding,
                            getHeight() / 2 + textHeight / 2 + mTabTextIndicatorPaddding,
                            left + selectedTitleView.getWidth() / 2 + textWidth / 2 + mTabTextIndicatorPaddding,
                            getHeight() / 2 + textHeight / 2 + mTabTextIndicatorPaddding,
                            mLinePaint
                    );

                }
            }

            // 分割线
            if (mTabDividerStroke > 0) {
                final int dividerHeightPx = (int) (Math.min(Math.max(0F, mTabDividerHeightRatio), 1F) * height);
                // Vertical separators between the titles
                int separatorTop = (height - dividerHeightPx) / 2;
                for (int i = 0; i < childCount - 1; i++) {
                    View child = getChildAt(i);
                    mLinePaint.setStrokeWidth(mTabDividerStroke);
                    mLinePaint.setColor(mTabDividerColor);
                    canvas.drawLine(child.getRight(), separatorTop, child.getRight(),
                            separatorTop + dividerHeightPx, mLinePaint);
                }
            }

        }

        /**
         * Blend {@code color1} and {@code color2} using the given ratio.
         *
         * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
         *              0.0 will return {@code color2}.
         */
        private int blendColors(int color1, int color2, float ratio) {
            final float inverseRation = 1f - ratio;
            float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
            float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
            float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
            return Color.rgb((int) r, (int) g, (int) b);
        }

        /**
         * Tab的和IndicatorColors和DividerColors
         */
        public class SimpleTabColorizer implements SlidingTabLayout.TabColorizer {

            private int[] mIndicatorColors;

            @Override
            public final int getIndicatorColor(int position) {
                return mIndicatorColors[position % mIndicatorColors.length];
            }

            void setIndicatorColors(int... colors) {
                mIndicatorColors = colors;
            }

        }

    }

    /**
     * 设置在tab没有占满屏幕的时候可以平分
     *
     * @param tabDistributeColEvenly true，平分，false，不平分
     */
    public void setTabDistributeColEvenly(boolean tabDistributeColEvenly) {
        mTabDistributeColEvenly = tabDistributeColEvenly;
    }

    /**
     * 设置在ViewPager滑动的时候是否平滑的滑动
     *
     * @param tabViewPagerSmoothScroll true，平滑滑动，false，直接选择tab
     */
    public void setTabViewPagerSmoothScroll(boolean tabViewPagerSmoothScroll) {
        mTabViewPagerSmoothScroll = tabViewPagerSmoothScroll;
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     * <p/>
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)}
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#addOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (listener == null) return;
        mViewPagerPageChangeListeners.add(listener);
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    /**
     * Tab TextView的ContentDescriptions
     *
     * @param i    tab位置
     * @param desc 描述
     */
    public void setContentDescription(int i, String desc) {
        mContentDescriptions.put(i, desc);
    }

    private DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, displayMetrics);
    }

    public int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, displayMetrics);
    }

    public float px2dp(float pxVal) {
        final float scale = displayMetrics.density;
        return (pxVal / scale);
    }

    public float px2sp(float pxVal) {
        return (pxVal / displayMetrics.scaledDensity);
    }

}
