package connection;

import java.io.IOException;
import java.net.DatagramPacket;

public class Receiver extends Thread {
	
	public Connection connection;
	public TransportLayer transportLayer;
	
	public Receiver(Connection c) {
		connection = c;
		transportLayer = c.getTransportLayer();
		this.start();
	}
	
	public void run() {
		receive();
	}

	private void receive() {
		byte buf[] = new byte[65000];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		while (!connection.receiveSocket.isClosed()) {
			try {
				connection.receiveSocket.receive(packet);
				transportLayer.handlePacket(packet);
				packet = new DatagramPacket(new byte[65000], buf.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
