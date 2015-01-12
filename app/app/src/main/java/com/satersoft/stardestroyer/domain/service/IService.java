package com.satersoft.stardestroyer.domain.service;


import com.satersoft.stardestroyer.GameActivity;
import com.satersoft.stardestroyer.domain.model.units.Projectile;
import com.satersoft.stardestroyer.domain.model.units.Ship;
import com.satersoft.stardestroyer.domain.util.KHLeuvenMobileException;
import com.satersoft.stardestroyer.domain.util.Observer;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface IService extends Serializable {
	void pauseGame() throws KHLeuvenMobileException;
	void resumeGame() throws KHLeuvenMobileException;
	void startGame();
	int getLives();
	int getScore();
	boolean isGameOver();
	boolean isVictory();
	List<Projectile> getProjectiles();
	List<Ship> getEnemies();
	Ship getPlayer();
    void command(Set<String> movement, float sensitivity);

    void fire(boolean b);
    void addObserver(Observer o);
    void addObserverToGame(Observer o);

    void removeObserverFromGame(Observer o);
}

