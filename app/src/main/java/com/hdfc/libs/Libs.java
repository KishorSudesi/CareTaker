package com.hdfc.libs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdfc.app42service.StorageService;
import com.hdfc.config.Config;
import com.hdfc.model.ActivityFeedBackModel;
import com.hdfc.model.ActivityListModel;
import com.hdfc.model.ActivityModel;
import com.hdfc.model.ActivityVideoModel;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.CustomerModel;
import com.hdfc.model.DependentModel;
import com.hdfc.model.NotificationModel;
import com.hdfc.model.ServiceModel;
import com.hdfc.newzeal.LoginActivity;
import com.hdfc.newzeal.R;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.newzeal.fragments.ActivityListFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;
import com.hdfc.newzeal.fragments.NotificationFragment;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Libs {

    public static Uri customerImageUri;
    private static Context _ctxt;
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()); //check the format and standardize it

    static {
        System.loadLibrary("stringGen");
    }

    public Libs(Context context) {
        _ctxt = context;

        WindowManager wm = (WindowManager) _ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Config.intScreenWidth = metrics.widthPixels;
        Config.intScreenHeight = metrics.heightPixels;
    }

    public static native String getString();

    public static String getStringJni() {
        try {
            //log(getStringJni(), " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getString();
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    //creating scaled bitmap with required width and height
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight) {

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight);

        Bitmap scaledBitmap = null;

        try {
            scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }

        if (scaledBitmap != null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        }

        return scaledBitmap;
    }

    //
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;

            bmp = createScaledBitmap(BitmapFactory.decodeResource(res, resId, options), reqWidth, reqHeight);

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bmp;
    }//

    //
    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        //check this logic

        // Raw height and width of image
        int inSampleSize = 1;

        if (srcHeight > dstHeight || srcWidth > dstWidth) {

            final int halfHeight = srcHeight / 2;
            final int halfWidth = srcWidth / 2;

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
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        //for crop
            /*final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }*/

        return new Rect(0, 0, srcWidth, srcHeight);
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) dstWidth / (float) dstHeight;

        if (srcAspect > dstAspect) {
            return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
        } else {
            return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
        }
        //for crop
        //return new Rect(0, 0, dstWidth, dstHeight);

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
            return (availableBlocks * blockSize) / 1024;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return (totalBlocks * blockSize) / 1024;
        } else {
            return 0;
        }
    }

    public static ArrayList<String> getExternals() {

        ArrayList<String> pathExternals;
        try {

            pathExternals = new ArrayList<String>();

            final String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {  // we can read the External Storage...
                //Retrieve the primary External Storage:
                final File primaryExternalStorage = Environment.getExternalStorageDirectory();

                //Retrieve the External Storages root directory:
                final String externalStorageRootDir;
                if ((externalStorageRootDir = primaryExternalStorage.getParent()) == null) {  // no parent...
                    pathExternals.add(primaryExternalStorage.getAbsolutePath());
                } else {
                    final File externalStorageRoot = new File(externalStorageRootDir);
                    final File[] files = externalStorageRoot.listFiles();
                    for (final File file : files) {
                        if (file.isDirectory() && file.canRead() && (file.listFiles().length > 0)) {  // it is a real directory (not a USB drive)...
                            pathExternals.add(file.getAbsolutePath());
                        }
                    }
                }
            } else pathExternals = null;

        } catch (Exception e) {
            e.printStackTrace();
            pathExternals = null;
        }

        return pathExternals;
    }
    //

    public static String sha512(final String toEncrypt) {

        try {

            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().toLowerCase();

        } catch (Exception exc) {
            return "";
        }
    }

    public static void recordAudio(String fileName) {

        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
    }

    public static String getDeviceID(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /*public static String encrypt(String Data) {

        String encryptedValue = null;
        Cipher c = null;
        try {
            Key key = generateKey();
            c = Cipher.getInstance(mode);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data.getBytes());
            encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
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
    }*/

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public static String decrypt(String encryptedData) {
        String decryptedValue = null;
        Cipher c = null;

        try {
            Key key = generateKey();
            c = Cipher.getInstance(mode);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
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
        Key key = new SecretKeySpec(Config.string.getBytes(), mode);
        return key;
    }*/

    public static String loadJSONFromFile(String path) {
        String json = null;
        try {

            File f = new File(path);
            InputStream is = new FileInputStream(f);
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

    public static void log(String message, String tag) {

        if ((tag == null || tag.equalsIgnoreCase("")) && _ctxt != null)
            tag = _ctxt.getClass().getName();

        if (Config.isDebuggable)
            Log.e(tag, message);

    }

    public static boolean isEmpty(String strInput) {

        boolean isEmpty = false;

        isEmpty = TextUtils.isEmpty(strInput);

        if (!isEmpty && strInput.equalsIgnoreCase(""))
            isEmpty = true;

        return isEmpty;
    }

    public static void logout() {
        try {
            Config.jsonObject = null;
            Config.jsonServer = null;
            Config.jsonDocId = "";

            Config.intSelectedMenu = 0;
            Config.intDependentsCount = 0;

            Config.serviceModels.clear();
            Config.dependentNames.clear();
            Config.notificationModels.clear();

            Config.intSelectedDependent = 0;

            Config.boolIsLoggedIn = false;

            Config.customerModel = null;
            Config.strUserName = "";

            Config.fileModels.clear();

            Config.bitmaps.clear();

            Intent dashboardIntent = new Intent(_ctxt, LoginActivity.class);
            _ctxt.startActivity(dashboardIntent);
            ((Activity) _ctxt).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toast(int type, int duration, String message) {

        String strColor = "#ffffff";

        if (type == 2)
            strColor = "#fcc485";

        try {
            LayoutInflater inflater = ((Activity) _ctxt).getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((Activity) _ctxt).findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);
            text.setTextColor(Color.parseColor(strColor));

            Toast toast = new Toast(_ctxt);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

            /*if (duration == 1)*/
                toast.setDuration(Toast.LENGTH_LONG);

            /*if (duration == 2)
                toast.setDuration(Toast.LENGTH_LONG);*/

            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_ctxt, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(_ctxt)
                .setMessage(message)
                .setPositiveButton(_ctxt.getString(R.string.ok), okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(_ctxt);
        alertbox.setTitle(_ctxt.getString(R.string.app_name));
        alertbox.setMessage(msg);
        alertbox.setPositiveButton(_ctxt.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }

   /* public String getUUID() {
        final TelephonyManager tm = (TelephonyManager) _ctxt.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(_ctxt.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        return deviceId;
    }*/

    public boolean isEmailValid(String email) {
        boolean b;

        b = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (b) {
            Pattern p = Pattern.compile("^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            b = m.matches();
        }

        return b;
    }

    public int getMemory() {
        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int) rt.maxMemory() / 1024;

        //int totalMemory = (int) rt.totalMemory() / (1024 * 1024);

        return maxMemory;
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
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
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
    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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
    }*/

    public boolean validCellPhone(String number) {
        //return android.util.Patterns.PHONE.matcher(number).matches();

        boolean isValid = false;

        if (number.length() >= 9 && number.length() <= 15)
            isValid = true;

        return isValid;
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        //if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard((Activity) _ctxt);
                    return false;
                }
            });
        // }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public File createFileInternalImage(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getExternalFilesDir(Environment.DIRECTORY_PICTURES), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public File createFileInternal(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getFilesDir(), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public void selectImage(final String strFileName, final Fragment fragment, final Activity activity) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(_ctxt);
        builder.setTitle("Add a Profile Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    openCamera(strFileName, fragment, activity);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    if (fragment != null)
                        fragment.startActivityForResult(Intent.createChooser(intent, "Select a Picture"), Config.START_GALLERY_REQUEST_CODE);
                    else
                        activity.startActivityForResult(Intent.createChooser(intent, "Select a Picture"), Config.START_GALLERY_REQUEST_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void openCamera(String strFileName, Fragment fragment, final Activity activity) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createFileInternalImage(strFileName);
        customerImageUri = Uri.fromFile(file);
        if (file != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, customerImageUri);

            if (fragment != null)
                fragment.startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
            else
                activity.startActivityForResult(cameraIntent, Config.START_CAMERA_REQUEST_CODE);
        }
    }

    public void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EditText traverseEditTexts(ViewGroup v, Drawable all, Drawable current, EditText editCurrent) {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++) {
            Object child = v.getChildAt(i);
            if (child instanceof EditText) {
                EditText e = (EditText) child;

                if (e.getId() == editCurrent.getId())
                    setEditTextDrawable(e, current);
                else
                    setEditTextDrawable(e, all);
            } else if (child instanceof ViewGroup) {
                invalid = traverseEditTexts((ViewGroup) child, all, current, editCurrent);  // Recursive call.
                if (invalid != null) {
                    break;
                }
            }
        }
        return invalid;
    }

    public void setEditTextDrawable(EditText editText, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            editText.setBackgroundDrawable(drw);
        else
            editText.setBackground(drw);
    }

    public void setDrawable(View v, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            v.setBackgroundDrawable(drw);
        else
            v.setBackground(drw);
    }

    /*private void updateView(int index, ListView listView) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        //TextView someText = (TextView) v.findViewById(R.id.sometextview);
        //someText.setText("Hi! I updated you manually!");
    }*/

    public void setStatusBarColor(String strColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) _ctxt).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(strColor));
        }
    }

    /*public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/

    //Application Specigfic Start

    public String replaceSpace(String string) {
        string = string.replace(" ", "_");
        return string;
    }

    public Bitmap getBitmapFromFile(String strPath, int intWidth, int intHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap original = null;
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            try {
                options.inJustDecodeBounds = true;
                original = BitmapFactory.decodeFile(strPath, options);
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, intWidth, intHeight);
                options.inJustDecodeBounds = false;
                original = BitmapFactory.decodeFile(strPath, options);
            } catch (OutOfMemoryError | Exception oOm) {
                oOm.printStackTrace();
            }
        }
        return original;
    }

    public int getBitmapHeightFromFile(String strPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //Bitmap original;
        int intSampleHeight = 0;
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            try {
                //
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(strPath, options);
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, Config.intWidth, Config.intHeight);
                options.inJustDecodeBounds = false;
                intSampleHeight = options.outHeight / options.inSampleSize;
                //original.recycle();
                //original=null;
            } catch (OutOfMemoryError | Exception oOm) {
                oOm.printStackTrace();
            }
        }
        return intSampleHeight;
    }

    public void fetchServices(){
        StorageService storageService = new StorageService(_ctxt);

        storageService.findDocsById("56c70aefe4b0067c8c7658bf", Config.collectionNameServices, new AsyncApp42ServiceApi.App42StorageServiceListener() {
            @Override
            public void onDocumentInserted(Storage response) {

            }

            @Override
            public void onUpdateDocSuccess(Storage response) {

            }

            @Override
            public void onFindDocSuccess(Storage response) {

                log(response.toString(), "");

                if (response.getJsonDocList().size() > 0) {

                    Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                    String strDocument = jsonDocument.getJsonDoc();

                    try {

                        JSONObject jsonObjectServices = new JSONObject(strDocument);

                        if (jsonObjectServices.has("services")) {

                            Config.serviceModels.clear();

                            JSONArray jsonArray = jsonObjectServices.getJSONArray("services");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ServiceModel serviceModel = new ServiceModel(jsonObject.getString("service_name"),
                                        jsonObject.getString("service_features"),
                                        jsonObject.getDouble("unit"),
                                        jsonObject.getDouble("cost"));

                                Config.serviceModels.add(serviceModel);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onInsertionFailed(App42Exception ex) {

            }

            @Override
            public void onFindDocFailed(App42Exception ex) {
                Libs.log(ex.getMessage(), "");
            }

            @Override
            public void onUpdateDocFailed(App42Exception ex) {

            }
        });
    }

    public void populateHeaderDependents(final LinearLayout dynamicUserTab, final int intWhichScreen) {

        int tabWidth;
        //calculate tab width according to screen width so that no. of tabs will fit to screen
        int screenSize = _ctxt.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                tabWidth = (Config.intScreenWidth - 24) / 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                tabWidth = (Config.intScreenWidth - 18) / 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                tabWidth = Config.intScreenWidth - 50;
                break;
            default:
                tabWidth = 100;
        }

        for (int i = 0; i < Config.dependentNames.size(); i++) {

            //Creates tab button
            Button bt = new Button(_ctxt);
            bt.setId(i);
            bt.setText(Config.dependentNames.get(i));

            log(Config.dependentNames.get(i), " Index ");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    tabWidth, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            //params.setMargins(3, 0, 3, 0);
            bt.setLayoutParams(params);
            bt.setAllCaps(false);
            bt.setTextAppearance(_ctxt, android.R.style.TextAppearance_Small);

            if (i == 0)
                bt.setBackgroundResource(R.drawable.one_side_border);
            else
                bt.setBackgroundResource(R.drawable.button_back_trans);

            bt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        Config.intSelectedDependent = v.getId();
                        updateTabColor(v.getId(), dynamicUserTab);

                        loadDependentData(intWhichScreen);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dynamicUserTab.addView(bt);
        }
    }

    public void loadDependentData(int intWhichScreen) {

        if (intWhichScreen == Config.intNotificationScreen) {

            NotificationFragment.staticNotificationModels.clear();

            /////
            try {

                if (Config.jsonObject.has("customer_name")) {

                    if (Config.jsonObject.has("dependents")) {

                        JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                        for (int i = Config.intSelectedDependent; i < Config.intSelectedDependent + 1; i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //Notifications
                            if (jsonObject.has("notifications")) {

                                JSONArray jsonArrayNotifications = jsonObject.getJSONArray("notifications");

                                for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                                    JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                                    NotificationModel notificationModel = new NotificationModel("",
                                            jsonObjectNotification.getString("notification_message"),
                                            jsonObjectNotification.getString("time"),
                                            jsonObjectNotification.getString("author"));

                                    NotificationFragment.staticNotificationModels.add(notificationModel);
                                }

                            } //else NotificationFragment.staticNotificationModels.clear();
                            //
                        }
                    }
                }

                NotificationFragment.notificationAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                e.printStackTrace();
            }
                /////
        }

        //
        if (intWhichScreen == Config.intActivityScreen) {

            /////
            try {

                ActivityListFragment.activitiesModelArrayList.clear();

                log(" 1 ", " IN ");

                if (Config.jsonObject.has("customer_name")) {

                    log(" 2 ", " IN ");

                    if (Config.jsonObject.has("dependents")) {

                        log(" 3 ", " IN ");

                        JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                        for (int index = Config.intSelectedDependent; index < Config.intSelectedDependent + 1; index++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(index);

                            log(" 4 ", " IN ");

                            //Notifications
                            if (jsonObject.has("activities")) {

                                JSONArray jsonArrayNotifications = jsonObject.getJSONArray("activities");

                                log(" 5 ", " IN ");

                                for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                                    log(" 6 ", " IN ");

                                    JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                                    if (jsonObjectNotification.has("activity_date")) {

                                        ActivityListModel activityListModel = new ActivityListModel();
                                        activityListModel.setStrDate(jsonObjectNotification.getString("activity_date").substring(3, 10));
                                        activityListModel.setStrDateTime(jsonObjectNotification.getString("activity_date"));
                                        activityListModel.setStrPerson(jsonObjectNotification.getString("provider_name"));
                                        activityListModel.setStrDateNumber(jsonObjectNotification.getString("activity_date").substring(0, 2));
                                        activityListModel.setStrMessage(jsonObjectNotification.getString("activity_name"));
                                        activityListModel.setStrStatus(jsonObjectNotification.getString("status"));
                                        activityListModel.setStrDesc(jsonObjectNotification.getString("provider_description"));

                                        ActivityListFragment.activitiesModelArrayList.add(activityListModel);

                                        ActivityListFragment.activityModels.clear();

                                        //
                                        ArrayList<ActivityFeedBackModel> activityFeedBackModels = new ArrayList<>();
                                        ArrayList<ActivityVideoModel> activityVideoModels = new ArrayList<>();

                                        if (jsonObjectNotification.has("feedbacks")) {

                                            JSONArray jsonArrayFeedback = jsonObjectNotification.getJSONArray("feedbacks");

                                            for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                                log(" 8", " IN ");

                                                JSONObject jsonObjectFeedback = jsonArrayFeedback.getJSONObject(k);

                                                ActivityFeedBackModel activityFeedBackModel = new ActivityFeedBackModel(
                                                        jsonObjectFeedback.getString("feedback_message"), jsonObjectFeedback.getString("feedback_by"),
                                                        jsonObjectFeedback.getInt("feedback_raring"), 0,
                                                        jsonObjectFeedback.getString("feedback_time"),
                                                        jsonObjectFeedback.getString("feedback_raring")
                                                );

                                                activityFeedBackModels.add(activityFeedBackModel);

                                            }
                                        }

                                        if (jsonObjectNotification.has("videos")) {

                                            JSONArray jsonArrayVideos = jsonObjectNotification.getJSONArray("videos");

                                            for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                                log(" 9 ", " IN ");

                                                JSONObject jsonObjectVideo = jsonArrayVideos.getJSONObject(k);

                                                ActivityVideoModel activityVideoModel = new ActivityVideoModel(
                                                        jsonObjectVideo.getString("video_name"),
                                                        jsonObjectVideo.getString("video_url"),
                                                        jsonObjectVideo.getString("video_description"),
                                                        jsonObjectVideo.getString("video_taken")
                                                );

                                                activityVideoModels.add(activityVideoModel);

                                            }
                                        }

                                        ActivityModel activityModel = new ActivityModel(
                                                jsonObjectNotification.getString("activity_name"), jsonObjectNotification.getString("activity_name"),
                                                jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("activity_date"),
                                                jsonObjectNotification.getString("status"),
                                                jsonObjectNotification.getString("provider_email"), jsonObjectNotification.getString("provider_contact_no"),
                                                jsonObjectNotification.getString("provider_name"), jsonObjectNotification.getString("provider_description"),
                                                activityVideoModels, activityFeedBackModels);

                                        ActivityListFragment.activityModels.add(activityModel);
                                        ///
                                    }
                                }
                            }
                        }
                    }
                }

                log(" 7 ", " IN ");

                ActivityListFragment.activityListAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                e.printStackTrace();
            }
                /////
        }

    }

    protected void updateTabColor(int id, View v) {

        for (int i = 0; i < Config.dependentNames.size(); i++) {
            Button tab = (Button) v.findViewById(i);
            if (i == id) {
                tab.setBackgroundResource(R.drawable.one_side_border);
            } else {
                tab.setBackgroundResource(R.drawable.button_back_trans);
            }

        }
    }

    public void parseData() {

        try {

            if (Config.jsonObject.has("customer_name")) {

                //fetch Customer data
                Config.customerModel = new CustomerModel(Config.jsonObject.getString("customer_name"),
                        Config.jsonObject.getString("paytm_account"), "URL",
                        Config.jsonObject.getString("customer_address"),
                        Config.jsonObject.getString("customer_contact_no"),
                        Config.jsonObject.getString("customer_email"));
                //

                if (Config.jsonObject.has("dependents")) {

                    Config.intDependentsCount = Config.jsonObject.getJSONArray("dependents").length();

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                    try {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //Dependant Names
                            Config.dependentNames.add(jsonObject.getString("dependent_name"));

                            //Notifications
                      /*  if (jsonObject.has("notifications")) {

                            ArrayList<NotificationModel> notificationModels = new ArrayList<>();

                            JSONArray jsonArrayNotifications = jsonObject.getJSONArray("notifications");

                            for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                                JSONObject jsonObjectNotification = jsonArrayNotifications.getJSONObject(j);

                                NotificationModel notificationModel = new NotificationModel("",
                                        jsonObjectNotification.getString("notification_message"),
                                        jsonObjectNotification.getString("time"),
                                        jsonObjectNotification.getString("author"));

                                notificationModels.add(notificationModel);
                            }

                            Config.notificationModels.add(notificationModels);
                        }*/
                            //
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            //TESTING
            /*try {
                JSONObject jsonObjectAct = new JSONObject();
                jsonObjectAct.put("provider_email", "provider@gmail.com");
                jsonObjectAct.put("provider_contact_no", "1230432432");
                jsonObjectAct.put("provider_description", "description");
                jsonObjectAct.put("provider_name", "calra");
                jsonObjectAct.put("activity_message", "message");
                jsonObjectAct.put("status", "upcoming");
                jsonObjectAct.put("activity_name", "NAME");
                jsonObjectAct.put("activity_date", "time");


                //
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("customer_email", Config.customerModel.getStrEmail());

                    JSONArray jsonArray = new JSONArray();

                    JSONObject jsonObjectDeps = new JSONObject();

                    JSONArray jsonArrayActivities = new JSONArray();


                    jsonArrayActivities.put(jsonObjectAct);

                    jsonObjectDeps.put("dependent_name", Config.dependentNames.get(Config.intSelectedDependent));

                    jsonObjectDeps.put("activities", jsonArrayActivities);

                    jsonArray.put(jsonObjectDeps);

                    jsonObject.put("dependents", jsonArray);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StorageService storageService = new StorageService(_ctxt);

                storageService.updateDocs(jsonObject, Config.jsonDocId, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        toast(2,2, o.toString());
                        log(o.toString(), " RESP ");

                    }

                    @Override
                    public void onException(Exception e) {

                        //toast(2,2, e.getMessage());
                        log( e.getMessage().toString(), " RESP ");
                    }
                });

            }catch (Exception e){

            }*/
            //

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public File getInternalFileImages(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getFilesDir(), "images/" + strFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public int retrieveDependants() {

        int count = SignupActivity.dependentModels.size();

        if (count == 0) {
            DependentModel dpndntModel = new DependentModel();
            dpndntModel.setStrName(_ctxt.getResources().getString(R.string.add_dependent));
            dpndntModel.setStrRelation("");
            dpndntModel.setStrImg("");
            dpndntModel.setStrDesc("");
            dpndntModel.setStrAddress("");
            dpndntModel.setStrContacts("");
            dpndntModel.setStrEmail("");

            SignupActivity.dependentModels.add(dpndntModel);
            count++;
        }

        return count;
    }

    public int retrieveConfirmDependants() {

        ConfirmViewModel confirmViewModel;

        ConfirmFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (!SignupActivity.strCustomerEmail.equalsIgnoreCase("")) {

            try {

                confirmViewModel = new ConfirmViewModel();
                confirmViewModel.setStrName(SignupActivity.strCustomerName);
                confirmViewModel.setStrDesc("");
                confirmViewModel.setStrAddress(SignupActivity.strCustomerAddress);
                confirmViewModel.setStrContacts(SignupActivity.strCustomerContactNo);
                confirmViewModel.setStrEmail(SignupActivity.strCustomerEmail);
                confirmViewModel.setStrImg(SignupActivity.strCustomerImg);

                count++;

                Libs.log(SignupActivity.strCustomerImg + " 4", "Image 4");

                ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);

                for (DependentModel dependentModel : SignupActivity.dependentModels) {

                    if (!dependentModel.getStrName().equalsIgnoreCase(_ctxt.getResources().getString(R.string.add_dependent))) {
                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(dependentModel.getStrName());
                        confirmViewModel.setStrDesc(dependentModel.getStrDesc());
                        confirmViewModel.setStrAddress(dependentModel.getStrAddress());
                        confirmViewModel.setStrContacts(dependentModel.getStrContacts());
                        confirmViewModel.setStrEmail(dependentModel.getStrEmail());
                        confirmViewModel.setStrImg(dependentModel.getStrImg());

                        Libs.log(dependentModel.getStrImg() + " 5", "Image 5");

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);
                        count++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return count;
    }

   /* SimpleDateFormat original = new SimpleDateFormat("yyyy-MM-ddThh:mm:sssZ");
    SimpleDateFormat output= new SimpleDateFormat("yyyy/MM/dd");
    String isoFormat = original.format(result.get("_id"));
    Date d = original.parse(isoFormat);
    String formattedTime = output.format(d);

    System.out.println(formattedTime);*/

    public boolean prepareData(String strCustomerImageUrl) {

        boolean isFormed;

        JSONObject jsonCustomer = new JSONObject();

        Config.jsonServer = null;

        try {

            jsonCustomer.put("customer_name", SignupActivity.strCustomerName);
            jsonCustomer.put("customer_address", SignupActivity.strCustomerAddress);
            jsonCustomer.put("customer_contact_no", SignupActivity.strCustomerContactNo);
            jsonCustomer.put("customer_email", SignupActivity.strCustomerEmail);
            jsonCustomer.put("customer_profile_url", strCustomerImageUrl);
            jsonCustomer.put("paytm_account", "paytm_account");

            isFormed = true;

            if (isFormed) {

                try {

                    int intCount = SignupActivity.dependentModels.size();

                    if (intCount > 0) {

                        JSONArray jsonArrayDependant = new JSONArray();

                        int intDependantCount = 0;

                        for (int cursorIndex = 0; cursorIndex < intCount; cursorIndex++) {

                            try {

                                DependentModel dependentModel = SignupActivity.dependentModels.get(cursorIndex);

                                if (!dependentModel.getStrName().equalsIgnoreCase(_ctxt.getResources().getString(R.string.add_dependent))) {

                                    JSONObject jsonDependant = new JSONObject();
                                    jsonDependant.put("dependent_name", dependentModel.getStrName());
                                    jsonDependant.put("dependent_email", dependentModel.getStrEmail());
                                    jsonDependant.put("dependent_contact_no", dependentModel.getStrContacts());
                                    jsonDependant.put("dependent_address", dependentModel.getStrAddress());
                                    jsonDependant.put("dependent_relation", dependentModel.getStrRelation());
                                    jsonDependant.put("dependent_notes", dependentModel.getStrDesc());
                                    jsonDependant.put("dependent_age", dependentModel.getIntAge());
                                    jsonDependant.put("dependent_illness", dependentModel.getStrIllness());

                                    jsonDependant.put("health_bp", 90);
                                    jsonDependant.put("health_heart_rate", 75);

                                    jsonDependant.put("dependent_profile_url", dependentModel.getStrImgServer());


                                    JSONArray jsonArrAct = new JSONArray();

                                    JSONObject pnObj2 = new JSONObject();

                                    pnObj2.put("provider_email", "provider@gmail.com");
                                    pnObj2.put("provider_contact_no", "321423423");
                                    pnObj2.put("provider_description", "This is a Test Data from APP42");
                                    pnObj2.put("provider_name", "Carla");
                                    pnObj2.put("activity_date", "19-03-2016 18:00:00");
                                    pnObj2.put("status", "upcoming");
                                    pnObj2.put("activity_name", "Buy grocery and delivery");
                                    pnObj2.put("author_profile_url", "http://url.com/23e3WQE124");

                                    jsonArrAct.put(pnObj2);

                                    for (int i = 0; i < 1; i++) {

                                        JSONObject pnObj3 = new JSONObject();

                                        pnObj3.put("provider_email", "provider@email.com");
                                        pnObj3.put("provider_contact_no", "9842103278");
                                        pnObj3.put("provider_description", "description");
                                        pnObj3.put("provider_name", "carla1");
                                        pnObj3.put("activity_date", i + "9-02-2016 18:00:00");
                                        pnObj3.put("status", "completed");
                                        pnObj3.put("activity_name", "Medical Checkups " + i);
                                        pnObj3.put("author_profile_url", "url");

                                        JSONArray jsonArrAct1 = new JSONArray();

                                        for (int j = 0; j < 1; j++) {

                                            JSONObject pnObj4 = new JSONObject();

                                            pnObj4.put("feedback_message", "good service by carla " + j);
                                            pnObj4.put("feedback_raring", 3);
                                            pnObj4.put("feedback_by", "hungal");
                                            pnObj4.put("report", 1);
                                            pnObj4.put("feedback_time", "19-02-2016 12:00:00");
                                            pnObj4.put("feedback_by_url", "url");

                                            jsonArrAct1.put(pnObj4);
                                        }

                                        pnObj3.put("feedbacks", jsonArrAct1);

                                        JSONArray jsonArrAct2 = new JSONArray();

                                        for (int k = 0; k < 1; k++) {

                                            JSONObject pnObj5 = new JSONObject();

                                            pnObj5.put("video_taken", "19-01-2016 18:00:00");
                                            pnObj5.put("video_description", "Video taken on hungal house");
                                            pnObj5.put("video_name", "Video name " + k);
                                            pnObj5.put("video_url", "url");

                                            jsonArrAct2.put(pnObj5);
                                        }

                                        pnObj3.put("videos", jsonArrAct2);

                                        JSONArray jsonArrAct3 = new JSONArray();

                                        for (int l = 0; l < 1; l++) {

                                            JSONObject pnObj6 = new JSONObject();

                                            pnObj6.put("image_name", "abc.jpg ");
                                            pnObj6.put("image_url", "http://url");
                                            pnObj6.put("image_description", "This is a Test Data from APP42");
                                            pnObj6.put("image_taken", "19-01-2016 18:00:00");

                                            jsonArrAct3.put(pnObj6);
                                        }

                                        pnObj3.put("images", jsonArrAct3);

                                        jsonArrAct.put(pnObj3);
                                    }

                                    jsonDependant.put("activities", jsonArrAct);

                                    JSONArray jsonArr0 = new JSONArray();

                                    for (int m = 0; m < 3; m++) {

                                        JSONObject pnObj0 = new JSONObject();
                                        pnObj0.put("notification_message", "This is a Test Data from APP42 " + m);
                                        pnObj0.put("author", "hungal");
                                        pnObj0.put("time", "19-01-2016 18:00:00");
                                        pnObj0.put("author_profile_url", "http://url.com/23e3WQE124");
                                        jsonArr0.put(pnObj0);
                                    }

                                    jsonDependant.put("notifications", jsonArr0);

                                    JSONArray jsonArr = new JSONArray();

                                    for (int n = 0; n < 3; n++) {

                                        JSONObject pnObj = new JSONObject();
                                        pnObj.put("service_name", "Medical Checkups and Health checkups" + n);
                                        pnObj.put("service_features", "BP, Heart Rate, etc");
                                        pnObj.put("unit", 3);
                                        pnObj.put("unit_consumed", 10);
                                        jsonArr.put(pnObj);
                                    }
                                    jsonDependant.put("services", jsonArr);

                                    JSONArray jsonArrHealth = new JSONArray();

                                    for (int o = 0; o < 3; o++) {

                                        JSONObject pnObjHealth = new JSONObject();
                                        pnObjHealth.put("bp", 80 + o);
                                        pnObjHealth.put("heart_rate", 230 + o);
                                        pnObjHealth.put("time_taken", "29-01-2016 18:00:00");
                                        jsonArrHealth.put(pnObjHealth);

                                    }
                                    jsonDependant.put("health_status", jsonArrHealth);

                                    jsonArrayDependant.put(jsonDependant);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            intDependantCount++;
                        }

                        jsonCustomer.put("dependents", jsonArrayDependant);

                        if (intCount != intDependantCount) {
                            isFormed = false;
                        } else {
                            Config.jsonServer = jsonCustomer;
                            Libs.log(jsonCustomer.toString(), "");
                            //
                            isFormed = true;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isFormed = false;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            isFormed = false;
        }

        return isFormed;
    }

    //Application Specig=fic End

}
