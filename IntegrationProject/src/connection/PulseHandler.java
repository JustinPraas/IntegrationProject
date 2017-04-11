package connection;

import application.Session;
import model.Person;
import packet.Packet;
import packet.Pulse;

public class PulseHandler extends Thread {
	
	private Session session;
	private Connection connection;
	
	public PulseHandler(Session session) {
		this.session = session;
		this.connection = session.getConnection();
		this.start();
	}
	
	@Override
	public void run() {
		while (!connection.sendSocket.isClosed()) {
			pulse();
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void pulse() {
		Pulse pulse = new Pulse(session.getID(), session.getName()); 
		Packet packet = new Packet(0, session.getNextSeq(), pulse);
		session.getConnection().getSender().send(packet);
	}
	
	public void decreaseTimeToLive() {
		for (Person person : session.getKnownPersons()) {
			int ttl = person.getTimeToLive();
			if (ttl > 0) {
				person.setTimeToLive(ttl - 1);
			}
		}
	}
}
