package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.models.NotificationModel;
import com.hdfc.caretaker.R;
import com.hdfc.views.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Admin on 19-02-2016.
 */
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context _context;
    private ArrayList<NotificationModel> adapterNotificationModels;
    private Libs libs;

    public NotificationAdapter(Context ctxt, ArrayList d) {
        _context = ctxt;
        adapterNotificationModels = d;
        libs = new Libs(ctxt);
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

            NotificationModel notificationModel = adapterNotificationModels.get(position);

            viewHolder.textViewText.setText(notificationModel.getStrMessage());
            viewHolder.textViewName.setText(notificationModel.getStrAuthor());

            try {
                String strDate = notificationModel.getStrDateTime();
                String strDisplayDate = _context.getResources().getString(R.string.space)+
                         _context.getResources().getString(R.string.at)+
                        _context.getResources().getString(R.string.space)+
                        libs.formatDate(strDate);

                viewHolder.textViewTime.setText(strDisplayDate);
            }catch (Exception e){
                e.printStackTrace();
            }

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
