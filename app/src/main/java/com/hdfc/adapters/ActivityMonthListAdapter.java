package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.models.ActivityModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityMonthListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<ActivityModel> data;

    public ActivityMonthListAdapter(Context ctxt, List<ActivityModel> y) {
        _context = ctxt;
        data = y;
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

            convertView = inflater.inflate(R.layout.activity_month_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.textViewActivity = (TextView) convertView.findViewById(R.id.textViewActivity);
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.
                    textViewDescription);
            viewHolder.imageViewPerson = (ImageView) convertView.findViewById(R.id.imageViewPerson);

            viewHolder.linearLayoutDone = (LinearLayout) convertView.findViewById(R.id.
                    linearLayoutDone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            viewHolder.textViewActivity.setText(data.get(position).getStrActivityName());

            String strTemp = data.get(position).getStrActivityDesc();

            if (strTemp.length() > 60)
                strTemp = strTemp.substring(0, 58) + "..";

            viewHolder.textViewDescription.setText(strTemp);

            viewHolder.textViewActivity.setTag(data.get(position));

            if ((data.get(position).getStrActivityStatus().equalsIgnoreCase("open"))
                    || data.get(position).getStrActivityStatus().equalsIgnoreCase("inprocess")) {
                viewHolder.linearLayoutDone.setVisibility(View.GONE);

            }

            if (data.get(position).getStrActivityStatus().equalsIgnoreCase("completed")) {
                viewHolder.linearLayoutDone.setVisibility(View.VISIBLE);
            }

            if (data.get(position).getStrActivityStatus().equalsIgnoreCase("new")) {
                viewHolder.linearLayoutDone.setVisibility(View.GONE);

            }

            try {
                int iPosition = Config.strProviderIdsAdded.indexOf(data.get(position).
                        getStrProviderID().trim());
                Glide.with(_context)
                        .load(Config.providerModels.get(iPosition).getStrImgUrl())
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(_context))
                        .placeholder(R.drawable.person_icon)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.imageViewPerson);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    public class ViewHolder {
        TextView textViewActivity;
        TextView textViewDescription;
        ImageView imageViewPerson;
        LinearLayout linearLayoutDone;
    }

}