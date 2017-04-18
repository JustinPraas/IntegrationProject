package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import model.Session;

/**
 * A class that stores this application's connection settings.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Connection {
	
	/**
	 * The maximum acceptable size of the data of a <code>DatagramPacket</code>.
	 */
	public static final int BUFFER_SIZE = 1024000;
	
	/**
	 * The socket from which <code>DatagramPackets</code> will be sent.
	 */
	public MulticastSocket sendSocket;
	
	/**
	 * The socket on which <code>DatagramPackets</code> will be received.
	 */
	public MulticastSocket receiveSocket;
	
	/**
	 * A <code>Sender</code> object that serves for intermediate sending functionality.
	 */
	public Sender sender;
	
	/**
	 * A <code>Receiver</code> object that serves for intermediate receiving functionality.
	 */
	public Receiver receiver;
	
	/**
	 * The transport layer for this application and connection.
	 */
	public TransportLayer transportLayer;
	
	/**
	 * The <code>PulesHandler</code> object for this connection
	 */
	public PulseHandler pulseHandler;
	
	/**
	 * The multicast group address.
	 */
	public static InetAddress group;
	
	/**
	 * The port on which the application communicates.
	 */
	public static final int port = 12345;
	
	/**
	 * Constructs a new <code>Connection</code> object for this
	 * application session with a new <code>Session</code> object with it.
	 * @param session the session object for this connection
	 */
	public Connection(Session session) {
		try {
			this.sendSocket = new MulticastSocket(port);
			sendSocket.setSendBufferSize(BUFFER_SIZE);
			
			this.receiveSocket = new MulticastSocket(port);
			receiveSocket.setReceiveBufferSize(BUFFER_SIZE);
			
			joinGroup(sendSocket, "default");
			joinGroup(receiveSocket, "default");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.transportLayer = new TransportLayer(session);
		this.sender = new Sender(this);
		this.receiver = new Receiver(this);
	}
	
	/**
	 * Assigns a group to the connection for the multicast socket.
	 * @param socket the socket to assign a group to
	 * @param address the address to which the given socket is grouped to
	 */
	public static void joinGroup(MulticastSocket socket, String address) {
		try {
			if (address.equals("default")) {
				group = InetAddress.getByName("228.0.0.0");
				socket.joinGroup(group);
			} else {
				group = InetAddress.getByName(address);
				socket.joinGroup(group);
			}
		} catch (UnknownHostException e) {
			System.err.println("Not a valid multicast address.");
			System.err.println("Use 'default' for a default valid address.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes this connection's sockets.
	 */
	public void close() {
		receiveSocket.close();
		sendSocket.close();
	}

	public Sender getSender() {
		return sender;
	}

	public MulticastSocket getSendSocket() {
		return sendSocket;
	}
	
	public TransportLayer getTransportLayer() {
		return transportLayer;
	}
	
}
