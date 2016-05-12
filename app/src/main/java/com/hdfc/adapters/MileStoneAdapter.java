package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.FieldModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2/23/2016.
 */
public class MileStoneAdapter extends BaseExpandableListAdapter {

    private static LayoutInflater inflater = null;
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<FieldModel>> _listDataChild;

    public MileStoneAdapter(Context context, HashMap<String, List<FieldModel>>
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

        final FieldModel fieldModel = (FieldModel) getChild(groupPosition, childPosition);

        final ViewHolder viewHolder;
        if (inflater == null) {
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mile_stone_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewLabel = (TextView) convertView.findViewById(R.id.textViewLabel);
            viewHolder.textViewValue = (TextView) convertView.findViewById(R.id.textViewValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewLabel.setText(fieldModel.getStrFieldLabel());
        viewHolder.textViewValue.setText(fieldModel.getStrFieldData());

        return convertView;
    }

    public class ViewHolder {
        TextView textViewLabel, textViewValue;
    }
}
