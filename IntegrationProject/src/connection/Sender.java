package connection;

import java.io.IOException;

import packet.Packet;

/**
 * A class that simplifies the sending of <code>Packet</code> objects.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Sender {
	
	/**
	 * The <code>Connection</code> from which the packets will be sent.
	 */
	public Connection connection;
	
	/**
	 * Constructs a <code>Sender</code> object with the current application's
	 * <code>Connection</code>.
	 * @param connection the current connection
	 */
	public Sender(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Converts the <code>Packet</code> to a <code>DatagramPacket</code> and tries
	 * to send it through the sendSocket of the application's <code>Connection</code>.
	 * @param packet the packet to be sent
	 */
	public void send(Packet packet) {
		try {
			connection.getSendSocket().send(packet.getDatagramPacket());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
