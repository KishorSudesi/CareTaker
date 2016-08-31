package com.hdfc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hdfc.caretaker.R;
import com.hdfc.caretaker.fragments.DashboardFragment;
import com.hdfc.caretaker.fragments.ImagesFragment;
import com.hdfc.caretaker.fragments.MyAccountFragment;
import com.hdfc.views.MyLinearView;

/**
 * Created by Sudesi infotech on 5/27/2016.
 */
public class DependAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearView cur = null;
    private MyLinearView next = null;
    private Context context;
    private FragmentManager fm;
    private float scale;

    public DependAdapter(Context context, FragmentManager fm) {
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
        return MyAccountFragment.PAGES * MyAccountFragment.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur = getRootView(position);
            cur.setScaleBoth(MyAccountFragment.BIG_SCALE - MyAccountFragment.DIFF_SCALE *
                    positionOffset);

            /*if(Config.intDependentsCount<=position)*/
            //Utils.log(String.valueOf(position), " position ");
            // MyAccountFragment.loadData(position);
           /* else
                DashboardFragment.loadData(0);*/

            if (position < MyAccountFragment.PAGES - 1) {
                next = getRootView(position + 1);
                next.setScaleBoth(MyAccountFragment.SMALL_SCALE + MyAccountFragment.DIFF_SCALE *
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
        return "android:switcher:" + MyAccountFragment.dependpager.getId() + ":" + position;
    }
}
