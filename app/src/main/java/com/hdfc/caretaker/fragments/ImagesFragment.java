package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.views.MyLinearView;

public class ImagesFragment extends Fragment {

    private static Bitmap bitmap;
    private static Utils utils;
    private static int intPosition;
    private static Handler threadHandler;
    private static ImageView imageView;
    private static RelativeLayout loadingPanel;

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

        imageView = (ImageView) l.findViewById(R.id.content);

        utils = new Utils(getActivity());

        intPosition = this.getArguments().getInt("pos");

        try {

            loadingPanel.setVisibility(View.VISIBLE);

            threadHandler = new ThreadHandler();
            Thread backgroundThread = new BackgroundThread();
            backgroundThread.start();

        } catch (Exception e) {
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
    }

    public static class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Utils.log("IN", "1");
            loadingPanel.setVisibility(View.GONE);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

               /* bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentNames.get(intPosition))).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);
*/
                bitmap = utils.getBitmapFromFile(utils.getInternalFileImages(
                        utils.replaceSpace(Config.dependentModels.get(intPosition).getStrDependentID())).getAbsolutePath(),
                        Config.intWidth, Config.intHeight);
                Utils.log("IN", "0");
                threadHandler.sendEmptyMessage(0);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }
}
