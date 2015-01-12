package com.satersoft.stardestroyer.domain.service;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.satersoft.stardestroyer.domain.chronometer.Chronometer;
import com.satersoft.stardestroyer.domain.model.GameBuilder;
import com.satersoft.stardestroyer.domain.model.StarGame;
import com.satersoft.stardestroyer.domain.model.units.Projectile;
import com.satersoft.stardestroyer.domain.model.units.Ship;
import com.satersoft.stardestroyer.domain.util.KHLeuvenMobileException;
import com.satersoft.stardestroyer.domain.util.Observer;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


public class Service implements IService, Serializable {
	private static final long serialVersionUID = -5240724257616140779L;
	private StarGame game;
	private Chronometer chrono;
	private GameBuilder gamebuilder;
	public Service(String choice, String dataString, Context cxt){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) cxt.getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

		gamebuilder = new GameBuilder(dataString, screenWidth, screenHeight, cxt);
		try {
			chrono = new Chronometer(30);
		} catch (KHLeuvenMobileException e) {
			e.printStackTrace();
		}

		game = gamebuilder.build(choice, chrono);
	}
	@Override
	public void pauseGame() throws KHLeuvenMobileException {
		chrono.stop();
		game.stop();
	}

	@Override
	public void resumeGame() throws KHLeuvenMobileException {
		chrono.resume();
		game.resume();
	}

	@Override
	public void startGame() {
		try {
            chrono.start();
            game.start();
		} catch (KHLeuvenMobileException e) {
			e.printStackTrace();
		}
	}
	@Override
	public List<Projectile> getProjectiles() {
		return game.getProjectiles();
	}
	@Override
	public List<Ship> getEnemies() {
		return game.getEnemies();
	}
	@Override
	public Ship getPlayer() {
		return game.getPlayer();
	}

    @Override
	public int getLives() {
		return game.getLives();
	}
	@Override
	public int getScore() {
		return game.getScore();
	}
	@Override
	public boolean isGameOver() {
		return game.isGameOver();
	}
	@Override
	public boolean isVictory() {
		return game.isVictor();
	}
	@Override
	public void command(Set<String> movement, float sensitivity) {
		//move command for playerShip object in game
		game.command(movement, sensitivity);
	}

    @Override
    public void fire(boolean b) {
        game.fire(b);
    }

    @Override
    public void addObserver(Observer o) {
        chrono.registerObserver(o);
    }

    @Override
    public void addObserverToGame(Observer o) {
        game.registerObserver(o);
    }

    @Override
    public void removeObserverFromGame(Observer o) {
        game.removeObserver(o);
    }
}
