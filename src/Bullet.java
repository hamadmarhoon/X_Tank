public class Bullet {

	private int xCoord;
	private int yCoord;

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

	public void incrementX(int x) {
		this.xCoord += x;
	}

	public void incrementY(int y) {
		this.yCoord += y;
	}
}
