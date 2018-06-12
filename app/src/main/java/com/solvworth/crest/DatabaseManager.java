package com.solvworth.crest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by temp on 3/28/2018.
 */
public class DatabaseManager {
    public static final String DB_NAME = "scoresDB";
    public static final String DB_TABLE = "scores";
    public static final int DB_VERSION = 3;
    public static final String KEY_ID = "_id";

    public static final String NAME = "name";
    public static final String SCORE = "score";

    private static final String TAG = "gradeDB";
    private DatabaseHelper mDB;
    private SQLiteDatabase mDb;
    private static final String DB_CREATE =
            "create table " + DB_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT NOT NULL, " +
                    SCORE+ " INT NOT NULL );";

    private Context mCtx;


    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.w(TAG, "Upgrading database from version" + i + "to"
                    + i1 + ", which will destroy all old result");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + DB_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
    public DatabaseManager(Context ctx){
        this.mCtx = ctx;
    }

    public DatabaseManager open() throws SQLException {
        mDB = new DatabaseHelper(mCtx);
        mDb = mDB.getWritableDatabase();
        return this;
    }

    public void close(){
        mDB.close();
    }

    public long addScore(String sub, String score){
        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME, sub);
        contentValues.put(SCORE, score);
        return mDb.insert(DB_TABLE, null, contentValues);
    }

    public boolean deleteScore(long rowId){
            return mDb.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllScores() {
        return mDb.query(DB_TABLE, new String[]{KEY_ID, NAME, SCORE},
                null, null, null, null, "score DESC", null);
    }

    public Cursor fetchScore(String name) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DB_TABLE, new String[] {KEY_ID,
                                NAME, SCORE},
                        NAME + " = \'" + name + "\'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updateScore(String sub, String score){
        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME, sub);
        contentValues.put(SCORE, score);
        return mDb.update(DB_TABLE, contentValues, NAME + " = \'" + sub+ "\'", null) > 0;

    }

}
