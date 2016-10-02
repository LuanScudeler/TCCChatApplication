package br.chatup.tcc.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;

import br.chatup.tcc.bean.ChatMessage;
import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 10/2/2016.
 */
public class ActiveChatsListAdapter extends ArrayAdapter<String> {

    private static final String TAG = Util.getTagForClass(ContactsListAdapter.class);
    private AppDataSource db;

    public ActiveChatsListAdapter(Context context, int textViewResourceId, List<String> contactsFromActiveChatsList) {
        super(context, textViewResourceId, contactsFromActiveChatsList);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        db = new AppDataSource(App.getCurrentActivity());

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.active_chats_list_item, null);
        }

        String contactJID = getItem(position);
        ChatMessage lastChatMessage = db.findLastOneByContactJID(contactJID);

        String lastMessageBody;
        if (lastChatMessage.isMe())
            lastMessageBody = "Me: ".concat(lastChatMessage.getBody());
        else
            lastMessageBody = lastChatMessage.getBody();

        if (contactJID != null) {
            TextView tvNickname = (TextView) v.findViewById(R.id.nickname);
            TextView tvLastMessage = (TextView) v.findViewById(R.id.last_message);
            //TextView tvPresence = (TextView) v.findViewById(R.id.presence);

            tvNickname.setText(lastChatMessage.getReceiver());
            tvLastMessage.setText(lastMessageBody);

            /*if(tvPresence != null)
                tvPresence.setText("TODO");*/
        }

        return v;
    }
}
