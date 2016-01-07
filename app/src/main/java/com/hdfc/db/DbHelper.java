package com.hdfc.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hdfc.config.Config;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.DatabaseObjectNotClosedException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "newzeal";
    private static DbHelper dbInstance = null;
    private static SQLiteDatabase db;
    public String Create_User_Tbl = "CREATE TABLE user ( user_id integer primary key autoincrement," +
            " name VARCHAR(100), email VARCHAR(100) UNIQUE, password VARCHAR(100), contact_no VARCHAR(15)," +
            " status integer)";
    public String Create_Dependant_Tbl = "CREATE TABLE dependant ( dependant_id integer primary key autoincrement," +
            " name VARCHAR(100) unique, contact_no VARCHAR(15), address VARCHAR(300), relationship VARCHAR(20)," +
            " age integer, diseases VARCHAR(200), notes VARCHAR(500), user_id integer, image_name varchar(100), status integer)";
    private Context _ctxt;

    private  File originalFile = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._ctxt = context;
        originalFile = _ctxt.getDatabasePath(DATABASE_NAME);
    }

    public static synchronized DbHelper getInstance(Context ctx) {

        if (dbInstance == null) {
            dbInstance = new DbHelper(ctx.getApplicationContext());
        }
        return dbInstance;
    }

    // Open the database connection.
    public void open() {
        try {
            SQLiteDatabase.loadLibs(_ctxt);
            db = this.getWritableDatabase(Config.dbPass);
        }catch (Exception e1){
            try {
                if(originalFile.exists())
                    encrypt(true);
                e1.printStackTrace();
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
        Log.i("DB", "open");
    }

    public void close() {
        try {
            if (db != null && db.isOpen())
                db.close();
        }catch (Exception e){
        }
        Log.i("DB", "close");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_User_Tbl);
        db.execSQL(Create_Dependant_Tbl);
        Log.i("DB", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            encrypt(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropDb(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS dependant");
    }

    public void closeCursor(Cursor cursor){
        if(cursor!=null&&!cursor.isClosed())
            cursor.close();
    }

    private ContentValues createContentValues(String values[], String names[]) {
        ContentValues values1 = new ContentValues();

        for (int i = 0; i < values.length; i++) {
            values1.put(names[i], values[i]);
        }

        return values1;
    }

    public long insert(String values[], String names[], String tbl) {

        if(!db.isOpen())
            open();

        ContentValues initialValues = createContentValues(values, names);
        long inserted =  0;
        try {
            inserted = db.insert(tbl, null, initialValues);
        }catch (Exception e){
        }
        return inserted;
    }

    @SuppressWarnings("DatabaseObjectNotClosedException")
    public Cursor fetch(String tbl, String names[], String where, String args[], String order, String limit, boolean isDistinct, String groupBy, String having) {

        if(!db.isOpen())
            open();

        Cursor cur = null;
        try{
            cur = db.query(isDistinct, tbl, names, where, args, groupBy, having, order, limit);
        }catch (DatabaseObjectNotClosedException e){
        }catch(Exception e){
        }

        return  cur;
    }

    public boolean delete(String tbl, String where, String args[]) {

        if(!db.isOpen())
            open();

        boolean isDeleted = false;
        try{
            isDeleted = db.delete(tbl, where, args) > 0;
        }catch (Exception e){
        }
        return isDeleted;
    }

    public boolean update(String where, String values[], String names[], String tbl, String args[]) {

        if(!db.isOpen())
            open();

        ContentValues updateValues = createContentValues(values, names);

        boolean isUpdated = false;
        try{
            isUpdated = db.update(tbl, updateValues, where, args) > 0;
        }catch (Exception e){
        }
        return isUpdated;
    }


    public boolean backupDatabase(){

        boolean isSuccess=false;

        try {
            File databaseFile = _ctxt.getDatabasePath(DATABASE_NAME);
            FileInputStream fIs = new FileInputStream(databaseFile);

            Date now = new Date();
            FileOutputStream fOs = _ctxt.openFileOutput(DATABASE_NAME+"_bkp_"+now.getTime(), Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;

            try {
                while ((length = fIs.read(buffer))>0){
                    fOs.write(buffer, 0, length);
                }
                fOs.flush();
                fOs.close();
                fIs.close();
            } catch (Exception e) {
                fOs.flush();
                fOs.close();
                fIs.close();
            }
            isSuccess=true;
        } catch (Exception e) {
        }

        return isSuccess;
    }

    public void encrypt(boolean isToOpen) throws IOException {

        if (originalFile.exists()) {

            File newFile = File.createTempFile("instafr", "_tmp_", _ctxt.getFilesDir());

            String dbPath = originalFile.getAbsolutePath().toString();

            SQLiteDatabase db = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), "", null, SQLiteDatabase.OPEN_READWRITE);

            db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';", newFile.getAbsolutePath(), Config.dbPass));
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            db.rawExecSQL("DETACH DATABASE encrypted;");

            int version = db.getVersion();

            if(version<DATABASE_VERSION)
                version=DATABASE_VERSION;

            db.close();

            db = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                    Config.dbPass, null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(version);
            db.close();

            originalFile.delete();
            newFile.renameTo(originalFile);

            if(isToOpen) {
                DbHelper.db = SQLiteDatabase.openDatabase(dbPath,
                        Config.dbPass, null,
                        SQLiteDatabase.OPEN_READWRITE);
            }
        }
    }
}