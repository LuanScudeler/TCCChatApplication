package br.chatup.tcc.chat;

import android.util.Log;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;

import br.chatup.tcc.utils.Constants;

/**
 * Created by Luan on 5/8/2016.
 */
public class ChatListener implements ChatManagerListener {
    /*
    * Chat listener trigges when user receives incoming messages..
    * It adds a message listener to the chat to receive all future messages
    *
    * */
    private static final String TAG = Constants.LOG_TAG + ChatListener.class.getSimpleName();

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally)
            chat.addMessageListener(new MessageListener());
            Log.d(TAG, "CHAT CREATED - ThreadID: " + chat.getThreadID() + " Listener: " + chat.getListeners().toString());
    }
}

