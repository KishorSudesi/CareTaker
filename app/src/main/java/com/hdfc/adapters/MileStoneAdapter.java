package com.hdfc.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.FieldModel;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private Dialog dialog;

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

        String strArrayData = null;

        JSONObject jsonObject = null;

        if (fieldModel.getStrArrayData() != null && !fieldModel.getStrArrayData().equalsIgnoreCase("")) {
            strArrayData = fieldModel.getStrArrayData();
            viewHolder.textViewValue.setText(_context.getString(R.string.click_here));

            try {
                jsonObject = new JSONObject(strArrayData);
            } catch (Exception e) {
                jsonObject = null;
                e.printStackTrace();
            }
        }

        viewHolder.textViewValue.setTag(jsonObject);
        viewHolder.textViewLabel.setTag(jsonObject);

        viewHolder.textViewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject s = (JSONObject) v.getTag();
                showArrayData(s);
            }
        });

        viewHolder.textViewValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject s = (JSONObject) v.getTag();
                showArrayData(s);
            }
        });

        return convertView;
    }

    private void showArrayData(JSONObject jsonObject) {

        try {
            if (jsonObject != null) {

                JSONArray jsonArray = jsonObject.getJSONArray("array_data");

                View view = ((Activity) _context).getLayoutInflater().inflate(R.layout.dialog_view, null, false);

                final LinearLayout layoutDialog = (LinearLayout) view.findViewById(R.id.linearLayoutDialog);
                final TextView textView = (TextView) view.findViewById(R.id.milestoneName);

                textView.setText(_context.getString(R.string.medicines));

                Button buttonDone = (Button) view.findViewById(R.id.buttonDone);

                LinearLayout linearLayoutArrayInner1 = new LinearLayout(_context);
                linearLayoutArrayInner1.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutArrayInnerParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutArrayInnerParams1.setMargins(10, 10, 10, 10);
                linearLayoutArrayInner1.setLayoutParams(layoutArrayInnerParams1);

                TextView textViewName1 = new TextView(_context);
                textViewName1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                textViewName1.setText(_context.getString(R.string.medicine_name));
                textViewName1.setTextAppearance(_context, R.style.boldtext);
                linearLayoutArrayInner1.addView(textViewName1);

                TextView textViewQty1 = new TextView(_context);
                textViewQty1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                textViewQty1.setText(_context.getString(R.string.quantity));
                textViewQty1.setTextAppearance(_context, R.style.boldtext);
                linearLayoutArrayInner1.addView(textViewQty1);

                layoutDialog.addView(linearLayoutArrayInner1);

                for (int j = 0; j < jsonArray.length(); j++) {

                    LinearLayout linearLayoutArrayInner = new LinearLayout(_context);
                    linearLayoutArrayInner.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams layoutArrayInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutArrayInnerParams.setMargins(10, 10, 10, 10);
                    linearLayoutArrayInner.setLayoutParams(layoutArrayInnerParams);

                    JSONObject jsonObjectInner = jsonArray.getJSONObject(j);

                    TextView textViewName = new TextView(_context);
                    textViewName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    textViewName.setText(jsonObjectInner.getString("medicine_name"));
                    linearLayoutArrayInner.addView(textViewName);

                    TextView textViewQty = new TextView(_context);
                    textViewQty.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    textViewQty.setText(String.valueOf(jsonObjectInner.getInt("medicine_qty")));
                    linearLayoutArrayInner.addView(textViewQty);

                    layoutDialog.addView(linearLayoutArrayInner);
                }


                buttonDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                builder.setView(view);
                dialog = builder.create();

                dialog.show();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class ViewHolder {
        TextView textViewLabel, textViewValue;
    }
}
