package com.satersoft.stardestroyer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;

import com.satersoft.stardestroyer.GameActivity;
import com.satersoft.stardestroyer.domain.model.units.Projectile;
import com.satersoft.stardestroyer.domain.model.units.Ship;
import com.satersoft.stardestroyer.domain.service.IService;
import com.satersoft.stardestroyer.domain.service.Service;
import com.satersoft.stardestroyer.domain.util.KHLeuvenMobileException;
import com.satersoft.stardestroyer.domain.util.Observer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomSurface extends SurfaceView /*implements Observer*/ {
    private Paint paint;
    private IService service;
    private Map<Integer, SerialBitmap> images;
    //private Handler mHandler = new Handler();

    public CustomSurface(Context context) {
        super(context);
        paint = new Paint();
        setWillNotDraw(false);
    }

    public void setService(IService service) {
        this.service = service;
        //service.addObserver(this);
    }

    private void drawIt(final Canvas canvas) {
        /*mHandler.post(new Runnable(){
            public void run(){*/
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPaint(paint);

                if(service != null) {
                    List<Ship> enemies = service.getEnemies();
                    List<Projectile> projectiles = service.getProjectiles();
                    Ship player = service.getPlayer();
                    int score = service.getScore();
                    int armor = service.getLives();

                    if(enemies != null) {
                        for(Ship enemy : enemies) {
                            canvas.drawBitmap(images.get(enemy.getImageID()).bitmap, null, new Rect(enemy.getX(), enemy.getY(), enemy.getX() + enemy.getWidth(), enemy.getY() + enemy.getHeight()), paint); // left top right bottom
                        }
                    }
                    if(projectiles != null) {
                        Iterator<Projectile> it = projectiles.iterator();
                        Projectile p;
                        while (it.hasNext()) {
                            p = it.next();
                            if(p != null)
                                canvas.drawBitmap(images.get(p.getImageID()).bitmap, null, new Rect(p.getX(), p.getY(), p.getX() + p.getWidth(), p.getY() + p.getHeight()), paint);
                        }
                    }
                    if(player != null) {
                        canvas.drawBitmap(images.get(player.getImageID()).bitmap, null, new Rect(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + player.getHeight()), paint);
                    }

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(72);
                    canvas.drawText("Armor: " + armor, 60, 60, paint);
                    canvas.drawText("Score: " + score, 60, 160, paint);
                    //Log.e("LIVES", "" + service.getLives());
                }
            /*}});*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawIt(canvas);
    }

    public void setImages(Map<Integer, SerialBitmap> images) {
        this.images = images;
    }


    public void update() {
        postInvalidate();
    }
}