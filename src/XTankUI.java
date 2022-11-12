
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
import java.util.Set;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class XTankUI
{
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
	
	private boolean bb = false;
	
	public XTankUI(ObjectInputStream in, ObjectOutputStream out, int initialX, int initialY) throws IOException
	{
		this.in = in;
		this.out = out;		
		this.otherTanks = new ArrayList<Tank>();
		this.tank = new Tank(300, 300, 1);
		this.bullet = new Bullet(0, 0);
	}
	
	private Tank getTank() {
		return this.tank;
	}
	
	private void tankX(int num) {
		this.tank.incrementX(num);
	}
	
	private void tankY(int num) {
		this.tank.incrementY(num);
	}
	
	private void writeTank() throws IOException {
		this.out.reset();
		this.out.writeObject(getTank());
	}
	
	public void start()
	{
		display = new Display();
		shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());
	
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);

		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(new Color(tank.getColor().get(0), tank.getColor().get(1), tank.getColor().get(2)));
			event.gc.fillRectangle(tank.getX() - tank.getBaseX(), tank.getY() - tank.getBaseY(), tank.getTankWidth(), tank.getTankHeight());
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(tank.getX() + tank.getOvalXPosition(), tank.getY() + tank.getOvalYPosition(), 40, 40);
			event.gc.setLineWidth(4);
			event.gc.drawLine(tank.getX() + tank.getBarrelX1(), tank.getY() + tank.getBarrelY1(), tank.getX() + tank.getBarrelX2(),
					tank.getY() + tank.getBarrelY2());
			
			if (otherTanks.size() > 0) {
				for (int i = 0; i < otherTanks.size(); i++) {
					event.gc.setBackground(new Color(getOtherTanks().get(i).getColor().get(0), getOtherTanks().get(i).getColor().get(1), getOtherTanks().get(i).getColor().get(2)));
					event.gc.fillRectangle(getOtherTanks().get(i).getX() - getOtherTanks().get(i).getBaseX(), getOtherTanks().get(i).getY() - getOtherTanks().get(i).getBaseY(), 
							getOtherTanks().get(i).getTankWidth(), getOtherTanks().get(i).getTankHeight());
					event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					event.gc.fillOval(getOtherTanks().get(i).getX() + getOtherTanks().get(i).getOvalXPosition(), getOtherTanks().get(i).getY() + getOtherTanks().get(i).getOvalYPosition(), 40, 40);
					event.gc.setLineWidth(4);
					event.gc.drawLine(getOtherTanks().get(i).getX() + getOtherTanks().get(i).getBarrelX1(), getOtherTanks().get(i).getY() + getOtherTanks().get(i).getBarrelY1(), getOtherTanks().get(i).getX()
							+ getOtherTanks().get(i).getBarrelX2(), getOtherTanks().get(i).getY() + getOtherTanks().get(i).getBarrelY2());
				}
			}
			
			canvas.redraw();
			
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// update tank location
				
				if (e.keyCode == SWT.ARROW_RIGHT) {
					checkCollisions();
					tank.setTankFields(25, -25, 100, 50, 9, 30, 50, 50, 90, 50);
					tankX(-directionX);
				} else if (e.keyCode == SWT.ARROW_LEFT) {
					checkCollisions();
					tank.setTankFields(25, -25, 100, 50, 0, 30, 0, 50, -40, 50);
					tankX(directionX);
				} else if (e.keyCode == SWT.ARROW_UP) {
					checkCollisions();
					tank.setTankFields(0, 0, 50, 100, 5, 25, 25, 25, 25, -15);
					tankY(directionY);
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					checkCollisions();
					tank.setTankFields(0, 0, 50, 100, 5, 35, 25, 75, 25, 115);
					tankY(-directionY);
				}
				try {
					out.writeInt(0);
					writeTank();
					out.flush();
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (write KL).");
				}
				canvas.redraw();
			}
			public void keyReleased(KeyEvent e) {}
		});

		try {
			out.writeInt(0);
			out.writeObject(getTank());
			
			out.flush();
		}
		catch(IOException ex) {
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
	
	private void checkCollisions() {
		for (int i = 0; i < getOtherTanks().size(); i++) {
			boolean check = tank.collision(getOtherTanks().get(i));
			System.out.println(check);
		}
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
	
	class Runner implements Runnable
	{
		public void run() 
		{
			try {
				
				if (in.available() > 0) {
					Tank otherTank;
					
					int zero = in.readInt();
					otherTank = (Tank) in.readObject();
					
					for (int i = 0; i < getOtherTanks().size(); i++) {
						if (otherTank.getX() == getOtherTanks().get(i).getX() || otherTank.getY() == getOtherTanks().get(i).getY()) {
							removeTank(i);
						}
					}
					
					if (!getOtherTanks().contains(otherTank)) {
						addTank(otherTank);
					}
					System.out.println(otherTanks);
				
				}	

			}
			catch(IOException | ClassNotFoundException ex) {
				System.out.println("The server did not respond (async).");
			}	
            display.timerExec(150, this);
		}
		
	};	
}


