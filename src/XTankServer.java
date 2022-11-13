/*
Name: Hamad Marhoon and Abdullah Alkhamis
Class: CSC 335
Purpose: The XTankServer manages communication between clients.
		 It will create threads to handle new communcations. All
		 movements made in each tank are being transmitted into
		 all other tanks. 
*/


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * When a client connects, a new thread is started to handle it.
 * The server handles creating new threads and adding clients to them.
 */
public class XTankServer {
	static ArrayList<ObjectOutputStream> sq;
	boolean startGame;

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		boolean startGame = false;
		canvas.setSize(600, 600);

		shell.setText("XTank Server");
		shell.setLayout(new FillLayout());

		shell.open();

		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		int tankNum = 0;
		try (var listener = new ServerSocket(59896)) {
			System.out.println("The XTank server is running...");

			var pool = Executors.newFixedThreadPool(20);
			while (true) {
				pool.execute(new XTankManager(listener.accept(), tankNum));
				tankNum++;
			}
		}

	}
	
	/*
	 * XTankManager is a runnable that manages the threads in the server. 
	 * It connects each client and then creates an input and output stream, 
	 * which will handle the connections between the server and the client
	 * It will get movements from other players and then transmit them to the UI.
	 */
	private static class XTankManager implements Runnable {
		private Socket socket;
		private int tankID;

		XTankManager(Socket socket, int tankID) {
			this.socket = socket;
			this.tankID = tankID;
		}

		@Override
		public void run() {
			System.out.println("Connected: " + socket);
			System.out.println("Connected to server as Tank " + tankID);

			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

				sq.add(out);

				Tank tank;

				while (true) {

					int zero = in.readInt();
					tank = (Tank) in.readObject();

					for (ObjectOutputStream o : sq) {

						o.writeInt(0);
						o.writeObject(tank);
						o.flush();
					}
				}
			} catch (Exception e) {
				System.out.println("Error:" + socket);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
				System.out.println("Closed: " + socket);
			}
		}
	}

}
