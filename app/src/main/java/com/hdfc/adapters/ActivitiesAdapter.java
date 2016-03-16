package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.ActivitiesModel;
import com.hdfc.caretaker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by balamurugan@adstringo.in on 2/17/2016.
 */
public class ActivitiesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private ArrayList data;
    private Libs libs;
    public MultiBitmapLoader multiBitmapLoader;

    public ActivitiesAdapter(Context context, ArrayList d) {
        _context = context;
        data = d;
        libs = new Libs(context);
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

        if (data.size() > 0) {

            ActivitiesModel activitiesModel = (ActivitiesModel) data.get(position);

            Date date = libs.convertStringToDate(activitiesModel.getStrDateTime()); //new Date();//

            Date dateNow = new Date();


            String strDisplayDate = _context.getResources().getString(R.string.space)+
                    _context.getResources().getString(R.string.at)+
                    _context.getResources().getString(R.string.space)+
                    libs.formatDate(activitiesModel.getStrDateTime());

            if (position % 2 == 0) {
                libs.setDrawable(viewHolder.linearLayout, _context.getResources().getDrawable(R.drawable.header_gradient));
                viewHolder.textViewTime.setTextColor(_context.getResources().getColor(R.color.colorWhite));
                //viewHolder.roundedImageView.setImageResource(R.drawable.carla2);
            } else {
                viewHolder.linearLayout.setBackgroundColor(_context.getResources().getColor(R.color.colorWhite));
                viewHolder.textViewTime.setTextColor(_context.getResources().getColor(R.color.colorAccentDark));
                //viewHolder.roundedImageView.setImageResource(R.drawable.carla1);
            }

            try {
                if (!dateNow.after(date) && position == 0) {//
                    viewHolder.textViewUpcoming.setVisibility(View.VISIBLE);
                    viewHolder.textViewUpcoming.setText("UP Next");//TODO use strings.xml
                    String strTemp=activitiesModel.getStrAuthor() + " " + strDisplayDate;
                    viewHolder.textViewTime.setText(strTemp);
                } else {
                    viewHolder.textViewUpcoming.setVisibility(View.GONE);

                    String strTemp=activitiesModel.getStrAuthor() + " " + strDisplayDate;
                    viewHolder.textViewTime.setText(strTemp);

                    //viewHolder.textViewTime.setText(activitiesModel.getStrDateTime());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (!activitiesModel.getStrActivityFeedback().equalsIgnoreCase(""))
                viewHolder.textViewText.setText(activitiesModel.getStrActivityFeedback());
            else
                viewHolder.textViewText.setText(activitiesModel.getStrActivityName());

            try {

                File f = libs.getInternalFileImages(libs.replaceSpace(activitiesModel.getStrAuthor()));

                if(f.exists())
                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);

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
