package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import connection.Connection;
import connection.PulseHandler;
import model.Person;
import model.Message;

public class Session {
	
	private String name;
	private int ID;
	private Connection connection;
	private Map<Integer, Person> knownPersons;
	private Map<Integer, Integer> secretKeysForPerson;
	private Map<Person, ArrayList<Message>> chatMessages;
	private Map<Integer, Map<Integer, ArrayList<byte[]>>> fileMessages;
	private ArrayList<Message> publicChatMessages;
	private int nextPublicMessageID;
	private int nextFileID;
	private int seq;

	public Session(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		this.connection = new Connection(this);
		this.knownPersons = new HashMap<>();
		this.secretKeysForPerson = new HashMap<>();
		this.chatMessages = new HashMap<>();
		this.fileMessages = new HashMap<>();
		this.publicChatMessages = new ArrayList<>();
		this.seq = 0;
		this.nextPublicMessageID = 0;
		this.nextFileID = 0;
		new PulseHandler(this);
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

	public Map<Integer, Person> getKnownPersons() {
		return knownPersons;
	}

	public Map<Person, ArrayList<Message>> getChatMessages() {
		return chatMessages;
	}
	
	public synchronized Map<Integer, Map<Integer, ArrayList<byte[]>>> getFileMessages() {
		return fileMessages;
	}

	public int getSeq() {
		return seq;
	}
	
	public synchronized int getNextSeq() {
		seq++;
		return seq;
	}

	public Map<Integer, Integer> getSecretKeysForPerson() {
		return secretKeysForPerson;
	}

	public ArrayList<Message> getPublicChatMessages() {
		return publicChatMessages;
	}

	public int getNextPublicMessageID() {
		nextPublicMessageID++;
		return nextPublicMessageID;
	}

	public int getNextFileID() {
		nextFileID++;
		return nextFileID;
	}

	public void setPublicChatMessages(ArrayList<Message> publicChatMessageList) {
		this.publicChatMessages = publicChatMessageList;		
	}	
}
