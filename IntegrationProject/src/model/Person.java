package model;

import java.util.HashMap;
import java.util.Map;
import encryption.EncryptionPair;

public class Person {
	
	// Person data
	public String name;
	public int ID;
	private int timeToLive;
	private int nextMessageID;
	private int nextFileID;
	


	// Diffie-Hellman data
	private int secretInteger;
	private EncryptionPair privateChatPair;

	/**
	 * Creates a <code>Person</code> object with a random ID 
	 * and the given name for this client session.
	 * @param name the person's name
	 */
	public Person(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		this.nextMessageID = 0;
	}

	/**
	 * Creates a <code>Person</code> object for an outsider
	 * @param name the outsider's name
	 * @param ID the outsider's ID
	 */
	public Person(String name, int ID) {
		this.name = name;
		this.ID = ID;
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
	
	public int getNextMessageID() {
		nextMessageID++;
		return nextMessageID;
	}
	
	public int getNextFileID() {
		nextFileID++;
		return nextFileID;
	}

	public int getSecretInteger() {
		return secretInteger;
	}

	public EncryptionPair getPrivateChatPair() {
		return privateChatPair;
	}

	public void setPrivateChatPair(EncryptionPair privateChatPair) {
		this.privateChatPair = privateChatPair;
	}
	
}
