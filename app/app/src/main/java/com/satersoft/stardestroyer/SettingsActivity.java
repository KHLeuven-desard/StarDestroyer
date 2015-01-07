package com.satersoft.stardestroyer;

import android.os.Bundle;
import android.view.View;

import com.satersoft.stardestroyer.ui.FullScreenActivity;


public class SettingsActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void back(View v) {
        finish();
    }
}
