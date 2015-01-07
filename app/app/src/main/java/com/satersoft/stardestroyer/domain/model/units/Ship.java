package com.satersoft.stardestroyer.domain.model.units;

public abstract class Ship extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4960907038485342909L;
	private int armor, maxHP;

	public Ship(int x,int y, int imageID,String type, int width, int height, int speed, int strength, int armor) {
		super(x,y,width, height, speed, imageID,type, strength);
		setArmor(armor);
		setMaxHP(armor);

	}

	protected void setArmor(int armor){
		this.armor = armor;
	}

	protected void setMaxHP(int maxHP){
		this.maxHP = maxHP;
	}
	public int getArmor(){
		return this.armor;
	}
	public int getMaxHP(){
		return this.maxHP;
	}
	public void assignDamage(int i) {
		setArmor(getArmor()-i);
	}
	public boolean hitBy(int x, int y, int width, int height){
		//checks if a ship has been hit by comparing locations and size
		boolean returnb = false;
		if (between (x,y)){
			returnb = true;
		} 
		if (between (x+width, y)){
			returnb = true;
		}
		if (between (x+width, y-height)){
			returnb = true;
		}
		if (between (x, y-height)){
			returnb = true;
		}
		return returnb;
	}
	public boolean between(int pX, int pY){
		boolean returnb = false;
		if (pX> this.getX() && pX<(this.getX()+this.getWidth())){
			if (pY>this.getY() && pY<(this.getY()+this.getHeight())){
				returnb = true;
			}
		}
		return returnb;
	}
	public void move(String movement){
		//provides movement in the four directions
	int newValue = 0;
		if(movement.equals("Up")){
			newValue = getY()-getSpeed();
		this.setY(newValue);
		}
	if(movement.equals("Right")){
		newValue = getX()+getSpeed();
		this.setX(newValue);
	}
	if(movement.equals("Down")){
		newValue = getY() + getSpeed();
		this.setY(newValue);
		
	}
	if(movement.equals("Left")){
		newValue = getX() - getSpeed();
		this.setX(newValue);
	}	
	}
		
		
	



}
