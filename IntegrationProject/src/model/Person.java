package model;

import encryption.EncryptionPair;

/**
 * A class that stores properties of a contact person.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Person {
	
	/**
	 * The name of the person.
	 */
	public String name;
	
	/**
	 * The ID of the person's application. The ID that is used to send packets to this user.
	 */
	public int ID;
	
	/**
	 * The number of seconds that specify how long this user is still reachable within our network.
	 * Usually, resets to 5 every second. If no pulses are received, decreases by 1 every second.
	 * At timeToLive = 0, this user will be shown as offline and messages that we send, won't 
	 * reach this person anymore.
	 */
	private int timeToLive;
	
	/**
	 * The ID of the message that we send to this person.
	 */
	private int nextMessageID;
	
	/**
	 * The ID of the file that we send to this person
	 */
	private int nextFileID;
	
	/**
	 * The level of this person.
	 */
	private int level;

	/**
	 * The EncryptionPair that we have for this person.
	 */
	private EncryptionPair privateChatPair;

	/**
	 * Constructs a <code>Person</code> object with a random ID 
	 * and the given name for this client session.
	 * @param name the person's name
	 */
	public Person(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		this.nextMessageID = 0;
		this.nextFileID = 0;
	}

	/**
	 * Constructs a <code>Person</code> object for a new contact person from which we 
	 * have received a <code>Pulse</code> packet.
	 * @param name the contact's name
	 * @param ID the contact's ID
	 */
	public Person(String name, int ID, int level) {
		this.name = name;
		this.ID = ID;
		this.level = level;
	}
	
	public int getTimeToLive() {
		return this.timeToLive;
	}
	
	public void setTimeToLive(int ttl) {
		this.timeToLive = ttl;
	}
		
	public String getName() {
		return name;
	}
	
	public int getID() {
		return ID;
	}
	
	/**
	 * Increases and returns the next messageID for the contact person.
	 * @return nextMessageID the messageID to send the next message with to this contact person
	 */
	public int getNextMessageID() {
		nextMessageID++;
		return nextMessageID;
	}

	/**
	 * Increases and returns the next fileID for the contact person.
	 * @return nextFileID the fileID to send the next file with to this contact person
	 */
	public int getNextFileID() {
		nextFileID++;
		return nextFileID;
	}

	public EncryptionPair getPrivateChatPair() {
		return privateChatPair;
	}
	
	public int getLevel() {
		return level;
	}
	
	/**
	 * Converts the level of this person to a String and return it
	 * @return levelString the level String of this person
	 */
	public String getLevelString() {
		return Integer.toString(level);
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setPrivateChatPair(EncryptionPair privateChatPair) {
		this.privateChatPair = privateChatPair;
	}
	
}
