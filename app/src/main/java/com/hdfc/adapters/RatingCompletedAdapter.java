package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.FeedBackModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2/22/2016.
 */
public class RatingCompletedAdapter extends BaseAdapter {

    private static final int[] intImageIds = new int[]{R.mipmap.rate_icon_1, R.mipmap.rate_icon_2, R.mipmap.rate_icon_3, R.mipmap.smiley_icon, R.mipmap.rate_icon_4};
    private static LayoutInflater inflater = null;
    private static Context _ctx;
    private static Utils utils;
    private static MultiBitmapLoader multiBitmapLoader;
    private List<FeedBackModel> data = new ArrayList<>();

    public RatingCompletedAdapter(Context context, List<FeedBackModel> x) {
        _ctx = context;
        data = x;
        utils = new Utils(context);
        multiBitmapLoader = new MultiBitmapLoader(context);
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
            inflater = (LayoutInflater) _ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.rating_completed_activity_item, null);
            viewHolder = new ViewHolder();

            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.dateTimeRating);
            viewHolder.personName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolder.feedback = (TextView) convertView.findViewById(R.id.feedBack);

            viewHolder.personImage = (ImageView) convertView.findViewById(R.id.personImage);
            viewHolder.smiley = (ImageView) convertView.findViewById(R.id.smileyImage);

            try {
                viewHolder.linearLayoutRoot = (LinearLayout) convertView.findViewById(R.id.confirmLayoutRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.size() > 0) {

            String strTimeStamp = utils.formatDate(data.get(position).getStrFeedBackTime());

            viewHolder.dateTime.setText(strTimeStamp);

            //todo fetch name

            String strAuthor = _ctx.getString(R.string.by) + "" + data.get(position).getStrFeedBackBy();

            viewHolder.personName.setText(strAuthor);

            //
            try {

                File file = utils.getInternalFileImages(utils.replaceSpace(data.get(position).getStrFeedBackBy()));

                //Utils.log(file.getAbsolutePath(), " PATH ");

                if (file.exists()) {

                    String strPath = file.getAbsolutePath();

                    int intImgHeight = utils.getBitmapHeightFromFile(strPath);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    viewHolder.linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);

                    if (Build.VERSION.SDK_INT <= 16)
                        viewHolder.linearLayoutRoot.setBackgroundDrawable(_ctx.getResources().getDrawable(R.drawable.confirm_view));
                    else
                        viewHolder.linearLayoutRoot.setBackground(_ctx.getResources().getDrawable(R.drawable.confirm_view));

                    layoutParams.setMargins(0, intImgHeight / 2, 0, 0); //left, top, right, bottom
                    viewHolder.linearLayoutRoot.setLayoutParams(layoutParams);

                    multiBitmapLoader.loadBitmap(strPath, viewHolder.personImage);
                }

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
            //

            viewHolder.feedback.setText(data.get(position).getStrFeedBackMessage());

            if (data.get(position).getBoolFeedBackReport())
                viewHolder.feedback.setTextColor(Color.RED);
            else
                viewHolder.feedback.setTextColor(Color.BLACK);

            viewHolder.smiley.setImageResource(intImageIds[data.get(position).getIntFeedBackRating() - 1]);
        }
        return convertView;
    }

    public class ViewHolder {
        public LinearLayout linearLayoutRoot;
        ImageView personImage;
        ImageView smiley;
        TextView feedback;
        TextView personName;
        TextView dateTime;
    }
}
