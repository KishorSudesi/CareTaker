package com.hdfc.newzeal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hdfc.config.Config;
import com.hdfc.newzeal.R;
import com.hdfc.views.MyLinearView;

public class ImagesFragment extends Fragment {

    //private static Bitmap bitmap;

    public static Fragment newInstance(Context context, int pos,
                                       float scale) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);

        return Fragment.instantiate(context, ImagesFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.fragment_images, container, false);

        // progressDialog = new ProgressDialog(getActivity());

        //libs = new Libs(getActivity());

        ImageView imageView = (ImageView) l.findViewById(R.id.content);

        int intPosition = this.getArguments().getInt("pos");

        try {
            imageView.setImageBitmap(Config.bitmaps.get(intPosition));
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }


        MyLinearView root = (MyLinearView) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }

    @Override
    public void onResume() {
        super.onResume();

       /* threadHandler = new ThreadHandler();
        backgroundThread = new BackgroundThread();
        backgroundThread.start();

        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
    }

    /*public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();

            if (bitmap != null)
                imageView.setImageBitmap(bitmap);

            //loadingPanel.setVisibility(View.GONE);
        }
    }*/

    /*public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                Libs.log(Config.dependentNames.get(intPosition), " FILE ");

                File f = libs.getInternalFileImages(Config.dependentNames.get(intPosition));

                bitmap = libs.getBitmapFromFile(f.getAbsolutePath(), Config.intWidth, Config.intHeight);

                threadHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
}
