package com.hdfc.caretaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.adapters.ViewPagerAdapter;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;
import com.hdfc.views.CustomViewPager;

import java.util.ArrayList;


public class SignupActivity extends FragmentActivity {

    public static ViewPager _mViewPager;
    //public static String strUserId = "";
    //public static String strCustomerName = "", strCustomerEmail = "", strCustomerContactNo = "",
    // strCustomerAddress = "", strCustomerImg = "", strCustomerPass = "";
    public static String strCustomerPass = "";
    public static ArrayList<DependentModel> dependentModels = new ArrayList<>();
    //public static ArrayList<CustomerModel> customerModels = new ArrayList<>();
    public static ArrayList<String> dependentNames = new ArrayList<>();
    //private static Utils utils;
    private Button _btn1, _btn2, _btn3;
    private TextView texViewHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_steps);
        setUpView();
        setTab();
    }

    private void setUpView() {
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter _adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);
        _mViewPager.setOffscreenPageLimit(3);

        texViewHeader = (TextView) findViewById(R.id.header);
        initButton();
    }

    private void setTab() {
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                btnAction(position);
            }
        });
    }

    private void btnAction(int action) {
        switch (action) {
            case 0:
                setButton(_btn1);
                texViewHeader.setText(getString(R.string.your_details));
                break;

            case 1:
                setButton(_btn2);
                texViewHeader.setText(getString(R.string.dependents));
                break;

            case 2:
                setButton(_btn3);
                texViewHeader.setText(getString(R.string.confirm_details));
                break;
        }
    }

    private void initButton() {
        _btn1 = (Button) findViewById(R.id.btn1);
        _btn2 = (Button) findViewById(R.id.btn2);
        _btn3 = (Button) findViewById(R.id.btn3);
    }

    public void setButton(Button btn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Utils.setDrawable(_btn1, getDrawable(R.drawable.rounded_cell_blue));
            Utils.setDrawable(_btn2, getDrawable(R.drawable.rounded_cell_blue));
            Utils.setDrawable(_btn3, getDrawable(R.drawable.rounded_cell_blue));

            Utils.setDrawable(btn, getDrawable(R.drawable.rounded_cell));
        } else {
            Utils.setDrawable(_btn1, getResources().getDrawable(R.drawable.rounded_cell_blue));
            Utils.setDrawable(_btn2, getResources().getDrawable(R.drawable.rounded_cell_blue));
            Utils.setDrawable(_btn3, getResources().getDrawable(R.drawable.rounded_cell_blue));

            Utils.setDrawable(btn, getResources().getDrawable(R.drawable.rounded_cell));
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //do nothing
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setButton(_btn1);

        boolean listDependant = getIntent().getBooleanExtra("LIST_DEPENDANT", false);

        CustomViewPager.setPagingEnabled(listDependant);

        if (listDependant) {
            _mViewPager.setCurrentItem(1);
            btnAction(1);
        } else btnAction(0);

        if (dependentModels.size() > 0)
            CustomViewPager.setPagingEnabled(true);

        getIntent().putExtra("LIST_DEPENDANT", false);
    }

    public void backToSelection(View v) {
        goBack();
    }

    public void goBack() {
        //delete Temp Users
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(SignupActivity.this);
        alertbox.setTitle(getString(R.string.app_code_name));
        alertbox.setMessage(getString(R.string.info_discard));
        alertbox.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    Intent selection = new Intent(SignupActivity.this, MainActivity.class);
                    arg0.dismiss();

                    //customerModels = new ArrayList<>();
                    dependentModels = new ArrayList<>();
                    dependentNames = new ArrayList<>();
                    DependentDetailPersonalActivity.dependentModel = null;

                    startActivity(selection);
                    finish();
            } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertbox.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
