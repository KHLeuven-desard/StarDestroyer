package com.satersoft.stardestroyer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class FullScreenActivity extends Activity {
    private boolean showSignIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // Fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void setShowSignIn(boolean showSignIn) {
        this.showSignIn = showSignIn;
        updateUI();
    }

    public boolean showSignIn() { return showSignIn; }

    public void updateUI() {}
}
