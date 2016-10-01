package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Util;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = Util.getTagForClass(SettingsActivity.class);
    private Switch swtTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        swtTranslation = (Switch) findViewById(R.id.swtTranslation_Settings);
        swtTranslation.setChecked(App.isTranslationEnabled());
        swtTranslation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Translation mode changed to: " + isChecked);
                App.setTranslationEnabled(isChecked);
            }
        });
    }
}
