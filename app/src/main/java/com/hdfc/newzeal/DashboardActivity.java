package com.hdfc.newzeal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hdfc.adapters.CarouselPagerAdapter;
import com.hdfc.libs.Libs;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity {

    public final static int PAGES = 5;
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
    private ImageButton buttonActivity;
    private TextView txtViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        libs = new Libs(DashboardActivity.this);

        //
        pager = (ViewPager) findViewById(R.id.dpndntCarousel);

        buttonActivity = (ImageButton) findViewById(R.id.buttonCallActivity);
        txtViewActivity = (TextView) findViewById(R.id.textViewActivity);

        adapter = new CarouselPagerAdapter(this, this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(adapter);


        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(1);

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(-330); //-200
        //

        txtViewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity();
            }
        });

        buttonActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity();
            }
        });

    }

    public void goToActivity() {
        Intent selection = new Intent(DashboardActivity.this, ActivityListActivity.class);
        startActivity(selection);
        finish();
    }

    public void selectedLovedOne(View v) {
        Intent selection = new Intent(DashboardActivity.this, SignupActivity.class);
        startActivity(selection);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }

}
