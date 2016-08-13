package br.chatup.tcc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;

import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.Util;

/**
 * Created by Luan on 5/29/2016.
 */
public class ContactsListAdapter extends ArrayAdapter<RosterEntry>{

    private static final String TAG = Util.getTagForClass(ContactsListAdapter.class);

    public ContactsListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ContactsListAdapter(Context context, int textViewResourceId, List<RosterEntry> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.contact_list_itemlistrow, null);
        }

        RosterEntry item = getItem(position);

        if (item != null) {
            TextView tvNickname = (TextView) v.findViewById(R.id.nickname);
            TextView tvPresence = (TextView) v.findViewById(R.id.presence);
            TextView tvUserJID = (TextView) v.findViewById(R.id.userJID);

            if(tvNickname != null)
                tvNickname.setText(item.getName());
            if(tvPresence != null)
                tvPresence.setText("Presence-TODO");
            if(tvUserJID != null)
                tvUserJID.setText(item.getUser());
        }

        return v;
    }
}
