package com.cdgpacific.sellercatpal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by kingp on 9/23/2016.
 */
public class MySqlLite {
    private static String TAG = "MySqlLite";
    public static String DB_NAME ="CatPalMobileDb";
    private SQLiteDatabase db;

    public MySqlLite(SQLiteDatabase database)
    {
        db = database;
        created_table("list_of_boxes", "box_uid VARCHAR, worker_id VARCHAR, yard_id VARCHAR, box_id VARCHAR, box_status INT");
        created_table("list_of_comments", "box_id VARCHAR, worker_id VARCHAR, image_name VARCHAR, comment VARCHAR");
    }

    public void created_table(String name, String fields_with_data_type) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ name +" ("+ fields_with_data_type +");");
        Log.d(TAG, "table was created.");
    }

    public boolean insert(String table, String values) {
        try {
            db.execSQL("INSERT INTO "+ table +" VALUES("+ values +");");
            Log.d(TAG, "DONE");
            return true;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public boolean execute(String query) {
        try {
            db.execSQL(query);
            Log.d(TAG, "DONE");
            return true;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public Cursor select(String query) {
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return c;
    }

    public int findListOfBoxes(String UID) {

        Log.d("String UID", UID);

        Cursor c = this.select("SELECT * FROM list_of_boxes WHERE box_uid = '"+UID+"';");
        while(c.moveToNext())
        {
            int box_uid = Integer.parseInt(c.getString(0));
            if(box_uid > 0) {
                return box_uid;
            }
        }
        return 0;
    }

}
