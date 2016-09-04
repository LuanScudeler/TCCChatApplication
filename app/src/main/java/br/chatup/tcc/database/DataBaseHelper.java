package br.chatup.tcc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Luan on 9/3/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "chatsArchive.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "chatMessages";
    private static final String COLUMN_1 = "contact";
    private static final String COLUMN_2 = "msgBody";
    private static final String COLUMN_3 = "isMe";
    private static final String COLUMN_4 = "date";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_ID INTEGER PRIMARY KEY, " +
                COLUMN_1 + " TEXT NOT NULL, " +
                COLUMN_2 + " TEXT NOT NULL, " +
                COLUMN_3 + " INTEGER NOT NULL, " +
                COLUMN_4 + " TEXT NOT NULL );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+TABLE_NAME+";");
        onCreate(db);
    }
}
