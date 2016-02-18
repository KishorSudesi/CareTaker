package com.hdfc.app42service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.hdfc.libs.AsyncApp42ServiceApi;
import com.hdfc.libs.Libs;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import java.io.InputStream;

public class UploadService implements
        AsyncApp42ServiceApi.App42UploadServiceListener, App42CallBack {

    private AsyncApp42ServiceApi asyncService;
    private Context _ctxt;
    private Libs libs;
    private View formView, progressView;
    private ImageView imgView;

    public UploadService(Context context) {
        this._ctxt = context;
        asyncService = AsyncApp42ServiceApi.instance(context);
        libs = new Libs(context);
    }

   /* public void uploadImage(String imagePath, String fileName, String desc, String userName, UploadFileType fileType, AsyncApp42ServiceApi.App42UploadServiceListener callBack) {
        //libs.showProgress(true, formView, progressView);

        asyncService.uploadImageForUser(fileName, userName, imagePath, fileType, desc, callBack);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }*/

    public void uploadImageCommon(String imagePath, String fileName, String desc, String userName, UploadFileType fileType, App42CallBack callBack) {
        //libs.showProgress(true, formView, progressView);

        asyncService.uploadImageForUser(fileName, userName, imagePath, fileType, desc, callBack);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }

    public void uploadFile(String imagePath, String fileName, String desc, UploadFileType fileType, App42CallBack callBack) {
        // libs.showProgress(true, formView, progressView);
        asyncService.uploadFile(fileName, imagePath, fileType, desc, callBack);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }

    public void getFile(String fileName) {
        //libs.showProgress(true, formView, progressView);
        asyncService.getFile(fileName, this);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }

    public void removeImage(String fileName, String userName, App42CallBack app42CallBack) {
        //libs.showProgress(true, formView, progressView);

        asyncService.removeImageByUser(fileName, userName, app42CallBack);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }

    public void getImageCount(String userName, App42CallBack app42CallBack) {
        //libs.showProgress(true, formView, progressView);

        asyncService.getImageCount(userName, app42CallBack);
        //asyncService.uploadImage(fileName, imagePath, UploadFileType.IMAGE,
        //desc, this);
    }

    public void loadImageFromUrl(final String url, final ImageView img) {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final Bitmap bitmap = loadBitmap(url);
                callerThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageBitmap(bitmap);
                    }
                });
            }
        }.start();
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap loadBitmap(String url) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            if (url.startsWith("http")) {
                InputStream in = new java.net.URL(url).openStream();
                BitmapFactory.decodeStream(in, null, o);
            } else
                BitmapFactory.decodeFile(url, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < 150 && height_tmp / 2 < 150)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            o2.inJustDecodeBounds = false;
            if (url.startsWith("http")) {
                InputStream in = new java.net.URL(url).openStream();
                return BitmapFactory.decodeStream(in, null, o2);
            } else
                return BitmapFactory.decodeFile(url, o2);
        } catch (Exception e) {
        }
        return null;
    }

    public void getProfileImage(String fileName, String userName) {
        //libs.showProgress(true, formView, progressView);
        //asyncService.getImage(fileName, this);
        asyncService.getImageByUser(fileName, userName, this);
    }

    @Override
    public void onUploadImageSuccess(Upload response, String fileName, String userName) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog("SuccessFully Uploaded : " + response);
        getProfileImage(fileName, userName);
    }

    @Override
    public void onUploadImageFailed(App42Exception ex) {
        // libs.showProgress(false, formView, progressView);
        libs.createAlertDialog("Exception Occurred : " + ex.getMessage());
    }

    @Override
    public void onGetImageSuccess(Upload response) {
        // libs.showProgress(false, formView, progressView);
        String imageUrl = response.getFileList().get(0).getUrl();
        loadImageFromUrl(imageUrl, imgView);
    }

    @Override
    public void onGetImageFailed(App42Exception ex) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog("Exception Occurred : " + ex.getMessage());
    }

    @Override
    public void onSuccess(Upload response) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog(response.toString());
    }

    @Override
    public void onSuccess(Object o) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog(o.toString());
    }

    @Override
    public void onException(Exception e) {
        //libs.showProgress(false, formView, progressView);
    }

    @Override
    public void onException(App42Exception e) {
        //libs.showProgress(false, formView, progressView);
    }
}