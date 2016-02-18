package com.hdfc.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.views.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by balamurugan@adstringo.in on 01-01-2016.
 */
public class ConfirmListViewAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context _ctxt;
    private ArrayList data;
    private ConfirmViewModel tempValues = null;

    public ConfirmListViewAdapter(Context ctxt, ArrayList d) {
        _ctxt = ctxt;
        data = d;
        inflater = (LayoutInflater) _ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            try {
                holder.linearLayoutRoot = (LinearLayout) vi.findViewById(R.id.confirmLayoutRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.image = (RoundedImageView) vi.findViewById(R.id.imageView);

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
                holder.tableRow.setVisibility(View.VISIBLE);

            } else {
                holder.tableRow.setVisibility(View.GONE);

            }

            try {

                //SignupActivity.loadBitmap(tempValues.getStrImg().trim(), holder.image);
                if (!tempValues.getStrImg().equalsIgnoreCase("")) {

                    Libs libs = new Libs(_ctxt);

                    int intImgHeight = libs.getBitmapHeightFromFile(tempValues.getStrImg().trim());

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    holder.linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);

                    if (Build.VERSION.SDK_INT <= 16)
                        holder.linearLayoutRoot.setBackgroundDrawable(_ctxt.getResources().getDrawable(R.drawable.confirm_view));
                    else
                        holder.linearLayoutRoot.setBackground(_ctxt.getResources().getDrawable(R.drawable.confirm_view));

                    layoutParams.setMargins(0, intImgHeight / 2, 0, 0); //left, top, right, bottom
                    holder.linearLayoutRoot.setLayoutParams(layoutParams);
                    SignupActivity.loadBitmap(tempValues.getStrImg().trim(), holder.image);

                }

                //holder.image.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return vi;
    }

    public static class ViewHolder {
        public TextView textName;
        public TextView textDesc;
        public TextView textAddress;
        public TextView textContact;
        public TextView textEmail;
        public RoundedImageView image;
        public TableRow tableRow;
        public LinearLayout linearLayoutRoot;
    }

}
