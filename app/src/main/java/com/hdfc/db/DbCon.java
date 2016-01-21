package com.hdfc.db;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.hdfc.libs.Libs;
import com.hdfc.model.ConfirmViewModel;
import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.fragments.AddDependantFragment;
import com.hdfc.newzeal.fragments.ConfirmFragment;
import com.hdfc.views.RoundedImageView;

import net.sqlcipher.Cursor;

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

                //Log.e("IMAEG_PAHT", strImagPath);

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

    public void retrieveUser(long longUserId, EditText editName, EditText editemail, EditText editContactNo, RoundedImageView roundedImageView, EditText editAddress) {

        if (isDbOpened) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"name", "email", "contact_no", "image_path", "address"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "user_id DESC", "0,1", true, null, null);
                if (cur.getCount() > 0) {
                    Libs libs = new Libs(context);
                    cur.moveToFirst();
                    String strImg = "";
                    while (cur.isAfterLast() == false) {
                        editName.setText(cur.getString(0));
                        editemail.setText(cur.getString(1));
                        editContactNo.setText(cur.getString(2));
                        editAddress.setText(cur.getString(4));
                        strImg = cur.getString(3);
                        Log.e("Image 1", strImg);

                        try {
                            if (!strImg.equalsIgnoreCase(""))
                                roundedImageView.setImageBitmap(libs.getBitmapFromFile(strImg, 300, 300));
                        } catch (OutOfMemoryError oOm) {

                        }
                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
                e.printStackTrace();
            }
        }
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

                    // Log.e("Dependant", strImageName+ "w ");

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

    public void retrieveDependantPersonal(long longUserId, EditText editName, EditText editContactNo, EditText editAddress, EditText editRelation, String strName, RoundedImageView imgButtonCamera, EditText editEmail) {

        if (isDbOpened && longUserId > 0) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "contact_no", "address", "relationship", "image_path", "email"}, "user_id=? and name=?", new String[]{String.valueOf(longUserId), strName}, "name DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    Libs libs = new Libs(context);
                    cur.moveToFirst();
                    String strImg = "";
                    while (cur.isAfterLast() == false) {

                        strImg = cur.getString(4);

                        //Log.d("TAG 2", cur.getString(4));
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        editEmail.setText(cur.getString(5));

                        Log.e("Image 2", strImg);

                        try {
                            if (!strImg.equalsIgnoreCase(""))
                                imgButtonCamera.setImageBitmap(libs.getBitmapFromFile(strImg, 300, 300));
                        } catch (OutOfMemoryError oOm) {

                        }

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
    }

    /*public void retrieveDependantMedical(long longUserId, EditText editAge, EditText editDiseases, EditText editNotes, String strName) {

        if (isDbOpened) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"age", "diseases", "notes"}, "user_id=? and name=?", new String[]{String.valueOf(longUserId), strName}, "name DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    cur.moveToFirst();
                    while (cur.isAfterLast() == false) {
                        editAge.setText(cur.getString(0));
                        editDiseases.setText(cur.getString(1));
                        editNotes.setText(cur.getString(2));
                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }
    }*/

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

        DependantModel dpndntModel = null;

        AddDependantFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (isDbOpened && longUserId > 0) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "image_path", "relationship"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "relationship ASC", null, true, null, null);

                if(cur.getCount()>0) {

                    cur.moveToFirst();
                    while(cur.isAfterLast()==false){

                        dpndntModel = new DependantModel();
                        dpndntModel.setStrName(cur.getString(0));
                        dpndntModel.setStrRelation(cur.getString(2));
                        dpndntModel.setStrImg(cur.getString(1));

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
        count++;
        AddDependantFragment.CustomListViewValuesArr.add(dpndntModel);

        return count;
    }

    public int retrieveConfirmDependants(long longUserId) {

        ConfirmViewModel confirmViewModel = null;

        ConfirmFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if (isDbOpened && longUserId > 0) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"name", "address", "email", "contact_no", "image_path"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "user_id DESC", "0,1", true, null, null);

                if (cur.getCount() > 0) {
                    //Libs libs = new Libs(context);
                    cur.moveToFirst();
                    while (cur.isAfterLast() == false) {

                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(cur.getString(0));
                        confirmViewModel.setStrDesc("");
                        confirmViewModel.setStrAddress(cur.getString(1));
                        confirmViewModel.setStrContacts(cur.getString(3));
                        confirmViewModel.setStrEmail(cur.getString(2));/////chk this
                        confirmViewModel.setStrImg(cur.getString(4));

                        Log.e("Image 4", cur.getString(4));

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);

                        count++;

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);

                //
                cur = null;
                cur = dbHelper.fetch("dependant", new String[]{"name", "notes", "address", "contact_no", "email", "image_path"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "relationship ASC", null, true, null, null);

                if (cur.getCount() > 0) {

                    cur.moveToFirst();
                    while (cur.isAfterLast() == false) {

                        confirmViewModel = new ConfirmViewModel();
                        confirmViewModel.setStrName(cur.getString(0));
                        confirmViewModel.setStrDesc(cur.getString(1));
                        confirmViewModel.setStrAddress(cur.getString(2));
                        confirmViewModel.setStrContacts(cur.getString(3));
                        confirmViewModel.setStrEmail(cur.getString(4));
                        confirmViewModel.setStrImg(cur.getString(5));

                        ConfirmFragment.CustomListViewValuesArr.add(confirmViewModel);
                        count++;

                        Log.e("Image 5", cur.getString(5));

                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);

                //

                //
            } catch (Exception e) {
                dbHelper.closeCursor(cur);
            }
        }


        return count;
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