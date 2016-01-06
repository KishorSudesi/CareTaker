package com.hdfc.libs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hdfc.config.Config;
import com.hdfc.config.NewZeal;
import com.hdfc.newzeal.SignupActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Libs {

    private static  Context _ctxt;
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static ArrayList<String> pathExternals;

    public Libs(Context context) {
        _ctxt = context;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("image") == 0;
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("audio") == 0;
    }

    public static boolean getAllFilesOfDirSize(File directory) {

        final File[] files = directory.listFiles();

        try {

            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder.
                            getAllFilesOfDirSize(file);

                        } else {  // it is a file...

                            if (file.exists() && file.canRead() && file.canWrite()) {

                            }
                        }
                    }
                }
            }

        }catch (Exception e){e.printStackTrace();}

        return true;
    }

    //creating scaled bitmap with required width and height
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);

        Bitmap scaledBitmap=null;

        try {
            scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        }catch (OutOfMemoryError oom){
            oom.printStackTrace();
        }

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    //
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }//

    //
    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        //check this logic

        // Raw height and width of image
        final int height = srcHeight;
        final int width = srcWidth;
        int inSampleSize = 1;

        if (height > dstHeight || width > dstWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > dstHeight
                    && (halfWidth / inSampleSize) > dstWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
        //
        /*
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }/////////////////*/
    }

    //source and destinatino rectangular regions to decode
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }

        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return (availableBlocks * blockSize)/1024;
        }else{
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return (totalBlocks * blockSize)/1024;
        } else {
            return 0;
        }
    }

    public static void toast(int type, int duration, String message)
    {
        String color="#000000";

        int time = Toast.LENGTH_LONG;

        if(duration==1)
            time = Toast.LENGTH_SHORT;

        if(type==2)
            color="#cccccc";

        if(type==2)
            color="#666666";

        Toast toast = Toast.makeText(_ctxt, message, time);
        View view = toast.getView();
        view.setBackgroundColor(Color.parseColor(color));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    //

    public static ArrayList<String> getExternals(){

        try {

            pathExternals = new ArrayList<String>();

            final String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {  // we can read the External Storage...
                //Retrieve the primary External Storage:
                final File primaryExternalStorage = Environment.getExternalStorageDirectory();

                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;
                if ((externalStorageRootDir = primaryExternalStorage.getParent()) == null) {  // no parent...
                    pathExternals.add(primaryExternalStorage.getAbsolutePath().toString());
                } else {
                    final File externalStorageRoot = new File(externalStorageRootDir);
                    final File[] files = externalStorageRoot.listFiles();
                    for (final File file : files) {
                        if (file.isDirectory() && file.canRead() && (file.listFiles().length > 0)) {  // it is a real directory (not a USB drive)...
                            pathExternals.add(file.getAbsolutePath().toString());
                        }
                    }
                }
            } else pathExternals = pathExternals = null;

        }catch (Exception e){
            e.printStackTrace();
        }

        return pathExternals;
    }

    public static final String sha512(final String toEncrypt) {

        try {

            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++)  {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().toLowerCase();

        } catch (Exception exc) {
            return "";
        }
    }

    public static void recordAudio(String fileName){

        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
        }

        mRecorder.start();
    }

    public static String encrypt(String Data){

        String encryptedValue =null;
        Cipher c = null;
        try {
            Key key = generateKey();
            c = Cipher.getInstance(Config.mode);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedValue;
    }

    public static String decrypt(String encryptedData) {
        String decryptedValue=null;
        Cipher c = null;

        try {
            Key key = generateKey();
            c = Cipher.getInstance(Config.mode);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(encryptedData,Base64.DEFAULT);
            byte[] decValue = c.doFinal(decordedValue);
            decryptedValue = new String(decValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(Config.key.getBytes(), Config.mode);
        return key;
    }

    public static String getDeviceID(Activity activity) {
        String deviceId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static String loadJSONFromAsset(Context ctx, String file) {
        String json = null;
        try {
            InputStream is = ctx.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void setBtnDrawable(Button btn, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            btn.setBackgroundDrawable(drw);
        else
            btn.setBackground(drw);
    }

    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(_ctxt);
        alertbox.setTitle("NewZeal");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getMemory() {
        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int) rt.maxMemory() / (1024 * 1024);
        int totalMemory = (int) rt.totalMemory() / (1024 * 1024);
    }

    public void createFolder(String path) {
        File root = new File(path);
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public void setExifData(String pathName) throws Exception {

        try {
            //working for Exif defined attributes
            ExifInterface exif = new ExifInterface(pathName);
            exif.setAttribute(ExifInterface.TAG_MAKE, "1000");
            exif.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUUID() {
        final TelephonyManager tm = (TelephonyManager) _ctxt.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(_ctxt.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }

    public Date convertStringToDate(String strDate) {

        Date date = null;
        try {
            date = fmt.parse(strDate);
            Log.i("Libs", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date; //
    }

    public String convertDateToString(Date dtDate) {

        String date = null;

        try {
            date = fmt.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Libs", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View mFormView, final View mProgressView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = _ctxt.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        try{
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            // Setting Dialog Title
            alertDialog.setTitle(title);
            // Setting Dialog Message
            alertDialog.setMessage(message);
            // Setting Dialog Cancelable
            alertDialog.setCancelable(false);
            // Setting OK Button
            /*alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });*/
            // Showing Alert Message
            alertDialog.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status, DialogInterface.OnClickListener listener) {
        try{
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            // Setting Dialog Title
            alertDialog.setTitle(title);
            // Setting Dialog Message
            alertDialog.setMessage(message);
            // Setting Dialog Cancelable
            alertDialog.setCancelable(false);
            // Setting OK Button
            //alertDialog.setButton("OK", listener);
            // Showing Alert Message
            alertDialog.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public File createFileInternal(String strFileName) {

        File file=null;
        try {
            file = new File(_ctxt.getFilesDir(), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public EditText traverseEditTexts(ViewGroup v, Drawable all, Drawable current, EditText editCurrent)
    {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++)
        {
            Object child = v.getChildAt(i);
            if (child instanceof EditText)
            {
                EditText e = (EditText)child;

                if(e.getId()==editCurrent.getId())
                    setEditTextDrawable(e, current);
                else
                    setEditTextDrawable(e, all);
            }
            else if(child instanceof ViewGroup)
            {
                invalid = traverseEditTexts((ViewGroup)child, all, current, editCurrent);  // Recursive call.
                if(invalid != null)
                {
                    break;
                }
            }
        }
        return invalid;
    }

    public void setEditTextDrawable(EditText editText, Drawable drw){
        if (Build.VERSION.SDK_INT <= 16)
            editText.setBackgroundDrawable(drw);
        else
            editText.setBackground(drw);
    }

    public void backToDependantList(){
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(_ctxt);
        alertbox.setTitle("NewZeal");
        alertbox.setMessage("All your Information will not be saved, Ok to Proceed?");
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //delete temp dependants
                NewZeal.dbCon.deleteTempDependants();
                Intent selection = new Intent(_ctxt, SignupActivity.class);
                selection.putExtra("LIST_DEPENDANT", true);
                arg0.dismiss();
                _ctxt.startActivity(selection);
            }
        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        alertbox.show();
    }

    public Bitmap getBitmapFromFile(String strPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap original =null;
        if(!strPath.equalsIgnoreCase("")) {
            try {
                Log.d(" 3 ", "getBitmap" + strPath);
                original = BitmapFactory.decodeFile(strPath, options);
            } catch (OutOfMemoryError oOm) {
            } catch (Exception e) {
            }
        }
        return original;
    }

    public String getPhoneNumber(String name) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = _ctxt.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if(ret==null)
            ret = "Unsaved";
        return ret;
    }

    public enum ScalingLogic {
        CROP, FIT
    }
}
