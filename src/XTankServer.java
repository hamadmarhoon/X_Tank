
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<ObjectOutputStream> sq;
	boolean startGame;
	
    public static void main(String[] args) throws Exception 
    {
    	
    	Display display = new Display();
    	Shell shell = new Shell(display);
    	Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
    	boolean startGame = false;
    	canvas.setSize(600, 600);
    	
		shell.setText("XTank Server");
		shell.setLayout(new FillLayout());
		
		shell.open();
		
		Button startButton = new Button(canvas, SWT.PUSH);
		
		startButton.setText("Start Game!");
		startButton.setSize(150, 80);
		startButton.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		startButton.setLocation(370, 300);
		
		Label label = new Label(canvas, SWT.PASSWORD);
		label.setLocation(350, 150);
		label.setSize(300, 300);
		label.setText("              Welcome to XTank! \nStart the server once all players join");
		
		startButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});

//		while (!shell.isDisposed()) {
//
//            if (!display.readAndDispatch()) {
//                display.sleep();
//            }
//        }
//        display.dispose();
		
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		int tankNum = 0;
        try (var listener = new ServerSocket(59896)) 
        {
            System.out.println("The XTank server is running...");
            
            var pool = Executors.newFixedThreadPool(20);
            while (true) 
            {
                pool.execute(new XTankManager(listener.accept(), tankNum));
                tankNum++;
                

            }
        }
        
        
    }
    private void changeStartGame() {
    	this.startGame = false;
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private int tankID;

        XTankManager(Socket socket, int tankID) { 
        	this.socket = socket;
        	this.tankID = tankID;
        }

        @Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            System.out.println("Connected to server as Tank " + tankID);
            
            try 
            {
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            	
                sq.add(out);
                
                Tank tank;
                
                while (true) {
                	
                	
                	int zero = in.readInt();
                	tank = (Tank) in.readObject();
                	
                	for (ObjectOutputStream o: sq) {
                		
    					
                		o.writeInt(0);
                		o.writeObject(tank);
    					o.flush();
                	}
                }
            } 
            catch (Exception e) 
            {
                System.out.println("Error:" + socket);
            } 
            finally 
            {
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
}


