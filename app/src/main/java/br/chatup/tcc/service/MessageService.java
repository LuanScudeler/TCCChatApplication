package br.chatup.tcc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 8/14/2016.
 */
public class MessageService extends Service{

    private static final String TAG = Util.getTagForClass(MessageService.class);

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return new LocalBinder<MessageService>(this);
    }

    public void notifyMessage(ChatMessage message) {
        Intent intent = new Intent("receivedMessage");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //sendBroadcast(intent); (BroadcastReceiver)
    }

}
