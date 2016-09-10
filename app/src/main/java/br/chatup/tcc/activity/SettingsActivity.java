package br.chatup.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.chatup.tcc.myapplication.R;
import br.chatup.tcc.utils.App;

public class SettingsActivity extends AppCompatActivity {

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
                App.setTranslationEnabled(isChecked);
            }
        });
    }
}
