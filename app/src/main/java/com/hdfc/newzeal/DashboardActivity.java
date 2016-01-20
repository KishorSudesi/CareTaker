package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hdfc.adapters.CarouselPagerAdapter;
import com.hdfc.libs.Libs;
import com.hdfc.newzeal.fragments.DashboardActivityFragment;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity {

    public final static int PAGES = 3;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f; //1.0f
    public final static float SMALL_SCALE = 0.7f; //0.7f
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public static ViewPager pager;
    public CarouselPagerAdapter adapter;
    private Libs libs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        libs = new Libs(DashboardActivity.this);

        //

        libs.dashboarMenuNavigation();

        pager = (ViewPager) findViewById(R.id.dpndntCarousel);

        adapter = new CarouselPagerAdapter(this, this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(adapter);


        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(3); //1

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(-210); //-200
        //


        DashboardActivityFragment newFragment = new DashboardActivityFragment();
        Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

    }

    public void selectedLovedOne(View v) {
        Intent selection = new Intent(DashboardActivity.this, SignupActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();

    }

}
