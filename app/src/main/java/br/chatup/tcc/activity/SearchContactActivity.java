package br.chatup.tcc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.chatup.tcc.bean.User;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.Constants;
import br.chatup.tcc.utils.JsonParser;
import br.chatup.tcc.utils.RestFacade;
import br.chatup.tcc.utils.Util;

public class SearchContactActivity extends AppCompatActivity {

    private EditText edtUsername;
    private Button btnSearch;
    private ProgressDialog pDialog;
    private ListView lvContacts;
    private List<User> usersFound;
    private ContactSearchAdapter adapter;
    private static final String TAG = Util.getTagForClass(SearchContactActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);
        edtUsername = (EditText) findViewById(R.id.edtUsername_SearchContactActivity);
        usersFound = new ArrayList<User>();
        btnSearch = (Button) findViewById(R.id.btnSearch_SearchContactActivity);
        lvContacts = (ListView) findViewById(R.id.lvContactsFound_SearchContactActivity);
        adapter = new ContactSearchAdapter();
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Animation animation1 = new AlphaAnimation(1.0f, 1.0f);
                animation1.setDuration(2000);
                view.startAnimation(animation1);
                Intent i = new Intent(SearchContactActivity.this, ContactDetailsActivity.class);
                i.putExtra("contact", JsonParser.toJson(usersFound.get(position)));
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUserTask sut = new SearchUserTask();
                sut.execute(edtUsername.getText().toString());
            }
        });
    }

    class SearchUserTask extends AsyncTask<String, Void, List<User>> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SearchContactActivity.this);
            pDialog.setMessage(Util.getStringResource(SearchContactActivity.this, R.string.please_wait));
            pDialog.show();
        }

        @Override
        protected List<User> doInBackground(String... params) {
            List<User> users = new ArrayList<User>();
            ResponseEntity<String> s = RestFacade.get(Constants.RESTAPI_USERS_URL + "?search="+params[0]);
            try {
                com.google.gson.JsonParser jp = new com.google.gson.JsonParser();
                JsonObject jobj = jp.parse(s.getBody()).getAsJsonObject();
                String objs = jobj.get("user").toString();
                if(objs.contains("[")) {
                    User[] usr = JsonParser.fromJson(User[].class, objs);
                    users = Arrays.asList(usr);
                }
                else {
                    users.add(JsonParser.fromJson(User.class,objs));
                }

            }
            catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return users;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            pDialog.cancel();
            usersFound = users;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BaseAdapter) lvContacts.getAdapter()).notifyDataSetChanged();
                }
            });
        }
    }

    public class ContactSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return usersFound.size();
        }

        @Override
        public Object getItem(int position) {
            return usersFound.get(position);
        }

        @Override
        public long getItemId(int position) {
            return usersFound.indexOf(usersFound.get(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User user = usersFound.get(position);
            View layout;

            LayoutInflater li = (LayoutInflater) SearchContactActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.contact_search_list_item, null);

            TextView username = (TextView) v.findViewById(R.id.txtUsername_ListItem);
            TextView email = (TextView) v.findViewById(R.id.txtEmail_ListItem);
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            return v;
        }
    }

}
