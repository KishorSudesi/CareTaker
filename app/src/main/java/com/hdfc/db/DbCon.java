package com.hdfc.db;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.hdfc.model.DependantModel;
import com.hdfc.newzeal.fragments.AddDependantFragment;

import net.sqlcipher.Cursor;

public class DbCon {

    private static DbHelper dbHelper;
    private static boolean isDbOpened=false;
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

    public DbCon open(){
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

    public long insertUser(String strName, String strEmail, String strPassword, String strContactNo, long longUserId){

        long longInserted=0;

        if(isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"user_id"}, "user_id=?", new String[]{String.valueOf(longUserId)}, null, null, false, null, null);

                if(cur.getCount()<=0) {

                    longInserted=dbHelper.insert(new String[]{strName, strEmail, strPassword, strContactNo, "0"},
                            new String[]{"name", "email", "password", "contact_no", "status"}, "user");
                }else{

                    dbHelper.update("user_id=?", new String[]{strName, strEmail, strPassword, strContactNo},
                            new String[]{"name", "email", "password", "contact_no"}, "user", new String[]{String.valueOf(longUserId)});
                    longInserted=longUserId;
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
                e.printStackTrace();
            }
        }
        return longInserted;
    }

    public void retrieveUser(long longUserId, EditText editName, EditText editemail, EditText editContactNo){

        if(isDbOpened) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"name", "email", "contact_no"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "user_id DESC", "0,1", true, null, null);

                if(cur.getCount()>0) {
                    cur.moveToFirst();
                    while(cur.isAfterLast()==false){
                        editName.setText(cur.getString(0));
                        editemail.setText(cur.getString(1));
                        editContactNo.setText(cur.getString(2));
                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
                e.printStackTrace();
            }
        }
    }

    public void deleteTempUsers(){

        if(isDbOpened) {
            try {
                dbHelper.delete("user","status=?", new String[]{"0"});
            }catch (Exception e){
            }
        }
    }

    public long insertDependant(String strName, String strContactNo, String strAddress, String strRelation, long longUserId){

        long longInserted=0;

        if(isDbOpened) {

            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"dependant_id"}, "name=? and user_id=?", new String[]{strName, String.valueOf(longUserId)}, null, null, false, null, null);

                if(cur.getCount()<=0) {

                    longInserted=dbHelper.insert(new String[]{strName, strContactNo, strAddress, strRelation, String.valueOf(longUserId), "0"},
                            new String[]{"name", "contact_no", "address", "relationship" ,"user_id", "status"}, "dependant");
                }else{

                    dbHelper.update("name=? and user_id=?", new String[]{strContactNo, strAddress, strRelation},
                            new String[]{"contact_no", "address", "relationship"}, "dependant", new String[]{strName, String.valueOf(longUserId)});
                    cur.moveToFirst();
                    longInserted=cur.getInt(0);
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
            }
        }
        return longInserted;
    }

    public void retrieveDependantPersonal(long longUserId, EditText editName, EditText editContactNo, EditText editAddress, EditText editRelation, String strName){

        if(isDbOpened) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("user", new String[]{"name", "contact_no", "address", "relationship"}, "user_id=? and name=?", new String[]{String.valueOf(longUserId), strName}, "name DESC", "0,1", true, null, null);

                if(cur.getCount()>0) {
                    cur.moveToFirst();
                    while(cur.isAfterLast()==false){
                        Log.d("TAG 2", cur.getString(0));
                        editName.setText(cur.getString(0));
                        editContactNo.setText(cur.getString(1));
                        editAddress.setText(cur.getString(2));
                        editRelation.setText(cur.getString(3));
                        cur.moveToNext();
                    }
                }

                dbHelper.closeCursor(cur);
            }catch (Exception e){
                dbHelper.closeCursor(cur);
            }
        }
    }

    public boolean updateDependantMedicalDetails(String strName, String strAge, String strDiseases, String strNotes, long longUserId){

        boolean isUpdated=false;

        if(isDbOpened) {

            try {
                isUpdated = dbHelper.update("name=? and user_id=?", new String[]{strAge, strDiseases, strNotes},
                            new String[]{"age", "diseases", "notes"}, "dependant", new String[]{strName, String.valueOf(longUserId)});

            }catch (Exception e){
            }
        }
        return isUpdated;
    }

    public void deleteTempDependants(){

        if(isDbOpened) {
            try {
                dbHelper.delete("dependant","status=?", new String[]{"0"});
            }catch (Exception e){
            }
        }
    }

    public int retrieveDependants(long longUserId) {

        DependantModel dpndntModel = null;

        AddDependantFragment.CustomListViewValuesArr.clear();

        int count = 0;

        if(isDbOpened) {
            Cursor cur = null;

            try {

                cur = dbHelper.fetch("dependant", new String[]{"name", "image_name", "relationship"}, "user_id=?", new String[]{String.valueOf(longUserId)}, "relationship ASC", null, true, null, null);

                if(cur.getCount()>0) {

                    cur.moveToFirst();
                    while(cur.isAfterLast()==false){

                        dpndntModel = new DependantModel();
                        dpndntModel.setStrName(cur.getString(0));
                        dpndntModel.setStrRelation(cur.getString(2));
                        dpndntModel.setStrImg(cur.getString(1));

                        AddDependantFragment.CustomListViewValuesArr.add(dpndntModel);

                        cur.moveToNext();
                    }

                    count = cur.getCount();
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

        AddDependantFragment.CustomListViewValuesArr.add(dpndntModel);

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