package model;

import java.util.HashMap;
import java.util.Map;

import encryption.DiffieHellman;
import encryption.EncryptionPair;

public class Person {
	
	// Person data
	public String name;
	public int ID;
	private int timeToLive;
	
	// Diffie-Hellman data
	private int secretInteger;
	private Map<Integer, EncryptionPair> privateChatPair = new HashMap<>();

	/**
	 * Creates a <code>Person</code> object with a random ID 
	 * and the given name for this client session.
	 * @param name the person's name
	 */
	public Person(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		this.secretInteger = 1 + (int) (Math.random() * 10);
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
	
	
}
