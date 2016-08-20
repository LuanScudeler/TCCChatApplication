package br.chatup.tcc.chat;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;

import br.chatup.tcc.activity.ChatActivity;
import br.chatup.tcc.cache.CacheStorage;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 5/8/2016.
 */
public class ChatListener implements ChatManagerListener {
    /*
    * Chat listener trigges when user receives incoming messages..
    * It adds a message listener to the chat to receive all future messages
    *
    * */
    private static final String TAG = Util.getTagForClass(ChatListener.class);
    private Context context;

    public ChatListener (Context context) {
        this.context = context;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally) {
            if(!CacheStorage.getInstanceCachedChats().containsKey(chat.getParticipant())){
                CacheStorage.addChatContact(chat.getParticipant(), chat.getThreadID());
                chat.addMessageListener(new MessageListener(context));
                Log.d(TAG, "[CHAT CREATED] Receiver not found in contacts cache. ADDING TO CACHE -> Contact: " + chat.getParticipant() + " | ThreadID: " + chat.getThreadID());
            }else{
                Log.d(TAG, "[CHAT ALREADY OPENED] " + "Contact: " + chat.getParticipant() + " | ThreadID: " + CacheStorage.getInstanceCachedChats().get(chat.getParticipant()));
            }
        }
    }
}

