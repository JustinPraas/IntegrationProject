package chat;

import java.util.Date;

/**
 * Message consisting of a sender, receiver, the message, a timestamp and its sequencenumber
 * @author Tim
 * @version 10-4-2017
 *
 */
public class Message {
	
	private int senderID;
	private int receiverID;
	private String msg;
	private long timestamp;
	private int sequenceNumber;
	
	/**
	 * Instantiates a new Message with msg as data
	 * @param senderID int
	 * @param receiverID int
	 * @param msg a Message object
	 * @param sequenceNumber int
	 */
	public Message(int senderID, int receiverID, String msg, int sequenceNumber) {
		
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.msg = msg;
		
		Date date = new Date();
		timestamp = date.getTime();
		
		this.sequenceNumber = sequenceNumber;
		
	}

	public int getSenderID() {
		return senderID;
	}

	public int getReceiverID() {
		return receiverID;
	}

	public String getMsg() {
		return msg;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

}
