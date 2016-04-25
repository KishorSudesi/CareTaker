package com.hdfc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hdfc.caretaker.R;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.ImagesFragment;
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
        /*if (position == DashboardFragment.FIRST_PAGE)*/
            scale = DashboardFragment.BIG_SCALE;
       /* else
            scale = DashboardFragment.SMALL_SCALE;
*/
        //position = position % DashboardFragment.PAGES;

        //Log.e("CarouselPagerAdapter", String.valueOf(position));
        return ImagesFragment.newInstance(context, position, scale);
    }

    @Override
    public int getCount() {
        return DashboardFragment.PAGES * DashboardFragment.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur = getRootView(position);
            cur.setScaleBoth(DashboardFragment.BIG_SCALE - DashboardFragment.DIFF_SCALE *
                    positionOffset);

            /*if(Config.intDependentsCount<=position)*/
            //Utils.log(String.valueOf(position), " position ");
            DashboardFragment.loadData(position);
           /* else
                DashboardFragment.loadData(0);*/

            if (position < DashboardFragment.PAGES - 1) {
                next = getRootView(position + 1);
                next.setScaleBoth(DashboardFragment.SMALL_SCALE + DashboardFragment.DIFF_SCALE *
                        positionOffset);
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

        //Utils.log(String.valueOf(fm.findFragmentByTag(this.getFragmentTag(position))+ " : " + position), " NPE ");

        return (MyLinearView)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        //Utils.log(String.valueOf("android:switcher:" + DashboardFragment.pager.getId() + ":" + position), " NPE 0 ");
        return "android:switcher:" + DashboardFragment.pager.getId() + ":" + position;
    }
}
