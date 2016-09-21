package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.FeedBackModel;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 2/22/2016.
 */
public class RatingCompletedAdapter extends BaseAdapter {

    private static final int[] intImageIds = new int[]{R.drawable.smiley_3, R.drawable.smiley_1,
            R.drawable.smiley_2, R.drawable.smiley_4, R.drawable.smiley_5};
    private static LayoutInflater inflater = null;
    private static Utils utils;
    private Context _ctx;
    //private static MultiBitmapLoader multiBitmapLoader;
    private List<FeedBackModel> data = new ArrayList<>();

    public RatingCompletedAdapter(Context context, List<FeedBackModel> x) {
        _ctx = context;
        data = x;
        utils = new Utils(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (inflater == null)
            inflater = (LayoutInflater) _ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.rating_completed_activity_item, null);
            viewHolder = new ViewHolder();

            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.dateTimeRating);
            viewHolder.personName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.feedBack);
            viewHolder.personImage = (ImageView) convertView.findViewById(R.id.personImage);
            viewHolder.smiley = (ImageView) convertView.findViewById(R.id.smileyImage);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            try {

                String strTimeStamp = _ctx.getString(R.string.at);

                try {
                    strTimeStamp += " " + utils.formatDate(data.get(position).getStrFeedBackTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    strTimeStamp += " " + data.get(position).getStrFeedBackTime();
                }

                viewHolder.dateTime.setText(strTimeStamp);

                String strType = data.get(position).getStrFeedBackByType();
                String strName = "";
                String strUrl = "";

                if (strType.equalsIgnoreCase("customer") && Config.customerModel != null) {
                    strName = Config.customerModel.getStrName();
                    strUrl = Config.customerModel.getStrImgUrl();
                }

                if (strType.equalsIgnoreCase("dependent")) {
                    int iPosition = Config.strDependentIds.indexOf(data.get(position).
                            getStrFeedBackBy());
                    strName = Config.dependentModels.get(iPosition).getStrName();
                    strUrl = Config.dependentModels.get(iPosition).getStrImageUrl();
                }

                String strAuthor = _ctx.getString(R.string.by) + strName;

                viewHolder.personName.setText(strAuthor);

                try {

                    Glide.with(_ctx)
                            .load(strUrl)
                            .asBitmap()
                            .centerCrop()
                            .override(Config.intWidth, Config.intHeight)
                            .transform(new CropCircleTransformation(_ctx))
                            .placeholder(R.drawable.person_icon)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource,
                                                            GlideAnimation<? super Bitmap>
                                                                    glideAnimation) {
                                    viewHolder.personImage.setImageBitmap(resource);
                                }
                            });

                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }

                viewHolder.feedback.setText(data.get(position).getStrFeedBackMessage());

                if (data.get(position).getBoolFeedBackReport())
                    viewHolder.feedback.setTextColor(Color.RED);
                else
                    viewHolder.feedback.setTextColor(Color.BLACK);

                viewHolder.smiley.setImageResource(intImageIds[data.get(position).
                        getIntFeedBackRating()
                        - 1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    public class ViewHolder {
        //public LinearLayout linearLayoutRoot;
        ImageView personImage;
        ImageView smiley;
        TextView feedback;
        TextView personName;
        TextView dateTime;
    }
}
