package com.hdfc.libs;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.StorageService;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.LoginActivity;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.caretaker.fragments.ConfirmFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.Config;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.ConfirmViewModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.NotificationModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.ServiceModel;
import com.hdfc.models.VideoModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
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
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Utils {

    //application specific
    public final static SimpleDateFormat readFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    public final static SimpleDateFormat writeFormat =
            new SimpleDateFormat("kk:mm aa dd MMM yyyy", Locale.US);
    public final static SimpleDateFormat writeFormatActivity =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
    public final static SimpleDateFormat writeFormatActivityMonthYear =
            new SimpleDateFormat("MMM yyyy", Locale.US);
    public static Uri customerImageUri;

    private static int iActivityCount = 0;
    private static int iProviderCount = 0;
    //

    private static Context _ctxt;

    static {
        System.loadLibrary("stringGen");
    }

    public Utils(Context context) {
        _ctxt = context;

        WindowManager wm = (WindowManager) _ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Config.intScreenWidth = metrics.widthPixels;
        Config.intScreenHeight = metrics.heightPixels;

        readFormat.setTimeZone(TimeZone.getDefault());
    }

    public static native String getString();

    public static String getStringJni() {
        try {
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

    //creating scaled bitmap with required width and height
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight) {

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight);

        Bitmap scaledBitmap = null;

        try {
            scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }

        if (scaledBitmap != null) {
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        }

        return scaledBitmap;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("image") == 0;
    }

    /*
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
                                //TODO file
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }*/

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
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight,
                    reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;

            bmp = createScaledBitmap(BitmapFactory.decodeResource(res, resId, options), reqWidth,
                    reqHeight);

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

    public static void hideSoftKeyboard(Activity activity) {
        try {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().
                        getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public static long getAvailableExternalMemorySize() {
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
    }*/

   /* public static String getDeviceID(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }*/

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

    /*public static String loadJSONFromFile(String path) {
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
    }*/

    public static void log(String message, String tag) {

        if ((tag == null || tag.equalsIgnoreCase("")) && _ctxt != null)
            tag = _ctxt.getClass().getName();

        if (Config.isDebuggable)
            Log.e(tag, message);
    }

    public static boolean isEmpty(String strInput) {

        boolean isEmpty;

        isEmpty = TextUtils.isEmpty(strInput);

        if (!isEmpty && strInput.equalsIgnoreCase(""))
            isEmpty = true;

        return isEmpty;
    }

    public static File createFileInternal(String strFileName) {

        File file = null;
        try {
            file = new File(_ctxt.getFilesDir(), strFileName);
            file.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
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

    public static boolean deleteAllFiles(File directory) {

        final File[] files = directory.listFiles();

        try {

            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder.
                            deleteAllFiles(file);
                        } else {
                            if (file.exists() && file.canRead() && file.canWrite()) {
                                file.delete();
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

   /* public void createFolder(String path) {
        File root = new File(path);
        if (!root.exists()) {
            root.mkdirs();
        }
    }*/

   /* public void setExifData(String pathName) throws Exception {

        try {
            //working for Exif defined attributes
            ExifInterface exif = new ExifInterface(pathName);
            exif.setAttribute(ExifInterface.TAG_MAKE, "1000");
            exif.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void setDrawable(View v, Drawable drw) {
        if (Build.VERSION.SDK_INT <= 16)
            v.setBackgroundDrawable(drw);
        else
            v.setBackground(drw);
    }

    public static void logout() {
        try {
            //Config.jsonObject = null;
            //Config.jsonServer = null;
            //Config.strCustomerDocId = "";

            Config.intSelectedMenu = 0;
            Config.intDependentsCount = 0;

            Config.serviceModels.clear();
            Config.dependentNames.clear();

            Config.intSelectedDependent = 0;

            Config.boolIsLoggedIn = false;

            Config.customerModel = null;
            Config.strUserName = "";

            Config.fileModels.clear();

            //todo clear shared pref.

            File fileImage = createFileInternal("images/");
            deleteAllFiles(fileImage);

            Intent dashboardIntent = new Intent(_ctxt, LoginActivity.class);
            ((Activity) _ctxt).finish();
            _ctxt.startActivity(dashboardIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveFile(File file, File newFile) throws IOException {
        //File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            file.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    /*public void setupUI(View view) {
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
    }*/

    public boolean isEmailValid(String email) {
        boolean b;

        b = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (b) {
            Pattern p = Pattern.compile("^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$",
                    Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            b = m.matches();
        }

        return b;
    }

    public int getMemory() {
        Runtime rt = Runtime.getRuntime();

        //int totalMemory = (int) rt.totalMemory() / (1024 * 1024);

        return (int) rt.maxMemory() / 1024;
    }

    public String convertDateToString(Date dtDate) {

        String date = null;

        try {
            date = readFormat.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.i("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public Date convertStringToDate(String strDate) {

        Date date = null;
        try {
            date = readFormat.parse(strDate);
            //Log.i("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date; //
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _ctxt.getSystemService(
                Context.CONNECTIVITY_SERVICE);
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

    public void selectImage(final String strFileName, final Fragment fragment,
                            final Activity activity) {
        final CharSequence[] items = {_ctxt.getResources().getString(R.string.take_photo),
                _ctxt.getResources().getString(R.string.choose_library),
                _ctxt.getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(_ctxt);
        builder.setTitle(_ctxt.getResources().getString(R.string.add_profile));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(_ctxt.getResources().getString(R.string.take_photo))) {

                    if (externalMemoryAvailable())
                        openCamera(strFileName, fragment, activity);
                    else
                        toast(2, 2, _ctxt.getResources().getString(R.string.no_memory_camera));

                } else if (items[item].equals(_ctxt.getResources().getString(R.string.choose_library))) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    if (fragment != null && fragment.isVisible())
                        fragment.startActivityForResult(Intent.createChooser(intent,
                                _ctxt.getResources().getString(R.string.select_picture)),
                                Config.START_GALLERY_REQUEST_CODE);
                    else if (activity != null && !activity.isFinishing())
                        activity.startActivityForResult(Intent.createChooser(intent,
                                _ctxt.getResources().getString(R.string.select_picture)),
                                Config.START_GALLERY_REQUEST_CODE);

                } else if (items[item].equals(_ctxt.getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*private void updateView(int index, ListView listView) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        //TextView someText = (TextView) v.findViewById(R.id.sometextview);
        //someText.setText("Hi! I updated you manually!");
    }*/

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

    /*public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/

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

    public EditText traverseEditTexts(ViewGroup v, Drawable all, Drawable current,
                                      EditText editCurrent) {
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
                invalid = traverseEditTexts((ViewGroup) child, all, current, editCurrent);
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

    public void setStatusBarColor(String strColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) _ctxt).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(strColor));
        }
    }

    public String replaceSpace(String string) {
        string = string.replace(" ", "_");
        return string;
    }

    public String[] jsonToStringArray(JSONArray jsonArray) {

        String strings[] = new String[0];

        try {
            int iLength = jsonArray.length();

            strings = new String[iLength];

            for (int i = 0; i < iLength; i++) {
                strings[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return strings;
    }

    //load image from url
    public void loadImageFromWeb(String strFileName, String strFileUrl) {

        strFileName = replaceSpace(strFileName);
        strFileUrl = replaceSpace(strFileUrl);

        File fileImage = createFileInternal("images/" + strFileName);

        log(strFileName + " ~ " + strFileUrl, " Path ");
        //fileImage.
        if (fileImage.length() <= 0) {

            InputStream input;
            try {

                URL url = new URL(strFileUrl);//URLEncoder.encode(fileModel.getStrFileUrl(), "UTF-8")
                input = url.openStream();
                byte[] buffer = new byte[1500];
                OutputStream output = new FileOutputStream(fileImage);
                try {
                    int bytesRead;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    output.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap roundedBitmap(Bitmap bmp) {
        Bitmap output = null;

        try {
            if (bmp != null) {
                output = Bitmap.createBitmap(bmp.getWidth(),
                        bmp.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(Color.parseColor("#BAB399"));
                canvas.drawCircle(bmp.getWidth() / 2 + 0.7f, bmp.getHeight() / 2 + 0.7f,//
                        bmp.getWidth() / 2 + 0.1f, paint); //
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bmp, rect, rect, paint);
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
        return output;
    }

    //Application Specigfic Start

    public Bitmap getBitmapFromFile(String strPath, int intWidth, int intHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap original = null;
        if (strPath != null && !strPath.equalsIgnoreCase("")) {
            try {
                options.inJustDecodeBounds = true;
                original = BitmapFactory.decodeFile(strPath, options);
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight,
                        intWidth, intHeight);
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
                options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight,
                        Config.intWidth, Config.intHeight);
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

        dynamicUserTab.removeAllViews();

        Config.intSelectedDependent = 0;

        loadDependentData(intWhichScreen);

        for (int i = 0; i < Config.dependentModels.size(); i++) {

            //Creates tab button
            Button bt = new Button(_ctxt);
            bt.setId(i);
            bt.setText(Config.dependentModels.get(i).getStrName());

            //log(Config.dependentNames.get(i), " Index ");

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
            loadNotifications();
        }

        if (intWhichScreen == Config.intListActivityScreen ||
                intWhichScreen == Config.intActivityScreen) {
            try {
                populateActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNotifications() {

        final ProgressDialog progressDialog = new ProgressDialog(_ctxt);

        if (isConnectingToInternet()) {

            progressDialog.setMessage(_ctxt.getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageService storageService = new StorageService(_ctxt);

            storageService.findDocsByKeyValue(Config.collectionNotification,
                    "user_id",
                    Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID(),
                    new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage storage) {

                            if (storage != null) {

                                if (storage.getJsonDocList().size() > 0) {

                                    ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                            getJsonDocList();

                                    for (int i = 0; i < jsonDocList.size(); i++) {

                                        createNotificationModel(jsonDocList.get(i).getDocId(),
                                                jsonDocList.get(i).getJsonDoc());
                                    }

                                    NotificationFragment.notificationAdapter =
                                            new NotificationAdapter(
                                                    _ctxt,
                                                    Config.dependentModels.
                                                            get(Config.intSelectedDependent).
                                                            getNotificationModels());
                                    //NotificationFragment.notificationAdapter.notifyDataSetChanged();
                                    //setAdapter(NotificationFragment.notificationAdapter);
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    NotificationFragment.listViewActivities.
                                            setAdapter(NotificationFragment.notificationAdapter);

                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (ex != null) {
                                try {
                                       /* JSONObject jsonObject = new JSONObject(ex.getMessage());
                                        JSONObject jsonObjectError = jsonObject.
                                                getJSONObject("app42Fault");
                                        String strMess = jsonObjectError.getString("details");

                                        toast(2, 2, strMess);*/
                                    toast(2, 2, _ctxt.getString(R.string.error));
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }
    }


    public void createNotificationModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectProvider = new JSONObject(strDocument);

            if (jsonObjectProvider.has("notification_message")) {

                NotificationModel notificationModel = new NotificationModel(
                        jsonObjectProvider.getString("notification_message"),
                        jsonObjectProvider.getString("time"),
                        jsonObjectProvider.getString("user_type"),
                        jsonObjectProvider.getString("created_by_type"),
                        jsonObjectProvider.getString("user_id"),
                        jsonObjectProvider.getString("created_by"), strDocumentId);

                if (!Config.strNotificationIds.contains(strDocumentId)) {
                    Config.strNotificationIds.add(strDocumentId);
                    Config.dependentModels.get(Config.intSelectedDependent).
                            setNotificationModels(notificationModel);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateActivity() {

        try {

            /*ActivityFragment.activitiesModelArrayList.clear();

            if (Config.jsonObject.has("customer_name")) {

                if (Config.jsonObject.has("dependents")) {

                    JSONArray jsonArray = Config.jsonObject.getJSONArray("dependents");

                    for (int index = Config.intSelectedDependent;
                         index < Config.intSelectedDependent + 1; index++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(index);

                        if (jsonObject.has("activities")) {

                            JSONArray jsonArrayNotifications = jsonObject.getJSONArray("activities");

                            for (int j = 0; j < jsonArrayNotifications.length(); j++) {

                                JSONObject jsonObjectNotification =
                                        jsonArrayNotifications.getJSONObject(j);

                                if (jsonObjectNotification.has("activity_date")) {

                                    ActivityModel activityModel = new ActivityModel();

                                    activityModel.setStrActivityDate(
                                            jsonObjectNotification.getString("activity_date"));
                                    //month-year

                                    activityModel.setStrActivityMessage(
                                            jsonObjectNotification.getString("activity_message"));
                                    activityModel.setStrActivityStatus(
                                            jsonObjectNotification.getString("status"));
                                    activityModel.setStrActivityDesc(
                                            jsonObjectNotification.getString("provider_description"));

                                    ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                                    ArrayList<VideoModel> videoModels = new ArrayList<>();
                                    ArrayList<ImageModel> imageModels = new ArrayList<>();

                                    if (jsonObjectNotification.has("feedbacks")) {

                                        JSONArray jsonArrayFeedback = jsonObjectNotification.
                                                getJSONArray("feedbacks");

                                        for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                                            JSONObject jsonObjectFeedback =
                                                    jsonArrayFeedback.getJSONObject(k);

                                        }
                                    }

                                    if (jsonObjectNotification.has("videos")) {

                                        JSONArray jsonArrayVideos = jsonObjectNotification.
                                                getJSONArray("videos");

                                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                            JSONObject jsonObjectVideo = jsonArrayVideos.
                                                    getJSONObject(k);

                                        }
                                    }

                                    if (jsonObjectNotification.has("images")) {

                                        JSONArray jsonArrayVideos = jsonObjectNotification.
                                                getJSONArray("images");

                                        for (int k = 0; k < jsonArrayVideos.length(); k++) {

                                            JSONObject jsonObjectImage = jsonArrayVideos.
                                                    getJSONObject(k);
                                        }
                                    }

                                    ActivityFragment.activitiesModelArrayList.add(activityModel);
                                }
                            }
                        }
                    }
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
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
            dpndntModel.setStrImagePath("");
            dpndntModel.setStrNotes("");
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

        if (!Config.customerModel.getStrEmail().equalsIgnoreCase("")) {

            try {

                confirmViewModel = new ConfirmViewModel();
                confirmViewModel.setStrName(Config.customerModel.getStrName());
                confirmViewModel.setStrDesc("");
                confirmViewModel.setStrAddress(Config.customerModel.getStrAddress());
                confirmViewModel.setStrContacts(Config.customerModel.getStrContacts());
                confirmViewModel.setStrEmail(Config.customerModel.getStrEmail());
                confirmViewModel.setStrImg(Config.customerModel.getStrImgPath());

                count++;

                ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);

                for (DependentModel dependentModel : SignupActivity.dependentModels) {

                    if (!dependentModel.getStrName().equalsIgnoreCase(_ctxt.getResources().
                            getString(R.string.add_dependent))) {
                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(dependentModel.getStrName());
                        confirmViewModel.setStrDesc(dependentModel.getStrNotes());
                        confirmViewModel.setStrAddress(dependentModel.getStrAddress());
                        confirmViewModel.setStrContacts(dependentModel.getStrContacts());
                        confirmViewModel.setStrEmail(dependentModel.getStrEmail());
                        confirmViewModel.setStrImg(dependentModel.getStrImagePath());

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

    public String formatDate(String strDate){

        String strDisplayDate="06-03-2016 20:55:00";

        if(strDate!=null&&!strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if(date!=null)
                strDisplayDate = writeFormat.format(date);
        }

        return strDisplayDate;
    }

    public String formatDateActivity(String strDate){

        String strDisplayDate="06-03-2016 20:55:00";

        if(strDate!=null&&!strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if(date!=null)
                strDisplayDate = writeFormatActivity.format(date);
        }

        return strDisplayDate;
    }

    public String formatDateActivityMonthYear(String strDate){

        String strDisplayDate="06-03-2016 20:55:00";

        if(strDate!=null&&!strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if(date!=null)
                strDisplayDate = writeFormatActivityMonthYear.format(date);
        }

        return strDisplayDate;
    }

    public void toast(int type, int duration, String message) {

        String strColor = "#ffffff";

        if (type == 2)
            strColor = "#fcc485";

        try {
            LayoutInflater inflater = ((Activity) _ctxt).getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) ((Activity) _ctxt).
                    findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);
            text.setTextColor(Color.parseColor(strColor));

            Toast toast = new Toast(_ctxt);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

            if (duration == 2)
                toast.setDuration(Toast.LENGTH_LONG);
            else
                toast.setDuration(Toast.LENGTH_SHORT);

            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_ctxt, message, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    public void fetchCustomer(final ProgressDialog progressDialog, final int iFlag) {

        if (isConnectingToInternet()) {

            Config.fileModels.clear();
            iActivityCount = 0;
            iProviderCount = 0;

            StorageService storageService = new StorageService(_ctxt);

            storageService.findDocsByKeyValue(Config.collectionCustomer, "customer_email",
                    Config.strUserName, new AsyncApp42ServiceApi.App42StorageServiceListener() {
                    @Override
                    public void onDocumentInserted(Storage response) {
                    }

                    @Override
                    public void onUpdateDocSuccess(Storage response) {
                    }

                    @Override
                    public void onFindDocSuccess(Storage response) {

                        if (response != null) {

                            if (response.getJsonDocList().size() > 0) {

                                Storage.JSONDocument jsonDocument = response.getJsonDocList().get(0);

                                String strDocument = jsonDocument.getJsonDoc();

                                try {
                                    Config.jsonCustomer = new JSONObject(strDocument);
                                    createCustomerModel(jsonDocument.getDocId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (iFlag == 1) {
                                    fetchDependents(Config.customerModel.getStrCustomerID(),
                                            progressDialog, 1);
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                toast(2, 2, _ctxt.getString(R.string.error));
                            }
                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            toast(2, 2, _ctxt.getString(R.string.warning_internet));
                        }
                    }

                    @Override
                    public void onInsertionFailed(App42Exception ex) {
                    }

                    @Override
                    public void onFindDocFailed(App42Exception ex) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(ex.getMessage());
                            JSONObject jsonObjectError =
                                    jsonObject.getJSONObject("app42Fault");
                            String strMess = jsonObjectError.getString("details");

                            toast(2, 2, strMess);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onUpdateDocFailed(App42Exception ex) {
                    }
                    });
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }
    }

    public void createCustomerModel(String strDocumentId) {
        try {
            if (Config.jsonCustomer.has("customer_name")) {

                Config.customerModel = new CustomerModel(
                        Config.jsonCustomer.getString("customer_name"),
                        Config.jsonCustomer.getString("paytm_account"),
                        Config.jsonCustomer.getString("customer_profile_url"),
                        Config.jsonCustomer.getString("customer_address"),
                        Config.jsonCustomer.getString("customer_contact_no"),
                        Config.jsonCustomer.getString("customer_email"),
                        strDocumentId, "");

                Config.fileModels.add(new FileModel(Config.customerModel.getStrCustomerID(),
                        Config.jsonCustomer.getString("customer_profile_url"), "IMAGE"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createProviderModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectProvider = new JSONObject(strDocument);

            if (jsonObjectProvider.has("provider_name")) {

                ProviderModel providerModel = new ProviderModel(
                        jsonObjectProvider.getString("provider_name"),
                        jsonObjectProvider.getString("provider_profile_url"), "",
                        jsonObjectProvider.getString("provider_address"),
                        jsonObjectProvider.getString("provider_contact_no"),
                        jsonObjectProvider.getString("provider_email"), strDocumentId);

                Config.providerModels.add(providerModel);

                Config.fileModels.add(new FileModel(strDocumentId,
                        jsonObjectProvider.getString("provider_profile_url"), "IMAGE"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createDependentModel(String strDependentDocId, String strDocument) {

        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

            if (jsonObjectDependent.has("dependent_name")) {

                if (!Config.strDependentIds.contains(strDependentDocId)) {
                    Config.strDependentIds.add(strDependentDocId);

                    DependentModel dependentModel = new DependentModel();

                    dependentModel.setStrIllness(jsonObjectDependent.getString("dependent_illness"));
                    dependentModel.setIntHealthBp(jsonObjectDependent.getInt("health_bp"));
                    dependentModel.setIntHealthHeartRate(jsonObjectDependent.getInt("health_heart_rate"));

                    dependentModel.setStrRelation(jsonObjectDependent.getString("dependent_relation"));
                    dependentModel.setStrAddress(jsonObjectDependent.getString("dependent_address"));
                    dependentModel.setStrNotes(jsonObjectDependent.getString("dependent_notes"));
                    dependentModel.setStrContacts(jsonObjectDependent.getString("dependent_contact_no"));
                    dependentModel.setStrName(jsonObjectDependent.getString("dependent_name"));
                    dependentModel.setStrImageUrl(jsonObjectDependent.getString("dependent_profile_url"));
                    dependentModel.setStrEmail(jsonObjectDependent.getString("dependent_email"));
                    dependentModel.setIntAge(jsonObjectDependent.getInt("dependent_age"));

                    dependentModel.setStrCustomerID(jsonObjectDependent.getString("customer_id"));

                    dependentModel.setStrDependentID(strDependentDocId);

                    Config.dependentNames.add(jsonObjectDependent.getString("dependent_name"));

                    ArrayList<ServiceModel> serviceModels = new ArrayList<>();

                    if (jsonObjectDependent.has("services")) {

                        JSONArray jsonArrayServices = jsonObjectDependent.getJSONArray("services");

                        for (int j = 0; j < jsonArrayServices.length(); j++) {

                            JSONObject jsonObjectService = jsonArrayServices.getJSONObject(j);

                            String strFeatures[] = jsonToStringArray(jsonObjectService.
                                    getJSONArray("service_features"));

                            ServiceModel serviceModel = new ServiceModel(
                                    jsonObjectService.getString("service_name"),
                                    jsonObjectService.getString("service_desc"),
                                    jsonObjectService.getString("updated_date"),
                                    strFeatures,
                                    jsonObjectService.getInt("unit"),
                                    jsonObjectService.getInt("unit_consumed"),
                                    jsonObjectService.getString("service_id"),
                                    jsonObjectService.getString("service_history_id")
                            );

                            serviceModels.add(serviceModel);
                            Config.dependentModels.get(iActivityCount).
                                    setServiceModels(serviceModel);
                        }
                        dependentModel.setServiceModels(serviceModels);
                    }

                    Config.dependentModels.add(dependentModel);

                    Config.fileModels.add(new FileModel(strDependentDocId,
                            dependentModel.getStrImageUrl(), "IMAGE"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createActivityModel(String strActivityId, String strDocument) {
        try {

            JSONObject jsonObjectActivity =
                    new JSONObject(strDocument);

            //log(strDocument, " Activity");

            if (jsonObjectActivity.has("activity_name")) {

                ActivityModel activityModel = new ActivityModel();

                activityModel.setStrActivityName(jsonObjectActivity.getString("activity_name"));
                activityModel.setStrActivityID(strActivityId);
                activityModel.setStrProviderID(jsonObjectActivity.getString("provider_id"));
                activityModel.setStrActivityStatus(jsonObjectActivity.getString("status"));
                activityModel.setStrActivityDesc(jsonObjectActivity.getString("activity_desc"));
                activityModel.setStrActivityMessage(jsonObjectActivity.
                        getString("activity_message"));

                //log(jsonObjectActivity.getString("provider_id"), " PROVIDER ID ");

                if (!Config.strProviderIds.contains(jsonObjectActivity.getString("provider_id")))
                    Config.strProviderIds.add(jsonObjectActivity.getString("provider_id"));

                activityModel.setStrServcieID(jsonObjectActivity.getString("service_id"));
                //activityModel.setStrServiceName(jsonObjectActivity.getString("service_name"));
                //activityModel.setStrServiceDesc(jsonObjectActivity.getString("service_desc"));

                activityModel.setStrActivityDate(jsonObjectActivity.getString("activity_date"));
                activityModel.setStrActivityDoneDate(jsonObjectActivity.
                        getString("activity_done_date"));

                activityModel.setbActivityOverdue(jsonObjectActivity.getBoolean("overdue"));

                activityModel.setStrActivityProviderStatus(jsonObjectActivity.
                        getString("provider_status"));

                String strFeatures[] = jsonToStringArray(jsonObjectActivity.
                        getJSONArray("features"));

                activityModel.setStrFeatures(strFeatures);

                String strFeaturesDone[] = jsonToStringArray(jsonObjectActivity.
                        getJSONArray("features"));

                activityModel.setStrFeaturesDone(strFeaturesDone);

                ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                ArrayList<VideoModel> videoModels = new ArrayList<>();
                ArrayList<ImageModel> imageModels = new ArrayList<>();

                if (jsonObjectActivity.has("feedbacks")) {

                    JSONArray jsonArrayFeedback = jsonObjectActivity.
                            getJSONArray("feedbacks");

                    for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                        JSONObject jsonObjectFeedback =
                                jsonArrayFeedback.getJSONObject(k);

                        FeedBackModel feedBackModel = new FeedBackModel(
                                jsonObjectFeedback.getString("feedback_message"),
                                jsonObjectFeedback.getString("feedback_by"),
                                jsonObjectFeedback.getInt("feedback_rating"),
                                jsonObjectFeedback.getBoolean("feedback_report"),
                                jsonObjectFeedback.getString("feedback_time"),
                                jsonObjectFeedback.getString("feedback_by_type"));

                        feedBackModels.add(feedBackModel);
                    }
                    activityModel.setFeedBackModels(feedBackModels);
                }

                if (jsonObjectActivity.has("videos")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            getJSONArray("videos");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectVideo = jsonArrayVideos.
                                getJSONObject(k);

                        VideoModel videoModel = new VideoModel(
                                jsonObjectVideo.getString("video_name"),
                                jsonObjectVideo.getString("video_url"),
                                jsonObjectVideo.getString("video_description"),
                                jsonObjectVideo.getString("video_taken"));

                        Config.fileModels.add(new FileModel(jsonObjectVideo.getString("video_name"),
                                jsonObjectVideo.getString("video_url"), "VIDEO"));

                        videoModels.add(videoModel);
                    }
                    activityModel.setVideoModels(videoModels);
                }

                if (jsonObjectActivity.has("images")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            getJSONArray("images");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectImage = jsonArrayVideos.
                                getJSONObject(k);

                        ImageModel imageModel = new ImageModel(
                                jsonObjectImage.getString("image_name"),
                                jsonObjectImage.getString("image_url"),
                                jsonObjectImage.getString("image_description"),
                                jsonObjectImage.getString("image_taken"));

                        Config.fileModels.add(new FileModel(jsonObjectImage.getString("image_name"),
                                jsonObjectImage.getString("image_url"), "IMAGE"));

                        imageModels.add(imageModel);
                    }
                    activityModel.setImageModels(imageModels);
                }

                Config.dependentModels.get(iActivityCount).
                        setActivityModels(activityModel);

                //Config.activityModels.add(activityModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchDependents(String strCustomerId, final ProgressDialog progressDialog, final int iFlag) {

        if (isConnectingToInternet()) {

            StorageService storageService = new StorageService(_ctxt);

            storageService.findDocsByKeyValue(Config.collectionDependent, "customer_id",
                    strCustomerId, new AsyncApp42ServiceApi.App42StorageServiceListener() {

                        @Override
                        public void onDocumentInserted(Storage response) {
                        }

                        @Override
                        public void onUpdateDocSuccess(Storage response) {
                        }

                        @Override
                        public void onFindDocSuccess(Storage response) {

                            if (response != null) {

                                if (response.getJsonDocList().size() > 0) {

                                    Config.strDependentIds.clear();
                                    Config.dependentModels.clear();

                                    for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                                get(i);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strDependentDocId = jsonDocument.getDocId();
                                        createDependentModel(strDependentDocId, strDocument);
                                    }

                                    /**/

                                    if (iFlag == 1)
                                        fetchLatestActivities(progressDialog);
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onInsertionFailed(App42Exception ex) {
                        }

                        @Override
                        public void onFindDocFailed(App42Exception ex) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(ex.getMessage());
                                JSONObject jsonObjectError =
                                        jsonObject.getJSONObject("app42Fault");
                                String strMess = jsonObjectError.getString("details");

                                toast(2, 2, strMess);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        @Override
                        public void onUpdateDocFailed(App42Exception ex) {
                        }
                    });

        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }
    }

    public Query generateQuery(String strStatus) {

        /*Calendar calendar = Calendar.getInstance();
        String value1 = convertDateToString(calendar.getTime());*/

        String key2 = "dependent_id";
        String value2 = Config.strDependentIds.get(iActivityCount);

        //Query q1 = QueryBuilder.build(strKey1, value1, operator);//todo check logic

        // Build query q1 for key1 equal to name and value1 equal to Nick
        Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
        // Build query q2 for key2 equal to age and value2

        Query q3 = QueryBuilder.build("status", strStatus, QueryBuilder.Operator.EQUALS);
        Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

        return q4; //query
    }

    public void fetchLatestActivities(final ProgressDialog progressDialog) {

        if (iActivityCount < Config.strDependentIds.size()) {

            Config.activityModels.clear();

            //log(String.valueOf(iActivityCount), " A Count 0 ");

            if (isConnectingToInternet()) {

                StorageService storageService = new StorageService(_ctxt);

                Query query = generateQuery("completed");

                int max = 1;
                int offset = 0;

                /*HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
                otherMetaHeaders.put("orderByDescending", "activity_done_date");// Use orderByDescending

                //Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

                storageService.setOtherMetaHeaders(otherMetaHeaders);*/

                //log(query.get(), " QUERY ");

                storageService.findDocsByQueryOrderBy(Config.collectionActivity, query, max, offset,
                        "activity_done_date", 1, //1 for descending
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                Storage response = (Storage) o;

                                if (response != null) {

                                    //log(response.toString(), " RESPONSE ");

                                    if (response.getJsonDocList().size() > 0) {

                                        for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                            Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                                    get(i);

                                            String strDocument = jsonDocument.getJsonDoc();
                                            String strActivityId = jsonDocument.getDocId();
                                            createActivityModel(strActivityId, strDocument);
                                        }

                                    }/* else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        toast(2, 2, _ctxt.getString(R.string.error));
                                    }*/
                                    fetchLatestActivitiesUpcoming(progressDialog);
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                try {
                                    if (e != null) {
                                        JSONObject jsonObject = new JSONObject(e.getMessage());
                                        JSONObject jsonObjectError =
                                                jsonObject.getJSONObject("app42Fault");
                                        int appErrorCode = jsonObjectError.getInt("appErrorCode");

                                        if (appErrorCode == 2608) {
                                            fetchLatestActivitiesUpcoming(progressDialog);
                                        }

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }
        }
    }
    //

    public void fetchProviders(final ProgressDialog progressDialog) {

        //log(String.valueOf(iProviderCount + "g " + Config.strProviderIds.size()), " Count -1");

        if (iProviderCount < Config.strProviderIds.size()) {

            if (isConnectingToInternet()) {

                StorageService storageService = new StorageService(_ctxt);

                // log(String.valueOf(iProviderCount), " Count ");

                storageService.findDocsByIdApp42CallBack(Config.strProviderIds.get(iProviderCount),
                        Config.collectionProvider, new App42CallBack() {
                            @Override
                            public void onSuccess(Object o) {

                                Storage storage = (Storage) o;

                                if (storage != null) {

                                    if (storage.getJsonDocList().size() > 0) {

                                        Storage.JSONDocument jsonDocument = storage.getJsonDocList().
                                                get(0);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strProviderDocId = jsonDocument.getDocId();
                                        createProviderModel(strProviderDocId, strDocument);
                                    }

                                    iProviderCount++;

                                    //log(String.valueOf(iProviderCount), " Count 0 ");

                                    if (iProviderCount == Config.strProviderIds.size()) {

                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        goToDashboard();

                                    } else fetchProviders(progressDialog);

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                try {

                                    if (e != null) {

                                        JSONObject jsonObject = new JSONObject(e.getMessage());
                                        JSONObject jsonObjectError =
                                                jsonObject.getJSONObject("app42Fault");

                                        int appErrorCode = jsonObjectError.getInt("appErrorCode");

                                        //log(e.getMessage(), " MESS ");

                                        if (appErrorCode == 2608 || appErrorCode == 2600) {
                                            iProviderCount++;
                                        }

                                        if (iProviderCount == Config.strProviderIds.size()) {

                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            goToDashboard();

                                        } else fetchProviders(progressDialog);

                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                );
            } else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }

        } else {
            //log(String.valueOf(iProviderCount + "g " + Config.strProviderIds.size()), " Count -2");
            if (iProviderCount == Config.strProviderIds.size()) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                goToDashboard();
            }
        }
    }

    public void goToDashboard() {

        toast(1, 1, _ctxt.getString(R.string.success_login));

        Config.intSelectedMenu = Config.intDashboardScreen;
        Config.boolIsLoggedIn = true;

        Intent intent = new Intent(_ctxt, DashboardActivity.class);
        _ctxt.startActivity(intent);
    }

    public void fetchLatestActivitiesUpcoming(final ProgressDialog progressDialog) {

        if (isConnectingToInternet()) {

            StorageService storageService = new StorageService(_ctxt);

            Query query = generateQuery("upcoming");

            int max = 1;
            int offset = 0;

          /*  HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
            otherMetaHeaders.put("orderByAscending", "activity_date");// Use orderByDescending

            //Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

            storageService.setOtherMetaHeaders(otherMetaHeaders);*/

            storageService.findDocsByQueryOrderBy(Config.collectionActivity, query, max, offset,
                    "activity_date", 0, //1 for descending
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            Storage response = (Storage) o;

                            if (response != null) {

                                if (response.getJsonDocList().size() > 0) {

                                    for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                                get(i);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strActivityId = jsonDocument.getDocId();
                                        createActivityModel(strActivityId, strDocument);
                                    }


                                } /*else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    toast(2, 2, _ctxt.getString(R.string.error));
                                }*/

                                /*Config.dependentModels.get(iActivityCount).
                                        setActivityModels(Config.activityModels);*/

                                iActivityCount++;

                                //log(String.valueOf(iActivityCount), " A Count 1 ");

                                if (iActivityCount == Config.strDependentIds.size())
                                    fetchProviders(progressDialog);
                                else
                                    fetchLatestActivities(progressDialog);

                            } else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            try {
                                if (e != null) {
                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError =
                                            jsonObject.getJSONObject("app42Fault");

                                    int appErrorCode = jsonObjectError.getInt("appErrorCode");

                                   /* Config.dependentModels.get(iActivityCount).
                                            setActivityModels(Config.activityModels);*/

                                    if (appErrorCode == 2608) {
                                        iActivityCount++;
                                    }
                                    //log(String.valueOf(iActivityCount + " E " + Config.strDependentIds.size()), " A Count 2 ");

                                    if (iActivityCount == Config.strDependentIds.size())
                                        fetchProviders(progressDialog);
                                    else
                                        fetchLatestActivities(progressDialog);

                                    //log(e.getMessage(), " test 2");

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
            );
        } else {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }
    }

    //Application Specig=fic End
}
