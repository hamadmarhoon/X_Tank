import java.io.Serializable;
import java.util.ArrayList;

public class Bullet implements Serializable{

	private int xCoord;
	private int yCoord;
	private int direction;

	public Bullet(int x, int y) {
		this.xCoord = x;
		this.yCoord = y;
	}

	public void setX(int x) {
		this.xCoord = x;
	}

	public void setY(int y) {
		this.yCoord = y;
	}

	public int getX() {
		return xCoord;
	}

	public int getY() {
		return yCoord;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public void setDirection(int direction) {
		System.out.println(direction);
		this.direction = direction;
	}

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
