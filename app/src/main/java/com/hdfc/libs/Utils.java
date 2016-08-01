package com.hdfc.libs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hdfc.adapters.NotificationAdapter;
import com.hdfc.app42service.App42GCMService;
import com.hdfc.app42service.StorageService;
import com.hdfc.app42service.UserService;
import com.hdfc.caretaker.DashboardActivity;
import com.hdfc.caretaker.LoginActivity;
import com.hdfc.caretaker.R;
import com.hdfc.caretaker.SignupActivity;
import com.hdfc.caretaker.fragments.ActivityFragment;
import com.hdfc.caretaker.fragments.NotificationFragment;
import com.hdfc.config.CareTaker;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.CategoryServiceModel;
import com.hdfc.models.CheckInCareActivityModel;
import com.hdfc.models.CheckInCareModel;
import com.hdfc.models.ClientModel;
import com.hdfc.models.CustomerModel;
import com.hdfc.models.DependentModel;
import com.hdfc.models.FeedBackModel;
import com.hdfc.models.FieldModel;
import com.hdfc.models.FileModel;
import com.hdfc.models.ImageModel;
import com.hdfc.models.ImageModelCheck;
import com.hdfc.models.MilestoneModel;
import com.hdfc.models.NotificationModel;
import com.hdfc.models.PictureModel;
import com.hdfc.models.ProviderModel;
import com.hdfc.models.ServiceModel;
import com.hdfc.models.SubActivityModel;
import com.hdfc.models.VideoModel;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by balamurugan@adstringo.in on 23-12-2015.
 */
public class Utils {

    public static String defaultDate = "2016-01-01T06:04:57.691Z";
    //application specific
    public static Locale locale = Locale.ENGLISH;
    public final static SimpleDateFormat readFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
    public final static SimpleDateFormat readFormatDB =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
    public final static SimpleDateFormat readFormatDate =
            new SimpleDateFormat("yyyy-MM-dd", locale);
    public final static SimpleDateFormat writeFormatMonth =
            new SimpleDateFormat("MMM yyyy", locale);
    public final static SimpleDateFormat writeFormat =
            new SimpleDateFormat("kk:mm dd MMM yyyy", locale);
    public final static SimpleDateFormat writeFormatDateDB = new
            SimpleDateFormat("yyyy-MM-dd", locale);
    public final static SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", locale);

    /*   public final static SimpleDateFormat writeFormatActivity =
               new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Config.locale);*/
    public final static SimpleDateFormat writeFormatActivityYear =
            new SimpleDateFormat("dd/MM/yyyy", locale);
    private final static SimpleDateFormat queryFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
    private final static SimpleDateFormat queryFormatDB =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
    public static Uri customerImageUri = null;
    public static int iProviderCount = 0;
    public static Bitmap noBitmap;
    public static int iActivityCount = 0;
    private static Handler threadHandler;
    private static ProgressDialog progressDialog;
    private static Context _ctxt;
    private static SessionManager sessionManager;

    static {
        System.loadLibrary("stringGen");
    }

    private boolean showCheckInButton = false;
    private boolean isUpdateServer = false;
    private Date dat;
    private List<String> dependentsIdsList;

    public Utils(Context context) {
        _ctxt = context;
        sessionManager = new SessionManager(context);
        dat = new Date();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Config.intScreenWidth = metrics.widthPixels;
        Config.intScreenHeight = metrics.heightPixels;

        readFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        queryFormat.setTimeZone(TimeZone.getDefault());

        try {
            noBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_icon);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static native String getString();

    public static String getStringJni() {
        //return "KaEO19Fc"; //"KaEO19Fc"
        return getString();//for temp fix on Native crash
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
                                //file
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

   /* public static void hideSoftKeyboard(Activity activity) {
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
    }*/

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

    public static void logout(Context context) {
        try {
            CareTaker.dbCon.delete(DbHelper.strTableNameCollection, null, null);
            //todo clear shared pref.
            SessionManager sessionManager = new SessionManager(context);
            sessionManager.logoutUser();
            //sessionManager = null;
            Config.intSelectedMenu = 0;
            //Config.intDependentsCount = 0;

            //Config.serviceModels.clear();
            Config.dependentNames.clear();
            Config.dependentModels.clear();
            Config.strDependentIds.clear();

            Config.categoryServiceModels.clear();
            Config.strNotificationIds.clear();
            Config.strServcieIds.clear();
            Config.strActivityIds.clear();
            Config.strProviderIds.clear();
            Config.strProviderIdsAdded.clear();
            Config.strServiceCategoryNames.clear();

            Config.intSelectedDependent = 0;

            Config.boolIsLoggedIn = false;

            // Config.customerModel = null;
            Config.strUserName = "";

            Config.fileModels.clear();

            if (CareTaker.dbCon != null) {
                //CareTaker.dbCon.close();
            }
            unregisterGcm();

            File fileImage = createFileInternal("images/");
            deleteAllFiles(fileImage);

            Intent dashboardIntent = new Intent(context, LoginActivity.class);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(dashboardIntent);
            ((Activity) context).finish();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterGcm() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    App42GCMService.unRegisterGcm();
                } catch (Exception bug) {
                    bug.printStackTrace();
                }
            }
        });
        thread.start();
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

    public static void refreshNotifications() {

        if (NotificationFragment.listViewActivities != null) {
            NotificationFragment.notificationAdapter = new NotificationAdapter(_ctxt,
                    Config.dependentModels.get(Config.intSelectedDependent).getNotificationModels());

            NotificationFragment.listViewActivities.
                    setAdapter(NotificationFragment.notificationAdapter);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        //////////////
       /* view = listAdapter.getView(0, view, listView);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
        view.measure(widthMeasureSpec, heightMeasureSpec);*/
        //////////////

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.EXACTLY);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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

    public String convertDateToStringFormat(Date dtDate, SimpleDateFormat simpleDateFormat) {

        String date = null;

        try {
            date = simpleDateFormat.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.i("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public String getMonthLastDate(String strFromDate) {

        String strLastDateMonth = "";

        Date today = null;
        try {
            today = readFormatDate.parse(strFromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        strLastDateMonth = dateFormat.format(lastDayOfMonth); // + "T05:29:59.999Z"
        log(strLastDateMonth, "LAST DATE ");
        //05:29:59.999

        return strLastDateMonth;
    }

    public Date convertStringToDate(String strDate) {

        Date date = null;
        try {
            // 2016-07-14T14:24:36.000+0530
            date = readFormat.parse(strDate);
            //Log.i("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        return date; //
    }

    /*private void updateView(int index, ListView listView) {
        View v = listView.getChildAt(index -
                listView.getFirstVisiblePosition());

        if (v == null)
            return;

        //TextView someText = (TextView) v.findViewById(R.id.sometextview);
        //someText.setText("Hi! I updated you manually!");
    }*/

    /**
     * Method to extract the user's age from the entered Date of Birth.
     *
     * @return ageS String The user's age in years based on the supplied DoB.
     */
    public String getAge(Date date) {

        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();

        dob.setTime(date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = age;

        return ageInt.toString();
    }

    public boolean isConnectingToInternet() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
    public boolean isValidAreaCode(String number) {
        if (number == null) {
            return false;
        } else {

            return number.length() > 1;
        }

    }

    public boolean validCellPhone(String number) {
        //return android.util.Patterns.PHONE.matcher(number).matches();

        boolean isValid = false;

//        if (number.length() >= 6 && number.length() <= 15)
//            isValid = true;

        if (number == null) {
            return false;
        } else {
            if (number.length() < 6 || number.length() > 13) {
                return false;
            } else {
                return android.util.Patterns.PHONE.matcher(number).matches();
            }
        }

    }

    public boolean validateMobile(String number) {
        boolean isValid = false;

        if (number.length() == 10)
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

                } else if (items[item].equals(_ctxt.getResources().
                        getString(R.string.choose_library))) {
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
        string = string.trim().replace(" ", "_");
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

    public int[] jsonToIntArray(JSONArray jsonArray) {

        int ints[] = new int[0];

        try {
            int iLength = jsonArray.length();

            ints = new int[iLength];

            for (int i = 0; i < iLength; i++) {
                ints[i] = jsonArray.getInt(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ints;
    }

    //Application Specigfic Start

    public JSONArray stringToJsonArray(String string[]) {

        JSONArray jsonArray = new JSONArray();

        try {
            int iLength = string.length;

            for (int i = 0; i < iLength; i++) {
                jsonArray.put(string[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public JSONArray intToJsonArray(int ints[]) {

        JSONArray jsonArray = new JSONArray();

        try {
            int iLength = ints.length;

            for (int i = 0; i < iLength; i++) {
                jsonArray.put(ints[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    //load image from url
    public void loadImageFromWeb(String strFileName, String strFileUrl) {

        strFileName = replaceSpace(strFileName);
        strFileUrl = replaceSpace(strFileUrl);

        File fileImage = createFileInternal("images/" + strFileName);

        log(strFileName + " ~ " + fileImage.lastModified() + " url " + strFileUrl, " Load Image ");

        if (fileImage.length() <= 0) {

            InputStream input;
            try {

                URL url = new URL(strFileUrl);
                //URLEncoder.encode(fileModel.getStrFileUrl(), "UTF-8")
                input = url.openStream();
                byte[] buffer = new byte[1500];
                OutputStream output = new FileOutputStream(fileImage);
                try {
                    int bytesRead;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                    log(" Done ", " Load Image ");
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

    public void populateHeaderDependents(final LinearLayout dynamicUserTab,
                                         final int intWhichScreen) {

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

        if (dynamicUserTab != null)
            dynamicUserTab.removeAllViews();

        Config.intSelectedDependent = 0;

        if (intWhichScreen == Config.intNotificationScreen)
            loadDependentData(intWhichScreen);

        for (int i = 0; i < Config.dependentModels.size(); i++) {

            Button bt = new Button(_ctxt);
            bt.setId(i);
            bt.setText((Config.dependentModels.get(i).getStrName()).toUpperCase());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    tabWidth, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(1, 10, 7, 0);
            bt.setLayoutParams(params);
            bt.setAllCaps(true);
            bt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            bt.setTextColor(_ctxt.getResources().getColor(R.color.colorBlackDark));
            // bt.setTextColor(Color.parseColor("white"));
            bt.setTextAppearance(_ctxt, android.R.style.TextAppearance_Medium);

            if (i == 0)
                bt.setBackgroundResource(R.drawable.tab_selected);
            else
                bt.setBackgroundResource(R.drawable.tab_normal);


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

            bt.setTextColor(_ctxt.getResources().getColor(R.color.colorBlackDark));

            if (dynamicUserTab != null)
                dynamicUserTab.addView(bt);
        }
    }

    public void loadDependentData(int intWhichScreen) {

        if (intWhichScreen == Config.intNotificationScreen) {
            loadNotifications();
        }

        if (intWhichScreen == Config.intListActivityScreen ||
                intWhichScreen == Config.intActivityScreen) {
            ActivityFragment.reload();
        }
    }

    public void loadNotifications() {

        final ProgressDialog progressDialog = new ProgressDialog(_ctxt);
        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

        if ((sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {

            try {

                // WHERE  clause
                String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                // WHERE clause arguments
                String[] selectionArgs = {Config.collectionNotification, Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()};
                Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, "datetime(" + DbHelper.COLUMN_UPDATE_DATE + ") DESC", null, false, null, null);
                Log.i("TAG", "Cursor count:" + cursor.getCount());
                if (cursor != null) {
                    cursor.moveToFirst();

                    do {

                        createNotificationModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                refreshNotifications();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        try {
            if (isConnectingToInternet()) {


                /*progressDialog.setMessage(_ctxt.getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();*/

                StorageService storageService = new StorageService(_ctxt);
             /*   storageService.findDocsByKeyValue(Config.collectionNotification,
                        "user_id",
                        Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID(),
                        new AsyncApp42ServiceApi.App42StorageServiceListener()*/
                Query finalQuery;
                Query q1 = QueryBuilder.build("user_id", Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID(), QueryBuilder.Operator.EQUALS);
                if ((sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                    String defaultDate = null;
                    Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionNotification, Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID());
                    if (cursorData != null && cursorData.getCount() > 0) {
                        cursorData.moveToFirst();
                        defaultDate = cursorData.getString(0);
                        cursorData.close();
                    } else {
                        defaultDate = Utils.defaultDate;
                    }
                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                    finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                } else {
                    finalQuery = q1;
                }


                storageService.findDocsByQueryOrderBy(Config.collectionNotification, finalQuery, 3000, 0,
                        "time", 1, new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                if (o != null) {

                                    Storage storage = (Storage) o;

                                    //Utils.log(storage.toString(), "not ");
                                    try {

                                        if (storage.getJsonDocList().size() > 0) {
                                            CareTaker.dbCon.beginDBTransaction();
                                            ArrayList<Storage.JSONDocument> jsonDocList = storage.
                                                    getJsonDocList();

                                            for (int i = 0; i < jsonDocList.size(); i++) {
                                                String values[] = {jsonDocList.get(i).getDocId(), jsonDocList.get(i).getUpdatedAt(), jsonDocList.get(i).getJsonDoc(), Config.collectionNotification, Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID(), "1", "", ""};
                                                if ((sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {jsonDocList.get(i).getDocId(), Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                } else {


                                                    CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                                    createNotificationModel(jsonDocList.get(i).getDocId(),
                                                            jsonDocList.get(i).getJsonDoc());
                                                }

                                            }

                                            iProviderCount = 0;
                                            CareTaker.dbCon.dbTransactionSucessFull();

                                            //fetchProviders(progressDialog, 1);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        CareTaker.dbCon.endDBTransaction();

                                    }
                                    if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                                        List<String> idsList = new ArrayList<String>();

                                        if (sessionManager.getNotificationIds().size() > 0) {
                                            idsList.addAll(sessionManager.getNotificationIds());
                                        }
                                        idsList.add(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID());
                                        sessionManager.saveNotificationIds(idsList);

                                        refreshNotifications();
                                    }

                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    if (sessionManager.getNotificationIds().size() == 0)
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);

                            }

                            @Override
                            public void onException(Exception e) {

                                  /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                if (e != null) {
                                   /* try {
                                           *//* JSONObject jsonObject = new JSONObject(ex.getMessage());
                                            JSONObject jsonObjectError = jsonObject.
                                                    getJSONObject("app42Fault");
                                            String strMess = jsonObjectError.getString("details");

                                            toast(2, 2, strMess);*//*
                                        //toast(2, 2, _ctxt.getString(R.string.error));
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }*/
                                } else if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                                if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                                    refreshNotifications();
                                }

                            }
                        });

            } else {
                /*if (progressDialog.isShowing())
                    progressDialog.dismiss();*/
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                if (!(sessionManager.getNotificationIds().contains(Config.dependentModels.get(Config.intSelectedDependent).getStrDependentID()))) {
                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                    refreshNotifications();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createNotificationModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectProvider = new JSONObject(strDocument);

            if (jsonObjectProvider.has("message")) {

                NotificationModel notificationModel = new NotificationModel(
                        jsonObjectProvider.getString("message"),
                        jsonObjectProvider.getString("time"),
                        jsonObjectProvider.getString("user_type"),
                        jsonObjectProvider.getString("created_by_type"),
                        jsonObjectProvider.getString("user_id"),
                        jsonObjectProvider.getString("created_by"), strDocumentId);

                notificationModel.setStrActivityId(jsonObjectProvider.optString("activity_id"));

                if (jsonObjectProvider.getString("created_by_type").equalsIgnoreCase("provider")) {
                    if (!Config.strProviderIds.contains(jsonObjectProvider.getString("created_by"))) {
                        Config.strProviderIds.add(jsonObjectProvider.getString("created_by"));
                        sessionManager.saveProvidersIds(Config.strProviderIds);
                    }
                }

                if (!Config.strNotificationIds.contains(strDocumentId)) {
                    Config.strNotificationIds.add(strDocumentId);
                    Config.dependentModels.get(Config.intSelectedDependent).
                            setNotificationModels(notificationModel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void updateTabColor(int id, View v) {

        for (int i = 0; i < Config.dependentModels.size(); i++) {
            Button tab = (Button) v.findViewById(i);
            tab.setTextColor(_ctxt.getResources().getColor(R.color.colorBlackDark));
            if (i == id) {
                tab.setBackgroundResource(R.drawable.tab_selected);
                tab.setTypeface(null, Typeface.BOLD);
                //tab.setTextColor(_ctxt.getResources().getColor(R.color.colorWhite));
            } else {
                tab.setBackgroundResource(R.drawable.tab_normal);
                tab.setTypeface(null, Typeface.NORMAL);
                //tab.setTextColor(_ctxt.getResources().getColor(R.color.colorWhite));
            }
        }
    }

   /* public String formatDateActivity(String strDate){

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
    }*/

   /* public int retrieveConfirmDependants() {

        ConfirmViewModel confirmViewModel;

        ConfirmFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (Config.customerModel != null && !Config.customerModel.getStrEmail().equalsIgnoreCase("")) {

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
                        confirmViewModel.setStrRela(dependentModel.getStrRelation());

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);
                        count++;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return count;
    }*/

    public File getInternalFileImages(String strFileName) {

        File file = null;
        try {
            File mFolder = new File(_ctxt.getFilesDir(), "images/");
            file = new File(_ctxt.getFilesDir(), "images/" + strFileName);

            //
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            //

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

    public String formatDate(String strDate) {

        String strDisplayDate = "06-03-2016 20:55:00";

        if (strDate != null && !strDate.equalsIgnoreCase("")) {
            Date date = convertStringToDate(strDate);

            if (date != null)
                strDisplayDate = writeFormat.format(date);
        }

        return strDisplayDate;
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    public void fetchCustomerFromDB() {

        String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

        // WHERE clause arguments
        String[] selectionArgs = {Config.collectionCustomer};
        Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                isUpdateServer = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_IS_UPDATED)));
                createCustomerModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
            } while (cursor.moveToNext());
            cursor.close();
        }

    }

    public void fetchCustomer(final ProgressDialog progressDialog, final int iFlag) {
        if (iFlag == 1) {
            Config.fileModels.clear();
            iActivityCount = 0;
            iProviderCount = 0;
        }
        Utils.progressDialog = progressDialog;

        try {
            if (sessionManager.isLoggedIn() && (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0)) {
                fetchCustomerFromDB();
                if (DashboardActivity.loadingPanel != null) {
                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                }
                if (iFlag == 1) {
                    goToDashboard();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isUpdateServer && isConnectingToInternet()) {
            updateorInsertCustomerData(iFlag);
        }

    }

    public void updateCustomerDetailOnServer() {
        StorageService storageService = new StorageService(_ctxt);

        JSONObject jsonToUpdate = new JSONObject();

        try {
            jsonToUpdate.put("customer_name", Config.customerModel.getStrName());
            jsonToUpdate.put("customer_contact_no", Config.customerModel.getStrContacts());
            jsonToUpdate.put("customer_address", Config.customerModel.getStrAddress());

            jsonToUpdate.put("customer_dob", Config.customerModel.getStrDob());
            jsonToUpdate.put("customer_country", Config.customerModel.getStrCountryCode());
            jsonToUpdate.put("customer_country_code", Config.customerModel.getStrCountryIsdCode());

            jsonToUpdate.put("customer_area_code", Config.customerModel.getStrCountryAreaCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        storageService.updateDocs(jsonToUpdate,
                Config.customerModel.getStrCustomerID(),
                Config.collectionCustomer, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {

                        String values[] = {"false"};
                        try {
                            //Config.jsonCustomer = new JSONObject(strDocument);

                            String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                            // WHERE clause arguments
                            String[] selectionArgs = {Config.collectionCustomer};
                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_update, selectionArgs);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            UserService userService = new UserService(_ctxt);


                            userService.onChangePassword(Config.strUserName, sessionManager.getOldPassword()
                                    , sessionManager.getUserDetails().get(SessionManager.KEY_PASSWORD), new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            // progressDialog.dismiss();

                                            sessionManager.saveOldPassword("");
                                        }

                                        @Override
                                        public void onException(Exception e) {
                                            // progressDialog.dismiss();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onException(Exception e) {
                        //progressDialog.dismiss();
                    }
                });

    }

    public void updateCustomerRegistrationDetailOnServer(boolean status) {
        StorageService storageService = new StorageService(_ctxt);

        JSONObject jsonToUpdate = new JSONObject();

        try {
            jsonToUpdate.put("customer_register", status);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        storageService.updateDocs(jsonToUpdate,
                Config.customerModel.getStrCustomerID(),
                Config.collectionCustomer, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {


                        try {
                            Config.customerModel.setCustomerRegistered(true);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onException(Exception e) {
                        //progressDialog.dismiss();
                    }
                });

    }

    private JSONObject createJson(JSONObject jsonDep, DependentModel dependentMod) {


        DependentModel dependentModel = dependentMod;
        //
        JSONObject jsonDependant = jsonDep;
        try {

            jsonDependant.put("dependent_name", dependentModel.getStrName());

            if (dependentModel.getStrIllness() == null || dependentModel.getStrIllness().equalsIgnoreCase(""))
                dependentModel.setStrIllness("NA");

            if (dependentModel.getStrNotes() == null || dependentModel.getStrNotes().equalsIgnoreCase(""))
                dependentModel.setStrNotes("NA");

            jsonDependant.put("dependent_illness", dependentModel.getStrIllness());

            jsonDependant.put("dependent_address", dependentModel.getStrAddress());
            jsonDependant.put("dependent_email", dependentModel.getStrEmail());

            jsonDependant.put("dependent_notes", dependentModel.getStrNotes());
            jsonDependant.put("dependent_age", dependentModel.getStrAge());
            jsonDependant.put("dependent_dob", dependentModel.getStrDob());
            jsonDependant.put("dependent_contact_no", dependentModel.getStrContacts());

            jsonDependant.put("dependent_profile_url", dependentModel.getStrImageUrl());
            jsonDependant.put("dependent_relation", dependentModel.getStrRelation());
            jsonDependant.put("customer_id", Config.customerModel.getStrCustomerID());

            jsonDependant.put("health_bp", dependentModel.getIntHealthBp());

            Config.dependentNames.add(dependentModel.getStrName());

            jsonDependant.put("health_heart_rate", dependentModel.getIntHealthHeartRate());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonDependant;
    }


    public void updateDependentsDetailOnServer(final DependentModel dependentModel) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    ((Activity) _ctxt).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            StorageService storageService = new StorageService(_ctxt);
                            final String dependentsId = dependentModel.getStrDependentID();
                            JSONObject jsonDependant = new JSONObject();

                            try {
                                createJson(jsonDependant, dependentModel);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //JSONObject jsonToUpdate = new JSONObject();

                            storageService.updateDocs(jsonDependant,
                                    dependentsId,
                                    Config.collectionDependent, new App42CallBack() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            try {
                                                if (o != null) {

                                                    Utils.log(o.toString(), "LOG_DATA");

                                                } else {

                                                }
                                            } catch (Exception e1) {

                                                e1.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onException(Exception e) {
                                        }
                                    });


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void updateorInsertCustomerData(final int iFlag) {
        try {
            if (isConnectingToInternet()) {


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


                                //                        DashboardActivity.loadingPanel.setVisibility(View.GONE);

                                if (response != null) {

                                    if (response.getJsonDocList().size() > 0) {


                                        boolean mIsRegistered = true;

                                        Storage.JSONDocument jsonDocument = response.getJsonDocList().
                                                get(0);


                                        String strDocument = jsonDocument.getJsonDoc();
                                        try {
                                            if (iFlag == 2) {
                                                JSONObject jsonObject = new JSONObject(strDocument);
                                                jsonObject.put("customer_profile_url", Config.customerModel.getStrImgUrl());
                                                strDocument = jsonObject.toString();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String values[] = {jsonDocument.getDocId(), jsonDocument.getUpdatedAt(), strDocument, Config.collectionCustomer, "", "1", "", ""};
                                        try {
                                            //Config.jsonCustomer = new JSONObject(strDocument);

                                            if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {
                                                String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                // WHERE clause arguments
                                                String[] selectionArgs = {jsonDocument.getDocId()};
                                                CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                            } else {
                                                createCustomerModel(jsonDocument.getDocId(), strDocument);
                                                CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                            }


                                            if (iFlag == 1) {
                                                JSONObject jsonObject = new JSONObject(strDocument);

                                                if (jsonObject.has("customer_register"))
                                                    mIsRegistered = jsonObject.getBoolean("customer_register");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {

                                        } else {


                                            sessionManager.saveCustomerId(jsonDocument.getDocId());
                                            if (iFlag == 1) {

                                                if (!mIsRegistered) {
                                                    //todo add logic for taking to dependent add screen
                                                }

                                                if (iFlag == 1) {
                                                    if (DashboardActivity.loadingPanel != null) {
                                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                                    }

                                                }
                                                goToDashboard();
                                            }
                                        }


                                    } else {
                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {

                                        } else {


                                            toast(2, 2, _ctxt.getString(R.string.error));
                                        }
                                    }
                                } else {
                                    if (iFlag == 1) {
                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {

                                        } else {
                                            toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onInsertionFailed(App42Exception ex) {
                            }

                            @Override
                            public void onFindDocFailed(App42Exception ex) {
                                try {
                                    if (progressDialog != null && progressDialog.isShowing())
                                        progressDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (iFlag == 1) {
                                    if (DashboardActivity.loadingPanel != null) {
                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    }

                                    try {
                                        JSONObject jsonObject = new JSONObject(ex.getMessage());
                                        JSONObject jsonObjectError =
                                                jsonObject.getJSONObject("app42Fault");

                                        int appErrorCode = jsonObjectError.getInt("appErrorCode");

                                        String strMess = jsonObjectError.getString("details");
                                        if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {

                                        } else {
                                            if (appErrorCode == 2601)
                                                toast(2, 2, _ctxt.getString(R.string.invalid_credentials));
                                            else
                                                toast(2, 2, strMess);
                                        }

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onUpdateDocFailed(App42Exception ex) {
                            }
                        });
            } else {
                try {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (iFlag == 1) {
                    if (DashboardActivity.loadingPanel != null)
                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                    if (sessionManager.getCustomerId() != null && sessionManager.getCustomerId().length() > 0) {

                    } else {
                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCustomerModel(String strDocumentId, String strDocument) {
        try {
            JSONObject jsonObject = new JSONObject(strDocument);
            //System.out.println("1st Part of obj : " + jsonObject.toString());
            //   if (jsonObject.has("customer_name")) {

            System.out.println("Police : " + jsonObject.getString("customer_name"));
            Config.customerModel = new CustomerModel(
                    jsonObject.getString("customer_name"),
                    jsonObject.getString("paytm_account"),
                    jsonObject.getString("customer_profile_url"),
                    jsonObject.getString("customer_address"),
                    jsonObject.getString("customer_contact_no"),
                    jsonObject.getString("customer_email"),
                    strDocumentId, "");

            //System.out.println("2nd Part of obj : "+jsonObject.toString());

            Config.customerModel.setStrDob(jsonObject.getString("customer_dob"));
            Config.customerModel.setStrCountryCode(jsonObject.getString("customer_country"));
            Config.customerModel.setStrCountryIsdCode(jsonObject.getString("customer_country_code"));
            Config.customerModel.setStrCountryAreaCode(jsonObject.getString("customer_area_code"));
            Config.customerModel.setStrCity(jsonObject.getString("customer_city"));
            Config.customerModel.setStrState(jsonObject.getString("customer_state"));

               /* Config.customerModels.add(Config.customerModel);

                ClientModel clientModel = new ClientModel();
                clientModel.setCustomerModel(Config.customerModel);*/
            Config.fileModels.add(new FileModel(Config.customerModel.getStrCustomerID(), jsonObject.getString("customer_profile_url"), "IMAGE"));
            Config.customerModel.setCustomerRegistered(jsonObject.optBoolean("customer_register"));
            if (isUpdateServer && isConnectingToInternet()) {
                updateCustomerDetailOnServer();
            }

            //     }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createServiceModel(String strDocumentId, JSONObject jsonObject) {

        try {

            ServiceModel serviceModel = new ServiceModel();

            serviceModel.setDoubleCost(jsonObject.optDouble("cost"));
            serviceModel.setStrServiceName(jsonObject.optString("service_name"));
            serviceModel.setiServiceNo(jsonObject.optInt("service_no"));
            serviceModel.setStrCategoryName(jsonObject.optString("category_name"));
            serviceModel.setiUnit(jsonObject.optInt("unit"));
            //  serviceModel.setStrServiceType(jsonObject.optString("service_type"));
            serviceModel.setiUnitValue(jsonObject.optInt("unit_value"));

            if (jsonObject.has("milestones")) {

                JSONArray jsonArrayMilestones = jsonObject.
                        optJSONArray("milestones");

                for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                    JSONObject jsonObjectMilestone =
                            jsonArrayMilestones.optJSONObject(k);

                    MilestoneModel milestoneModel = new MilestoneModel();

                    milestoneModel.setiMilestoneId(jsonObjectMilestone.optInt("id"));
                    milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.optString("status"));
                    milestoneModel.setStrMilestoneName(jsonObjectMilestone.optString("name"));
                    milestoneModel.setStrMilestoneDate(jsonObjectMilestone.optString("date"));
                    // milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));

                    //

                    if (jsonObjectMilestone.has("show")) {

                        try {
                            milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));
                        } catch (Exception e) {
                            boolean b = true;
                            try {
                                if (jsonObjectMilestone.optInt("show") == 0)
                                    b = false;
                                milestoneModel.setVisible(b);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    if (jsonObjectMilestone.has("reschedule")) {

                        try {
                            milestoneModel.setReschedule(jsonObjectMilestone.optBoolean("reschedule"));
                        } catch (Exception e) {
                            boolean b = true;
                            try {
                                if (jsonObjectMilestone.optInt("reschedule") == 0)
                                    b = false;
                                milestoneModel.setReschedule(b);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    if (jsonObjectMilestone.has("scheduled_date"))
                        milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                optString("scheduled_date"));

                    if (jsonObjectMilestone.has("fields")) {

                        JSONArray jsonArrayFields = jsonObjectMilestone.
                                optJSONArray("fields");

                        for (int l = 0; l < jsonArrayFields.length(); l++) {

                            JSONObject jsonObjectField =
                                    jsonArrayFields.optJSONObject(l);

                            FieldModel fieldModel = new FieldModel();

                            fieldModel.setiFieldID(jsonObjectField.optInt("id"));

                            if (jsonObjectField.has("hide"))
                                fieldModel.setFieldView(jsonObjectField.optBoolean("hide"));

                            fieldModel.setFieldRequired(jsonObjectField.optBoolean("required"));
                            fieldModel.setStrFieldData(jsonObjectField.optString("data"));
                            fieldModel.setStrFieldLabel(jsonObjectField.optString("label"));
                            fieldModel.setStrFieldType(jsonObjectField.optString("type"));

                            if (jsonObjectField.has("values")) {

                                fieldModel.setStrFieldValues(jsonToStringArray(jsonObjectField.
                                        optJSONArray("values")));
                            }

                            if (jsonObjectField.has("child")) {

                                fieldModel.setChild(jsonObjectField.optBoolean("child"));

                                if (jsonObjectField.has("child_type"))
                                    fieldModel.setStrChildType(jsonToStringArray(jsonObjectField.
                                            optJSONArray("child_type")));

                                if (jsonObjectField.has("child_value"))
                                    fieldModel.setStrChildValue(jsonToStringArray(jsonObjectField.
                                            optJSONArray("child_value")));

                                if (jsonObjectField.has("child_condition"))
                                    fieldModel.setStrChildCondition(jsonToStringArray(jsonObjectField.
                                            optJSONArray("child_condition")));

                                if (jsonObjectField.has("child_field"))
                                    fieldModel.setiChildfieldID(jsonToIntArray(jsonObjectField.
                                            optJSONArray("child_field")));
                            }
                            ////
                            if (jsonObjectField.has("array_fields")) {

                                try {
                                    fieldModel.setiArrayCount(jsonObjectField.optInt("array_fields"));
                                } catch (Exception e) {
                                    int i = 0;
                                    try {
                                        i = Integer.parseInt(jsonObjectField.optString("array_fields"));
                                        fieldModel.setiArrayCount(i);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                if (jsonObjectField.has("array_type"))
                                    fieldModel.setStrArrayType(jsonToStringArray(jsonObjectField.
                                            optJSONArray("array_type")));

                                if (jsonObjectField.has("array_data"))
                                    fieldModel.setStrArrayData(jsonObjectField.optString("array_data"));

                            }
                            ////

                            milestoneModel.setFieldModel(fieldModel);
                        }
                    }

                    serviceModel.setMilestoneModels(milestoneModel);
                }
            }

            serviceModel.setStrServiceId(strDocumentId);

            if (!Config.strServcieIds.contains(strDocumentId)) {
                //Config.serviceModels.add(serviceModel);
                Config.strServcieIds.add(strDocumentId);

                //
                if (!Config.strServiceCategoryNames.contains(jsonObject.optString("category_name"))) {
                    Config.strServiceCategoryNames.add(jsonObject.optString("category_name"));

                    CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
                    categoryServiceModel.setStrCategoryName(jsonObject.optString("category_name"));
                    categoryServiceModel.setServiceModels(serviceModel);

                    Config.categoryServiceModels.add(categoryServiceModel);
                } else {
                    int iPosition = Config.strServiceCategoryNames.indexOf(jsonObject.optString("category_name"));

                    if (iPosition > 0 && iPosition < Config.categoryServiceModels.size())
                        Config.categoryServiceModels.get(iPosition).setServiceModels(serviceModel);
                }
                //
            }
            Log.i("TAG", "ServiceModel size:" + Config.categoryServiceModels.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createProviderModel(String strDocumentId, String strDocument) {
        try {

            JSONObject jsonObjectProvider = new JSONObject(strDocument);

            if (jsonObjectProvider.has("provider_name")) {

                ProviderModel providerModel = new ProviderModel(
                        jsonObjectProvider.optString("provider_name"),
                        jsonObjectProvider.optString("provider_profile_url"), "",
                        jsonObjectProvider.optString("provider_address"),
                        jsonObjectProvider.optString("provider_contact_no"),
                        jsonObjectProvider.optString("provider_email"), strDocumentId);

                if (!Config.strProviderIdsAdded.contains(strDocumentId)) {

                    Config.strProviderIdsAdded.add(strDocumentId);

                    Config.providerModels.add(providerModel);

                    Config.fileModels.add(new FileModel(strDocumentId,
                            jsonObjectProvider.optString("provider_profile_url"), "IMAGE"));
                } else {
                    int iPosition = Config.strProviderIdsAdded.indexOf(providerModel.getStrProviderId());

                    if (iPosition > -1 && iPosition < Config.providerModels.size()) {
                        Config.providerModels.set(iPosition, providerModel);
                    } else {

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public DependentModel createDependentModel(String strDependentDocId, String strDocument) {
        DependentModel dependentModel = null;
        try {

            JSONObject jsonObjectDependent = new JSONObject(strDocument);

            if (jsonObjectDependent.has("dependent_name")) {

                if (!Config.strDependentIds.contains(strDependentDocId)) {
                    Config.strDependentIds.add(strDependentDocId);
                    sessionManager.saveDependentsIds(Config.strDependentIds);


                    Config.dependentNames.add(jsonObjectDependent.getString("dependent_contact_no"));


                    dependentModel = new DependentModel();

                    dependentModel.setStrIllness(jsonObjectDependent.
                            optString("dependent_illness"));
                    dependentModel.setIntHealthBp(jsonObjectDependent.optInt("health_bp"));
                    dependentModel.setIntHealthHeartRate(jsonObjectDependent.
                            optInt("health_heart_rate"));

                    dependentModel.setStrRelation(jsonObjectDependent.
                            optString("dependent_relation"));
                    dependentModel.setStrAddress(jsonObjectDependent.
                            optString("dependent_address"));
                    dependentModel.setStrNotes(jsonObjectDependent.optString("dependent_notes"));
                    dependentModel.setStrContacts(jsonObjectDependent.
                            optString("dependent_contact_no"));
                    dependentModel.setStrName(jsonObjectDependent.optString("dependent_name"));

                    dependentModel.setStrDob(jsonObjectDependent.getString("dependent_dob"));

                    if (jsonObjectDependent.has("dependent_profile_url")) {
                        dependentModel.setStrImageUrl(jsonObjectDependent.
                                optString("dependent_profile_url"));
                    }

                    dependentModel.setStrEmail(jsonObjectDependent.optString("dependent_email"));
                    dependentModel.setStrAge(jsonObjectDependent.optString("dependent_age"));

                    dependentModel.setStrCustomerID(jsonObjectDependent.optString("customer_id"));

                    dependentModel.setStrDependentID(strDependentDocId);

                    Config.dependentNames.add(jsonObjectDependent.optString("dependent_name"));


                    //ArrayList<ServiceModel> serviceModels = new ArrayList<>();

                    /*if (jsonObjectDependent.has("services")) {

                        JSONArray jsonArrayServices = jsonObjectDependent.optJSONArray("services");

                        for (int j = 0; j < jsonArrayServices.length(); j++) {

                            JSONObject jsonObjectService = jsonArrayServices.optJSONObject(j);

                            String strFeatures[] = jsonToStringArray(jsonObjectService.
                                    optJSONArray("service_features"));

                            ServiceModel serviceModel = new ServiceModel(
                                    jsonObjectService.optString("service_name"),
                                    jsonObjectService.optString("service_desc"),
                                    jsonObjectService.optString("updated_date"),
                                    strFeatures,
                                    jsonObjectService.optInt("unit"),
                                    jsonObjectService.optInt("unit_consumed"),
                                    jsonObjectService.optString("service_id"),
                                    jsonObjectService.optString("service_history_id")
                            );

                            //serviceModels.add(serviceModel);
                            *//*Config.dependentModels.opt(iActivityCount).
                                    setServiceModels(serviceModel);*//*

                            dependentModel.setServiceModel(serviceModel);
                        }
                    }*/


                    Config.dependentModels.add(dependentModel);


                    ClientModel clientModel = new ClientModel();
                    clientModel.setDependentModels(Config.dependentModels);

                    Config.fileModels.add(new FileModel(strDependentDocId,
                            dependentModel.getStrImageUrl(), "IMAGE"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependentModel;
    }


    public void createCheckInCareModel(String strActivityId, String strDocument) {


        try {
            JSONObject jsonObjectCheck = new JSONObject(strDocument);


            CheckInCareModel checkInCareModel = new CheckInCareModel();
            //
            checkInCareModel.setStrName(jsonObjectCheck.optString("check_in_care_name"));
            checkInCareModel.setStrMediaComment(jsonObjectCheck.optString("media_comment"));
            checkInCareModel.setStrProviderID(jsonObjectCheck.optString("provider_id"));
            JSONArray subMainactivities = jsonObjectCheck.optJSONArray("activities");
            JSONArray picture = jsonObjectCheck.optJSONArray("picture");

            try {
                if (picture != null && picture.length() > 0) {

                    for (int m = 0; m < picture.length(); m++) {
                        JSONObject jsonObject = picture.getJSONObject(m);


                        if (jsonObject != null && jsonObject.length() > 0) {

                            PictureModel pictureModel = new PictureModel();

                            pictureModel.setStrStatus(jsonObject.getString("status"));
                            pictureModel.setStrRoomName(jsonObject.getString("room_name"));
                            List<ImageModelCheck> imageModels = new ArrayList<>();

                            JSONArray imageDetails = jsonObject.optJSONArray("pictures_details");
                            for (int k = 0; k < imageDetails.length(); k++) {
                                JSONObject jsonObjectImage = imageDetails.getJSONObject(k);

                                ImageModelCheck imageModelCheck = new ImageModelCheck(jsonObjectImage.optString("image_url"),
                                        jsonObjectImage.optString("description"), jsonObjectImage.optString("date_time"));
                                imageModels.add(imageModelCheck);
                            }

                            pictureModel.setImageModels(imageModels);

                            checkInCareModel.setPictureModel(pictureModel);

                            Config.roomtypeName.add(pictureModel);


                        }

                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            List<CheckInCareActivityModel> checkInCareActivityModels = new ArrayList<CheckInCareActivityModel>();

            try {
                if (subMainactivities != null && subMainactivities.length() > 0) {
                    for (int i = 0; i < subMainactivities.length(); i++) {


                        JSONObject jsonObjectsubactivitites = subMainactivities.getJSONObject(i);
                        if (jsonObjectsubactivitites != null && jsonObjectsubactivitites.length() > 0) {
                            List<SubActivityModel> subActivityModels = new ArrayList<SubActivityModel>();

                            JSONArray subactivities = jsonObjectsubactivitites.optJSONArray("sub_activities");
                            for (int j = 0; j < subactivities.length(); j++) {
                                JSONObject jsonObjectsubactivity = subactivities.getJSONObject(j);

                                SubActivityModel subActivityModel = new SubActivityModel(jsonObjectsubactivity.optString("sub_activity_name"),
                                        jsonObjectsubactivity.optString("status"), jsonObjectsubactivity.optString("due_status"),
                                        jsonObjectsubactivity.optString("due_date"), jsonObjectsubactivity.optString("utility_name"));
                                subActivityModels.add(subActivityModel);
                            }
                            CheckInCareActivityModel checkInCareActivityModel = new CheckInCareActivityModel(jsonObjectsubactivitites.optString("activity_name"), subActivityModels);
                            checkInCareActivityModels.add(checkInCareActivityModel);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }


            checkInCareModel.setCheckInCareActivityModels(checkInCareActivityModels);

            Config.checkInCareActivityNames.add(checkInCareModel);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createActivityModel(String strActivityId, String strDocument, int iFlag, JSONArray jArray) {
        try {

            JSONObject jsonObjectActivity =
                    new JSONObject(strDocument);


            if (jsonObjectActivity.has("activity_name")) {

                ActivityModel activityModel = new ActivityModel();

                activityModel.setStrActivityName(jsonObjectActivity.optString("activity_name"));
                activityModel.setStrActivityID(strActivityId);
                activityModel.setStrProviderID(jsonObjectActivity.optString("provider_id"));
                activityModel.setStrDependentID(jsonObjectActivity.optString("dependent_id"));
                activityModel.setStrustomerID(jsonObjectActivity.optString("customer_id"));
                activityModel.setStrActivityStatus(jsonObjectActivity.optString("status"));
                activityModel.setStrActivityDesc(jsonObjectActivity.optString("activity_desc"));
              /*  activityModel.setStrActivityMessage(jsonObjectActivity.
                        optString("activity_message"));*/

                activityModel.setStrCreatedBy(jsonObjectActivity.optString("created_by"));

                if (!Config.strProviderIds.contains(jsonObjectActivity.optString("provider_id"))) {
                    Config.strProviderIds.add(jsonObjectActivity.optString("provider_id"));
                    sessionManager.saveProvidersIds(Config.strProviderIds);
                }

                activityModel.setStrServcieID(jsonObjectActivity.optString("service_id"));
                activityModel.setStrServiceName(jsonObjectActivity.optString("service_name"));
               /* activityModel.setStrServiceDesc(jsonObjectActivity.optString("service_desc"));*/

                if (jsonObjectActivity.has("activity_date"))
                    activityModel.setStrActivityDate(jsonObjectActivity.optString("activity_date"));
                else
                    activityModel.setStrActivityDate("");

                if (jsonObjectActivity.has("activity_done_date"))
                    activityModel.setStrActivityDoneDate(jsonObjectActivity.
                            optString("activity_done_date"));

                activityModel.setbActivityOverdue(jsonObjectActivity.optBoolean("overdue"));

                activityModel.setStrActivityProviderStatus(jsonObjectActivity.
                        optString("provider_status"));

                activityModel.setStrActivityProviderMessage(jsonObjectActivity.
                        optString("provider_message"));

                ArrayList<FeedBackModel> feedBackModels = new ArrayList<>();
                ArrayList<VideoModel> videoModels = new ArrayList<>();
                ArrayList<ImageModel> imageModels = new ArrayList<>();

                if (jsonObjectActivity.has("feedbacks")) {

                    JSONArray jsonArrayFeedback = jsonObjectActivity.
                            optJSONArray("feedbacks");

                    for (int k = 0; k < jsonArrayFeedback.length(); k++) {

                        JSONObject jsonObjectFeedback =
                                jsonArrayFeedback.optJSONObject(k);

                        if (jsonObjectFeedback.has("feedback_message")) {

                            FeedBackModel feedBackModel = new FeedBackModel(
                                    jsonObjectFeedback.optString("feedback_message"),
                                    jsonObjectFeedback.optString("feedback_by"),
                                    jsonObjectFeedback.optInt("feedback_rating"),
                                    false, //jsonObjectFeedback.optBoolean("feedback_report")
                                    jsonObjectFeedback.optString("feedback_time"),
                                    jsonObjectFeedback.optString("feedback_by_type"));

                            feedBackModels.add(feedBackModel);

                            if (feedBackModel.getStrFeedBackByType().equalsIgnoreCase("customer") &&
                                    feedBackModel.getStrFeedBackBy().equalsIgnoreCase(activityModel.getStrustomerID())) {
                                activityModel.setRatingAddedByCutsm(true);
                            }
                        }
                    }
                    activityModel.setFeedBackModels(feedBackModels);
                }

                if (jsonObjectActivity.has("videos")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            optJSONArray("videos");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectVideo = jsonArrayVideos.
                                optJSONObject(k);

                        if (jsonObjectVideo.has("video_name")) {

                            VideoModel videoModel = new VideoModel(
                                    jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"),
                                    jsonObjectVideo.optString("video_description"),
                                    jsonObjectVideo.optString("video_taken"));

                            Config.fileModels.add(new FileModel(jsonObjectVideo.optString("video_name"),
                                    jsonObjectVideo.optString("video_url"), "VIDEO"));

                            videoModels.add(videoModel);
                        }
                    }
                    activityModel.setVideoModels(videoModels);
                }

                if (jsonObjectActivity.has("images")) {

                    JSONArray jsonArrayVideos = jsonObjectActivity.
                            optJSONArray("images");

                    for (int k = 0; k < jsonArrayVideos.length(); k++) {

                        JSONObject jsonObjectImage = jsonArrayVideos.
                                optJSONObject(k);

                        if (jsonObjectImage.has("image_name")) {

                            ImageModel imageModel = new ImageModel(
                                    jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"),
                                    jsonObjectImage.optString("image_description"),
                                    jsonObjectImage.optString("image_taken"));


                            Config.fileModels.add(new FileModel(jsonObjectImage.optString("image_name"),
                                    jsonObjectImage.optString("image_url"), "IMAGE"));

                            imageModels.add(imageModel);

                        }
                    }
                    activityModel.setImageModels(imageModels);
                }

                //milestones start
                if (jsonObjectActivity.has("milestones") || (jArray != null && jArray.length() > 0)) {
                    JSONArray jsonArrayMilestones;

                    if (jsonObjectActivity.has("milestones")) {
                        jsonArrayMilestones = jsonObjectActivity.
                                optJSONArray("milestones");
                    } else {
                        jsonArrayMilestones = jArray;
                    }


                    for (int k = 0; k < jsonArrayMilestones.length(); k++) {

                        JSONObject jsonObjectMilestone =
                                jsonArrayMilestones.optJSONObject(k);

                        MilestoneModel milestoneModel = new MilestoneModel();

                        milestoneModel.setiMilestoneId(jsonObjectMilestone.optInt("id"));
                        milestoneModel.setStrMilestoneStatus(jsonObjectMilestone.optString("status"));
                        milestoneModel.setStrMilestoneName(jsonObjectMilestone.optString("name"));
                        milestoneModel.setStrMilestoneDate(jsonObjectMilestone.optString("date"));
                        milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));

                        // ArrayList<FileModel> fileModels = new ArrayList<>();

                        if (jsonObjectMilestone.has("files")) {

                            JSONArray jsonArrayMsFiles = jsonObjectMilestone.
                                    optJSONArray("files");

                            for (int m = 0; m < jsonArrayMsFiles.length(); m++) {

                                JSONObject jsonObjectMsFile = jsonArrayMsFiles.
                                        optJSONObject(m);

                                if (jsonObjectMsFile.has("file_name")) {

                                    Utils.log(jsonObjectMsFile.toString(), " JSONOBJECT ");

                                    FileModel fileModel = new FileModel(
                                            jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"),
                                            jsonObjectMsFile.optString("file_type"),
                                            jsonObjectMsFile.optString("file_desc"),
                                            jsonObjectMsFile.optString("file_path"),
                                            jsonObjectMsFile.optString("file_time"));


                                    Config.fileModels.add(new FileModel(jsonObjectMsFile.optString("file_name"),
                                            jsonObjectMsFile.optString("file_url"), jsonObjectMsFile.optString("file_type")));

                                    milestoneModel.setFileModel(fileModel);
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("show")) {

                            try {
                                milestoneModel.setVisible(jsonObjectMilestone.optBoolean("show"));
                            } catch (Exception e) {
                                boolean b = true;
                                try {
                                    if (jsonObjectMilestone.optInt("show") == 0)
                                        b = false;
                                    milestoneModel.setVisible(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("reschedule")) {

                            try {
                                milestoneModel.setReschedule(jsonObjectMilestone.optBoolean("reschedule"));
                            } catch (Exception e) {
                                boolean b = true;
                                try {
                                    if (jsonObjectMilestone.optInt("reschedule") == 0)
                                        b = false;
                                    milestoneModel.setReschedule(b);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        if (jsonObjectMilestone.has("scheduled_date"))
                            milestoneModel.setStrMilestoneScheduledDate(jsonObjectMilestone.
                                    optString("scheduled_date"));

                        if (jsonObjectMilestone.has("fields")) {

                            JSONArray jsonArrayFields = jsonObjectMilestone.
                                    optJSONArray("fields");

                            for (int l = 0; l < jsonArrayFields.length(); l++) {

                                JSONObject jsonObjectField =
                                        jsonArrayFields.optJSONObject(l);

                                FieldModel fieldModel = new FieldModel();

                                fieldModel.setiFieldID(jsonObjectField.optInt("id"));

                                if (jsonObjectField.has("hide"))
                                    fieldModel.setFieldView(jsonObjectField.optBoolean("hide"));

                                fieldModel.setFieldRequired(jsonObjectField.optBoolean("required"));
                                fieldModel.setStrFieldData(jsonObjectField.optString("data"));
                                fieldModel.setStrFieldLabel(jsonObjectField.optString("label"));
                                fieldModel.setStrFieldType(jsonObjectField.optString("type"));

                                if (jsonObjectField.has("values")) {

                                    fieldModel.setStrFieldValues(jsonToStringArray(jsonObjectField.
                                            optJSONArray("values")));
                                }

                                if (jsonObjectField.has("child")) {

                                    fieldModel.setChild(jsonObjectField.optBoolean("child"));

                                    if (jsonObjectField.has("child_type"))
                                        fieldModel.setStrChildType(jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_type")));

                                    if (jsonObjectField.has("child_value"))
                                        fieldModel.setStrChildValue(jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_value")));

                                    if (jsonObjectField.has("child_condition"))
                                        fieldModel.setStrChildCondition(jsonToStringArray(jsonObjectField.
                                                optJSONArray("child_condition")));

                                    if (jsonObjectField.has("child_field"))
                                        fieldModel.setiChildfieldID(jsonToIntArray(jsonObjectField.
                                                optJSONArray("child_field")));
                                }
                                if (jsonObjectField.has("array_fields")) {

                                    try {
                                        fieldModel.setiArrayCount(jsonObjectField.optInt("array_fields"));
                                    } catch (Exception e) {
                                        int i = 0;
                                        try {
                                            i = Integer.parseInt(jsonObjectField.optString("array_fields"));
                                            fieldModel.setiArrayCount(i);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    if (jsonObjectField.has("array_type"))
                                        fieldModel.setStrArrayType(jsonToStringArray(jsonObjectField.
                                                optJSONArray("array_type")));

                                    if (jsonObjectField.has("array_data"))
                                        fieldModel.setStrArrayData(jsonObjectField.optString("array_data"));

                                }
                                ////

                                milestoneModel.setFieldModel(fieldModel);
                            }
                        }
                        activityModel.setMilestoneModel(milestoneModel);
                    }
                }
                //milestones end

                if (iFlag == 0) {
                    if (!Config.strActivityIds.contains(strActivityId)) {

                        if (iActivityCount < Config.dependentModels.size()) {
                            Config.dependentModels.get(iActivityCount).
                                    setActivityModels(activityModel);
                        }

                        Config.strActivityIds.add(strActivityId);
                    }
                }

                if (iFlag == 1) {

                    int iPosition = Config.strDependentIds.indexOf(jsonObjectActivity.optString("dependent_id"));

                    if (iPosition > -1 && iPosition < Config.dependentModels.size())
                        Config.dependentModels.get(iPosition).setMonthActivityModel(activityModel);

                    if (!Config.strActivityIds.contains(strActivityId))
                        Config.strActivityIds.add(strActivityId);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchDependents(String strCustomerId, final ProgressDialog progressDialog,
                                final int iFlag) {


        if (strCustomerId == null || strCustomerId.length() == 0 || (strCustomerId.equalsIgnoreCase(""))) {
            strCustomerId = sessionManager.getCustomerId();
        } else {

        }
        if (sessionManager.getDependentsStatus()) {
            try {

                // WHERE   clause
                String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                // WHERE clause arguments
                String[] selectionArgs = {Config.collectionDependent};
                Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
                Log.i("TAG", "Cursor count:" + cursor.getCount());
                if (cursor != null) {
                    cursor.moveToFirst();
                    do {
                        DependentModel dependentModel = null;
                        dependentModel = createDependentModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                        dependentsIdsList = new ArrayList<>();
                        dependentsIdsList.clear();
                        dependentsIdsList.addAll(sessionManager.getUpdateDependent());
                        if (dependentModel != null && dependentModel.getStrDependentID() != null) {
                            if (isConnectingToInternet() && dependentsIdsList.
                                    contains(dependentModel.getStrDependentID())) {
                                updateDependentsDetailOnServer(dependentModel);
                            }
                        }

                    } while (cursor.moveToNext());
                    cursor.close();

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                fetchProviders(progressDialog, iFlag);
            }

        }


        if (isConnectingToInternet()) {

            String defaultDate = null;
            Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionDependent);
            if (cursorData != null && cursorData.getCount() > 0) {
                cursorData.moveToFirst();
                defaultDate = cursorData.getString(0);
                cursorData.close();
            } else {
                defaultDate = Utils.defaultDate;
            }
            Query finalQuery = null;
            Query q1 = QueryBuilder.build("customer_id", strCustomerId, QueryBuilder.Operator.EQUALS);
            // Build query q2
            if (sessionManager.getDependentsStatus()) {
                Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                finalQuery = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
            } else {
                finalQuery = q1;
            }


            StorageService storageService = new StorageService(_ctxt);

            storageService.findDocsByQuery(Config.collectionDependent, finalQuery, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {

                    Storage storage = (Storage) o;
                    if (o != null) {

                        if (storage.getJsonDocList().size() > 0) {

                            //Utils.log(response.toString(), " 1 ");

                                    /*Config.strDependentIds.clear();
                                    Config.dependentModels.clear();*/


                            try {
                                CareTaker.dbCon.beginDBTransaction();
                                for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                    Storage.JSONDocument jsonDocument = storage.
                                            getJsonDocList().get(i);

                                    String strDocument = jsonDocument.getJsonDoc();
                                    String strDependentDocId = jsonDocument.getDocId();

                                    if (sessionManager.getUpdateDependent().contains(strDependentDocId)) {
                                        Utils.log("in contains index=" + i, "LOG_DATA");
                                        Utils.log("updated id :" + strDependentDocId, "LOG_DATA");
                                        List<String> dependentsIdsList = new ArrayList<String>();
                                        dependentsIdsList.addAll(sessionManager.getUpdateDependent());
                                        Utils.log("List Before :" + dependentsIdsList.toString(), "LOG_DATA");
                                        dependentsIdsList.remove(strDependentDocId);
                                        sessionManager.saveUpdateDependent(dependentsIdsList);
                                        Utils.log("List After :" + dependentsIdsList.toString(), "LOG_DATA");
                                    } else {
                                        String values[] = {strDependentDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionDependent, "", "1", "", ""};

                                        if (sessionManager.getDependentsStatus()) {
                                            String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                            // WHERE clause arguments
                                            String[] selectionArgs = {strDependentDocId};
                                            CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                        } else {

                                            CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                            createDependentModel(strDependentDocId, strDocument);
                                        }
                                    }
                                }
                                CareTaker.dbCon.dbTransactionSucessFull();

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                CareTaker.dbCon.endDBTransaction();

                            }


                            //if (iFlag == 1)
                            //fetchLatestActivities(progressDialog, iFlag);
                            //fetchProviders(progressDialog, iFlag);
                            //if (iFlag == 1)
                            //fetchLatestActivities(progressDialog, iFlag);


                        } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                            DashboardActivity.loadingPanel.setVisibility(View.GONE);
                            if (!sessionManager.getDependentsStatus())
                                toast(2, 2, _ctxt.getString(R.string.error));
                        }

                        if (!sessionManager.getDependentsStatus()) {
                            sessionManager.saveDependentsStatus(true);
                            fetchProviders(progressDialog, iFlag);
                        }

                    } else {
                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                        if (!sessionManager.getDependentsStatus())
                            toast(2, 2, _ctxt.getString(R.string.warning_internet));
                    }

                            /*} else {
                                *//*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*//*
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }*/

                }

                @Override
                public void onException(Exception e) {
                    try {
                        Utils.log(e.getMessage(), " 9 ");
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        JSONObject jsonObjectError =
                                jsonObject.optJSONObject("app42Fault");
                        String strMess = jsonObjectError.optString("details");

                        //toast(2, 2, strMess);

                        //fetchLatestActivities(progressDialog, iFlag);
                        if (!sessionManager.getDependentsStatus()) {
                            fetchProviders(progressDialog, iFlag);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });

//            storageService.findDocsByKeyValue(Config.collectionDependent, "customer_id",
//                    strCustomerId, new AsyncApp42ServiceApi.App42StorageServiceListener() {
//
//                        @Override
//                        public void onDocumentInserted(Storage response) {
//                        }
//
//                        @Override
//                        public void onUpdateDocSuccess(Storage response) {
//                        }
//
//                        @Override
//                        public void onFindDocSuccess(Storage response) {
//
//
//                        }
//
//                        @Override
//                        public void onInsertionFailed(App42Exception ex) {
//                        }
//
//                        @Override
//                        public void onFindDocFailed(App42Exception ex) {
//
//
//
//                        }
//
//                        @Override
//                        public void onUpdateDocFailed(App42Exception ex) {
//                        }
//                    });

        } else {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            if (!sessionManager.getDependentsStatus())
                toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }


    }

    public Query generateQuery(String strStatus) {

        /*Calendar calendar = Calendar.optInstance();
        String value1 = convertDateToString(calendar.optTime());*/

        String key2 = "dependent_id";
        String value2 = Config.strDependentIds.get(iActivityCount);

        //Query q1 = QueryBuilder.build(strKey1, value1, operator);//check logic working

        // Build query q1 for key1 equal to name and value1 equal to Nick
        Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
        // Build query q2 for key2 equal to age and value2

        Query q3 = QueryBuilder.build("status", strStatus, QueryBuilder.Operator.EQUALS);
        Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

        return q4;
    }

    public void fetchLatestActivities(final ProgressDialog progressDialog, final int iFlag) {

        if (iActivityCount < Config.strDependentIds.size()) {

            if (isConnectingToInternet()) {

                DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

                StorageService storageService = new StorageService(_ctxt);

                Query query = generateQuery("completed");

                int max = 1;
                int offset = 0;

                /*HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();
                otherMetaHeaders.put("orderByDescending", "activity_done_date");// Use orderByDescending

                //Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

                storageService.setOtherMetaHeaders(otherMetaHeaders);*/

                //log(query.opt(), " QUERY ");

                storageService.findDocsByQueryOrderBy(Config.collectionActivity, query, max, offset,
                        "activity_done_date", 1, //1 for descending
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {

                                Storage response = (Storage) o;

                                if (response != null) {

                                    Utils.log(response.toString(), " 3 ");

                                    if (response.getJsonDocList().size() > 0) {

                                        for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                            Storage.JSONDocument jsonDocument = response.
                                                    getJsonDocList().get(i);

                                            String strDocument = jsonDocument.getJsonDoc();
                                            String strActivityId = jsonDocument.getDocId();
                                            try {
                                                JSONObject jsonObjectActivity =
                                                        new JSONObject(strDocument);
                                                JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                                jsonObjectActivity.remove("milestones");
                                                strDocument = jsonObjectActivity.toString();
                                                createActivityModel(strActivityId, strDocument, 0, jArray);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                    fetchLatestActivitiesUpcoming(progressDialog, iFlag);
                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            }

                            @Override
                            public void onException(Exception e) {

                                try {
                                    if (e != null) {
                                        JSONObject jsonObject = new JSONObject(e.getMessage());
                                        JSONObject jsonObjectError =
                                                jsonObject.optJSONObject("app42Fault");
                                        int appErrorCode = jsonObjectError.optInt("appErrorCode");

                                        Utils.log(e.getMessage(), " 4 ");

                                        //if (appErrorCode == 2608) {
                                        fetchLatestActivitiesUpcoming(progressDialog, iFlag);
                                        //}

                                    } else {
                                        /*if (progressDialog.isShowing())
                                            progressDialog.dismiss();*/
                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            } else {
                /*if (progressDialog.isShowing())
                    progressDialog.dismiss();*/
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                toast(2, 2, _ctxt.getString(R.string.warning_internet));
            }
        }
    }
    //

    public void fetchProviders(final ProgressDialog progressDialog, final int iFlag) {

        //todo remove this after offline sync enabled
        Config.strProviderIds.clear();
        sessionManager.getProvidersIds().clear();
        Config.strProviderIds.add("5715c39ee4b0d2aca6fe5d17");
        sessionManager.saveProvidersIds(Config.strProviderIds);

        if (Config.strProviderIds.size() > 0) {
            DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
            if (sessionManager.getProviderStatus()) {
                try {

                    // WHERE  clause
                    String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                    // WHERE clause arguments
                    String[] selectionArgs = {Config.collectionProvider};
                    Cursor cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
                    Log.i("TAG", "Cursor count:" + cursor.getCount());
                    if (cursor != null) {
                        cursor.moveToFirst();
                        do {

                            createProviderModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)));
                        } while (cursor.moveToNext());

                        cursor.close();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                // toast(2, 2, _ctxt.getString(R.string.warning_internet));
                if (iFlag == 0)
                    loadImages();
                if (iFlag == 1)
                    refreshNotificationsImages();
                if (iFlag == 2)
                    loadImagesActivityMonth();
            }


            if (isConnectingToInternet()) {

              /*  if (!Config.strProviderIdsAdded.contains(Config.strProviderIds.
                        get(iProviderCount))) {*/
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionProvider);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }


                StorageService storageService = new StorageService(_ctxt);
                Query finalQuery = null;
                Query query = QueryBuilder.build("_id", Config.strProviderIds,
                        QueryBuilder.Operator.INLIST);
                if (sessionManager.getProviderStatus()) {
                    // Build query q2
                    Query q2 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);

                    finalQuery = QueryBuilder.compoundOperator(query, QueryBuilder.Operator.AND, q2);

                } else {
                    finalQuery = query;
                }


                storageService.findDocsByQuery(Config.collectionProvider, finalQuery,
                        new App42CallBack() {

                            @Override
                            public void onSuccess(Object o) {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                try {
                                    if (o != null) {

                                        Utils.log(o.toString(), " Response Success");

                                        Storage storage = (Storage) o;
                                        try {
                                            if (storage.getJsonDocList().size() > 0) {
                                                CareTaker.dbCon.beginDBTransaction();
                                                for (int i = 0; i < storage.getJsonDocList().size(); i++) {

                                                    Storage.JSONDocument jsonDocument = storage.
                                                            getJsonDocList().get(i);

                                                    String strDocument = jsonDocument.getJsonDoc();
                                                    String strProviderDocId = jsonDocument.
                                                            getDocId();

                                                    String values[] = {strProviderDocId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionProvider, "", "1", "", ""};
                                                    if (sessionManager.getProviderStatus()) {

                                                        String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                        // WHERE clause arguments
                                                        String[] selectionArgs = {strProviderDocId};
                                                        CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                    } else {
                                                        CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                                        createProviderModel(strProviderDocId,
                                                                strDocument);
                                                    }


                                                }
                                                CareTaker.dbCon.dbTransactionSucessFull();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            CareTaker.dbCon.endDBTransaction();

                                        }
                                        if (!sessionManager.getProviderStatus()) {
                                            sessionManager.saveProviderStatus(true);
                                            if (iFlag == 0)
                                                loadImages();
                                            if (iFlag == 1)
                                                refreshNotificationsImages();
                                            if (iFlag == 2)
                                                loadImagesActivityMonth();
                                        }
                                    } else if (!sessionManager.getProviderStatus()) {
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (!sessionManager.getProviderStatus())
                                        toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            }

                            @Override
                            public void onException(Exception e) {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                try {
                                    Utils.log(e.getMessage(), " Response Failure");

                                    if (e != null) {
                                        if (iFlag == 0)
                                            loadImages();
                                        if (iFlag == 1)
                                            refreshNotificationsImages();
                                        if (iFlag == 2)
                                            loadImagesActivityMonth();
                                    } else if (!sessionManager.getProviderStatus()) {
                                        toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    if (!sessionManager.getProviderStatus())
                                        toast(2, 2, _ctxt.getString(R.string.error));
                                }
                            }
                        });
            }
        } else {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            if (!sessionManager.getProviderStatus()) {
                if (iFlag == 0)
                    loadImages();
                if (iFlag == 1)
                    refreshNotificationsImages();
                if (iFlag == 2)
                    loadImagesActivityMonth();
            }
        }

    }

    //
    public boolean compressImageFromPath(String strPath, int reqWidth, int reqHeight, int iQuality) {

        boolean b = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(strPath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;

          /*  reqWidth = options.outWidth;
            reqHeight = options.outHeight;*/

            log(String.valueOf(reqWidth), " WIDTH ");
            log(String.valueOf(reqHeight), " HEIGHT ");

            bmp = createScaledBitmap(BitmapFactory.decodeFile(strPath), reqWidth,
                    reqHeight);

            //jpeg compress
            byte[] bmpPicByteArray;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, iQuality, bos);
            bmpPicByteArray = bos.toByteArray();
            bmp.recycle();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(strPath));
                fos.write(bmpPicByteArray);
                fos.flush();
                fos.close();
                bos.reset();

            } catch (IOException e) {
                fos.flush();
                fos.close();
                bos.reset();
                e.printStackTrace();
                b = false;
            }
            //

        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            b = false;
        }

        return b;
    }

    public void loadAllFiles() {
        for (int i = 0; i < Config.fileModels.size(); i++) {
            FileModel fileModel = Config.fileModels.get(i);

            if (fileModel != null && fileModel.getStrFileUrl() != null &&
                    !fileModel.getStrFileUrl().equalsIgnoreCase("")) {

                loadImageFromWeb(fileModel.getStrFileName(),
                        fileModel.getStrFileUrl());
            }
        }
    }

    public void refreshNotificationsImages() {

        /*progressDialog = new ProgressDialog(_ctxt);
        progressDialog.setMessage(_ctxt.getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();
*/
        if (Config.intSelectedMenu == Config.intNotificationScreen)
            refreshNotifications();

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    public void goToDashboard() {

        try {
            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Config.intSelectedMenu = Config.intDashboardScreen;

            toast(1, 1, _ctxt.getString(R.string.success_login));
            Config.boolIsLoggedIn = true;
            Intent intent = new Intent(_ctxt, DashboardActivity.class);
            _ctxt.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImages() {

       /* progressDialog = new ProgressDialog(_ctxt);
        progressDialog.setMessage(_ctxt.getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

        threadHandler = new ThreadHandler();

        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    public void loadImagesActivityMonth() {

        /*progressDialog = new ProgressDialog(_ctxt);
        progressDialog.setMessage(_ctxt.getString(R.string.uploading_image));
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

        if (Config.intSelectedMenu == Config.intActivityScreen
                || Config.intSelectedMenu == Config.intListActivityScreen) {
            ActivityFragment.reload();
        }

        threadHandler = new ThreadHandler();
        Thread backgroundThread = new BackgroundThread();
        backgroundThread.start();
    }

    public void fetchLatestActivitiesUpcoming(final ProgressDialog progressDialog, final int iFlag) {

        if (isConnectingToInternet()) {

            DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
            StorageService storageService = new StorageService(_ctxt);

            String key2 = "dependent_id";
            String value2 = "";

            if (iActivityCount <= Config.strDependentIds.size()) {
                value2 = Config.strDependentIds.get(iActivityCount);
            }

           /* Query q1 = QueryBuilder.build("provider_status", "scheduled", QueryBuilder.
                    Operator.EQUALS);*/

            // Build query q1 for key1 equal to name and value1 equal to Nick
            Query q2 = QueryBuilder.build(key2, value2, QueryBuilder.Operator.EQUALS);
            // Build query q2 for key2 equal to age and value2

            Query q3 = QueryBuilder.build("status", "new", QueryBuilder.Operator.EQUALS);
            Query q31 = QueryBuilder.build("status", "inprocess", QueryBuilder.Operator.EQUALS);
            Query q32 = QueryBuilder.build("status", "open", QueryBuilder.Operator.EQUALS);

            Query q33 = QueryBuilder.compoundOperator(q3, QueryBuilder.Operator.OR, q31);
            Query q34 = QueryBuilder.compoundOperator(q33, QueryBuilder.Operator.OR, q32);


            //Query q4 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);

            Query q5 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q34);

            int max = 1;
            int offset = 0;

            storageService.findDocsByQueryOrderBy(Config.collectionActivity, q5, max, offset,
                    "activity_date", 0, //1 for descending
                    new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {

                            Storage response = (Storage) o;

                            if (response != null) {

                                Utils.log(response.toString(), " 5 ");

                                if (response.getJsonDocList().size() > 0) {

                                    for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                        Storage.JSONDocument jsonDocument = response.
                                                getJsonDocList().get(i);

                                        String strDocument = jsonDocument.getJsonDoc();
                                        String strActivityId = jsonDocument.getDocId();
                                        try {
                                            JSONObject jsonObjectActivity =
                                                    new JSONObject(strDocument);
                                            JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                            jsonObjectActivity.remove("milestones");
                                            strDocument = jsonObjectActivity.toString();
                                            createActivityModel(strActivityId, strDocument, 0, jArray);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                iActivityCount++;

                                if (iActivityCount == Config.strDependentIds.size())
                                    fetchProviders(progressDialog, 0);
                                else
                                    fetchLatestActivities(progressDialog, iFlag);

                            } else {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                        }

                        @Override
                        public void onException(Exception e) {

                            try {
                                if (e != null) {

                                    Utils.log(e.getMessage(), " 1 ");

                                    JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError =
                                            jsonObject.getJSONObject("app42Fault");

                                    int appErrorCode = jsonObjectError.getInt("appErrorCode");

                                   /* Config.dependentModels.get(iActivityCount).
                                            setActivityModels(Config.activityModels);*/

                                    if (appErrorCode == 2608) { //no document exists for query
                                        iActivityCount++;
                                    }

                                    if (iActivityCount == Config.strDependentIds.size())
                                        fetchProviders(progressDialog, iFlag);
                                    else
                                        fetchLatestActivities(progressDialog, iFlag);

                                    //log(e.getMessage(), " test 2");

                                } else {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
            );
        } else {
            /*if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }
    }

    public void clearActivityMonthModel() {
        try {
            for (DependentModel dependentModel : Config.dependentModels)
                dependentModel.clearMonthActivityModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertDateToStringQuery(Date dtDate) {

        String date = null;

        try {
            date = readFormat.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public String convertDateToStringQueryDB(Date dtDate) {

        String date = null;

        try {
            date = readFormatDB.format(dtDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public Date convertStringToDateQuery(String strDate) {

        Date date = null;

        try {
            date = queryFormat.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public Date convertStringToDateQueryDB(String strDate) {

        Date date = null;

        try {
            date = queryFormatDB.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log("Utils", String.valueOf(date)); //Mon Sep 14 00:00:00 IST 2015
        return date; //
    }

    public void fetchLatestActivitiesByMonth(int iMonth, int iYear,
                                             final ProgressDialog progressDialog) {

        DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);
        iMonth = iMonth; // - 1

        String strMonth = String.valueOf(iMonth);
        String strMonthDate, strStartDate, strToDate, strEndDate, strEndDateDB, strStartDateDB, strToDateDB;

        if (iMonth <= 9)
            strMonth = String.valueOf("0" + iMonth);

        strMonthDate = String.valueOf(iYear + "-" + strMonth + "-01");

        //String strFromDate = strMonthDate + "T05:30:00.000Z";

        strStartDateDB = convertDateToStringQueryDB(convertStringToDateQueryDB(strMonthDate + "T00:00:00.000"));
        //
        strToDateDB = getMonthLastDate(strMonthDate);

        log(strToDateDB, " EDATE ");

        strEndDateDB = convertDateToStringQueryDB(convertStringToDateQueryDB(strToDateDB + "T24:00:00.00"));


        if (sessionManager.getActivityStatus()) {
            Cursor cursor = null;
            try {

                // WHERE clause
                String whereClause = " where " + DbHelper.COLUMN_COLLECTION_NAME + " = '" + Config.collectionActivity + "' AND " + DbHelper.COLUMN_DOC_DATE + " >= Datetime('" + strStartDateDB + "') and " + DbHelper.COLUMN_DOC_DATE + " <= Datetime('" + strEndDateDB + "')";

                cursor = CareTaker.dbCon.fetchFromSelect(DbHelper.strTableNameCollection, whereClause);

                if (cursor != null && cursor.getCount() > 0) {
                    Log.i("TAG", "cursor count:" + cursor.getCount());
                    cursor.moveToFirst();
                    do {

                        try {
                            //String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ? AND " + DbHelper.COLUMN_OBJECT_ID + " =?";
                            // WHERE clause arguments
                            //String selectionArgsMile[] = {Config.collectionMilestones, cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID))};
                            String whereClauseMile = " where " + DbHelper.COLUMN_COLLECTION_NAME + " = '" + Config.collectionMilestones + "' AND " + DbHelper.COLUMN_OBJECT_ID + " = '" + cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)) + "' AND " + DbHelper.COLUMN_DOC_DATE + " >= Datetime('" + strStartDateDB + "') and " + DbHelper.COLUMN_DOC_DATE + " <= Datetime('" + strEndDateDB + "')";

                            //Cursor cursorMilestone = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgsMile, DbHelper.COLUMN_DOC_DATE, null, false, null, null);
                            Cursor cursorMilestone = CareTaker.dbCon.fetchFromSelect(DbHelper.strTableNameCollection, whereClauseMile);
                            JSONArray jArray = new JSONArray();
                            int index = 0;
                            if (cursorMilestone != null && cursorMilestone.getCount() > 0) {
                                cursorMilestone.moveToFirst();
                                do {

                                    String strDocument = cursorMilestone.getString(cursorMilestone.getColumnIndex(DbHelper.COLUMN_DOCUMENT));
                                    JSONObject jsonObjectActivity = new JSONObject(strDocument);


                                    jArray.put(index, jsonObjectActivity);
                                    index++;
                                } while (cursorMilestone.moveToNext());


                            }
                            if (cursorMilestone != null) {
                                cursorMilestone.close();
                            }
                            createActivityModel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID)), cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT)), 1, jArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } while (cursor.moveToNext());

                    //ActivityFragment.reload();
                    //todo uncomment for multiple carlas
                    //fetchProviders(progressDialog, 2);

                } else {


                }
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                loadImagesActivityMonth();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

//        strMonthDate = String.valueOf(iYear + "-" + strMonth + "-01");
//
//        //String strFromDate = strMonthDate + "T05:30:00.000Z";
//
//        strStartDate = convertDateToStringQuery(convertStringToDateQuery(strMonthDate + "T00:00:00.000"));
//        //
//        strToDate = getMonthLastDate(strMonthDate);
//
//        log(strToDate, " EDATE ");
//
//        strEndDate = convertDateToStringQuery(convertStringToDateQuery(strToDate + "T23:59:59.999"));

        String key2 = "dependent_id";


        if (isConnectingToInternet()) {


            StorageService storageService = new StorageService(_ctxt);

            strMonthDate = String.valueOf(iYear + "-" + strMonth + "-01");

            //String strFromDate = strMonthDate + "T05:30:00.000Z";

            strStartDate = convertDateToStringQuery(convertStringToDateQuery(strMonthDate + "T00:00:00.000"));
            //
            strToDate = getMonthLastDate(strMonthDate);

            log(strToDate, " EDATE ");

            strEndDate = convertDateToStringQuery(convertStringToDateQuery(strToDate + "T23:59:59.999"));
            //String value2 = Config.strDependentIds.get(iActivityCount);

            Query q1 = QueryBuilder.build(key2, Config.strDependentIds, QueryBuilder.Operator.INLIST);

            Query q2 = QueryBuilder.build("activity_date", strStartDate, QueryBuilder.
                    Operator.GREATER_THAN_EQUALTO);

            // Build query q1 for key1 equal to name and value1 equal to Nick

            // Build query q2 for key2 equal to age and value2

            Query q3 = QueryBuilder.build("activity_date", strEndDate, QueryBuilder.Operator.LESS_THAN_EQUALTO);

            Query q4 = QueryBuilder.compoundOperator(q2, QueryBuilder.Operator.AND, q3);

            Query q5 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q4);

            if (sessionManager.getActivityStatus()) {
                String defaultDate = null;
                Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionActivity);
                if (cursorData != null && cursorData.getCount() > 0) {
                    cursorData.moveToFirst();
                    defaultDate = cursorData.getString(0);
                    if (defaultDate == null || defaultDate.length() == 0) {
                        defaultDate = Utils.defaultDate;
                    }
                    cursorData.close();
                } else {
                    defaultDate = Utils.defaultDate;
                }

                Query q6 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);
                q5 = QueryBuilder.compoundOperator(q5, QueryBuilder.Operator.AND, q6);
            } else {

            }

           /* int max = 1;
            int offset = 0;
*/


            // storageService.findDocsByQuery(Config.collectionActivity, q5, //1 for descending
            //new App42CallBack()
            storageService.findDocsByQueryOrderBy(Config.collectionActivity, q5, 3000, 0,
                    "activity_date", 1, new App42CallBack() {

                        @Override
                        public void onSuccess(Object o) {
                            /*if (progressDialog.isShowing())
                                progressDialog.dismiss();
*/
                            DashboardActivity.loadingPanel.setVisibility(View.GONE);
                            Storage response = (Storage) o;

                            if (response != null) {

                                log(response.toString(), " S ");
                                log("Size : " + response.getJsonDocList().size(), " S ");
                                if (response.getJsonDocList().size() > 0) {
                                    try {
                                        for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                            Storage.JSONDocument jsonDocument = response.
                                                    getJsonDocList().get(i);

                                            String strDocument = jsonDocument.getJsonDoc();
                                            String strActivityId = jsonDocument.getDocId();
                                            JSONObject jsonObjectActivity =
                                                    new JSONObject(strDocument);
                                            JSONArray jArray = jsonObjectActivity.optJSONArray("milestones");
                                            jsonObjectActivity.remove("milestones");
                                            strDocument = jsonObjectActivity.toString();
                                            String values[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionActivity, "", "1", jsonObjectActivity.optString("activity_date"), ""};
                                            Log.i("TAG", "Date :" + jsonObjectActivity.optString("activity_date"));
                                            if (sessionManager.getActivityStatus()) {
                                                String selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                                                // WHERE clause arguments
                                                String[] selectionArgs = {strActivityId, Config.collectionActivity};
                                                CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);
                                                for (int j = 0; j < jArray.length(); j++) {
                                                    JSONObject jObj = jArray.optJSONObject(j);
                                                    strDocument = jObj.toString();
                                                    selection = DbHelper.COLUMN_OBJECT_ID + " = ? AND " + DbHelper.COLUMN_COLLECTION_NAME + " =? AND " + DbHelper.COLUMN_DEPENDENT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgsMile = {strActivityId, Config.collectionMilestones, jObj.optString("id")};
                                                    String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, valuesMilestone, Config.names_collection_table, selectionArgsMile);


                                                }

                                            } else {
                                                CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);

                                                createActivityModel(strActivityId, strDocument, 1, jArray);
                                                for (int j = 0; j < jArray.length(); j++) {
                                                    JSONObject jObj = jArray.optJSONObject(j);
                                                    strDocument = jObj.toString();

                                                    String valuesMilestone[] = {strActivityId, response.getJsonDocList().get(i).getUpdatedAt(), strDocument, Config.collectionMilestones, jObj.optString("id"), "1", jObj.optString("date")};
                                                    CareTaker.dbCon.insert(DbHelper.strTableNameCollection, valuesMilestone, Config.names_collection_table);
                                                }


                                            }


                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }

                            } else {
                                /*if (progressDialog.isShowing())
                                    progressDialog.dismiss();*/
                                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                //ActivityFragment.activitiesModelArrayList.clear();
                                //toast(2, 2, _ctxt.getString(R.string.warning_internet));
                            }
                            //ActivityFragment.reload();
                            //todo uncomment for multiple carlas
                            //fetchProviders(progressDialog, 2);

                            if (!sessionManager.getActivityStatus()) {
                                sessionManager.saveActivityStatus(true);
                                loadImagesActivityMonth();
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            /*if (progressDialog.isShowing())
                                progressDialog.dismiss();*/
                            DashboardActivity.loadingPanel.setVisibility(View.GONE);
                            try {
                                if (e != null) {
                                    log(e.getMessage(), " f ");
                                   /* JSONObject jsonObject = new JSONObject(e.getMessage());
                                    JSONObject jsonObjectError =
                                            jsonObject.getJSONObject("app42Fault");

                                    String strMess = jsonObjectError.getString("details");

                                    toast(2, 2, strMess);
*/
                                } else if (!sessionManager.getActivityStatus()) {
                                    /*if (progressDialog.isShowing())
                                        progressDialog.dismiss();*/
                                    DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                    toast(2, 2, _ctxt.getString(R.string.warning_internet));
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            //ActivityFragment.reload();
                            //fetchProviders(progressDialog, 2);
                            if (!sessionManager.getActivityStatus()) {
                                loadImagesActivityMonth();
                            }
                        }
                    }
            );
        } else if (!sessionManager.getActivityStatus()) {
          /*  if (progressDialog.isShowing())
                progressDialog.dismiss();*/
            DashboardActivity.loadingPanel.setVisibility(View.GONE);
            //ActivityFragment.reload();
            //fetchProviders(progressDialog, 2);
            toast(2, 2, _ctxt.getString(R.string.warning_internet));
        }


    }


    //Application Specig=fic End

    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /* method to set expandable lsit view's height based on children when any group is expanded */
    public void setListViewHeight(ExpandableListView listView,
                                  int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /* method to set expandable lsit view's height based on children */

    public Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        ExifInterface exif = new ExifInterface(src);
        String orientationString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        try {
            orientation = Integer.parseInt(orientationString);
        } catch (NumberFormatException e) {
        }

        return orientation;
    }

//    public void deleteCollectionDoc(String collectionName) {
//
//        StorageService storageService = new StorageService(_ctxt);
//
//        storageService.deleteAllDocs(collectionName, new App42CallBack() {
//            public void onSuccess(Object response) {
//                App42Response app42response = (App42Response) response;
//                System.out.println("response is " + app42response);
//            }
//
//            public void onException(Exception ex) {
//                System.out.println("Exception Message" + ex.getMessage());
//            }
//        });
//    }

    public String getRealPathFromURI(Uri contentUri) {
        //Uri contentUri = Uri.parse(contentURI);
        try {
            Cursor cursor = _ctxt.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean fetchLatestCheckInCare(String iMonth, String iYear, String CustomerId) {

        //iMonth = iMonth; // - 1
        Config.checkInCareActivityNames.clear();
        showCheckInButton = false;
        if (sessionManager.getCheckInCareStatus()) {

            DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

            Cursor cursor = null;
            try {

                // WHERE   clause
                String selection = DbHelper.COLUMN_COLLECTION_NAME + " = ?";

                // WHERE clause arguments
                String[] selectionArgs = {Config.collectionCheckInCare};
                cursor = CareTaker.dbCon.fetch(DbHelper.strTableNameCollection, Config.names_collection_table, selection, selectionArgs, null, null, false, null, null);
                Log.i("TAG", "Cursor count:" + cursor.getCount());
                if (cursor != null) {
                    cursor.moveToFirst();
                    do {

                        String strDocument = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DOCUMENT));
                        String strActivityId = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_OBJECT_ID));
                        //JSONObject jsonObjectActivity = new JSONObject(strDocument);


                        createCheckInCareModel(strActivityId, strDocument);
                    } while (cursor.moveToNext());

                    showCheckInButton = true;
                }/* else {

                }*/


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DashboardActivity.loadingPanel.setVisibility(View.GONE);
                if (cursor != null)
                    cursor.close();
            }
        }

        if (isConnectingToInternet()) {

            DashboardActivity.loadingPanel.setVisibility(View.VISIBLE);

            String defaultDate = null;
            Cursor cursorData = CareTaker.dbCon.getMaxDate(Config.collectionCheckInCare);
            if (cursorData != null && cursorData.getCount() > 0) {
                cursorData.moveToFirst();
                defaultDate = cursorData.getString(0);
                cursorData.close();
            } else {
                defaultDate = Utils.defaultDate;
            }

            StorageService storageService = new StorageService(_ctxt);

            Query q1 = QueryBuilder.build("year", iYear, QueryBuilder.
                    Operator.EQUALS);
            Query q2 = QueryBuilder.build("month", iMonth, QueryBuilder.
                    Operator.EQUALS);
            Query q3 = QueryBuilder.build("customer_id", CustomerId, QueryBuilder.
                    Operator.EQUALS);

            // Build query q1 for key1 equal to name and value1 equal to Nick

            // Build query q2 for key2 equal to age and value2

            Query q4 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
            Query q5 = QueryBuilder.compoundOperator(q3, QueryBuilder.Operator.AND, q4);
            if (sessionManager.getCheckInCareStatus()) {
                // Build query q2
                Query q6 = QueryBuilder.build("_$updatedAt", defaultDate, QueryBuilder.Operator.GREATER_THAN);
                q5 = QueryBuilder.compoundOperator(q5, QueryBuilder.Operator.AND, q6);
            }

            storageService.findDocsByQueryOrderBy(Config.collectionCheckInCare, q5, 3000, 0, "created_date", 1, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {


                            Storage response = (Storage) o;
                            DashboardActivity.loadingPanel.setVisibility(View.GONE);

                            if (response != null) {

                                Utils.log(response.toString(), " S ");
                                Utils.log("Size : " + response.getJsonDocList().size(), " S ");
                                if (response.getJsonDocList().size() > 0) {
                                    try {
                                        try {
                                            for (int i = 0; i < response.getJsonDocList().size(); i++) {

                                                Storage.JSONDocument jsonDocument = response.
                                                        getJsonDocList().get(i);

                                                String strDocument = jsonDocument.getJsonDoc();
                                                String strActivityId = jsonDocument.getDocId();
                                                //JSONObject jsonObjectActivity = new JSONObject(strDocument);

                                                String values[] = {strActivityId, jsonDocument.getUpdatedAt(), strDocument, Config.collectionCheckInCare, "", "1", "", ""};

                                                if (sessionManager.getCheckInCareStatus()) {
                                                    String selection = DbHelper.COLUMN_OBJECT_ID + " = ?";

                                                    // WHERE clause arguments
                                                    String[] selectionArgs = {strActivityId};
                                                    CareTaker.dbCon.update(DbHelper.strTableNameCollection, selection, values, Config.names_collection_table, selectionArgs);

                                                } else {

                                                    CareTaker.dbCon.insert(DbHelper.strTableNameCollection, values, Config.names_collection_table);
                                                    createCheckInCareModel(strActivityId, strDocument);

                                                }


                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        DashboardActivity.loadingPanel.setVisibility(View.GONE);
                                        if (!sessionManager.getCheckInCareStatus()) {
                                            sessionManager.saveCheckInCareStatus(true);
                                            showCheckInButton = true;
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onException(Exception e) {
                            DashboardActivity.loadingPanel.setVisibility(View.GONE);
                        }
                    }
            );
        }
        return showCheckInButton;
    }

    public class ThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            /*if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();*/

            if (Config.intSelectedMenu == Config.intDashboardScreen) {
                DashboardActivity.goToDashboard();
            }

            DashboardActivity.loadingPanel.setVisibility(View.GONE);


        }
    }

    public class BackgroundThread extends Thread {
        @Override
        public void run() {
            try {
                loadAllFiles();

            } catch (Exception e) {
                e.printStackTrace();
            }
            threadHandler.sendEmptyMessage(0);
        }
    }

}
