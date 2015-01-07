package com.satersoft.stardestroyer.domain.model;

import android.content.Context;

import com.satersoft.stardestroyer.domain.model.units.EntityFactory;
import com.satersoft.stardestroyer.domain.model.units.Ship;
import com.satersoft.stardestroyer.domain.util.Subject;

import java.io.Serializable;

public class GameBuilder implements Serializable{

	private static final long serialVersionUID = -4551540335954655613L;
	private int amountEnemies = 30;
	private int lives = 6;
	private int fieldHeight = 500;
	private int fieldWidth = 400;
	private EntityFactory factory;

	public GameBuilder(String dataString, Context cxt) {
		factory = new EntityFactory(dataString, cxt, 1);
	}

    public GameBuilder(String dataString, int width, int height, Context cxt) {
        double factor = height / fieldHeight;
        fieldHeight = height;
        fieldWidth = width;
        factory = new EntityFactory(dataString, cxt, factor);
    }

	public StarGame build(String choice, Subject subject) {
		Ship playerShip = factory.createShip(true, choice, fieldWidth/2, fieldHeight - 50);
		StarGame game = new StarGame(fieldHeight, fieldWidth, playerShip, amountEnemies, lives, factory);
		subject.registerObserver(game);
		return game;
	}

}
