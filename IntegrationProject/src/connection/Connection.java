package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import application.Session;

public class Connection {
	
	public MulticastSocket sendSocket;
	public MulticastSocket receiveSocket;
	
	public Sender sender;
	public Receiver receiver;
	
	public TransportLayer transportLayer;
	public PulseHandler pulseHandler;
	public InetAddress group;
	
	public Connection(Session session) {
		try {
			this.sendSocket = new MulticastSocket();
			this.receiveSocket = new MulticastSocket();
			this.joinGroup("default");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.sender = new Sender(this);
		this.receiver = new Receiver(this);
		this.transportLayer = new TransportLayer(session);
	}
	
	public void joinGroup(String address) {
		try {
			if (address.equals("default")) {
				group = InetAddress.getByName("224.0.0.5");
				sendSocket.joinGroup(group);
				receiveSocket.joinGroup(group);
			} else {
				group = InetAddress.getByName(address);
				sendSocket.joinGroup(group);
				receiveSocket.joinGroup(group);
			}
		} catch (UnknownHostException e) {
			System.out.println("Not a valid multicast address");
			System.out.println("Use 'default' for a default valid address");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
	
}
