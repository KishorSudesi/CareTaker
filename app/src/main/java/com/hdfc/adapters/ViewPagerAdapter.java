package com.hdfc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hdfc.newzeal.fragments.AddDependantFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;
import com.hdfc.newzeal.fragments.GuruDetailsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static int totalPage = 3;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = GuruDetailsFragment.newInstance();
                break;
            case 1:
                f = AddDependantFragment.newInstance();
                break;
            case 2:
                f = ConfirmFragment.newInstance();
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return totalPage;
    }


}