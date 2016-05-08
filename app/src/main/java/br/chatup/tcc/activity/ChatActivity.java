package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.xmpp.XmppManager;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnSendMessage;
    private static final String TAG = Constants.LOG_TAG + ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSendMessage = (ImageButton) findViewById(R.id.btnSendMessage);
    }

    public void btnSendMessageClick (View v) {
        sendMessage();
        //TODO: Send message setting message textBody from user input
    }

    public void sendMessage(){
        ChatManager chatmanager = ChatManager.getInstanceFor(XmppManager.getConn());
        //Change  "luanpc" for your xmpp.domain value. Can be found in openfire at Server Manager > System Properties
        //TODO: No need to create new instance of MessageListener if user has received the first message. Because ChatListener already create a MessageListener to the chat
        Chat newChat = chatmanager.createChat("username1@luanpc", new br.chatup.tcc.chat.MessageListener());

        try {
            newChat.sendMessage("A mizeravi, acerto!");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    //TODO: Handle message receivement, allowing user to answer the first message
}


