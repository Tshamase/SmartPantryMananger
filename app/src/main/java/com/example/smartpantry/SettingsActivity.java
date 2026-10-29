
package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);setContentView(R.layout.activity_settings);
        prefs=getSharedPreferences("smart_pantry_preferences",MODE_PRIVATE);
        Switch alerts=findViewById(R.id.expiryAlertsSwitch);
        alerts.setChecked(prefs.getBoolean("expiry_alerts",true));
        alerts.setOnCheckedChangeListener((button,checked)->prefs.edit().putBoolean("expiry_alerts",checked).apply());
        findViewById(R.id.backHome).setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class));finish();});
    }
}
