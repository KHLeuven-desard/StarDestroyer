package com.satersoft.stardestroyer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
    private transient IService service;
    private SensorManager sensorManager;
    public static final String STATE_SERVICE = "service";

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        try {
            //this.service = (Service) savedInstanceState.getSerializable(STATE_SERVICE);
            service.resumeGame();
        } catch (Exception ex){
            // misschien hier het spel restarten?
            //service.startGame();
        }
        update();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        try{
            this.service.pauseGame();
        } catch (KHLeuvenMobileException ex){
            // misschien hier het spel restarten?
            //service.startGame();
        }
        try {
            //savedInstanceState.putSerializable(STATE_SERVICE, this.service);
        } catch(Exception e) {}
        super.onSaveInstanceState(savedInstanceState);
    }
    /*
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        service = (IService) savedInstanceState.getSerializable(STATE_SERVICE);
        info = (InfoWrapper)savedInstanceState.getSerializable(INFO);
        registerSensorListener();
        try {
            service.resumeGame();
        } catch (KHLeuvenMobileException ex){
            // misschien hier het spel restarten?
        }
        update();

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        try{
            service.pauseGame();
        } catch (KHLeuvenMobileException ex){
            // misschien hier het spel restarten?
        }
        savedInstanceState.putSerializable(STATE_SERVICE, this.service);
        savedInstanceState.putSerializable(INFO, this.info);
        unregisterSensorListener();
        super.onSaveInstanceState(savedInstanceState);

    }*/

    private String shipToString(int choice) {
        String ship = "Medium";

        switch(choice){
            case 0:
                ship = "Light";
                break;
            case 1:
                ship = "Medium";
                break;
            case 2:
                ship = "Heavy";
                break;
        }

        return ship;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Super
        super.onCreate(savedInstanceState);

        info = (InfoWrapper) getIntent().getExtras().get("info");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        service = new Service(shipToString(info.selectedShip), info.file, getApplicationContext());

        // We create our Surfaceview for our OpenGL here.
        customSurface = new CustomSurface(this);
        customSurface.setService(service);
        customSurface.setImages(info.bitHash);

        //service.startGame();

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
        try {
            service.removeObserverFromGame(this);
            service.pauseGame();
        } catch (KHLeuvenMobileException e) {
        }
        unregisterSensorListener();
    }

    private void gameOver() {
        if(service != null && service.isGameOver()) {
            int score = service.getScore();
            Intent intent = new Intent(this, GameOverActivity.class);
            info.score = new Integer(score);
            info.victor = service.getVictor();
            try {
                service.pauseGame();
            } catch (KHLeuvenMobileException e) {
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
        try {
            service.addObserverToGame(this);
            customSurface.setService(service);
            customSurface.setImages(info.bitHash);
            service.resumeGame();

        } catch (KHLeuvenMobileException e) {
            //e.printStackTrace();
        }
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
        customSurface.update();
    }
}
