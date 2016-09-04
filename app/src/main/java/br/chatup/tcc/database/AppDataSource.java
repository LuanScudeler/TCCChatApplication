package br.chatup.tcc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.utils.Constants;

/**
 * Created by Luan on 9/3/2016.
 */
public class AppDataSource implements ChatMessagesDao{
    private static final String TAG = Constants.LOG_TAG + AppDataSource.class.getSimpleName();
    private SQLiteDatabase db;

    public AppDataSource(Context ctx) {
        DataBaseHelper dbHelper = new DataBaseHelper(ctx);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void insert(ChatMessage chatMessage){
        ContentValues v = new ContentValues();
        v.put("contact", chatMessage.getReceiver());
        v.put("msgBody", chatMessage.getBody());
        v.put("isMe", chatMessage.isMe() == true ? 1 : 0);
        v.put("date", chatMessage.getDate());

        db.insert("chatMessages", null, v);
    }

    @Override
    public ArrayList<ChatMessage> findAllByContact(String contact){
        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        Cursor cursor = db.
                rawQuery("SELECT * FROM chatMessages WHERE contact = ?", new String[]{contact} );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ChatMessage chatMessage = cursorToModelo(cursor);
            Log.d(TAG, "[findAllByContact] contact: " + chatMessage.getReceiver() +
                    " | msgBody: " + chatMessage.getBody() +
                    " | isMe: " + chatMessage.isMe() +
                    " | date: " + chatMessage.getDate());
            chatMessageList.add(chatMessage);
            cursor.moveToNext();
        }
        cursor.close();

        return chatMessageList;
    }

    private ChatMessage cursorToModelo(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setReceiver(cursor.getString(1));
        chatMessage.setBody(cursor.getString(2));
        chatMessage.setIsMe(cursor.getInt(3) == 1 ? true : false);
        chatMessage.setDate(cursor.getString(4));
        return chatMessage;
    }
}
