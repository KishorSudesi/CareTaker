package com.hdfc.newzeal;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.hdfc.libs.Libs;
import com.hdfc.newzeal.fragments.DashboardFragment;

/**
 * Created by user on 08-01-2016.
 */
public class DashboardActivity extends AppCompatActivity {

    private Libs libs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        libs = new Libs(DashboardActivity.this);

        libs.dashboarMenuNavigation();

        DashboardFragment newFragment = DashboardFragment.newInstance();
        Bundle args = new Bundle();
        //args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_dashboard, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
        finish();

    }

}
