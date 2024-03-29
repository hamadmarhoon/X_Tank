/**
 * This is the XTankUI Class where we create the user interface and
 * and draw the tanks from all clients. it uses multiple functions
 * from the Tank class to aid with drawing and logic.
 * 
 * This class was implemented by both Hamad Marhoon and Abdullah Alkhamis
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XTankUI {
	// The location and direction of the "tank"
	private int directionX = -10;
	private int directionY = -10;
	private ArrayList<Tank> otherTanks;

	private Canvas canvas;
	private Display display;
	private Shell shell;

	private Bullet bullet;

	int tankID;
	ObjectInputStream in;
	ObjectOutputStream out;
	Tank tank;

	/**
	 * The constructor creates a tank object and bullet object
	 * and sets the tank fields to to initilize drawing location 
	 * and direction. It also initiates the ways of communication between
	 * to and from the server.
	 */
	public XTankUI(ObjectInputStream in, ObjectOutputStream out, int initialX, int initialY) throws IOException {
		this.in = in;
		this.out = out;
		this.otherTanks = new ArrayList<Tank>();
		this.tank = new Tank(getRandomX(), getRandomY(), getRandomDir());
		this.bullet = new Bullet(0, 0);
		if (getTank().getDirection() == 0) {
			getTank().setTankFields(0, 0, 50, 100, 5, 25, 25, 25, 25, -15);
		} else if (getTank().getDirection() == 1) {
			getTank().setTankFields(25, -25, 100, 50, 9, 30, 50, 50, 90, 50);
		} else if (getTank().getDirection() == 2) {
			getTank().setTankFields(0, 0, 50, 100, 5, 35, 25, 75, 25, 115);
		} else {
			getTank().setTankFields(25, -25, 100, 50, 0, 30, 0, 50, -40, 50);
		}
	}
	
	/**
	 * This gets a random x-coordinate to draw the tank
	 */
	private int getRandomX() {
		Random rand = new Random();
		int x = rand.nextInt(50, 550);
		return x;
	}
	
	/**
	 * This gets a random y-coordinate to draw the tank
	 */
	private int getRandomY() {
		Random rand = new Random();
		int y = rand.nextInt(50, 550);
		return y;
	}

	/**
	 * This gets a random direction to draw the tank
	 */
	private int getRandomDir() {
		Random rand = new Random();
		int dir = rand.nextInt(0, 4);
		return dir;
	}

	/**
	 * This implement conditional moving and collision checking for x-coordinate
	 */
	private void tankX(int num) {
		getTank().incrementX(num);
		if (checkCollisions()) {
			getTank().incrementX(-num);
		}
	}

	/**
	 * This implement conditional moving and collision checking for x-coordinate
	 */
	private void tankY(int num) {
		getTank().incrementY(num);
		if (checkCollisions()) {
			getTank().incrementY(-num);
		}
	}
	
	private void tankGotShot() {
		this.tank.IGotShot();
	}

	/**
	 * This gets the tank instance that invoked this UI
	 */
	private Tank getTank() {
		return this.tank;
	}

	/**
	 * This sends the tank object to the server
	 */
	private void writeTank() throws IOException {
		this.out.reset();
		this.out.writeObject(getTank());
	}

	/**
	 * This increments the Bullet object
	 */
	public void incrementBullet(int num) {
		this.tank.incrementBullet(num, otherTanks);
	}

	/**
	 * This gets the bullet object
	 */
	public Bullet getBullet() {
		return this.tank.getBullet();
	}

	/**
	 * This method implements the bullet animations.
	 */
	public void animate() {
		incrementBullet(10);
		int xCoord = getBullet().getX();
		int yCoord = getBullet().getY();
		boolean check = false;
		for (int i = 0; i < getOtherTanks().size(); i++) {
			if (!tank.getUID().equals(getOtherTanks().get(i).getUID())) {
				check = tank.getBullet().getBounds().intersects((getOtherTanks().get(i).getBullet().getBounds()));
				if (check == true) {
					otherTanks.get(i).IGotShot();
					System.out.println("Shot!");
				}
			}
		}
		canvas.redraw();
	}

	/**
	 * This sets the bullet's direction
	 */
	public void setBulletDirection() {
		this.tank.setBulletDirection();
	}

	/**
	 * This sets the bullet coordinates
	 */
	public void setBulletCoords() {
		this.tank.setBullet();
	}

	/**
	 * This starts the UI and draws the battlefield and it also handles the movement
	 * of the tanks and calls a runnable that runs the threads read from the server.
	 */
	public void start() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		canvas.setSize(900, 600);
		shell.setSize(900, 600);
		Image image = getImage();

		canvas.addPaintListener(event -> {
			
				event.gc.fillRectangle(canvas.getBounds());
				event.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, 900, 600);
				event.gc.setBackground(new Color(tank.getColor().get(0), tank.getColor().get(1), tank.getColor().get(2)));
				event.gc.fillRectangle(tank.getX() - tank.getBaseX(), tank.getY() - tank.getBaseY(), tank.getTankWidth(),
						tank.getTankHeight());
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(tank.getX() + tank.getOvalXPosition(), tank.getY() + tank.getOvalYPosition(), 40, 40);
				event.gc.setLineWidth(4);
				event.gc.drawLine(tank.getX() + tank.getBarrelX1(), tank.getY() + tank.getBarrelY1(),
						tank.getX() + tank.getBarrelX2(), tank.getY() + tank.getBarrelY2());
	
				if (otherTanks.size() > 0) {
					for (int i = 0; i < otherTanks.size(); i++) {
						if (!getTank().getUID().equals(getOtherTanks().get(i).getUID())) {
							if (!getOtherTanks().get(i).amIShot()) {
								event.gc.setBackground(new Color(getOtherTanks().get(i).getColor().get(0),
										getOtherTanks().get(i).getColor().get(1), getOtherTanks().get(i).getColor().get(2)));
								event.gc.fillRectangle(getOtherTanks().get(i).getX() - getOtherTanks().get(i).getBaseX(),
										getOtherTanks().get(i).getY() - getOtherTanks().get(i).getBaseY(),
										getOtherTanks().get(i).getTankWidth(), getOtherTanks().get(i).getTankHeight());
								event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
								event.gc.fillOval(getOtherTanks().get(i).getX() + getOtherTanks().get(i).getOvalXPosition(),
										getOtherTanks().get(i).getY() + getOtherTanks().get(i).getOvalYPosition(), 40, 40);
								event.gc.setLineWidth(4);
								event.gc.drawLine(getOtherTanks().get(i).getX() + getOtherTanks().get(i).getBarrelX1(),
										getOtherTanks().get(i).getY() + getOtherTanks().get(i).getBarrelY1(),
										getOtherTanks().get(i).getX() + getOtherTanks().get(i).getBarrelX2(),
										getOtherTanks().get(i).getY() + getOtherTanks().get(i).getBarrelY2());
							}
							
						}
					
				}
				canvas.redraw();
			}
		});

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			}

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		/**
		 * This handles all the tank actions in the UI.
		 */
		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {

				// update tank location
				if (e.keyCode == SWT.ARROW_RIGHT) {
					tank.setTankFields(25, -25, 100, 50, 9, 30, 50, 50, 90, 50);
					tankX(-directionX);
					tank.changeDirection(e);
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					tank.setTankFields(25, -25, 100, 50, 0, 30, 0, 50, -40, 50);
					tankX(directionX);
					tank.changeDirection(e);
				} else if (e.keyCode == SWT.ARROW_UP) {
					tank.setTankFields(0, 0, 50, 100, 5, 25, 25, 25, 25, -15);
					tankY(directionY);
					tank.changeDirection(e);
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					tank.setTankFields(0, 0, 50, 100, 5, 35, 25, 75, 25, 115);
					tankY(-directionY);
					tank.changeDirection(e);
					
				} else if (e.keyCode == SWT.SPACE) {
					setBulletDirection();
					setBulletCoords();
					canvas.addPaintListener(new PaintListener() {
						public void paintControl(PaintEvent event) {
							// Set the color of the ball
							event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));

							// Draw the ball
							event.gc.fillOval(tank.getBullet().getX(), tank.getBullet().getY(), 50, 50);

						}
					});
					Runnable runnable1 = new Runnable() {
						public void run() {
							animate();

							display.timerExec(50, this);
						}
					};

					display.timerExec(10, runnable1);
				}
				try {
					out.writeInt(0);
					writeTank();
					out.flush();
				} catch (IOException ex) {
					System.out.println("The server did not respond (write KL).");
				}
				canvas.redraw();
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		try {
			out.writeInt(0);
			out.writeObject(getTank());

			out.flush();
		} catch (IOException ex) {
			System.out.println("The server did not respond (initial write).");
		}
		Runnable runnable = new Runner();
		display.asyncExec(runnable);
		shell.open();
		
		if (getTank().amIShot()) {
			shell.dispose();
		}
		
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();

	}

	/**
	 * This gets a random image from an image list 
	 * to set as a canvas background.
	 */
	private Image getImage() {
		ArrayList<Image> images = new ArrayList<Image>();
		images.add(new Image(display, "gravel.png"));
		images.add(new Image(display, "dclaveau.png"));
		images.add(new Image(display, "grass.png"));
		images.add(new Image(display, "sand.png"));

		Random rand = new Random();
		int select = rand.nextInt(0, 4);
		return images.get(select);
	}

	/**
	 * This checks for collisions between tanks.
	 */
	private boolean checkCollisions() {
		boolean check = false;
		for (int i = 0; i < getOtherTanks().size(); i++) {
			if (!tank.getUID().equals(getOtherTanks().get(i).getUID())) {
				check = tank.getBounds().intersects((getOtherTanks().get(i).getBounds()));
				if (check == true) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * gets an array of all other tank objects/client objects.
	 */
	private ArrayList<Tank> getOtherTanks() {
		return this.otherTanks;
	}

	/**
	 * adds a tank object to the tank's list
	 */
	private void addTank(Tank tank) {
		this.otherTanks.add(tank);
	}

	/**
	 * This removes the tank at the given 
	 * index when it gets destroyed.
	 */
	private void removeTank(int i) {
		this.otherTanks.remove(i);
	}
	
	private void endUI() {
		shell.dispose();
	}

	/**
	 * This is the runner that runs the threads coming from the server
	 * and does the main communication between the UI and server.
	 */
	class Runner implements Runnable {
		public void run() {
			try {

				if (in.available() > 0) {
					Tank otherTank;

					int zero = in.readInt();
					otherTank = (Tank) in.readObject();

					for (int i = 0; i < getOtherTanks().size(); i++) {
						if (getTank().getUID().equals(getOtherTanks().get(i).getUID())) {
							if (getOtherTanks().get(i).amIShot()) {
								System.out.println("I'm shot");
								endUI();
							}
							removeTank(i);
						}
					}
					for (int i = 0; i < getOtherTanks().size(); i++) {
						if (otherTank.getUID().equals(getOtherTanks().get(i).getUID())) {
							if (otherTank.amIShot()) {
								System.out.println("I'm shot");
								endUI();
							}
							removeTank(i);
						
						}
					}
					
					addTank(otherTank);
					
					
					System.out.println(otherTanks);

				}

			} catch (IOException | ClassNotFoundException ex) {
				System.out.println("The server did not respond (async).");
			}
			display.timerExec(150, this);
		}

	};
}
