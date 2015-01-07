package com.satersoft.stardestroyer.domain.model.units;

public class Projectile extends Entity implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1199536320305667720L;
	boolean shotByPlayer;
	public Projectile(int x, int y, int width, int height, int speed,
			int imageID, String type, int strength, boolean shotByPlayer) {
		super(x, y, width, height, speed,imageID, type, strength);
		setShotByPlayer(shotByPlayer);
	}
	public void move(){
		this.setY(getY() + getSpeed());
	}
	public boolean getShotByPlayer(){
		return this.shotByPlayer;
	}
	private void setShotByPlayer(boolean shotByPlayer){
		this.shotByPlayer = shotByPlayer;
	}

    @Override
    public Projectile clone() {
        return new Projectile(getX(),getY(), getWidth(), getHeight(), getSpeed(), getImageID(), "" + getType(), getStrength(), getShotByPlayer());
    }

}
