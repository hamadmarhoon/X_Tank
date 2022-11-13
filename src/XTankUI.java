/**
 * This is the XTankUI Class where we create the user interface and
 * and draw the tanks from all clients.
 * 
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
import org.eclipse.swt.graphics.Rectangle;
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
	 * 
	 * @param in
	 * @param out
	 * @param initialX
	 * @param initialY
	 * @throws IOException
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

	private Tank getTank() {
		return this.tank;
	}
	
	private int getRandomX() {
		Random rand = new Random();
		int x = rand.nextInt(50, 550);
		return x;
	}
	
	private int getRandomY() {
		Random rand = new Random();
		int y = rand.nextInt(50, 550);
		return y;
	}
	
	private int getRandomDir() {
		Random rand = new Random();
		int dir = rand.nextInt(0, 4);
		return dir;
	}

	private void tankX(int num) {
		getTank().incrementX(num);
		if (checkCollisions()) {
			getTank().incrementX(-num);
		}
	}

	private void tankY(int num) {
		getTank().incrementY(num);
		if (checkCollisions()) {
			getTank().incrementY(-num);
		}
	}

	private void writeTank() throws IOException {
		this.out.reset();
		this.out.writeObject(getTank());
	}

	public void animateFiring() {

		Runnable runnable1 = new Runnable() {
			public void run() {
				animate();
				display.timerExec(10, this);
			}
		};

		display.timerExec(10, runnable1);
		display.timerExec(-1, runnable1);
	}

	public void start() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		canvas.setSize(600, 600);
		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
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

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {

				// update tank location
				if (e.keyCode == SWT.ARROW_RIGHT) {
					tank.setTankFields(25, -25, 100, 50, 9, 30, 50, 50, 90, 50);
					tankX(-directionX);
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					tank.setTankFields(25, -25, 100, 50, 0, 30, 0, 50, -40, 50);
					tankX(directionX);
				} else if (e.keyCode == SWT.ARROW_UP) {
					tank.setTankFields(0, 0, 50, 100, 5, 25, 25, 25, 25, -15);
					tankY(directionY);
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					tank.setTankFields(0, 0, 50, 100, 5, 35, 25, 75, 25, 115);
					tankY(-directionY);
				} else if (e.keyCode == SWT.SPACE) {
					canvas.addPaintListener(new PaintListener() {
						public void paintControl(PaintEvent event) {
							// Set the color of the ball
							event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
							// Draw the ball
							event.gc.fillOval(tank.getX(), tank.getY(), 50, 50);
						}
					});
					animateFiring();
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
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();

	}

	public void animate() {
		// Determine the ball's location
		bullet.incrementX(directionX);
		bullet.incrementY(directionY);

		// Determine out of bounds
		Rectangle rect = canvas.getClientArea();
		if (bullet.getX() < 0) {
			bullet.setX(0);
			bullet.setDirection(1);
		} else if (bullet.getX() > rect.width - 600) {
			bullet.setX(rect.width - 600);
			bullet.setDirection(-1);
		}
		if (bullet.getY() < 0) {
			bullet.setY(0);
			bullet.setDirection(1);
		} else if (bullet.getY() > rect.height - 600) {
			bullet.setY(rect.height - 600);
			bullet.setDirection(-1);
		}
		canvas.redraw();
	}

	private Image getImage() {
		ArrayList<Image> images = new ArrayList<Image>();
		images.add(new Image(display, canvas.getBounds()));
		images.add(new Image(display, canvas.getBounds()));
		images.add(new Image(display, canvas.getBounds()));
		images.add(new Image(display, canvas.getBounds()));

		Random rand = new Random();
		int select = rand.nextInt(0, 4);
		return images.get(select);
	}

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

	private ArrayList<Tank> getOtherTanks() {
		return this.otherTanks;
	}

	private void addTank(Tank tank) {
		this.otherTanks.add(tank);
	}

	private void removeTank(int i) {
		this.otherTanks.remove(i);
	}

	class Runner implements Runnable {
		public void run() {
			try {

				if (in.available() > 0) {
					Tank otherTank;

					int zero = in.readInt();
					otherTank = (Tank) in.readObject();

					for (int i = 0; i < getOtherTanks().size(); i++) {
						if (getTank().getUID().equals(getOtherTanks().get(i).getUID())) {
							removeTank(i);
						}
					}
					for (int i = 0; i < getOtherTanks().size(); i++) {
						if (otherTank.getUID().equals(getOtherTanks().get(i).getUID())) {
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
