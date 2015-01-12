package com.satersoft.stardestroyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.satersoft.stardestroyer.domain.InfoWrapper;
import com.satersoft.stardestroyer.ui.FullScreenActivity;


public class GameOverActivity extends FullScreenActivity {
    private InfoWrapper info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        info = (InfoWrapper) getIntent().getExtras().get("info");

        TextView score = ((TextView)findViewById(R.id.txtScore));
        score.setText("" + info.score);


        //backToMain();
    }

    public void backToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("info", info);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        info.score = -1;
        intent.putExtra("info", info);
        startActivity(intent);
        finish();
    }
}
