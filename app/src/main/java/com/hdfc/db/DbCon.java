package com.hdfc.db;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

import com.hdfc.config.Config;
import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.SignupActivity;
import com.hdfc.newzeal.fragments.AddDependantFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;
import com.ricardo.androidcipher.PRNGFixes;
import com.scottyab.aescrypt.AESCrypt;

import net.sqlcipher.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbCon {

    public static String strPass;
    private static DbHelper dbHelper;
    private static boolean isDbOpened = false;
    private static DbCon dbConInstance = null;
    private static Handler dbOpenHandler;
    private static int intDependantCount = 0;
    private static JSONArray jsonArrayDependant = new JSONArray();
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
            Thread dbOpenThread = new DbOpenThread();
            dbOpenThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void close() {
        //TODO will be called in future
        dbHelper.close();
    }


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
                dbHelper.delete("dependant", "status<=?", new String[]{"1"});
                dbHelper.delete("user", "status=?", new String[]{"0"});
            } catch (Exception e) {
                e.printStackTrace();
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

                    while (!cur.isAfterLast()) {

                        strImg = cur.getString(4);
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        editEmail.setText(cur.getString(5));

                        Libs.log(strImg + " 2", "Image 2");

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

    public boolean updateDependantMedicalDetails(String strName, String strAge, String strDiseases, String strNotes, long longUserId, String strImagePathServer) {

        boolean isUpdated = false;

        if (isDbOpened) {

            try {
                isUpdated = dbHelper.update("name=? and user_id=?", new String[]{strAge, strDiseases, strNotes, "1", strImagePathServer},
                        new String[]{"age", "diseases", "notes", "status", "image_path_server"}, "dependant", new String[]{strName, String.valueOf(longUserId)});

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
                e.printStackTrace();
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
                    while (!cur.isAfterLast()) {

                        dpndntModel = new DependantModel();
                        dpndntModel.setStrName(cur.getString(0));
                        dpndntModel.setStrRelation(cur.getString(2));
                        dpndntModel.setStrImg(cur.getString(1));
                        dpndntModel.setStrDesc(cur.getString(3));
                        dpndntModel.setStrAddress(cur.getString(4));
                        dpndntModel.setStrContacts(cur.getString(5));
                        dpndntModel.setStrEmail(cur.getString(6));

                        Libs.log(cur.getString(1) + " 3", "Image 3");

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

        ConfirmViewModel confirmViewModel;

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

                Libs.log(SignupActivity.strCustomerImg + " 4", "Image 4");

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

                        Libs.log(dpndntModel.getStrImg() + " 5", "Image 5");

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

    public boolean prepareData(String strCustomerImageUrl) {

        boolean isFormed = false;

        JSONObject jsonCustomer = new JSONObject();

        Config.jsonServer = null;

        try {

            if (isDbOpened) {

                jsonCustomer.put("customer_name", SignupActivity.strCustomerName);
                jsonCustomer.put("customer_address", SignupActivity.strCustomerAddress);
                jsonCustomer.put("customer_contact_no", SignupActivity.strCustomerContactNo);
                jsonCustomer.put("customer_email", SignupActivity.strCustomerEmail);/////chk this
                jsonCustomer.put("customer_profile_url", strCustomerImageUrl);/////chk this
                jsonCustomer.put("paytm_account", "paytm_account");/////chk this

                isFormed = true;

                Cursor cur = null;

                try {

                    cur = dbHelper.fetch("user", new String[]{"password"}, "email=?", new String[]{SignupActivity.strCustomerEmail}, null, null, false, null, null);

                    if (cur.getCount() > 0) {
                        cur.moveToFirst();
                        while (!cur.isAfterLast()) {

                            try {
                                PRNGFixes.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String str = cur.getString(0);

                           /* MyCipher myCipher = new MyCipher(Config.string);
                            MyCipherData myCipherData = myCipher.encryptUTF8(str);*/

                            String strPassLocal = AESCrypt.encrypt(Config.string, str);
                            jsonCustomer.put("customer_password", strPassLocal);
                            strPass = strPassLocal;

                            //Libs.bytesToHex(myCipherData.getData());

                            isFormed = true;
                            cur.moveToNext();
                        }
                        dbHelper.closeCursor(cur);
                    } else isFormed = false;
                } catch (Exception e) {
                    dbHelper.closeCursor(cur);
                    isFormed = false;
                }

                if (isFormed) {

                    Cursor cursorSubmit = null;

                    try {

                        cursorSubmit = dbHelper.fetch("dependant", new String[]{"name", "email", "contact_no", "address", "relationship", "notes", "age", "diseases", "image_path", "image_path_server"}, "user_id=?", new String[]{String.valueOf(SignupActivity.longUserId)}, "relationship ASC", null, true, null, null);

                        int intCount = cursorSubmit.getCount();

                        if (intCount > 0) {

                            cursorSubmit.moveToFirst();
                            while (!cursorSubmit.isAfterLast()) {

                                try {

                                    JSONObject jsonDependant = new JSONObject();
                                    jsonDependant.put("dependant_name", cursorSubmit.getString(0));
                                    jsonDependant.put("dependant_email", cursorSubmit.getString(1));
                                    jsonDependant.put("dependant_contact_no", cursorSubmit.getString(2));
                                    jsonDependant.put("dependant_address", cursorSubmit.getString(3));
                                    jsonDependant.put("dependant_relation", cursorSubmit.getString(4));
                                    jsonDependant.put("dependant_notes", cursorSubmit.getString(5));
                                    jsonDependant.put("dependant_age", cursorSubmit.getString(6));
                                    jsonDependant.put("dependant_illness", cursorSubmit.getString(7));

                                    jsonDependant.put("dependant_profile_url", cursorSubmit.getString(9));

                                    jsonArrayDependant.put(jsonDependant);

                                    intDependantCount++;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                cursorSubmit.moveToNext();
                            }

                            jsonCustomer.put("Dependants", jsonArrayDependant);

                            if (intCount != intDependantCount) {
                                isFormed = false;
                            } else {
                                Config.jsonServer = jsonCustomer;
                                isFormed = true;
                            }
                        }

                        dbHelper.closeCursor(cursorSubmit);
                    } catch (Exception e) {
                        dbHelper.closeCursor(cursorSubmit);
                        isFormed = false;
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            isFormed = false;
        }

        return isFormed;
    }

    public static class DbOpenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            isDbOpened = true;
        }
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
}