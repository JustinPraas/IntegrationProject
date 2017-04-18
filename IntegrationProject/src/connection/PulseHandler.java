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

/**
 * A class that handles the 'keep-alive' messages, by sending pulses periodically
 * in a separate <code>Thread</code>.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class PulseHandler extends Thread {
	
	/**
	 * The interval at which this <code>PulseHandler</code> should send pulses.
	 */
	private static final long PULSE_INTERVAL = 1000;

	/**
	 * The <code>Session</code> on which this <code>PulseHandler</code> operates.
	 */
	private Session session;
	
	/**
	 * The <code>Connection</code> on which this <code>PulseHandler</code> operates.
	 */
	private Connection connection;
	
	/**
	 * Constructs a <code>PulseHandler</code> object. Starts this <code>Thread</code>.
	 * @param session the <code>Session</code> on which this <code>PulseHandler</code> operates
	 */
	public PulseHandler(Session session) {
		this.session = session;
		this.connection = session.getConnection();
		this.start();
	}
	
	/**
	 * The thread that sends pulses every <code>PULSE_INTERVAL</code> milliseconds, decreases the 
	 * TTL of persons in the session and sends an <code>EncryptionPair</code> when necessary. 
	 */
	@Override
	public void run() {
		while (!connection.sendSocket.isClosed()) {
			pulse();
			decreaseTimeToLive();
			session.getStatistics().increaseSessionTime();
			session.getStatistics().increasePulsesSent();
			sendEncryptionPair();
			try { Thread.sleep(PULSE_INTERVAL); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Sends a pulse to all nearby neighbors. 
	 */
	public void pulse() {
		int nameLength = session.getName().length();
		Pulse pulse = new Pulse(nameLength, session.getExperienceTracker().getCurrentLevel(), 
				session.getName()); 
		Packet packet = new Packet(session.getID(), 0, session.getNextSeqNumber(), Payload.PULSE, pulse);
		session.getConnection().getSender().send(packet);
	}
	
	/**
	 * Decreases the time-to-live for all known persons in this application's <code>Session</code>.
	 */
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
	
	/**
	 * Sends an <code>EncryptionPair</code> to all persons that do not yet have 
	 * an <code>EncryptionPair</code> with this application's user.
	 */
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
					session.getStatistics().increaseSecurityMessagesSent();
					EncryptionPairExchange epe = new EncryptionPairExchange(ep.getPrime(), ep.getGenerator(), ep.getLocalHalfKey());
					Packet packet = new Packet(session.getID(), entry.getValue().getID(), 
							session.getNextSeqNumber(), Payload.ENCRYPTION_PAIR, epe);
					session.getConnection().getSender().send(packet);
				}	
				
			} else if (!entry.getValue().getPrivateChatPair().isAcknowledged()) {
				// Send the packet
				EncryptionPair ep = entry.getValue().getPrivateChatPair();
				EncryptionPairExchange epe = new EncryptionPairExchange(ep.getPrime(), ep.getGenerator(), ep.getLocalHalfKey());
				session.getStatistics().increaseSecurityMessagesSent();
				Packet packet = new Packet(session.getID(), entry.getValue().getID(), 
						session.getNextSeqNumber(), Payload.ENCRYPTION_PAIR, epe);
				session.getConnection().getSender().send(packet);
			}
		}
	}
}
