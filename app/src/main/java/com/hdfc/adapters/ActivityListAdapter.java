package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.ActivityModel;

import java.util.List;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<ActivityModel> data;

    public ActivityListAdapter(Context ctxt, List<ActivityModel> y) {
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

            convertView = inflater.inflate(R.layout.activity_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.dateNumber = (TextView) convertView.findViewById(R.id.textView41);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textView31);
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.txtDateTime);

            viewHolder.Message = (TextView) convertView.findViewById(R.id.textViewkop);
            viewHolder.person = (TextView) convertView.findViewById(R.id.txtPerson);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(
                    R.id.completedActivityOne);

            viewHolder.linearLayoutDone = (LinearLayout) convertView.findViewById(
                    R.id.linearLayoutDone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            ActivityModel activityListModel = data.get(position);

            if (activityListModel.getStrActivityStatus().equalsIgnoreCase("upcoming")) {
                viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                        getColor(R.color.colorWhite));
                viewHolder.linearLayoutDone.setVisibility(View.GONE);

            } else {
                viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                        getColor(R.color.colorPrimary));
                viewHolder.linearLayoutDone.setVisibility(View.VISIBLE);
            }

            viewHolder.dateNumber.setText(activityListModel.getStrActivityDate());
            viewHolder.date.setText(activityListModel.getStrActivityDate());

            String strTimeStamp = activityListModel.getStrActivityDate();

            viewHolder.dateTime.setText(strTimeStamp);

            viewHolder.Message.setText(activityListModel.getStrActivityDesc());

            String strAuthor = activityListModel.getStrActivityName() + _context.getResources().getString(R.string.space) + _context.getResources().getString(R.string.at) + _context.getResources().getString(R.string.space);

            viewHolder.person.setText(strAuthor);
            // viewHolder.Message.setText(data.get(i).getStrMessage());
            // viewHolder.dateTime.setText(data.get(i).getStrDateTime());

        }

        return convertView;

    }

    public class ViewHolder {
        TextView dateNumber;
        TextView date;
        TextView Message;
        TextView dateTime;
        TextView person;
        LinearLayout linearLayout;
        LinearLayout linearLayoutDone;
    }

}