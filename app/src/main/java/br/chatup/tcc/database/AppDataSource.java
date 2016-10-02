package br.chatup.tcc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.utils.Constants;

import static br.chatup.tcc.database.DataBaseHelper.COLUMN_CONTACT;
import static br.chatup.tcc.database.DataBaseHelper.COLUMN_DATE;
import static br.chatup.tcc.database.DataBaseHelper.COLUMN_IS_ME;
import static br.chatup.tcc.database.DataBaseHelper.COLUMN_MSG_BODY;
import static br.chatup.tcc.database.DataBaseHelper.COLUMN_MSG_BODY_TRANSLATED;
import static br.chatup.tcc.database.DataBaseHelper.TABLE_CHAT_MESSAGES;

/**
 * Created by Luan on 9/3/2016.
 */
public class AppDataSource implements ChatMessagesDao {
    private static final String TAG = Constants.LOG_TAG + AppDataSource.class.getSimpleName();
    private SQLiteDatabase db;

    public AppDataSource(Context ctx) {
        DataBaseHelper dbHelper = new DataBaseHelper(ctx);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void insert(ChatMessage chatMessage) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_CONTACT, chatMessage.getReceiver());
        v.put(COLUMN_MSG_BODY, chatMessage.getBody());
        v.put(COLUMN_IS_ME, chatMessage.isMe() == true ? 1 : 0);
        v.put(COLUMN_DATE, chatMessage.getDate());
        v.put(COLUMN_MSG_BODY_TRANSLATED, chatMessage.getBodyTranslated());

        db.insert(TABLE_CHAT_MESSAGES, null, v);
    }

    @Override
    public ArrayList<ChatMessage> findAllByContactJID(String contactJID) {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        Cursor cursor = db.
                rawQuery("SELECT * FROM chatMessages WHERE contact = ?", new String[]{contactJID});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ChatMessage chatMessage = cursorToChatMessage(cursor);
            /*Log.d(TAG, "[findAllByContactJID] contact: " + chatMessage.getReceiver() +
                    " | msgBody: " + chatMessage.getBody() +
                    " | isMe: " + chatMessage.isMe() +
                    " | date: " + chatMessage.getDate() +
                    " | messageBodyTranslated: " + chatMessage.getBodyTranslated());*/
            chatMessageList.add(chatMessage);
            cursor.moveToNext();
        }
        cursor.close();

        return chatMessageList;
    }

    @Override
    public ChatMessage findLastOneByContactJID(String contactJID) {
        Log.d(TAG, "[findLastOneByContactJID]: Searching on database...");
        ChatMessage chatMessage = null;
        Cursor cursor = db.
                rawQuery("SELECT * FROM chatMessages WHERE contact = ?", new String[]{contactJID});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.isLast()){
                chatMessage = cursorToChatMessage(cursor);
                /*Log.d(TAG, "[findLastOneByContactJID]: " + chatMessage.getReceiver() +
                        " | msgBody: " + chatMessage.getBody() +
                        " | isMe: " + chatMessage.isMe() +
                        " | date: " + chatMessage.getDate() +
                        " | messageBodyTranslated: " + chatMessage.getBodyTranslated());*/
            }
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return chatMessage;
    }


    @Override
    public List<String> findContactsFromActiveChats() {
        ArrayList<String> contactsFromActiveChatsList = new ArrayList<String>();
        Cursor cursor = db.
                rawQuery("SELECT DISTINCT (contact) FROM chatMessages", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d(TAG, "[findContactsFromActiveChats] contact: " + cursor.getString(0) );
            contactsFromActiveChatsList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();

        return contactsFromActiveChatsList;
    }

    private ChatMessage cursorToChatMessage(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setReceiver(cursor.getString(1));
        chatMessage.setBody(cursor.getString(2));
        chatMessage.setMe(cursor.getInt(3) == 1 ? true : false);
        chatMessage.setDate(cursor.getString(4));
        chatMessage.setBodyTranslated(cursor.getString(5));
        return chatMessage;
    }
}
