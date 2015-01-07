package com.satersoft.stardestroyer.domain.model.units;


	
public class PlayerShip extends Ship implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8778440259963966192L;
	public PlayerShip(int x, int y, int imgID,String type, int width, int height, int speed, int strength, int armor) {
		super(x, y, imgID,type, width, height, speed, strength, armor);
		
	}

    @Override
    public PlayerShip clone() {
        return new PlayerShip(getX(),getY(), getImageID(), "" + getType(), getWidth(), getHeight(), getSpeed(), getStrength(), getArmor());
    }

    public void move(String movement, float sensitivity){
        //provides movement in the four directions
        int newValue = 0;
        if(movement.equals("Up")){
            newValue = (int) (getY()-getSpeed());
            this.setY(newValue);
        }
        if(movement.equals("Right")){
            newValue = (int) (getX()+getSpeed());
            this.setX(newValue);
        }
        if(movement.equals("Down")){
            newValue = (int) (getY() + getSpeed());
            this.setY(newValue);

        }
        if(movement.equals("Left")){
            newValue = (int) (getX() - getSpeed());
            this.setX(newValue);
        }
    }

}
