package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.ServiceModel;

import java.util.List;

/**
 * Created by Admin on 2/23/2016.
 */
public class AdditionalServicesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<ServiceModel> data;

    public AdditionalServicesAdapter(Context context, List<ServiceModel> r) {
        _context = context;
        data = r;
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
        if (inflater == null) {
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.additional_services_item, null);
            viewHolder = new ViewHolder();
            viewHolder.activityTitle = (TextView) convertView.findViewById(R.id.txtActivityTitle);
            viewHolder.activityDetails = (TextView) convertView.findViewById(R.id.txtActivityDetails);
            viewHolder.checkBoxService = (CheckBox) convertView.findViewById(R.id.checkBoxService);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            String strTemp = data.get(position).getStrServiceName() + "-" +
                    String.valueOf(data.get(position).getDoubleCost()) + "(" +
                    String.valueOf(data.get(position).getiUnit() + ")");

            viewHolder.activityTitle.setText(strTemp);
            //viewHolder.activityDetails.setText(data.get(position).getStrServiceDesc());

            viewHolder.checkBoxService.setTag(data.get(position));
        }
        return convertView;
    }

    public class ViewHolder {
        TextView activityTitle;
        TextView activityDetails;
        CheckBox checkBoxService;
    }
}
