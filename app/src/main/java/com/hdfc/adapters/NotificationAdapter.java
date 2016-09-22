package com.hdfc.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.NotificationModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Admin on 19-02-2016.
 */

public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context _context;
    private List<NotificationModel> adapterNotificationModels;
    private Utils utils;

    public NotificationAdapter(Context ctxt, List<NotificationModel> d) {
        _context = ctxt;
        adapterNotificationModels = d;
        utils = new Utils(ctxt);
    }

    @Override
    public int getCount() {
        return adapterNotificationModels.size();
    }

    @Override
    public Object getItem(int position) {
        return adapterNotificationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        int mNotifyPosition;

        if (inflater == null)
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.notification_activity_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.textViewText = (TextView) convertView.findViewById(R.id.textViewText);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            viewHolder.textReadMore = (TextView) convertView.findViewById(R.id.textReadMore);
            viewHolder.roundedImageView = (ImageView) convertView.findViewById(R.id.roundedImageView);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.activityList);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterNotificationModels.size() > 0) {

            String strUrl = "";
            String strName = "";

            if (adapterNotificationModels.get(position).getStrMessage().length() > 70) {
                viewHolder.textReadMore.setVisibility(View.VISIBLE);
                viewHolder.textReadMore.setTag(adapterNotificationModels.get(position).
                        getStrMessage());
                viewHolder.textViewText.setText(adapterNotificationModels.get(position).
                        getStrMessage().substring(0, 68));
            } else {
                viewHolder.textReadMore.setVisibility(View.GONE);
                viewHolder.textReadMore.setEnabled(false);
                viewHolder.textViewText.setText(adapterNotificationModels.get(position).
                        getStrMessage());
            }

            viewHolder.textReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strMessage = (String) v.getTag();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                    builder.setTitle(_context.getString(R.string.text_notification));
                    builder.setMessage(strMessage);
                    builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("provider")) {
                if (Config.strProviderIds.contains(adapterNotificationModels.get(position).
                        getStrCreatedByID())) {
                    mNotifyPosition = Config.strProviderIds.indexOf(adapterNotificationModels.
                            get(position).getStrCreatedByID());

                    if (mNotifyPosition > -1 && mNotifyPosition < Config.providerModels.size())
                        strName = Config.providerModels.get(mNotifyPosition).getStrName();

                    if (mNotifyPosition > -1 && mNotifyPosition < Config.providerModels.size())
                        strUrl = Config.providerModels.get(mNotifyPosition).getStrImgUrl();

                }
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("dependent")) {
                if (Config.strDependentIds.contains(adapterNotificationModels.get(position).
                        getStrCreatedByID())) {
                    mNotifyPosition = Config.strDependentIds.indexOf(adapterNotificationModels.
                            get(position).getStrCreatedByID());

                    if (mNotifyPosition > -1 && mNotifyPosition < Config.dependentModels.size())
                        strName = Config.dependentModels.get(mNotifyPosition).getStrName();
                    strUrl = Config.dependentModels.get(mNotifyPosition).getStrImageUrl();
                }
            }

            if (adapterNotificationModels.get(position).getStrCreatedByType().
                    equalsIgnoreCase("customer")) {
                strName = Config.customerModel.getStrName();
                strUrl = Config.customerModel.getStrImgUrl();
            }

            try {
                String strDate = adapterNotificationModels.get(position).getStrDateTime();
                String strDisplayDate = _context.getResources().getString(R.string.space) +
                        _context.getResources().getString(R.string.at) +
                        _context.getResources().getString(R.string.space) + utils.formatDate(strDate);

                viewHolder.textViewTime.setText(strDisplayDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.textViewName.setText(strName);

            try {
                //File f = utils.getInternalFileImages(utils.replaceSpace(strId));

                //Utils.log(f.getAbsolutePath(), " P ");

//                if (f.exists())
//                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);


                Glide.with(_context)
                        .load(strUrl)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(_context))
                        .placeholder(R.drawable.person_icon)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.roundedImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    public class ViewHolder {
        TextView textViewName;
        TextView textViewText;
        TextView textViewTime;
        TextView textReadMore;
        ImageView roundedImageView;
        LinearLayout linearLayout;
    }
}
