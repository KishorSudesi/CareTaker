package com.hdfc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdfc.caretaker.R;
import com.hdfc.models.VideoModel;

import java.util.List;

/**
 * Created by Admin on 2/22/2016.
 */
public class VideoCompletedAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private final Integer[] imgId = null;
    private Context _context;
    private List<VideoModel> data;

    public VideoCompletedAdapter(Context ctxt, List<VideoModel> c) {
        _context = ctxt;
        data = c;
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
            convertView = inflater.inflate(R.layout.list_video_item, null);
            viewHolder = new ViewHolder();
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.dateTime);
            viewHolder.information = (TextView) convertView.findViewById(R.id.information);
            viewHolder.vid = (ImageView) convertView.findViewById(R.id.video);
            //viewHolder.vid.setImageResource(R.drawable.vidlink); todo add video thumbnail
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dateTime.setText(data.get(position).getStrDateTime());
        viewHolder.information.setText(data.get(position).getStrDescription());
        viewHolder.vid.setImageResource(data.get(position).getiVideoId());
        return convertView;
    }

    public class ViewHolder {
        TextView dateTime;
        TextView information;
        ImageView vid;
    }
}
