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
	public ArrayList<Person> knownPersons; // TODO: change back to private
	public Map<Person, ArrayList<Message>> chatMessages; // TODO: change back to private
	private int seq;

	public Session(String name) {
		this.name = name;
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
//		this.connection = new Connection(); TODO: uncomment
		this.knownPersons = new ArrayList<>();
		this.chatMessages = new HashMap<>();
		this.seq = 0;
//		new PulseHandler(this); TODO: uncomment
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

	public ArrayList<Person> getKnownPersons() {
		return knownPersons;
	}

	public Map<Person, ArrayList<Message>> getChatMessages() {
		return chatMessages;
	}
	
	public int getSeq() {
		return seq;
	}
	
	public int getNextSeq() {
		seq++;
		return seq;
	}
	
}
