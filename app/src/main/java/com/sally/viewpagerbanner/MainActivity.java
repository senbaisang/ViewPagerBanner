package com.sally.viewpagerbanner;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int BANNER_MAX_SIZE = 500;

    private ViewPager mViewPager;
    private LinearLayout mPoints;
    private List<ImageView> mPointImageViews;
    private int lastPosition;

    private int[] mImages = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.f, R.drawable.g};

    private boolean isRunning;
    private static final int RUNNING_MSG = 0x110;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RUNNING_MSG:
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                    if(isRunning) {
                        mHandler.sendEmptyMessageDelayed(RUNNING_MSG, 3000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

        isRunning = true;
        mHandler.sendEmptyMessageDelayed(RUNNING_MSG, 3000);
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_view_pager);
        mPoints = (LinearLayout) findViewById(R.id.id_points);

        mPointImageViews = new ArrayList<>();
        for(int i=0; i< mImages.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(mImages[i]);
            mPointImageViews.add(imageView);

            ImageView point = new ImageView(this);
            point.setImageDrawable(getResources().getDrawable(R.drawable.point_selector));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 20;
            point.setLayoutParams(lp);
            mPoints.addView(point);

            if(i == 0) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
            }
        }
    }


    private void initEvent() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                position = position % mPointImageViews.size();
                mPoints.getChildAt(position).setEnabled(true);
                mPoints.getChildAt(lastPosition).setEnabled(false);
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return BANNER_MAX_SIZE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = mPointImageViews.get(position % mPointImageViews.size());
                ViewParent parent = imageView.getParent();
                if(parent != null) {
                    // 在这里移除
                    ViewGroup parentGroup = (ViewGroup) parent;
                    parentGroup.removeView(imageView);
                }
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//      不要在这里移除，会报错 ：IllegalStateException
//     container.removeView((View) object);
            }

            @Override
            public void finishUpdate(ViewGroup container) {
                int currentPosition = mViewPager.getCurrentItem();
                if(currentPosition == 0) {
                    // 倒着滑
                    currentPosition = mPointImageViews.size();
                    mViewPager.setCurrentItem(currentPosition, false);
                } else if(currentPosition == BANNER_MAX_SIZE - 1) {
                    // 因为 getCount() 没有用 Integer.MaxValue, 所以，当滑动道 BANNER_MAX_SIZE的前一个值时，从新给position赋值，以防滑动不了
                    currentPosition = mPointImageViews.size() - 1;
                    mViewPager.setCurrentItem(currentPosition, false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
