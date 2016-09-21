package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ConfirmListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<CustomerModel> _listDataHeader;
    private HashMap<CustomerModel, List<DependentModel>> _listDataChild;
    private MultiBitmapLoader multiBitmapLoader;

    public ConfirmListAdapter(Context context, List<CustomerModel> listDataHeader,
                              HashMap<CustomerModel, List<DependentModel>> listChildData) {
        this._context = context;
        multiBitmapLoader = new MultiBitmapLoader(_context);
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
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

        final DependentModel confirmDependentModel = (DependentModel) getChild(groupPosition,
                childPosition);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_dependents, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.client = (ImageView) convertView.findViewById(R.id.imageClients);
            viewHolder.age = (TextView) convertView.findViewById(R.id.textViewClient_age);
            viewHolder.relation = (TextView) convertView.findViewById(R.id.textViewRealtion);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.client.setTag(confirmDependentModel);

        String strName = confirmDependentModel.getStrName();

        if (strName != null && strName.length() > 20)
            strName = confirmDependentModel.getStrName().substring(0, 18) + "..";

        String strAddress = confirmDependentModel.getStrAddress();

        if (strAddress != null && strAddress.length() > 20)
            strAddress = confirmDependentModel.getStrAddress().substring(0, 18) + "..";

        viewHolder.name.setText(strName);
        viewHolder.address.setText(strAddress);
        viewHolder.age.setText(confirmDependentModel.getStrAge());
        viewHolder.relation.setText(confirmDependentModel.getStrRelation());

        File fileImage = new File(confirmDependentModel.getStrImagePath());
        //  File fileImage = Utils.createFileInternal("images/" + "rushikesh_img");

        if (fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.client);
        } else {
            viewHolder.client.setImageDrawable(_context.getResources().getDrawable(
                    R.drawable.person_icon));
        }

        viewHolder.client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("editflag", true);
                bundle.putInt("childposition", childPosition);
                Intent intent = new Intent(_context, DependentDetailPersonalActivity.class);
                intent.putExtras(bundle);
                _context.startActivity(intent);
            }
        });

        return convertView;
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

        ExpandableListView expandableListView = (ExpandableListView) parent;
        expandableListView.expandGroup(groupPosition);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_customers, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.address = (TextView) convertView.findViewById(R.id.textViewAddress);
            viewHolder.contact = (TextView) convertView.findViewById(R.id.textViewContact);
            viewHolder.customer = (ImageView) convertView.findViewById(R.id.imageClients);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String strName = Config.customerModel.getStrName();

        if (strName != null && strName.length() > 20)
            strName = Config.customerModel.getStrName().substring(0, 18) + "..";

        viewHolder.name.setText(strName);
        viewHolder.contact.setText(Config.customerModel.getStrContacts());
        viewHolder.address.setText(Config.customerModel.getStrAddress());

        File fileImage = new File(Config.customerModel.getStrImgPath());

        if (fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, viewHolder.customer);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewHolder {
        TextView name, address, contact, age, relation;
        ImageView client, customer;
    }
}
