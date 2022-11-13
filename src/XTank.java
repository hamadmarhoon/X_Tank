
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class XTank {
	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("127.0.0.1", 59896)) {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			var ui = new XTankUI(in, out, 300, 300);
			ui.start();
		}
	}
}
