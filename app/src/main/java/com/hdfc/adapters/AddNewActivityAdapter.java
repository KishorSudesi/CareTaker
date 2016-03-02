package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hdfc.model.DependentServiceModel;
import com.hdfc.newzeal.R;

import java.util.List;

/**
 * Created by Admin on 2/20/2016.
 */
public class AddNewActivityAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<DependentServiceModel> data;

    public AddNewActivityAdapter(Context context, List<DependentServiceModel> p) {
        _context = context;
        data = p;
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
            convertView = inflater.inflate(R.layout.add_new_item, null);
            viewHolder = new ViewHolder();
            viewHolder.activityTitle = (TextView) convertView.findViewById(R.id.activityTitle);
            viewHolder.activityDetails = (TextView) convertView.findViewById(R.id.activityDetails);
            viewHolder.checkBoxService = (CheckBox) convertView.findViewById(R.id.checkBoxService);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {
            viewHolder.activityTitle.setText(data.get(position).getStrDependantServiceName() + " " +
                    (data.get(position).getIntDependantServiceUnit() - data.get(position).getIntDependantServiceUnitUsed()) + " Left");
            viewHolder.activityDetails.setText(data.get(position).getStrDependantServiceFeatures());

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
