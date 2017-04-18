package connection;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * A class that handles the receiving side of the <code>Connection</code> by starting 
 * a separate <code>Thread</code> that fetches Datagram packets from the receiveSocket.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Receiver extends Thread {
	
	/**
	 * The <code>Connection</code> on which this <code>PulseHandler</code> operates.
	 */
	public Connection connection;
	
	/**
	 * The <code>TransportLayer</code> to which the Datagram packets are forwarded.
	 */
	public TransportLayer transportLayer;
	
	/**
	 * Creates a <code>Receiver</code> object that starts the receiving thread 
	 * for this connection.
	 * @param connection the <code>Connection</code> from which we receive packets
	 */
	public Receiver(Connection connection) {
		this.connection = connection;
		this.transportLayer = connection.getTransportLayer();
		this.start();
	}
	
	/**
	 * Start fetching packets.
	 */
	@Override
	public void run() {
		receive();
	}

	/**
	 * Fetches packets from the <code>receiveSocket</code>. Forwards them
	 * to the <code>TransportLayer</code>.
	 */
	private void receive() {
		byte buf[] = new byte[Connection.BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		while (!connection.receiveSocket.isClosed()) {
			try {
				connection.receiveSocket.receive(packet);
				transportLayer.handlePacket(packet);
				packet = new DatagramPacket(new byte[Connection.BUFFER_SIZE], buf.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
