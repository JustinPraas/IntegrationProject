package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import connection.Connection;
import connection.PulseHandler;
import model.Message;
import model.Person;

/**
 * A class that keeps track of most of the data and the current
 * application's session.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 *
 */
public class Session {
	
	/**
	 * The name of this session's user.
	 */
	private String name;
	
	/**
	 * The ID of this sessino's user.
	 */
	private int ID;
	
	/**
	 * The connection on which this session runs.
	 */
	private Connection connection;
	
	/**
	 * The experience tracker of this session.
	 */
	private ExperienceTracker experienceTracker;
	
	/**
	 * A map consisting of known persons: 
	 * Map<the person's ID, the person itself>.
	 */
	private Map<Integer, Person> knownPersons;
	
	/**
	 *  A map that keeps track of secretInteger for each contact person:
	 *  Map<The contact person's ID, the secretInteger>.
	 */
	private Map<Integer, Integer> secretKeysForPerson;
	
	/**
	 * A map that keeps track of the messages exchanged with the contact person
	 * Map<The contact person's ID, the array of messages>.
	 */
	private Map<Person, ArrayList<Message>> chatMessages;
	
	/**
	 * A list that keeps track of the public chat messages (a.k.a. global chat messages).
	 */
	private ArrayList<Message> publicChatMessages;
	
	/**
	 * An integer that indicates what the next message ID should be for an outgoing 
	 * public (global) message.
	 */
	private int nextPublicMessageID;
	
	/**
	 * An integer that indicates what the sequence number of the last sent packet is.
	 */
	private int sequenceNumber;

	/**
	 * Constructs a <code>Session</code> object. Generates a random ID for this session
	 * (between 0 and 2^31 - 1). Sets the session's name to the given name
	 * Creates a <code>PulseHandler</code> object which sends 'keep-alive' messages.
	 * @param name the user's name
	 */
	public Session(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		this.connection = new Connection(this);
		this.knownPersons = new HashMap<>();
		this.secretKeysForPerson = new HashMap<>();
		this.chatMessages = new HashMap<>();
		this.publicChatMessages = new ArrayList<>();
		this.sequenceNumber = 0;
		this.nextPublicMessageID = 0;
		this.experienceTracker = new ExperienceTracker();
		new PulseHandler(this);
	}

	/**
	 * Increments the <code>nextPublicMessageID</code> and returns it.
	 * @return nextPublicMessageID the public message ID with which the next public
	 * message is transmitted
	 */
	public int getNextPublicMessageID() {
		nextPublicMessageID++;
		return nextPublicMessageID;
	}
	
	/**
	 * Increments the <code>sequenceNumber</code> and returns it.
	 * @return the sequence number that is to be used by the next outgoing packet.
	 */
	public synchronized int getNextSeqNumber() {
		sequenceNumber++;
		return sequenceNumber;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return ID;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public ExperienceTracker getExperienceTracker() {
		return experienceTracker;
	}

	public Map<Integer, Person> getKnownPersons() {
		return knownPersons;
	}

	public Map<Person, ArrayList<Message>> getChatMessages() {
		return chatMessages;
	}

	public int getSeq() {
		return sequenceNumber;
	}

	public Map<Integer, Integer> getSecretKeysForPerson() {
		return secretKeysForPerson;
	}

	public ArrayList<Message> getPublicChatMessages() {
		return publicChatMessages;
	}

	public void setPublicChatMessages(ArrayList<Message> publicChatMessageList) {
		this.publicChatMessages = publicChatMessageList;		
	}	
}
