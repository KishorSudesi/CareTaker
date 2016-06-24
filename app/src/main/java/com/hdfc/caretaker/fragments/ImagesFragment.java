package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.MyLinearView;
import com.hdfc.views.RoundedImageView;

public class ImagesFragment extends Fragment {

    private static Utils utils;
    private static Handler threadHandler;
    private Bitmap bitmap;
    private int intPosition;
    private RoundedImageView imageView;
    private RelativeLayout loadingPanel;

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

        loadingPanel = (RelativeLayout) l.findViewById(R.id.loadingPanel);

        imageView = (RoundedImageView) l.findViewById(R.id.content);

        utils = new Utils(getActivity());

        intPosition = this.getArguments().getInt("pos");

        try {

            Utils.log(" 3 ", " IN ");

            loadingPanel.setVisibility(View.VISIBLE);

            bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                    utils.replaceSpace(Config.dependentModels.get(intPosition).getStrDependentID())).getAbsolutePath(),
                    Config.intWidth, Config.intHeight);

            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageBitmap(Utils.noBitmap);

            loadingPanel.setVisibility(View.GONE);

          /*  threadHandler = new ThreadHandler();
            Thread backgroundThread = new BackgroundThread();
            backgroundThread.start();*/

        } catch (Exception e) {
            loadingPanel.setVisibility(View.GONE);
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
        Utils.log(" 4 ", " IN ");

    }

 /*   public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            loadingPanel.setVisibility(View.GONE);
            Utils.log(" 5 ", " IN ");
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageBitmap(Utils.noBitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(intPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);
                Utils.log(Config.dependentModels.get(intPosition).getStrDependentID() + " ~ " +
                        Config.dependentModels.get(intPosition).getStrName(), " Pah ");
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }*/
}
