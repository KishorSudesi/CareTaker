package com.hdfc.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hdfc.caretaker.fragments.AddDependentFragment;
import com.hdfc.caretaker.fragments.ConfirmFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = AddDependentFragment.newInstance();
                break;
            case 1:
                f = ConfirmFragment.newInstance();

                break;

        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }
}