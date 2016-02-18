package com.hdfc.newzeal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hdfc.newzeal.R;
import com.hdfc.views.MyLinearView;

public class ImagesFragment extends Fragment {

    public static Fragment newInstance(Context context, int pos,
                                       float scale) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);

        //
       /* imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp);
        roundedBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);*/

        //

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

        int pos = this.getArguments().getInt("pos");
        Log.e("ERER", String.valueOf(pos));
        //ImageView tv = (ImageView) l.findViewById(R.id.content);

        /*roundedBitmapDrawable.setCornerRadius(40.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        tv.setImageDrawable(roundedBitmapDrawable);*/

        MyLinearView root = (MyLinearView) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);

        return l;
    }
}
