package com.example.notifymeproject;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SQLiteService extends SQLiteOpenHelper {
    public static final String DATABASE = "NotifyMeDB.db";
    public static final String TABLE = "notifymelogs";
    public static final String USER = "User";
    public static final String HEADERS = "Headers";
    public static final String TITLE = "Title";
    public static final String BODY = "Body";
    public static final String GENERATED_DATE = "Date";
    private static final String TAG = "DBHELPER";

    public SQLiteService(Context context) {
        super(context, DATABASE , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE+"("+USER+" text, "+HEADERS+" text, "+TITLE+" text, "+BODY+" text, "+GENERATED_DATE+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE);
        onCreate(db);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE);
        Log.d(TAG, "Record inserted"+numRows);
        return numRows;
    }

    public boolean insertNotifications (String User, String Headers, String Title, String Body) {
        Boolean res;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into "+TABLE+" values('"+User+"','"+Headers+"','"+Title+"','"+Body+"','"+ dateFormat.format(date)+"')");
        if (numberOfRows()>0){
            Log.d(TAG, "Record inserted "+numberOfRows());
            res=true;
        }
        else{
            Log.d(TAG, "Record not inserted");
            res=false;
        }
        return res;
    }

    public boolean deleteAllNotifications () {
        Boolean res;
        /*String sql = "DELETE FROM myTable WHERE Save_Date <= date('now','-2 day')";
        db.execSQL(sql);*/
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE);
        if (numberOfRows()==0){
            Log.d(TAG, "Record deleted");
            res=true;
        }
        else{
            Log.d(TAG, "Error occured");
            res=false;
        }
        return res;
    }

    public boolean deleteNotifications () {
        Boolean res;
        /*String sql = "DELETE FROM myTable WHERE Save_Date <= date('now','-2 day')";
        db.execSQL(sql);*/
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from "+TABLE+" where "+GENERATED_DATE +" <= DATETIME('NOW', '-7 days')");
        int deletedrows=db.delete(TABLE,GENERATED_DATE +" <= DATETIME('NOW', '-7 days')",null);
        if (deletedrows>0){
            Log.d(TAG, "Records deleted");
            res=true;
        }
        else{
            Log.d(TAG, "No records found matching condition.");
            res=false;
        }
        return res;
//        return db.delete(TABLE,GENERATED_DATE +" <= DATETIME('NOW', '-7 days')",null) > 0;
    }

    public HashMap<String,ArrayList<String>> getAllNotifications(String User) {
        ArrayList<String> H = new ArrayList<>();
        ArrayList<String> T = new ArrayList<>();
        ArrayList<String> B = new ArrayList<>();
        //ArrayList<String> array_list = new ArrayList<>();
        HashMap<String,ArrayList<String>> hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE+" where "+USER+"='"+User+"'", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            H.add(res.getString(res.getColumnIndex(HEADERS)));
            T.add(res.getString(res.getColumnIndex(TITLE)));
            B.add(res.getString(res.getColumnIndex(BODY)));

            /*array_list.add(res.getString(res.getColumnIndex(HEADERS)));
            array_list.add(res.getString(res.getColumnIndex(TITLE)));
            array_list.add(res.getString(res.getColumnIndex(BODY)));*/
            res.moveToNext();
        }
        res.close();
       /* array_list.add(H);
        array_list.add(T);
        array_list.add(B);*/
       hp.put("header",H);
        hp.put("title",T);
        hp.put("body",B);
        return hp;
    }
}
