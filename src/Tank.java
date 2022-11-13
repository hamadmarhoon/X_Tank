/**
 * This is a tank class where we create a tank object.
 * It has a lot of functionalities to help ease the use
 * of this class. it is also serializable so we can send tank
 * objects between server and clients. 
 * 
 * This Class was implemented by Abdullah Alkhamis.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class Tank implements Serializable {
	private static final long serialVersionUID = 1L;
	private int xCoord;
	private int yCoord;
	private int direction;
	private List<Integer> color;
	private Bullet bullet;

	UUID uuid = UUID.randomUUID();

	// tank body
	private int baseXDiff = 0;
	private int baseYDiff = 0;
	private int tankWidth = 50;
	private int tankHeight = 100;
	private boolean gotShot;
	// tank oval
	private int ovalXPos = 5;
	private int ovalYPos = 25;

	// tank barrel
	private int barrelX1 = 25;
	private int barrelY1 = 25;
	private int barrelX2 = 25;
	private int barrelY2 = -15;
	
	/*
	 * Tank constructor takes the coordiante the tank will be created
	 * and the direction its facing as well the color of the tank and
	 * the tank's own bullets
	 */
	public Tank(int x, int y, int dir) {
		xCoord = x;
		yCoord = y;
		direction = dir;
		color = generateColor();
		bullet = new Bullet(x, y);
	}

	/**
	 * This gets the tanks uniq identifier to 
	 * diffrenciate between tanks.
	 */
	public UUID getUID() {
		return uuid;
	}
	
	/**
	 * This boolean determines if the 
	 * tank got shot.
	 */
	public void IGotShot() {
		this.gotShot = true;
	}
	
	/**
	 * This is a random color generator for the tank.
	 * it returns a list of rgb values.
	 */
	private List<Integer> generateColor() {
		Random rand = new Random();
		int r = rand.nextInt(100, 256);
		int g = rand.nextInt(100, 256);
		int b = rand.nextInt(100, 256);

		List<Integer> rgbs = new ArrayList<Integer>();
		rgbs.add(r);
		rgbs.add(g);
		rgbs.add(b);
		return rgbs;
	}

	/**
	 * This is a setter for the tank's x-coordinate
	 */
	public void setX(int x) {
		this.xCoord = x;
	}
	
	/**
	 * This is a setter for the tank's y-coordinate
	 */
	public void setY(int y) {
		this.yCoord = y;
	}
	
	/**
	 * This is a setter fot the tank's direction
	 */
	public void setDirection(int dir) {
		this.direction = dir;
	}
	
	/**
	 * This is a setter for the bullet coordinates
	 */
	public void setBullet() {
		bullet.setX(xCoord);
		bullet.setY(yCoord);
	}
	
	/**
	 * This gets the tank's bullet object
	 */
	public Bullet getBullet() {
		return bullet;
	}
	
	/**
	 * This moves the bullet object
	 */
	public void incrementBullet(int num, ArrayList<Tank> otherTanks) {
		this.bullet.increment(num, otherTanks);
	}
	
	/**
	 * This sets the bullet direction.
	 */
	public void setBulletDirection() {
		this.bullet.setDirection(direction);
	}

	/**
	 * This gets the tank's x-coordinate
	 */
	public int getX() {
		return xCoord;
	}

	/**
	 * This gets the tank's y-coordinate
	 */
	public int getY() {
		return yCoord;
	}
	
	/**
	 * This gets the exact center of the tank object
	 */
	public List<Integer> getCenter() {
		List<Integer> coords = new ArrayList<Integer>();
		if (getDirection() == 0) {
			coords.add(xCoord + 20);
			coords.add(yCoord + 35);
		} else if (getDirection() == 1) {
			coords.add(xCoord + 30);
			coords.add(yCoord + 45);
		} else if (getDirection() == 2) {
			coords.add(xCoord + 20);
			coords.add(yCoord + 55);
		} else {
			coords.add(xCoord + 10);
			coords.add(yCoord + 45);
		}
		return coords;
	}
	
	/**
	 * This gets the tank's direction.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * This gets the color of the tank represented as 
	 * a list of rgb values.
	 */
	public List<Integer> getColor() {
		return color;
	}

	/**
	 * This moves the tank on the x-axis
	 */
	public void incrementX(int num) {
		this.xCoord += num;
	}

	/**
	 * This moves the tank on the y-axis
	 */
	public void incrementY(int num) {
		this.yCoord += num;
	}

	/**
	 * This inverts the coords of the tank 
	 * to help with calulations
	 */
	private void invertCoords() {
		int temp = getX();
		this.xCoord = getY();
		this.yCoord = temp;
	}

	private boolean withinRange(int x, int y, int dir) {
		int currTankX = getCenter().get(0);
		int currTankY = getCenter().get(1);
		if (dir == 1 || dir == 3) {
			int temp = x;
			x = y;
			y = temp;
		}
		return rangeHelper(currTankX, currTankY, x, y);
	}

	private boolean rangeHelper(int currX, int currY, int shotX, int shotY) {
		if (currX >= shotX && currX <= shotX) {
			if (currY <= shotY) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This function determines if the current
	 * instance of the tank class got shot by 
	 * another tank.
	 */
	public boolean gotShot(Tank shootingTank) {
		int shootingX = shootingTank.getCenter().get(0);
		int shootingY = shootingTank.getCenter().get(1);
		int dir = shootingTank.getDirection();
		boolean inverted = false;
		if (getDirection() == 1 || getDirection() == 3) {
			invertCoords();
			inverted = true;
		}
		if (withinRange(shootingX, shootingY, dir)) {
			if (inverted) {
				invertCoords();
			}
			return true;
		}
		if (inverted) {
			invertCoords();
		}
		return false;
	}

	/**
	 * This function detemines if the tank is out
	 * of bounds.
	 */
	public boolean outOfBounds(Canvas canvas) {
		int centerX = getCenter().get(0);
		int centerY = getCenter().get(1);
		int canvasHeight = canvas.getBounds().height;
		int canvasWidth = canvas.getBounds().width;
		if (centerX > canvasWidth || centerX < canvasWidth) {
			return true;
		}
		if (centerY + 25 >= canvasHeight || centerY + 25 <= canvasHeight) {
			return true;
		}
		return false;
	}
	
	/**
	 * This sets the tank fields to help the UI with drawing
	 * the tank and in all four direction.
	 */
	public void setTankFields(int bxd, int byd, int tWidth, int tHeight, int oxp, int oyp, int bx1, int by1, int bx2,
			int by2) {
		baseXDiff = bxd;
		baseYDiff = byd;
		tankWidth = tWidth;
		tankHeight = tHeight;

		ovalXPos = oxp;
		ovalYPos = oyp;

		barrelX1 = bx1;
		barrelY1 = by1;
		barrelX2 = bx2;
		barrelY2 = by2;
	}

	/**
	 * all the getters below are used in the tank UI 
	 * to draw each tank object;
	 */
	public int getBaseX() {
		return baseXDiff;
	}

	public int getBaseY() {
		return baseYDiff;
	}

	public int getTankWidth() {
		return tankWidth;
	}

	public int getTankHeight() {
		return tankHeight;
	}

	public int getOvalXPosition() {
		return ovalXPos;
	}

	public int getOvalYPosition() {
		return ovalYPos;
	}

	public int getBarrelX1() {
		return barrelX1;
	}

	public int getBarrelY1() {
		return barrelY1;
	}

	public int getBarrelX2() {
		return barrelX2;
	}

	public int getBarrelY2() {
		return barrelY2;
	}
	
	/**
	 * This method gets the bounds of each tank
	 * to aid with collision detection.
	 */
    public Rectangle getBounds() {
    	if (getDirection() == 0 || getDirection() == 2) {
    		return new Rectangle(getX(), getY(), 85, 95);
    	} else {
    		return new Rectangle(getX(), getY(), 95, 95);
    	}
    }
    
    /**
     * This method changes the direction of the tank object.
     */
	public void changeDirection(KeyEvent e) {
		if (e.keyCode == SWT.ARROW_UP) {
			this.direction = 0;
		} else if (e.keyCode == SWT.ARROW_RIGHT) {
			this.direction = 1;
		} else if (e.keyCode == SWT.ARROW_DOWN) {
			this.direction = 2;
		} else if (e.keyCode == SWT.ARROW_LEFT) {
			this.direction = 3;
		}
	}

}
