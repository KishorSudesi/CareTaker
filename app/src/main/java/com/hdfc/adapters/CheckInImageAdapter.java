package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hdfc.caretaker.R;
import com.hdfc.models.ImageModelCheck;

import java.util.List;

/**
 * Created by Admin on 7/18/2016.
 */
public class CheckInImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImageModelCheck> data;
    private LayoutInflater inflater;
    private int height = 240, width = 240;

    // Constructor
    public CheckInImageAdapter(Context c, List<ImageModelCheck> list) {
        mContext = c;
        data = list;
        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.check_in_grid_row_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // imageView.setImageResource(Integer.parseInt(data.get(position).getStrImageUrl()));
        if (data.size() > 0) {
            Glide.with(mContext)
                    .load(data.get(position).getStrImageUrl())
                    .asBitmap()
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    // .transform(new CropCircleTransformation(mContext))
                    .placeholder(R.drawable.person_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(width, height)
                    .into(viewHolder.imageView);
        }
        return convertView;
    }

    public class ViewHolder {
        public ProgressBar progressBar;
        public ImageView imageView;
    }
}
