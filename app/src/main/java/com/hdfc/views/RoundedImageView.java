package com.hdfc.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    private static Thread backgroundThread;
    private static Handler backgroundHandler;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp) {
        Bitmap output = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(bmp.getWidth() / 2 + 0.7f, bmp.getHeight() / 2 + 0.7f,
                bmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);

        return output;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        Bitmap roundBitmap = getCroppedBitmap(b);

        //
        backgroundHandler = new BackgroundHandler();
        backgroundThread = new BackgroundThread();
        backgroundThread.start();
        //

        super.setImageDrawable(new BitmapDrawable(getResources(), roundBitmap));
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(getCroppedBitmap(bm));
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {

                backgroundHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

}