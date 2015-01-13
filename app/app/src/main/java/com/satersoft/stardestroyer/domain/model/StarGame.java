package com.satersoft.stardestroyer.domain.model;

import com.satersoft.stardestroyer.domain.chronometer.StartableObject;
import com.satersoft.stardestroyer.domain.model.units.EnemyShip;
import com.satersoft.stardestroyer.domain.model.units.EntityFactory;
import com.satersoft.stardestroyer.domain.model.units.PlayerShip;
import com.satersoft.stardestroyer.domain.model.units.Projectile;
import com.satersoft.stardestroyer.domain.model.units.Ship;
import com.satersoft.stardestroyer.domain.util.Observer;
import com.satersoft.stardestroyer.domain.util.Subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StarGame extends StartableObject implements Subject, Observer, Serializable {
	private boolean isGameOver=false, isVictor=false;
	private List<Ship> enemies = new ArrayList<Ship>();
	private List<Projectile> projectiles = new ArrayList<Projectile>();
	private Ship player;
	private int fieldHeight, fieldWidth, amountEnemies, lives, score, generateTick = 0;
	private EntityFactory factory;
	private Set<Observer> observers = new HashSet<Observer>();
	private Set<String> commands = new HashSet<String>();
    private float sensitivity = 1;
	
	private static final long serialVersionUID = 1652307700719380157L;
    private boolean pFire;

    public StarGame(int fieldHeight, int fieldWidth, Ship ship, int amountEnemies, int lives, EntityFactory factory){
		setHeight(fieldHeight);
		setWidth(fieldWidth);
		setAmountOfEnemies(amountEnemies);
		setLives(lives);
		setPlayer(ship);
		this.factory = factory;
	}

    public static List<Ship> cloneShipList(List<Ship> list) {
        List<Ship> clone = new ArrayList<Ship>(list.size());
        for(Ship item: list){
            if(item instanceof EnemyShip)
                clone.add(((EnemyShip)item).clone());
            else if(item instanceof PlayerShip)
                clone.add(((PlayerShip)item).clone());
        }
        return clone;
    }

    public static List<Projectile> cloneProjectileList(List<Projectile> list) {
        List<Projectile> clone = new ArrayList<Projectile>(list.size());
        for(Projectile item: list) clone.add(item.clone());
        return clone;
    }

	private void setHeight(int height){
		this.fieldHeight = height;
	}
	private void setWidth(int width){
		this.fieldWidth = width;
	}
	public int getHeight(){
		return this.fieldHeight;
	}
	public int getWidth(){
		return this.fieldWidth;
	}
	private void setAmountOfEnemies(int enemies){
		this.amountEnemies = enemies;
	}
	public int getAmountOfEnemies(){
		return this.amountEnemies;
	}
	private void setLives(int lives){
		this.lives = lives;
	}
	private void setPlayer(Ship player){
		this.player = player;
	}
    private int moveTicks = 3;

	public List<Ship> getEnemies() {
		return cloneShipList(enemies);
		
	}
	public int getLives() {
		return player.getArmor();//this.lives;
	}
	public int getScore() {
		return this.score;
	}
	public boolean isGameOver() {
		return this.isGameOver;
	}
	public boolean isVictor() {
		return this.isVictor;
	}

	public List<Projectile> getProjectiles() {
		return cloneProjectileList(projectiles);
	}

	public Ship getPlayer() {
		return ((PlayerShip)this.player).clone();
	}
	public void command(Set<String> movement, float sensitivity) {
		this.commands  = movement;
        this.sensitivity = sensitivity;
	}
	private void movePlayer() {
		Iterator<String> it = commands.iterator();
		 
		while(it.hasNext()){
			String command = it.next();
            ((PlayerShip)player).move(command, sensitivity);
		}
        moveTicks--;

        if(moveTicks < 0) {
            commands.clear();
            moveTicks = 3;
        }
	}
	private void generateEnemies(){
		generateTick++;
		if(generateTick == 30){
		int option, x, y;
		String type = "light";
		Ship enemy;
		int amount = (int) (Math.random() * 3);
		if (getAmountOfEnemies() - enemies.size() < amount){
			amount = getAmountOfEnemies() - enemies.size();
		}
		for (int i = 0; i < amount; i++){
			option = (int) (Math.random() * 6);
			if (option < 4){
			type = "Light";
			} else {
			if (option < 6){
				type = "Medium";
			} else {
				type = "Heavy";
			}
		}
		}
		x = (int) (Math.random() * (this.getWidth()-30))+30;
		y = -10;
		enemy = factory.createShip(false, type, x, y);
		enemies.add(enemy);
		}
	}
	private void generateProjectiles(){
		checkEnemyFire();
		checkPlayerFire();
	}
	private void checkPlayerFire(){
		if(pFire && generateTick % 5 == 0){
			fire(getPlayer());
            //Log.v("FIRE", "Player fired!");
        }
	}
	private void checkEnemyFire(){
		for (Ship enemy : enemies){
			if ( (int)(Math.random()*15) == 1 ){
				fire(enemy);
			}
		}
	}
	private void fire(Ship ship) {
		boolean shotByPlayer = false;
        int playerMod = 1;
		if (ship instanceof PlayerShip){
			shotByPlayer = true;
            playerMod = -1;
		}
		//creates projectile by pulling data from Ship class
		//followed by throwing it into the entityfactory
		int pFireX, pFireY;
		pFireX = ship.getX() + (ship.getWidth()/2)-1;
		pFireY = ship.getY();
		int speed = ship.getSpeed()*3;
		int strength = ship.getStrength();
		Projectile projectile = factory.createProjectile(pFireX,pFireY, shotByPlayer, speed * playerMod, strength);
		projectiles.add(projectile);
		
	}
	private void moveEnemies() {
		for (Ship enemy  : enemies) {
	        	if (enemy != null){
	        		enemy.move("Down");
	        	} 
	      }
		
	}
	private void checkOutOfBounds(){
		checkProjectileBounds();
		checkEnemyBounds();
		checkPlayerBounds();
	}
	private void moveProjectiles() {
		for (Projectile projectile  : projectiles) {
        	if (projectile != null){
        		projectile.move(); 
        		}
        	
      }
	}
	private void checkProjectileBounds(){
        synchronized (projectiles) {
            Iterator<Projectile> pi = projectiles.iterator();
            while (pi.hasNext()) {
                Projectile projectile = pi.next();
                if (projectile.getY() < 0 || projectile.getY() > this.getHeight()) {
                    pi.remove();
                    //projectiles.remove(projectile);
                }
            }
        }
	}
	private void checkEnemyBounds(){
		Iterator<Ship> ei = enemies.iterator(); 
	    while (ei.hasNext()){
	    	Ship enemy =  ei.next();
			if (enemy.getY() > this.getHeight()){
				ei.remove();
				//enemies.remove(enemy);
			}
		}
	}
	private void checkPlayerBounds(){
		//different from others because if the player reaches a border he will arrive on the other side
		if (player.getX() < getPlayer().getWidth()*-1 ){
			player.setX(this.getWidth()-getPlayer().getWidth());		
		}
		if (player.getX() > this.getWidth()){
			player.setX(0);
		}
		if (player.getY() < getPlayer().getHeight()*-1){
			player.setY(this.getHeight());
		}
		if (player.getY() > this.getHeight()){
			player.setY(-getPlayer().getHeight());
		}
	}

	private void collisionDetection() {
		playerHit();
		enemyHit();

	}
	private void playerHit(){
	    Iterator<Projectile> pi = projectiles.iterator(); 
	    while (pi.hasNext()){
	    	Projectile projectile =  pi.next();
			if (!projectile.getShotByPlayer()){
			if (player.hitBy(projectile.getX(), projectile.getY(),projectile.getWidth(), projectile.getHeight())){
				player.assignDamage(projectile.getStrength());
				pi.remove();
				//projectiles.remove(projectile);
			}
			}
		}
	}
	private void enemyHit(){
		Iterator<Ship> ei = enemies.iterator();
		while (ei.hasNext()) {
		    Ship enemy =  ei.next();
		    Iterator<Projectile> pi = projectiles.iterator(); 
		    while (pi.hasNext()){
		    	Projectile projectile =  pi.next();
				if (projectile.getShotByPlayer()){
				if (enemy.hitBy(projectile.getX(), projectile.getY(),projectile.getWidth(), projectile.getHeight())){
					enemy.assignDamage(projectile.getStrength());

					pi.remove();
					//projectiles.remove(projectile);
				}
				}
			}
		}
	}
	private void removeDead(){
		removeDeadShips();
		removeDeadPlayer();
	}
	private void removeDeadShips(){
		Iterator<Ship> ei = enemies.iterator();
		while (ei.hasNext()) {
		    Ship enemy =  ei.next();
			if (enemy.getArmor() <= 0){
				if (enemy instanceof EnemyShip){
				EnemyShip e = (EnemyShip)enemy;
				score +=e.getScore();
				ei.remove();
				//enemies.remove(enemy);
				amountEnemies--;
			}
			}
		}
	}
	private void removeDeadPlayer(){
		if (player.getArmor()<= 0){
			isGameOver = true;
		}
	}
	private void checkIfGameOver(){
	if (getAmountOfEnemies() == 0){
		isGameOver=true;
		isVictor=true;
	} else if(player.getArmor() <= 0) {
        isGameOver = true;
        isVictor=false;
    }

	}

	private void gameTick(){
		movePlayer();
		generateEnemies();
		generateProjectiles();
		//move all NPC's
		moveProjectiles();
		moveEnemies();
		//remove npc's that are out of bounds and move player if needed
		checkOutOfBounds();
		//detect collisions and assign damage
		collisionDetection();
		//remove those killed
		removeDead();
		//update view
		checkIfGameOver();
		notifyObservers();

        if(generateTick >= 100)
            generateTick = 0;
	}
	@Override
	public void update() {
		gameTick();
	}
	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		Iterator<Observer> iterator = observers.iterator();
		while (iterator.hasNext()) {
			Observer o = iterator.next();
			if (o.equals(observer)) {
				iterator.remove();
			}
		}
	}

	@Override
	public void notifyObservers() {
		for (Observer o : observers) {
			o.update();
		}
	}

    public void fire(boolean fire) { // Player fires!
        pFire = fire;
    }
}
