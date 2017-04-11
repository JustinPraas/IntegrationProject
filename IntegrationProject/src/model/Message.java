package model;

import java.text.SimpleDateFormat;

public class Message {

	private int senderID;
	private int receiverID;
	private int messageID;
	private long timestamp;
	private String text;
	
	public Message(int senderID, int receiverID, int messageID, int timestamp, String text) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageID = messageID;
		this.timestamp = timestamp;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public int getMessageID() {
		return messageID;
	}
	
	public String getTimestampString() {
		SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm");
		return timestampFormat.format(timestamp);
	}

}
