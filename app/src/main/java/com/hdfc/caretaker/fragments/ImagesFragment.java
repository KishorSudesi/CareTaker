package com.hdfc.caretaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hdfc.caretaker.R;
import com.hdfc.views.MyLinearView;

public class ImagesFragment extends Fragment {

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

        ImageView imageView = (ImageView) l.findViewById(R.id.content);

        int intPosition = this.getArguments().getInt("pos");

        try {
            /*libs.getBitmapFromFile(libs.getInternalFileImages(
                                libs.replaceSpace(Config.dependentNames.get(i))).getAbsolutePath(),
                                Config.intWidth, Config.intHeight)*/
            imageView.setImageBitmap();
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
    }
}
