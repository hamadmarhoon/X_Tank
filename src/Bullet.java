/*
 * Name: Hamad Marhoon and Abdullah Alkhamis
 * Class: CSC 335
 * Purpose: Bullet class represents a shooting projectile that
 * tanks shoot at each other. It can be moved and it can detect
 * when it hits other tanks. 
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Rectangle;


/*
 * A moving projectile
 */
public class Bullet implements Serializable{

	private int xCoord;
	private int yCoord;
	private int direction;

	public Bullet(int x, int y) {
		this.xCoord = x;
		this.yCoord = y;
	}
	
	/*
	 * sets X
	 */
	public void setX(int x) {
		this.xCoord = x;
	}
	
	/*
	 * sets Y
	 */
	public void setY(int y) {
		this.yCoord = y;
	}

	/*
	 * gets X
	 */
	public int getX() {
		return xCoord;
	}
	
	/*
	 * gets Y
	 */
	public int getY() {
		return yCoord;
	}
	
	/*
	 * gets direction
	 */
	public int getDirection() {
		return this.direction;
	}
	
	/*
	 * sets direction
	 */
	public void setDirection(int direction) {
		System.out.println(direction);
		this.direction = direction;
	}
	
	/*
	 * gets bounds, for collision detection
	 */
	public Rectangle getBounds() {
    	if (getDirection() == 0 || getDirection() == 2) {
    		return new Rectangle(getX(), getY(), 25, 25);
    	} else {
    		return new Rectangle(getX(), getY(), 25, 25);
    	}
    }
	
	/*
	 * Increments bullet according to the direction it was shot
	 */
	public void increment(int num, ArrayList<Tank> otherTanks) {
		
		if (this.xCoord < 1000 && this.xCoord > -1000 && this.yCoord < 1000 && this.yCoord > -1000) {
			if (direction == 0) {
				this.yCoord -= num;
			}
			else if (direction == 1) {
				this.xCoord += num;
			}
			else if (direction == 2) {
				this.yCoord += num;
			}
			if (direction == 3) {
				this.xCoord -= num;
			}
		}
	}
}
