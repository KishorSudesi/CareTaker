package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.ServiceModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2/23/2016.
 */
public class AdditionalServicesAdapter extends BaseExpandableListAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<ServiceModel>> _listDataChild;

    public AdditionalServicesAdapter(Context context, HashMap<String, List<ServiceModel>>
            listChildData, List<String> listDataHeader) {
        _context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_service_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ServiceModel serviceModel = (ServiceModel) getChild(groupPosition, childPosition);

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

        String strTemp = serviceModel.getStrServiceName() + "-" +
                String.valueOf(serviceModel.getDoubleCost()) + "(" +
                String.valueOf(serviceModel.getiUnit() + ")");

        viewHolder.activityTitle.setText(strTemp);
        viewHolder.activityDetails.setText(serviceModel.getStrServiceName());

        viewHolder.checkBoxService.setTag(serviceModel);


        return convertView;
    }

    public class ViewHolder {
        TextView activityTitle;
        TextView activityDetails;
        CheckBox checkBoxService;
    }
}
