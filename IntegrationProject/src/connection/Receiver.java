package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

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
		byte buf[] = new byte[160];
		DatagramPacket pkt = new DatagramPacket(buf, buf.length);
		while (!connection.receiveSocket.isClosed()) {
			try {
				connection.receiveSocket.receive(pkt);
				System.out.println(Arrays.toString(pkt.getData()));
				transportLayer.handlePacket(pkt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
