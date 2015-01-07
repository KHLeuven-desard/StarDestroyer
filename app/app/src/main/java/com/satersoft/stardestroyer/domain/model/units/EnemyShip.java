package com.satersoft.stardestroyer.domain.model.units;

//extends the ship
//only used for enemies
//new addition is the score
//can be used as super class for more complicated enemies
public class EnemyShip extends Ship implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3814036459002387676L;
	private int score = 0;
	public EnemyShip(int x, int y, int imageID,String type, int width, int height, int speed, int strength, int armor, int score) {
		super(x, y, imageID,type, width, height, speed, strength, armor);
		setScore(score);
	}
	protected void setScore(int score){
		this.score = score;
	}
	public int getScore(){
		return this.score;
	}


    @Override
    public EnemyShip clone() {
        return new EnemyShip(getX(),getY(), getImageID(), "" + getType(), getWidth(), getHeight(), getSpeed(), getStrength(), getArmor(), getScore());
    }
}
