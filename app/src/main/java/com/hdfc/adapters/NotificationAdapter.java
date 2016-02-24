package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.model.NotificationModel;
import com.hdfc.newzeal.R;
import com.hdfc.views.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Admin on 19-02-2016.
 */
public class NotificationAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private ArrayList<NotificationModel> adapterNotificationModels;
    private NotificationModel tempValues = null;

    public NotificationAdapter(Context ctxt, ArrayList d) {
        _context = ctxt;
        adapterNotificationModels = d;
    }

    @Override
    public int getCount() {
        return adapterNotificationModels.size();
    }

    @Override
    public Object getItem(int position) {
        return adapterNotificationModels.get(position);
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

            convertView = inflater.inflate(R.layout.notification_activity_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.textViewText = (TextView) convertView.findViewById(R.id.textViewText);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            viewHolder.roundedImageView = (RoundedImageView) convertView.findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            tempValues = adapterNotificationModels.get(position);

            viewHolder.textViewText.setText(tempValues.getStrMessage());
            viewHolder.textViewName.setText(tempValues.getStrAuthor());
            viewHolder.textViewTime.setText(tempValues.getStrDateTime());

            if (position % 2 == 0)
                viewHolder.roundedImageView.setImageResource(R.drawable.carla2);
            else
                viewHolder.roundedImageView.setImageResource(R.drawable.carla1);

        }

        return convertView;
    }

    public class ViewHolder {
        TextView textViewName;
        TextView textViewText;
        TextView textViewTime;

        RoundedImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
