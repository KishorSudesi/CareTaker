package com.hdfc.newzeal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hdfc.adapters.ViewPagerAdapter;
import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.DependentModel;
import com.hdfc.views.CustomViewPager;
import com.hdfc.views.RoundedImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class SignupActivity extends FragmentActivity {

    public static ViewPager _mViewPager;
    public static String strUserId;
    public static String strCustomerName = "", strCustomerEmail = "", strCustomerContactNo = "", strCustomerAddress = "", strCustomerImg = "", strCustomerPass = "";
    public static LruCache<String, Bitmap> mMemoryCache;
    public static ArrayList<DependentModel> dependentModels = new ArrayList<>();
    public static ArrayList<String> dependentNames = new ArrayList<>();
    private static Libs libs;
    private Button _btn1, _btn2, _btn3;
    private TextView texViewHeader;

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Log.e("addBitmapToMemoryCache", key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        Log.e("getBitmapFromMemCache", key);
        return mMemoryCache.get(key);
    }

    public static void loadBitmap(String strPath, RoundedImageView imgView) {
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            if (cancelPotentialWork(strPath, imgView)) {
                Log.e("loadBitmap", strPath);
                final Bitmap bitmap = getBitmapFromMemCache(strPath);
                if (bitmap != null) {
                    imgView.setImageBitmap(bitmap);
                } else {
                    Log.e("loadBitmap", "1");
                    imgView.setImageResource(R.drawable.person_icon);
                    BitmapWorkerTask task = new BitmapWorkerTask(imgView);
                    task.execute(strPath);
                }
            }
        }
    }

    public static boolean cancelPotentialWork(String data, RoundedImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        Log.e("cancelPotentialWork", "0");
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData.equalsIgnoreCase("") || bitmapData != data) {
                // Cancel previous task
                Log.e("cancelPotentialWork", "1");
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                Log.e("cancelPotentialWork", "2");
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(RoundedImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            Log.e("BitmapWorkerTask", "0");
            if (drawable instanceof AsyncDrawable) {
                Log.e("BitmapWorkerTask", "1");
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_steps);
        libs = new Libs(SignupActivity.this);
        setUpView();
        setTab();

        //
        final int maxMemory = libs.getMemory();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        //
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

    ////

    private void initButton() {
        _btn1 = (Button) findViewById(R.id.btn1);
        _btn2 = (Button) findViewById(R.id.btn2);
        _btn3 = (Button) findViewById(R.id.btn3);
    }

    public void setButton(Button btn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Libs.setBtnDrawable(_btn1, getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn2, getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn3, getDrawable(R.drawable.rounded_cell_blue));

            Libs.setBtnDrawable(btn, getDrawable(R.drawable.rounded_cell));
        } else {
            Libs.setBtnDrawable(_btn1, getResources().getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn2, getResources().getDrawable(R.drawable.rounded_cell_blue));
            Libs.setBtnDrawable(_btn3, getResources().getDrawable(R.drawable.rounded_cell_blue));

            Libs.setBtnDrawable(btn, getResources().getDrawable(R.drawable.rounded_cell));
        }
    }

    ///

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
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

    public void backToSelection(View v) {
        //delete Temp Users
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(SignupActivity.this);
        alertbox.setTitle(getString(R.string.app_name));
        alertbox.setMessage(getString(R.string.info_discard));//
        alertbox.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    //NewZeal.dbCon.deleteTempUsers();
                    Intent selection = new Intent(SignupActivity.this, CareSelectionActivity.class);
                    arg0.dismiss();

                    SignupActivity.strCustomerName = "";
                    SignupActivity.strCustomerEmail = "";
                    SignupActivity.strCustomerContactNo = "";
                    SignupActivity.strCustomerAddress = "";
                    SignupActivity.strCustomerImg = "";
                    SignupActivity.strUserId = "";

                    dependentModels = new ArrayList<>();
                    dependentNames = new ArrayList<>();
                    DependentDetailPersonalActivity.dependentModel = null;

                    startActivity(selection);
                    finish();
                } catch (Exception e) {
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
    ////

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<RoundedImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(RoundedImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<RoundedImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Log.e("doInBackground", data);
            Bitmap tempBitmap = libs.getBitmapFromFile(data, Config.intWidth, Config.intHeight);
            addBitmapToMemoryCache(String.valueOf(params[0]), tempBitmap);
            return tempBitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                Log.e("onPostExecute", "1");
                final RoundedImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    Log.e("onPostExecute", "2");
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}
