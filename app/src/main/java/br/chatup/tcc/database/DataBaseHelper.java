package br.chatup.tcc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Luan on 9/3/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "chatsArchive.db";
    public static final int DB_VERSION = 2;
    public static final String TABLE_CHAT_MESSAGES = "chatMessages";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_MSG_BODY = "msgBody";
    public static final String COLUMN_IS_ME = "isMe";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MSG_BODY_TRANSLATED = "msgBodyTranslated";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CHAT_MESSAGES + "(_ID INTEGER PRIMARY KEY, " +
                COLUMN_CONTACT + " TEXT NOT NULL, " +
                COLUMN_MSG_BODY + " TEXT NOT NULL, " +
                COLUMN_IS_ME + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_MSG_BODY_TRANSLATED + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_CHAT_MESSAGES + ";");
        onCreate(db);
    }
}
