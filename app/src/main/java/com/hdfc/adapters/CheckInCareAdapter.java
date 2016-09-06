package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.CheckInCareModel;

import java.text.DateFormatSymbols;
import java.util.List;

/**
 * Created by Admin on 7/16/2016.
 */
public class CheckInCareAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public Context _context;
    public List<CheckInCareModel> data;
    //private Utils utils;

    public CheckInCareAdapter(Context context, List<CheckInCareModel> d) {
        _context = context;
        data = d;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (inflater == null)
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.check_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//+ "-" + data.get(position).getStrStatus() this text to be added later
        viewHolder.name.setText(getMonth(data.get(position).getStrMonth()) + "-" + data.get(position).getStrName());

        return convertView;
    }

    public String getMonth(String month) {
        int intMonth = 0;
        try {
            intMonth = Integer.parseInt(month);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            intMonth = 1;
        }
        return new DateFormatSymbols().getMonths()[intMonth - 1];
    }

    public class ViewHolder {
        TextView name;

    }

}
