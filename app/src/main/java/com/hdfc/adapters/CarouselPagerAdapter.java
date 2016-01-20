package com.hdfc.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.hdfc.newzeal.DashboardActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.fragments.ImagesFragment;
import com.hdfc.views.MyLinearView;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearView cur = null;
    private MyLinearView next = null;
    private Context context;
    private FragmentManager fm;
    private float scale;

    public CarouselPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == DashboardActivity.FIRST_PAGE)
            scale = DashboardActivity.BIG_SCALE;
        else
            scale = DashboardActivity.SMALL_SCALE;

        position = position % DashboardActivity.PAGES;

        Log.e("CarouselPagerAdapter", String.valueOf(position));
        return ImagesFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {
        return DashboardActivity.PAGES * DashboardActivity.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur = getRootView(position);
            cur.setScaleBoth(DashboardActivity.BIG_SCALE - DashboardActivity.DIFF_SCALE * positionOffset);


            Log.e("CarouselPagerAdapter 1 ", String.valueOf(position + " ~ " + positionOffset));

            if (position < DashboardActivity.PAGES - 1) {
                next = getRootView(position + 1);
                next.setScaleBoth(DashboardActivity.SMALL_SCALE + DashboardActivity.DIFF_SCALE * positionOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    next.setBackground(context.getResources().getDrawable(R.drawable.rounded_image_view));
                } else
                    next.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_image_view));
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private MyLinearView getRootView(int position) {
        return (MyLinearView)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + DashboardActivity.pager.getId() + ":" + position;
    }
}
