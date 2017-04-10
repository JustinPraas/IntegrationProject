package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Connection {
	
	public MulticastSocket socket;
	public InetAddress group;
	
	public Connection() {
		try {
			socket = new MulticastSocket();
			this.joinGroup("default");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void joinGroup(String address) {
		try {
			if (address.equals("default")) {
				group = InetAddress.getByName("224.0.0.0");
				socket.joinGroup(group);
			} else {
				group = InetAddress.getByName(address);
				socket.joinGroup(group);
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
		socket.close();
	}
	
}
