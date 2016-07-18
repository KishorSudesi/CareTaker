package com.hdfc.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hdfc.caretaker.R;
import com.hdfc.models.ImageModelCheck;

import java.util.List;

/**
 * Created by Admin on 7/18/2016.
 */
public class CheckInImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImageModelCheck> data;

    // Constructor
    public CheckInImageAdapter(Context c, List<ImageModelCheck> list) {
        mContext = c;
        data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        // imageView.setImageResource(Integer.parseInt(data.get(position).getStrImageUrl()));
        if (data.size() > 0) {
            Glide.with(mContext)
                    .load(data.get(position).getStrImageUrl())
                    .asBitmap()
                    .centerCrop()
                    // .transform(new CropCircleTransformation(mContext))
                    .placeholder(R.drawable.person_icon)
                    .into(imageView);
        }
        return imageView;
    }
}
