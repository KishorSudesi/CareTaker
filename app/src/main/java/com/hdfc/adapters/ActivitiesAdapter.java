package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.model.ActivitiesModel;
import com.hdfc.newzeal.R;
import com.hdfc.views.RoundedImageView;

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

    public ActivitiesAdapter(Context context, ArrayList d) {
        _context = context;
        data = d;
        libs = new Libs(context);
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
            viewHolder.roundedImageView = (RoundedImageView) convertView.findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            ActivitiesModel activitiesModel = (ActivitiesModel) data.get(position);

            Date date = libs.convertStringToDate(activitiesModel.getStrDateTime()); //new Date();//

            Date dateNow = new Date();

            if (position % 2 == 0) {
                libs.setDrawable(viewHolder.linearLayout, _context.getResources().getDrawable(R.drawable.header_gradient));
                viewHolder.textViewTime.setTextColor(_context.getResources().getColor(R.color.colorWhite));
                viewHolder.roundedImageView.setImageResource(R.drawable.carla2);
            } else {
                viewHolder.linearLayout.setBackgroundColor(_context.getResources().getColor(R.color.colorWhite));
                viewHolder.textViewTime.setTextColor(_context.getResources().getColor(R.color.colorAccentDark));
                viewHolder.roundedImageView.setImageResource(R.drawable.carla1);
            }

            if (!dateNow.after(date) && position == 0) {
                viewHolder.textViewUpcoming.setVisibility(View.VISIBLE);
                viewHolder.textViewUpcoming.setText("UP Next");
                viewHolder.textViewTime.setText(activitiesModel.getStrAuthor() + " " + activitiesModel.getStrDateTime());
            } else {
                viewHolder.textViewUpcoming.setVisibility(View.GONE);
                viewHolder.textViewTime.setText(activitiesModel.getStrDateTime());
            }

            if (!activitiesModel.getStrActivityFeedback().equalsIgnoreCase(""))
                viewHolder.textViewText.setText(activitiesModel.getStrActivityFeedback());
            else
                viewHolder.textViewText.setText(activitiesModel.getStrActivityName());

            try {

                if (!activitiesModel.getStrCarlaImagePath().equalsIgnoreCase("")) {
                    //SignupActivity.loadBitmap(activitiesModel.getStrCarlaImagePath().trim(), viewHolder.roundedImageView);
                }

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

        RoundedImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
