package com.yong.slidingtablayout.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yong.slidingtablayout.SlidingTabLayout;


public class SampleFragment extends Fragment {

    static final String LOG_TAG = "SampleFragment";

    private SlidingTabLayout mSlidingTabLayout;
    private SlidingTabLayout mSlidingTabLayout2;
    private SlidingTabLayout mSlidingTabLayout3;
    private SlidingTabLayout mSlidingTabLayout4;
    private SlidingTabLayout mSlidingTabLayout5;
    private SlidingTabLayout mSlidingTabLayout6;

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout2 = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs2);
        mSlidingTabLayout3 = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs3);
        mSlidingTabLayout4 = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs4);
        mSlidingTabLayout5 = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs5);
        mSlidingTabLayout6 = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs6);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout2.setViewPager(mViewPager);
        mSlidingTabLayout3.setViewPager(mViewPager);
        mSlidingTabLayout4.setViewPager(mViewPager);
        mSlidingTabLayout5.setViewPager(mViewPager);
        mSlidingTabLayout6.setViewPager(mViewPager);
        mSlidingTabLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(LOG_TAG, "第" + position + "页被选中");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(2);
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position + 1));
            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }
}
