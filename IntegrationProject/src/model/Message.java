package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class that stores message properties.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Message {

	/**
	 * The ID of the sender of this message.
	 */
	private int senderID;
	
	/**
	 * The ID of the receiver (destination) of this message.
	 */
	private int receiverID;
	
	/**
	 * The ID of the message.
	 */
	private int messageID;
	
	/**
	 * The timestamp at which this message is received.
	 */
	private long timestamp;
	
	/**
	 * The text of the message.
	 */
	private String text;
	
	/**
	 * Constructs a <code>Message</code> object. Assigns the time of the user's machine to
	 * the message and specifies whether the message was from ourselves or not.
	 * @param senderID the ID of the sender of this message
	 * @param receiverID the ID of the receiver (destination) of this message
	 * @param messageID the ID of the message itself
	 * @param text the text of the message
	 * @param myMessage true if this message is from ourselves, otherwise false
	 */
	public Message(int senderID, int receiverID, int messageID, String text, boolean myMessage) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageID = messageID;
		timestamp = new Date().getTime();
		this.text = text;		
	}
	
	public String getText() {
		return text;
	}
	
	public int getMessageID() {
		return messageID;
	}
	
	public int getSenderID() {
		return senderID;
	}
	
	public int getReceiverID() {
		return receiverID;
	}
	
	/**
	 * Returns the time as a String in the format HH:mm.
	 * @return timestampString the HH:mm format of this message's timestamp
	 */
	public String getTimestampString() {
		return new SimpleDateFormat("HH:mm").format(timestamp);
	}
}
