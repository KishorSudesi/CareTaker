package com.hdfc.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.newzeal.R;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class ConfirmListViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private static Libs libs;
    public Resources res;
    private Context _ctxt;
    private ArrayList data;
    private ConfirmViewModel tempValues = null;

    private Bitmap imageBitmap = null;
    private RoundedBitmapDrawable roundedBitmapDrawable = null;

    public ConfirmListViewAdapter(Context ctxt, ArrayList d, Resources resLocal) { //
        _ctxt = ctxt;
        data = d;
        res = resLocal;
        inflater = (LayoutInflater) _ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        libs = new Libs(_ctxt);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.confirm_list, null);

            holder = new ViewHolder();

            holder.textName = (TextView) vi.findViewById(R.id.textName);
            holder.textDesc = (TextView) vi.findViewById(R.id.textDesc);
            holder.textAddress = (TextView) vi.findViewById(R.id.textAddress);
            holder.textContact = (TextView) vi.findViewById(R.id.textContacNo);
            holder.textEmail = (TextView) vi.findViewById(R.id.textEmail);
            holder.tableRow = (TableRow) vi.findViewById(R.id.tableDesc);

            holder.image = (ImageView) vi.findViewById(R.id.imageView);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() > 0) {

            tempValues = null;
            tempValues = (ConfirmViewModel) data.get(position);

            holder.textName.setText(tempValues.getStrName());
            holder.textAddress.setText(tempValues.getStrAddress());
            holder.textContact.setText(tempValues.getStrContacts());
            holder.textEmail.setText(tempValues.getStrEmail());


            if (!tempValues.getStrDesc().equalsIgnoreCase("")) {

                holder.textDesc.setText(tempValues.getStrDesc());
                //holder.textDesc.setVisibility(View.VISIBLE);
                holder.tableRow.setVisibility(View.VISIBLE);

            } else {
                holder.tableRow.setVisibility(View.GONE);
                //holder.tableRow.setVisibility(View.GONE);
            }

            try {
                if (tempValues.getStrImg().equalsIgnoreCase("1"))
                    imageBitmap = BitmapFactory.decodeResource(_ctxt.getResources(), R.drawable.guru_circle);

                if (tempValues.getStrImg().equalsIgnoreCase("2"))
                    imageBitmap = BitmapFactory.decodeResource(_ctxt.getResources(), R.drawable.hungal_circle);

                if (tempValues.getStrImg().equalsIgnoreCase("3"))
                    imageBitmap = BitmapFactory.decodeResource(_ctxt.getResources(), R.drawable.mrs_hungal_circle);

                roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(_ctxt.getResources(), imageBitmap);

                roundedBitmapDrawable.setCornerRadius(40.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                holder.image.setImageDrawable(roundedBitmapDrawable);
            } catch (OutOfMemoryError oOm) {
            } catch (Exception e) {
            }

            //vi.setOnClickListener(makeListener);
        }

        return vi;
    }

    public static class ViewHolder {
        public TextView textName;
        public TextView textDesc;
        public TextView textAddress;
        public TextView textContact;
        public TextView textEmail;
        public ImageView image;
        public TableRow tableRow;
    }

}
