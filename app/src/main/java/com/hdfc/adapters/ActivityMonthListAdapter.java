package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

import java.io.File;
import java.util.List;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityMonthListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public MultiBitmapLoader multiBitmapLoader;
    private Context _context;
    private List<ActivityModel> data;
    private Utils utils;

    public ActivityMonthListAdapter(Context ctxt, List<ActivityModel> y) {
        _context = ctxt;
        data = y;
        multiBitmapLoader = new MultiBitmapLoader(ctxt);
        utils = new Utils(ctxt);
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
            viewHolder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            viewHolder.imageViewPerson = (ImageView) convertView.findViewById(R.id.imageViewPerson);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            // ActivityModel activityModel = data.get(position);

            viewHolder.textViewActivity.setText(data.get(position).getStrActivityName());

            String strTemp = data.get(position).getStrActivityDesc();

            if (strTemp.length() > 60)
                strTemp = strTemp.substring(0, 58) + "..";

            viewHolder.textViewDescription.setText(strTemp);

            viewHolder.textViewActivity.setTag(data.get(position));

            //multiBitmapLoader.loadBitmap(activityModel.getStrDependentName(), viewHolder.imageViewPerson);

            try {

                File f = utils.getInternalFileImages(utils.replaceSpace(data.get(position).getStrDependentID().trim()));

                if(f.exists())
                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.imageViewPerson);

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
    }

}