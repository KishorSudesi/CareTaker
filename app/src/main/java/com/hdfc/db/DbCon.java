package com.hdfc.db;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.hdfc.app42service.UploadService;
import com.hdfc.config.Config;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.newzeal.fragments.AddDependantFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbCon {

    private static DbHelper dbHelper;
    private static boolean isDbOpened = false;
    private static DbCon dbConInstance = null;
    private static Thread dbOpenThread;
    private static Handler dbOpenHandler;
    private Context context;

    public DbCon(Context context) {
        this.context = context;
        open();
    }

    public static synchronized DbCon getInstance(Context ctx) {

        if (dbConInstance == null) {
            dbConInstance = new DbCon(ctx.getApplicationContext());
        }
        return dbConInstance;
    }

    public DbCon open() {
        try {
            dbOpenHandler = new DbOpenHandler();
            dbOpenThread = new DbOpenThread();
            dbOpenThread.start();
        } catch (Exception e) {
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }
    //

    public long insertUser(String strName, String strEmail, String strPassword, String strContactNo, long longUserId, String strImagPath, String strAddress) {

        long longInserted = 0;

        if (isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"user_id"}, "user_id=?", new String[]{String.valueOf(longUserId)}, null, null, false, null, null);

                if (cur.getCount() <= 0) {

                    longInserted = dbHelper.insert(new String[]{strName, strEmail, strPassword, strContactNo, "0", strImagPath, strAddress},
                            new String[]{"name", "email", "password", "contact_no", "status", "image_path", "address"}, "user");
                } else {

                    dbHelper.update("user_id=?", new String[]{strName, strEmail, strPassword, strContactNo, strAddress},
                            new String[]{"name", "email", "password", "contact_no", "address"}, "user", new String[]{String.valueOf(longUserId)});

                    if (!strImagPath.equalsIgnoreCase("")) {
                        dbHelper.update("user_id=?", new String[]{strImagPath},
                                new String[]{"image_path"}, "user", new String[]{String.valueOf(longUserId)});
                    }

                    longInserted = longUserId;
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
                e.printStackTrace();
            }
        }
        return longInserted;
    }

    public void deleteTempUsers() {

        if (isDbOpened) {
            try {
                dbHelper.delete("user", "status=?", new String[]{"0"});
            } catch (Exception e) {
            }
        }
    }

    public long insertDependant(String strName, String strContactNo, String strAddress, String strRelation, long longUserId, String strImageName, String strEmail) {

        long longInserted = 0;

        if (isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"dependant_id"}, "name=? and user_id=?", new String[]{strName, String.valueOf(longUserId)}, null, null, false, null, null);

                if (cur.getCount() <= 0) {

                    longInserted = dbHelper.insert(new String[]{strName, strContactNo, strAddress, strRelation, String.valueOf(longUserId), "0", strImageName, strEmail},
                            new String[]{"name", "contact_no", "address", "relationship", "user_id", "status", "image_path", "email"}, "dependant");
                } else {

                    dbHelper.update("name=? and user_id=?", new String[]{strContactNo, strAddress, strRelation, strEmail},
                            new String[]{"contact_no", "address", "relationship", "email"}, "dependant", new String[]{strName, String.valueOf(longUserId)});

                    if (!strImageName.equalsIgnoreCase("")) {
                        dbHelper.update("name=? and user_id=?", new String[]{strImageName},
                                new String[]{"image_path"}, "dependant", new String[]{strName, String.valueOf(longUserId)});
                    }
                    cur.moveToFirst();
                    longInserted = cur.getInt(0);
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
        return longInserted;
    }

    public String retrieveDependantPersonal(long longUserId, EditText editName, EditText editContactNo, EditText editAddress, EditText editRelation, String strName, EditText editEmail) {

        String strImg = "";

        if (isDbOpened && longUserId > 0) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "contact_no", "address", "relationship", "image_path", "email"}, "user_id=? and name=?", new String[]{String.valueOf(longUserId), strName}, "name DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    cur.moveToFirst();

                    while (cur.isAfterLast() == false) {

                        strImg = cur.getString(4);
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        editEmail.setText(cur.getString(5));

                        Log.e("Image 2", strImg);

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
        return strImg;
    }

    public boolean updateDependantMedicalDetails(String strName, String strAge, String strDiseases, String strNotes, long longUserId) {

        boolean isUpdated = false;

        if (isDbOpened) {

            try {
                isUpdated = dbHelper.update("name=? and user_id=?", new String[]{strAge, strDiseases, strNotes, "1"},
                        new String[]{"age", "diseases", "notes", "status"}, "dependant", new String[]{strName, String.valueOf(longUserId)});

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isUpdated;
    }

    public void deleteTempDependants() {

        if (isDbOpened) {
            try {
                dbHelper.delete("dependant", "status=?", new String[]{"0"});
            } catch (Exception e) {
            }
        }
    }

    public int retrieveDependants(long longUserId) {

        DependantModel dpndntModel;

        AddDependantFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (isDbOpened && longUserId > 0) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "image_path", "relationship", "notes", "address", "contact_no", "email"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "relationship ASC", null, true, null, null);

                if(cur.getCount()>0) {

                    cur.moveToFirst();
                    while(cur.isAfterLast()==false){

                        dpndntModel = new DependantModel();
                        dpndntModel.setStrName(cur.getString(0));
                        dpndntModel.setStrRelation(cur.getString(2));
                        dpndntModel.setStrImg(cur.getString(1));
                        dpndntModel.setStrDesc(cur.getString(3));
                        dpndntModel.setStrAddress(cur.getString(4));
                        dpndntModel.setStrContacts(cur.getString(5));
                        dpndntModel.setStrEmail(cur.getString(6));

                        Log.e("Image 3", cur.getString(1));

                        AddDependantFragment.CustomListViewValuesArr.add(dpndntModel);

                        cur.moveToNext();
                        count++;
                    }
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
            }
        }

        dpndntModel = new DependantModel();
        dpndntModel.setStrName("Add Dependant");
        dpndntModel.setStrRelation("");
        dpndntModel.setStrImg("");
        dpndntModel.setStrDesc("");
        dpndntModel.setStrAddress("");
        dpndntModel.setStrContacts("");
        dpndntModel.setStrEmail("");

        AddDependantFragment.CustomListViewValuesArr.add(dpndntModel);

        count++;

        return count;
    }

    public int retrieveConfirmDependants(long longUserId) {

        ConfirmViewModel confirmViewModel = null;

        ConfirmFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (longUserId > 0) {

            try {

                confirmViewModel = new ConfirmViewModel();
                confirmViewModel.setStrName(SignupActivity.strCustomerName);
                confirmViewModel.setStrDesc("");
                confirmViewModel.setStrAddress(SignupActivity.strCustomerAddress);
                confirmViewModel.setStrContacts(SignupActivity.strCustomerContactNo);
                confirmViewModel.setStrEmail(SignupActivity.strCustomerEmail);/////chk this
                confirmViewModel.setStrImg(SignupActivity.strCustomerImg);

                count++;

                Log.e("Image 4", SignupActivity.strCustomerImg);

                ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);

                for (DependantModel dpndntModel : AddDependantFragment.CustomListViewValuesArr) {

                    if (!dpndntModel.getStrName().equalsIgnoreCase("Add Dependant")) {
                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(dpndntModel.getStrName());
                        confirmViewModel.setStrDesc(dpndntModel.getStrDesc());
                        confirmViewModel.setStrAddress(dpndntModel.getStrAddress());
                        confirmViewModel.setStrContacts(dpndntModel.getStrContacts());
                        confirmViewModel.setStrEmail(dpndntModel.getStrEmail());
                        confirmViewModel.setStrImg(dpndntModel.getStrImg());

                        Log.e("Image 5", dpndntModel.getStrImg());

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);
                        count++;
                    }
                    }

            } catch (Exception e) {

            }
        }


        return count;
    }

    public boolean sendToServer() {

        UploadService uploadService;

        final boolean[] isFormed = {true};

        JSONObject jsonCustomer = new JSONObject();

        JSONArray jsonArrayDependant = new JSONArray();

        uploadService = new UploadService(context);

        Config.jsonServer = null;

        try {

            jsonCustomer.put("Customer_Name", SignupActivity.strCustomerName);
            jsonCustomer.put("Customer_Address", SignupActivity.strCustomerAddress);
            jsonCustomer.put("Customer_Contact_No", SignupActivity.strCustomerContactNo);
            jsonCustomer.put("Customer_Email", SignupActivity.strCustomerEmail);/////chk this

           /* uploadService.uploadImage(SignupActivity.strCustomerImg, SignupActivity.strCustomerName, "Profile Picture", SignupActivity.strCustomerEmail, UploadFileType.IMAGE, new AsyncApp42ServiceApi.App42UploadServiceListener() {
                @Override
                public void onUploadImageSuccess(Upload response, String fileName, String userName) {
                    isFormed[0] =true;
                }

                @Override
                public void onUploadImageFailed(App42Exception ex) {
                    isFormed[0] =false;
                }

                @Override
                public void onGetImageSuccess(Upload response) {
                    isFormed[0] =true;
                }

                @Override
                public void onGetImageFailed(App42Exception ex) {
                    isFormed[0] =false;
                }

                @Override
                public void onSuccess(Upload response) {
                    isFormed[0] =true;
                }

                @Override
                public void onException(App42Exception ex) {
                    isFormed[0] =false;
                }

                @Override
                public void onSuccess(Object o) {
                    isFormed[0] =true;
                }

                @Override
                public void onException(Exception e) {
                    isFormed[0] =false;
                }
            });*/

            if (isDbOpened) {

                Cursor cur = null;

                try {

                    cur = dbHelper.fetch("user", new String[]{"password"}, "email=?", new String[]{SignupActivity.strCustomerEmail}, null, null, false, null, null);

                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                        while (cur.isAfterLast() == false) {
                            jsonCustomer.put("Customer_Password", cur.getString(0));
                            cur.moveToNext();
                        }
                        dbHelper.closeCursor(cur);
                    }
                } catch (Exception e) {
                    dbHelper.closeCursor(cur);
                }

                cur = null;

                try {

                    cur = dbHelper.fetch("dependant", new String[]{"name", "email", "contact_no", "address", "relationship", "notes", "age", "diseases", "image_path"}, "user_id=?", new String[]{String.valueOf(SignupActivity.longUserId)}, "relationship ASC", null, true, null, null);

                    if (cur.getCount() > 0) {

                        cur.moveToFirst();
                        while (cur.isAfterLast() == false) {

                            JSONObject jsonDependant = new JSONObject();
                            jsonDependant.put("Dependant_Name", cur.getString(0));
                            jsonDependant.put("Dependant_Email", cur.getString(1));
                            jsonDependant.put("Dependant_Contact_No", cur.getString(2));
                            jsonDependant.put("Dependant_Address", cur.getString(3));
                            jsonDependant.put("Dependant_Relation", cur.getString(4));
                            jsonDependant.put("Dependant_Notes", cur.getString(5));
                            jsonDependant.put("Dependant_Age", cur.getString(6));
                            jsonDependant.put("Dependant_Diseases", cur.getString(7));

                           /* uploadService.uploadImage(cur.getString(8), cur.getString(0), "Profile Picture", cur.getString(1), UploadFileType.IMAGE, new AsyncApp42ServiceApi.App42UploadServiceListener() {
                                @Override
                                public void onUploadImageSuccess(Upload response, String fileName, String userName) {
                                    isFormed[0] =true;
                                }

                                @Override
                                public void onUploadImageFailed(App42Exception ex) {
                                    isFormed[0] =false;
                                }

                                @Override
                                public void onGetImageSuccess(Upload response) {
                                    isFormed[0] =true;
                                }

                                @Override
                                public void onGetImageFailed(App42Exception ex) {
                                    isFormed[0] =false;
                                }

                                @Override
                                public void onSuccess(Upload response) {
                                    isFormed[0] =true;
                                }

                                @Override
                                public void onException(App42Exception ex) {
                                    isFormed[0] =false;
                                }

                                @Override
                                public void onSuccess(Object o) {
                                    isFormed[0] =true;
                                }

                                @Override
                                public void onException(Exception e) {
                                    isFormed[0] =false;
                                }
                            });*/

                            jsonArrayDependant.put(jsonDependant);
                            cur.moveToNext();
                        }

                        jsonCustomer.put("Dependants", jsonArrayDependant);
                    }

                    dbHelper.closeCursor(cur);
                } catch (Exception e) {
                    dbHelper.closeCursor(cur);
                }
            }

            Config.jsonServer = jsonCustomer;

            isFormed[0] = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isFormed[0];
    }


    public class DbOpenThread extends Thread {
        @Override
        public void run() {
            try {
                dbHelper = DbHelper.getInstance(context);
                dbHelper.open();
                dbOpenHandler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DbOpenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            isDbOpened = true;
        }
    }
}