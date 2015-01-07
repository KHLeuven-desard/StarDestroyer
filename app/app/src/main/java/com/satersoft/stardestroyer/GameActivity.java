package com.satersoft.stardestroyer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.satersoft.stardestroyer.domain.InfoWrapper;
import com.satersoft.stardestroyer.domain.service.IService;
import com.satersoft.stardestroyer.domain.service.Service;
import com.satersoft.stardestroyer.domain.util.KHLeuvenMobileException;
import com.satersoft.stardestroyer.domain.util.Observer;
import com.satersoft.stardestroyer.ui.CustomSurface;
import com.satersoft.stardestroyer.ui.FullScreenActivity;

import java.util.HashSet;
import java.util.Set;


public class GameActivity extends FullScreenActivity implements SensorEventListener, Observer {
    private CustomSurface customSurface;

    private InfoWrapper info;

    private IService service;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super
        super.onCreate(savedInstanceState);

        info = (InfoWrapper) getIntent().getExtras().get("info");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        service = new Service("Medium", info.file, getApplicationContext());
        service.addObserverToGame(this);

        // We create our Surfaceview for our OpenGL here.
        customSurface = new CustomSurface(this);
        customSurface.setService(service);
        customSurface.setImages(info.bitHash);

        service.startGame();

        // Set our view.
        setContentView(R.layout.activity_game);

        // Retrieve our Relative layout from our main layout we just set to our view.
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main);

        // Attach our surfaceview to our relative layout from our main layout.
        RelativeLayout.LayoutParams glParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.addView(customSurface, glParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //customSurface.onPause();
        unregisterSensorListener();
    }

    private void gameOver() {
        if(service != null && service.isGameOver()) {
            int score = service.getScore();
            Log.e("GAMEOVER", "ITS FUCKING OVER (score: " + score + ")");
            Intent intent = new Intent(this, GameOverActivity.class);
            info.score = new Integer(score);
            try {
                service.pauseGame();
            } catch (KHLeuvenMobileException e) {
                e.printStackTrace();
            }
            intent.putExtra("info", info);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //customSurface.onResume();
        registerSensorListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor. TYPE_ACCELEROMETER) {
            float xAcceleration = -event.values[0],
                yAcceleration = -event.values[1];
            Set<String> cmd = new HashSet<String>();
            float sens = 1;
            if(xAcceleration < -1){
                cmd.add("Left");
                sens  =xAcceleration;
            }
            if(xAcceleration > 1){
                cmd.add("Right");
                sens  =xAcceleration;
            }
            if(yAcceleration < -1){
                cmd.add("Down");
                sens  =yAcceleration;
            }
            if(yAcceleration > 1) {
                cmd.add("Up");
                sens  =yAcceleration;
            }

            if(cmd.size() > 0)
                service.command(cmd, sens);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensorListener() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }
    private void unregisterSensorListener(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        info.score = -1;
        intent.putExtra("info", info);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();

        int eventaction = event.getAction();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN:
                //isTouch = true;
                service.fire(true);
                break;

            case MotionEvent.ACTION_MOVE:

                //Toast.makeText(this, "MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();

                break;

            case MotionEvent.ACTION_UP:

                //Toast.makeText(this, "ACTION_UP "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                service.fire(false);
                break;

        }

        return true;

    }

    @Override
    public void update() {
        gameOver();
    }
}
