package connection;

import java.io.IOException;
import java.net.DatagramPacket;

public class Receiver extends Thread {
	
	public Connection connection;
	
	public Receiver(Connection c) {
		connection = c;
	}
	
	public void run() {
		receive();
	}

	private void receive() {
		byte buf[] = new byte[65535];
		DatagramPacket pkt = new DatagramPacket(buf, buf.length);
		while (!connection.receiveSocket.isClosed()) {
			try {
				TransportLayer.handlePacket(connection.receiveSocket.receive(pkt));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
