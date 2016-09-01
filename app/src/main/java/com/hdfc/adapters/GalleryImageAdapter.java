package com.hdfc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hdfc.caretaker.R;
import com.hdfc.config.Config;

import java.util.List;

/**
 * Created by Sudesi infotech on 8/16/2016.
 */
public class GalleryImageAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Context _context;
    private List<String> imageUrlList;
    //private Utils utils;


    public GalleryImageAdapter(Context ctxt, List<String> urlList) {
        _context = ctxt;
        imageUrlList = urlList;
        //utils = new Utils(ctxt);
    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrlList.get(position);
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

            convertView = inflater.inflate(R.layout.gallery_image_row, null);

            viewHolder = new ViewHolder();


            viewHolder.roundedImageView = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (imageUrlList.size() > 0) {


            String strUrl = "";


            //
            strUrl = imageUrlList.get(position);


//            viewHolder.roundedImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String strMessage = (String) v.getTag();
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(_context);
//                    builder.setTitle("Notification");
//                    builder.setMessage(strMessage);
//                    builder.setPositiveButton(_context.getString(R.string.ok), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    builder.show();
//                }
//            });


            try {
                //File f = utils.getInternalFileImages(utils.replaceSpace(strId));

                //Utils.log(f.getAbsolutePath(), " P ");

//                if (f.exists())
//                    multiBitmapLoader.loadBitmap(f.getAbsolutePath(), viewHolder.roundedImageView);


                Glide.with(_context)
                        .load(strUrl)
                        .asBitmap()
                        .centerCrop()
                        .override(Config.intWidth, Config.intHeight)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.person_icon)
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target,
                                                       boolean isFirstResource) {
                                if (viewHolder.progressBar != null)
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model,
                                                           Target<Bitmap> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                if (viewHolder.progressBar != null)
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(viewHolder.roundedImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    public class ViewHolder {

        ImageView roundedImageView;
        ProgressBar progressBar;

    }
}