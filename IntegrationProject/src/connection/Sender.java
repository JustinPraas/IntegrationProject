package connection;

import java.io.IOException;

import packet.Packet;

public class Sender {
	
	public Connection connection;
	
	public Sender(Connection connection) {
		this.connection = connection;
	}
	
	public void send(Packet packet) {
		try {
			connection.getSendSocket().send(packet.getDatagramPacket());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
