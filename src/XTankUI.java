
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class XTankUI {
	// The location and direction of the "tank"
	private int directionX = -10;
	private int directionY = -10;
	private ArrayList<Tank> otherTanks;

	private Canvas canvas;
	private Display display;
	private Shell shell;
	
	int tankID;
	ObjectInputStream in;
	ObjectOutputStream out;
	Tank tank;

	public XTankUI(ObjectInputStream in, ObjectOutputStream out, int initialX, int initialY) throws IOException {
		this.in = in;
		this.out = out;
		this.otherTanks = new ArrayList<Tank>();
		this.tank = new Tank(300, 300, 1);
	}
	
	private void tankGotShot() {
		this.tank.IGotShot();
	}
	private Tank getTank() {
		return this.tank;
	}

	private void tankX(int num) {
		this.tank.incrementX(num);
		if (checkCollisions()) {
			this.tank.incrementX(-num);
		}
	}

	private void tankY(int num) {
		this.tank.incrementY(num);
		if (checkCollisions()) {
			this.tank.incrementX(-num);
		}
	}

	private void writeTank() throws IOException {
		this.out.reset();
		this.out.writeObject(getTank());
	}
	
	public void incrementBullet(int num) {
		this.tank.incrementBullet(num, otherTanks);
	}
	
	public Bullet getBullet() {
		return this.tank.getBullet();
	}
	
	public void animate() {
		incrementBullet(10);
		int xCoord = getBullet().getX();
		int yCoord = getBullet().getY();
		System.out.println(xCoord);
		System.out.println(yCoord);
		for (int i = 0; i < otherTanks.size(); i++) {
			if (!otherTanks.get(i).getUID().equals(tank.getUID())) {
				if (xCoord <= otherTanks.get(i).getX() + 25 && xCoord <= otherTanks.get(i).getX() - 25 
					    && yCoord <= otherTanks.get(i).getY() + 25 && yCoord >= otherTanks.get(i).getY() - 25 ) {
						System.out.println("Shot!");
				}
			}
			
		}
		canvas.redraw();
	}
	
	public void setBulletDirection() {
		this.tank.setBulletDirection();
	}
	
	public void setBulletCoords() {
		this.tank.setBullet();
	}
	

	public void start() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		canvas.setSize(900, 600);
		shell.setSize(900, 600);
		Image image = new Image(display, "dclaveau.png");
		
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
			ex.printStackTrace();
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

	private boolean checkCollisions() {
		for (int i = 0; i < getOtherTanks().size(); i++) {
			boolean check = tank.collision(getOtherTanks().get(i));
			if (check == true) {
				return true;
			}
			System.out.println(check);
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
