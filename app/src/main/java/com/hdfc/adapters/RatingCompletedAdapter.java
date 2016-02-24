package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.model.RatingCompletedModel;
import com.hdfc.newzeal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2/22/2016.
 */
public class RatingCompletedAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context _ctx;
    List<RatingCompletedModel> data = new ArrayList<RatingCompletedModel>();

    public RatingCompletedAdapter(Context context, List<RatingCompletedModel> x) {
        _ctx = context;
        data = x;
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
            viewHolder.personName = (TextView) convertView.findViewById(R.id.personName);
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.feedBack);
            viewHolder.personImage = (ImageView) convertView.findViewById(R.id.personImage);
            viewHolder.smiley = (ImageView) convertView.findViewById(R.id.smileyImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dateTime.setText(data.get(position).getTimeDate());
        viewHolder.feedback.setText(data.get(position).getFeedback());
        viewHolder.personName.setText(data.get(position).getPersonName());
        viewHolder.personImage.setImageResource(data.get(position).getImg());
        viewHolder.smiley.setImageResource(data.get(position).getRatingSmiley());
        return convertView;
    }

    public class ViewHolder {
        ImageView personImage;
        ImageView smiley;
        TextView feedback;
        TextView personName;
        TextView dateTime;
    }
}
