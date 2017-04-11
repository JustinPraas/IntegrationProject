package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

	private int senderID;
	private int receiverID;
	private int messageID;
	private long timestamp;
	private String text;
	
	public Message(int senderID, int receiverID, int messageID, String text) {
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
	
	public String getTimestampString() {
		return new SimpleDateFormat("HH:mm").format(timestamp);
	}

}
