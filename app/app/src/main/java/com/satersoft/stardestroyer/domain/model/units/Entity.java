package com.satersoft.stardestroyer.domain.model.units;

import java.io.Serializable;

//superclass used to reduce code redundancy
//entity isn't used in the game itself, just saves time
public abstract class Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1866985426399540102L;
	private int x,y, width,height, speed, strength, imageID;
	private String type;

	
	public Entity(int x,int y,int width, int height, int speed, int imageID,String type, int strength){
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
		setSpeed(speed);
		setImageID(imageID);
		setStrength(strength);
		setType(type);
	}
	protected void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	protected void setStrength(int strength){
		this.strength = strength;
	}
	public int getStrength(){
		return this.strength;
	}
	protected void setImageID(int imageID){
		this.imageID = imageID;
	}
	public int getImageID(){
		return this.imageID;
	}
	protected int setSpeed(int speed){
		return this.speed = speed;
	}
	public int getSpeed(){
		return this.speed;
	}
	protected void setHeight(int height){
		this.height = height;
	}
	public int getHeight(){
		return this.height;
	}
	protected void setWidth(int width){
		this.width = width;
	}
	public int getWidth(){
		return this.width;
	}
	public int getX(){
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
