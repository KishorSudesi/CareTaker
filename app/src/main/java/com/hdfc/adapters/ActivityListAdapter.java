package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.config.Config;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 2/19/2016.
 */
public class ActivityListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private static Utils utils;
    private Context _context;
    private List<ActivityModel> data;

    public ActivityListAdapter(Context ctxt, List<ActivityModel> y) {
        _context = ctxt;
        data = y;
        utils = new Utils(ctxt);
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
        if (inflater == null)
            inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.activity_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.dateNumber = (TextView) convertView.findViewById(R.id.textView41);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textView31);
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.txtDateTime);

            viewHolder.Message = (TextView) convertView.findViewById(R.id.textViewkop);
            viewHolder.person = (TextView) convertView.findViewById(R.id.txtPerson);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.completedActivityOne);

            viewHolder.linearLayoutDone = (LinearLayout) convertView.findViewById(R.id.linearLayoutDone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            ActivityModel activityListModel = data.get(position);

            if ((activityListModel.getStrActivityStatus().equalsIgnoreCase("open"))
                    || activityListModel.getStrActivityStatus().equalsIgnoreCase("inprocess")) {
                /*viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                        getColor(R.color.orange));*/
                viewHolder.linearLayoutDone.setVisibility(View.GONE);

            }

            if (activityListModel.getStrActivityStatus().equalsIgnoreCase("completed")) {
                /*viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                        getColor(R.color.colorPrimary));*/
                viewHolder.linearLayoutDone.setVisibility(View.VISIBLE);
            }

            if (activityListModel.getStrActivityStatus().equalsIgnoreCase("new")) {
               /* viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                        getColor(R.color.colorWhite));*/
                viewHolder.linearLayoutDone.setVisibility(View.GONE);

            }


            int iPosition = Config.strProviderIds.indexOf(activityListModel.getStrProviderID());

            String strCarlaName = "";

            if (iPosition > -1 && iPosition < Config.providerModels.size())
                strCarlaName = Config.providerModels.get(iPosition).getStrName();

            Date date = new Date();

            String strTimeStamp = utils.convertDateToString(date);

            if (activityListModel.getStrActivityDate() != null && !activityListModel.getStrActivityDate().equalsIgnoreCase(""))
                strTimeStamp = activityListModel.getStrActivityDate();

            viewHolder.dateNumber.setText(strTimeStamp.substring(8, 10));

            String strMonthYear = "";

            Date fullDate = utils.convertStringToDate(strTimeStamp);
            String strFullTimeStamp = utils.convertDateToStringFormat(fullDate, Utils.writeFormat);

            try {
                date = Utils.readFormatDate.parse(strTimeStamp.substring(0, 10));
                strMonthYear = Utils.writeFormatMonth.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.date.setText(strMonthYear);

            viewHolder.dateTime.setText(strFullTimeStamp);

            String strMess = activityListModel.getStrActivityName() + _context.getResources().getString(R.string.hyphen);

            if (activityListModel.getStrActivityStatus().equalsIgnoreCase("new")
                    || activityListModel.getStrActivityStatus().equalsIgnoreCase("upcoming"))
                strMess += activityListModel.getStrActivityDesc();
            else
                strMess += activityListModel.getStrActivityDesc();//getStrActivityProviderMessage

            if (strMess.length() > 38)
                strMess = strMess.substring(0, 36) + "..";

            viewHolder.Message.setText(strMess);

            strMess = "";

            String strAuthor = strCarlaName + _context.getResources().getString(R.string.at);

            viewHolder.person.setText(strAuthor);
            // viewHolder.Message.setText(data.get(i).getStrMessage());
            // viewHolder.dateTime.setText(data.get(i).getStrDateTime());

            viewHolder.linearLayout.setBackgroundColor(_context.getResources().
                    getColor(R.color.colorWhite));

        }

        return convertView;

    }

    public class ViewHolder {
        TextView dateNumber;
        TextView date;
        TextView Message;
        TextView dateTime;
        TextView person;
        LinearLayout linearLayout;
        LinearLayout linearLayoutDone;
    }

}