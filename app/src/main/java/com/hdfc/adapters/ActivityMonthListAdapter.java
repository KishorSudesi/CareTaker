package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.libs.Libs;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.ActivityListModel;

import java.io.File;
import java.util.List;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityMonthListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public MultiBitmapLoader multiBitmapLoader;
    private Context _context;
    private List<ActivityListModel> data;
    private Libs libs;

    public ActivityMonthListAdapter(Context ctxt, List<ActivityListModel> y) {
        _context = ctxt;
        data = y;
        multiBitmapLoader = new MultiBitmapLoader(ctxt);
        libs = new Libs(ctxt);
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

            ActivityListModel activityListModel = data.get(position);

            viewHolder.textViewActivity.setText(activityListModel.getStrMessage());
            viewHolder.textViewDescription.setText(activityListModel.getStrDesc());

            multiBitmapLoader.loadBitmap(activityListModel.getStrDependentName(), viewHolder.imageViewPerson);

            try {

                File f = libs.getInternalFileImages(libs.replaceSpace(activityListModel.getStrDependentName()));

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