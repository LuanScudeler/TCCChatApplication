package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.chatup.tcc.database.AppDataSource;
import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.App;
import br.chatup.tcc.utils.Util;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = Util.getTagForClass(SettingsActivity.class);
    private Switch swtTranslation;
    private AppDataSource db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new AppDataSource(this);

        swtTranslation = (Switch) findViewById(R.id.swtTranslation_Settings);
        swtTranslation.setChecked(db.findTranslationMode("translationMode")==1?true:false);

        swtTranslation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Translation mode changed to: " + isChecked);

                db.updatePreference("translationMode", Boolean.toString(isChecked)=="true"?"1":"0");
                App.setTranslationEnabled(isChecked);
            }
        });
    }
}
