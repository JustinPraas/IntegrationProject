package connection;

import java.util.Map;

import encryption.DiffieHellman;
import encryption.EncryptionPair;
import model.Person;
import model.Session;
import packet.Packet;
import packet.*;
import packet.Pulse;
import userinterface.GUIHandler;

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
			decreaseTimeToLive();
			sendEncryptionPair();
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void pulse() {
		int nameLength = session.getName().length();
		Pulse pulse = new Pulse(nameLength, session.getExperienceTracker().getCurrentLevel(), 
				session.getName()); 
		Packet packet = new Packet(session.getID(), 0, session.getNextSeqNumber(), Payload.PULSE, pulse);
		session.getConnection().getSender().send(packet);
	}
	
	public void decreaseTimeToLive() {
		for (Map.Entry<Integer, Person> entry : session.getKnownPersons().entrySet()) {
			Person person = entry.getValue();
			
			// Change TTL
			int ttl = person.getTimeToLive();
			person.setTimeToLive(ttl - 1);
			session.getKnownPersons().put(person.getID(), person);
			
			// Update GUI
			if (ttl == 0) {
				GUIHandler.changedPersonList();
			}
		}
	}
	
	public void sendEncryptionPair() {
		for (Map.Entry<Integer, Person> entry : session.getKnownPersons().entrySet()) {
			if (entry.getValue().getPrivateChatPair() == null) {
				
				// If I have a higher ID than the other person, create an encryption pair
				// pair it to the other person and, generate a secretKey for myself to use with this person
				// and send the encryption pair to the other person
				if (entry.getValue().getID() < session.getID()) {
					EncryptionPair ep = new EncryptionPair();
					int secretInteger = DiffieHellman.produceSecretKey(ep.getPrime());
					session.getSecretKeysForPerson().put(entry.getKey(), secretInteger);	
					ep.setLocalHalfKey(secretInteger);
					entry.getValue().setPrivateChatPair(ep);
					
					// Send the packet
					EncryptionPairExchange epe = new EncryptionPairExchange(ep.getPrime(), ep.getGenerator(), ep.getLocalHalfKey());
					Packet packet = new Packet(session.getID(), entry.getValue().getID(), 
							session.getNextSeqNumber(), Payload.ENCRYPTION_PAIR, epe);
					session.getConnection().getSender().send(packet);
				}	
				
			} else if (!entry.getValue().getPrivateChatPair().isAcknowledged()) {
				// Send the packet
				EncryptionPair ep = entry.getValue().getPrivateChatPair();
				EncryptionPairExchange epe = new EncryptionPairExchange(ep.getPrime(), ep.getGenerator(), ep.getLocalHalfKey());
				Packet packet = new Packet(session.getID(), entry.getValue().getID(), 
						session.getNextSeqNumber(), Payload.ENCRYPTION_PAIR, epe);
				session.getConnection().getSender().send(packet);
			}
		}
	}
}
