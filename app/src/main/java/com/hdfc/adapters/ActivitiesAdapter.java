package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ProviderModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by balamurugan@adstringo.in on 2/17/2016.
 */
public class ActivitiesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private MultiBitmapLoader multiBitmapLoader;
    private Context _context;
    private ArrayList data;
    private Utils utils;

    public ActivitiesAdapter(Context context, ArrayList d) {
        _context = context;
        data = d;
        utils = new Utils(context);
        multiBitmapLoader = new MultiBitmapLoader(context);
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
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.dashboard_activity_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewUpcoming = (TextView) convertView.findViewById(R.id.textViewUpcoming);
            viewHolder.textViewText = (TextView) convertView.findViewById(R.id.textViewText);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            viewHolder.roundedImageView = (ImageView) convertView.findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Utils.log(String.valueOf(data.size()), " SIZE ");

        if (data.size() > 0) {

            ActivityModel activityModel = (ActivityModel) data.get(position);

            //Date date = utils.convertStringToDate(activityModel.getStrActivityDate());
            // new Date();//

            //Date dateNow = new Date();

            ProviderModel providerModel = null;

           /* for (int i = 0; i < Config.strProviderIds.size(); i++) {
                Utils.log(Config.strProviderIds.get(i), " NAME ");
            }*/

            if (Config.strProviderIds.contains(activityModel.getStrProviderID())) {
                providerModel = Config.providerModels.get(Config.strProviderIds.
                        indexOf(activityModel.getStrProviderID()));
            }

            try {
                if (activityModel.getStrActivityStatus().equalsIgnoreCase("new") ||
                        activityModel.getStrActivityStatus().equalsIgnoreCase("open")
                        || activityModel.getStrActivityStatus().equalsIgnoreCase("inprocess")) {

                    String strDisplayDate = _context.getResources().getString(R.string.space) +
                            _context.getResources().getString(R.string.at) +
                            _context.getResources().getString(R.string.space) +
                            utils.formatDate(activityModel.getStrActivityDate());

                    viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                            getColor(R.color.colorPrimary));
                    viewHolder.textViewTime.setTextColor(_context.getResources().
                            getColor(R.color.colorWhite));

                    viewHolder.textViewUpcoming.setVisibility(View.VISIBLE);
                    viewHolder.textViewUpcoming.setText(_context.getString(R.string.up_next));
                    String strTemp = providerModel.getStrName() + " " + strDisplayDate;
                    viewHolder.textViewTime.setText(strTemp);

                    viewHolder.textViewText.setText(activityModel.getStrActivityName());
                } else {

                    String strDisplayDate = _context.getResources().getString(R.string.space) +
                            _context.getResources().getString(R.string.at) +
                            _context.getResources().getString(R.string.space) +
                            utils.formatDate(activityModel.getStrActivityDoneDate());

                    Utils.setDrawable(viewHolder.linearLayout, _context.getResources().
                            getDrawable(R.drawable.header_gradient));
                    viewHolder.textViewTime.setTextColor(_context.getResources().
                            getColor(R.color.colorWhite));

                    viewHolder.textViewUpcoming.setVisibility(View.GONE);

                    String strTemp = providerModel.getStrName() + " " + strDisplayDate;
                    viewHolder.textViewTime.setText(strTemp);

                    viewHolder.textViewText.setText(activityModel.getStrActivityName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*if (!activityModel.getStrActivityDesc().equalsIgnoreCase(""))
                viewHolder.textViewText.setText(activityModel.getStrActivityName());
            else
                viewHolder.textViewText.setText(activityModel.getStrActivityName());*/


            try {

//                File f = utils.getInternalFileImages(utils.replaceSpace(
//                        activityModel.getStrProviderID()));
//
//                if (f.exists())
//                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);

                int iPosition = Config.strProviderIds.indexOf(activityModel.getStrProviderID());
                Glide.with(_context)
                        .load(Config.providerModels.get(iPosition).getStrImgUrl())
                        .asBitmap()
                        .centerCrop()
                        .transform(new CropCircleTransformation(_context))
                        .placeholder(R.drawable.person_icon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                viewHolder.roundedImageView.setImageBitmap(resource);
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }

    public class ViewHolder {
        TextView textViewUpcoming;
        TextView textViewText;
        TextView textViewTime;

        ImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
