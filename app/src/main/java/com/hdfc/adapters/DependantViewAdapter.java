package com.hdfc.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.libs.Libs;
import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.DependantDetailPersonalActivity;
import com.hdfc.newzeal.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 01-01-2016.
 */
public class DependantViewAdapter extends BaseAdapter{

    private Context _ctxt;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    DependantModel tempValues=null;
    int i=0;
    private Bitmap imageBitmap = null;
    private RoundedBitmapDrawable roundedBitmapDrawable = null;

    private static Libs libs;

    public DependantViewAdapter(Context ctxt, ArrayList d,Resources resLocal) {
        _ctxt = ctxt;
        data=d;
        res = resLocal;
        inflater = ( LayoutInflater )_ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public static class ViewHolder{
        public TextView textName;
        public TextView textRelation;
        public ImageView image;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.dependant_layout, null);

            holder = new ViewHolder();
            holder.textName = (TextView) vi.findViewById(R.id.textViewName);
            holder.textRelation=(TextView)vi.findViewById(R.id.textViewRealtion);
            holder.image=(ImageView)vi.findViewById(R.id.image);

            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()>0){

            tempValues=null;
            tempValues = (DependantModel) data.get( position );

            holder.textName.setText(tempValues.getStrName());
            holder.textRelation.setText(tempValues.getStrRelation());

            Log.e(this.getClass().getName(), tempValues.getStrName()+" ~ "+tempValues.getStrRelation());

            if(!tempValues.getStrName().equalsIgnoreCase("Add Dependant")&&!tempValues.getStrRelation().equalsIgnoreCase("")) {

                imageBitmap = libs.getBitmapFromFile(_ctxt.getFilesDir()+ File.pathSeparator + tempValues.getStrImg() );
                roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(_ctxt.getResources(), imageBitmap);

            }else {
                imageBitmap = BitmapFactory.decodeResource(_ctxt.getResources(), R.mipmap.plus_icon);
                roundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(_ctxt.getResources(), imageBitmap);

            }

            try {
                //get bitmap of the image
                roundedBitmapDrawable.setCornerRadius(40.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                holder.image.setImageDrawable(roundedBitmapDrawable);
            } catch (OutOfMemoryError oOm) {
            } catch (Exception e) {
            }


            final View.OnClickListener makeListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        String strName = ((TextView)v.findViewById(R.id.textViewName)).getText().toString();
                        String strRelation = ((TextView)v.findViewById(R.id.textViewRealtion)).getText().toString();

                        if (strName.equalsIgnoreCase("Add Dependant")&&strRelation.equalsIgnoreCase("")) {
                            Intent selection = new Intent(_ctxt, DependantDetailPersonalActivity.class);
                            _ctxt.startActivity(selection);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            };

            vi.setOnClickListener(makeListener);
        }

        return vi;
    }

}
