package com.hdfc.newzeal;

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
import com.hdfc.config.NewZeal;
import com.hdfc.libs.Libs;
import com.hdfc.views.CustomViewPager;

public class SignupActivity extends FragmentActivity{

    public static ViewPager _mViewPager;
    public static long longUserId;
    private ViewPagerAdapter _adapter;
    private Button _btn1,_btn2,_btn3;
    private TextView texViewHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_steps);

        setUpView();
        setTab();
    }

    private void setUpView(){
        _mViewPager = (ViewPager) findViewById(R.id.viewPager);
        _adapter = new ViewPagerAdapter(getApplicationContext(),getSupportFragmentManager());
        _mViewPager.setAdapter(_adapter);
        _mViewPager.setCurrentItem(0);

        texViewHeader = (TextView) findViewById(R.id.header);
        initButton();
    }

    private void setTab(){
        _mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                    btnAction(position);
            }
        });
    }

    private void btnAction(int action){
        switch(action){
            case 0:
                setButton(_btn1);
                texViewHeader.setText(getString(R.string.your_details));
                break;

            case 1:
                setButton(_btn2);
                texViewHeader.setText(getString(R.string.dependants));
                break;

            case 2: setButton(_btn3);
                texViewHeader.setText(getString(R.string.confirm_details));
                break;
        }
    }
    private void initButton(){
        _btn1=(Button)findViewById(R.id.btn1);
        _btn2=(Button)findViewById(R.id.btn2);
        _btn3=(Button)findViewById(R.id.btn3);
    }

    public void setButton(Button btn){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Libs.setBtnDrawable(_btn1,getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn2,getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn3,getDrawable(R.drawable.rounded_cell_blue));

            Libs.setBtnDrawable(btn,getDrawable(R.drawable.rounded_cell));
        }else{
            Libs.setBtnDrawable(_btn1, getResources().getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn2,getResources().getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn3,getResources().getDrawable(R.drawable.rounded_cell_blue));

            Libs.setBtnDrawable(btn,getResources().getDrawable(R.drawable.rounded_cell));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //do nothing
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
    }

    public void backToSelection(View v){
        //delete Temp Users
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(SignupActivity.this);
        alertbox.setTitle(getString(R.string.app_name));
        alertbox.setMessage(getString(R.string.info_discard));//
        alertbox.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                NewZeal.dbCon.deleteTempUsers();
                Intent selection = new Intent(SignupActivity.this, CareSelectionActivity.class);
                arg0.dismiss();
                startActivity(selection);
                finish();
            }
        });
        alertbox.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertbox.show();
    }
}
