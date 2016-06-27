package com.hdfc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.DependentDetailPersonal;
import com.hdfc.caretaker.DependentDetailPersonalActivity;
import com.hdfc.caretaker.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.DependentModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Admin on 27-06-2016.
 */
public class DependentListViewAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    public MultiBitmapLoader multiBitmapLoader;
    private Context contxt;
    private ArrayList data;
    private Utils utils;

    public DependentListViewAdapter(Context ctxt, ArrayList d) {
        contxt = ctxt;
        data = d;
        inflater = (LayoutInflater) contxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        multiBitmapLoader = new MultiBitmapLoader(contxt);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        DependentModel editDependentModel = (DependentModel) data.get(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.dependent_list_item, null);

            holder = new ViewHolder();
            holder.linearlayoutview = (LinearLayout)vi.findViewById(R.id.linearlayoutClient);
            holder.textName = (TextView) vi.findViewById(R.id.txtViewName);
            holder.textRelation = (TextView) vi.findViewById(R.id.txtViewRelation);
            holder.image = (ImageView) vi.findViewById(R.id.imgdependent);
            holder.address = (TextView) vi.findViewById(R.id.txtViewAdd);
            holder.age = (TextView)vi.findViewById(R.id.txtViewClient_age);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.image.setTag(editDependentModel);

        String strName = editDependentModel.getStrName();

        if (strName != null && strName.length() > 20)
            strName = editDependentModel.getStrName().substring(0, 18) + "..";

        String strAddress = editDependentModel.getStrAddress();

        if (strAddress != null && strAddress.length() > 20)
            strAddress = editDependentModel.getStrAddress().substring(0, 18) + "..";

        holder.textName.setText(strName);
        holder.address.setText(strAddress);
        holder.age.setText(editDependentModel.getStrAge());
        holder.textRelation.setText(editDependentModel.getStrRelation());

        //File fileImage = new File(editDependentModel.getStrImagePath());
        utils = new Utils(contxt);
        File fileImage = utils.getInternalFileImages(editDependentModel.getStrDependentID());

        if (fileImage.exists()) {
            String filename = fileImage.getAbsolutePath();
            multiBitmapLoader.loadBitmap(filename, holder.image);
        } else {
            holder.image.setImageDrawable(contxt.getResources().getDrawable(R.drawable.person_icon));
        }


/*
        holder.linearlayoutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("editflag",true);
                bundle.putInt("childposition",position);
                Intent intent = new Intent(contxt, DependentDetailPersonal.class);
                intent.putExtras(bundle);
                contxt.startActivity(intent);
            }
        });
*/

        return vi;
    }

    public static class ViewHolder {
        public TextView textName;
        public TextView textRelation;
        public ImageView image;
        public TextView address;
        public TextView age;
        public LinearLayout linearlayoutview;
    }
}
