package model;

import java.text.SimpleDateFormat;
import java.util.Date;

import packet.FileMessage;

public class Message {

	private int senderID;
	private int receiverID;
	private int messageID;
	private long timestamp;
	//private long timestampDiff;
	private String text;
	private byte[] fileData;
	
	public Message(int senderID, int receiverID, int messageID, String text, boolean myMessage) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageID = messageID;
		timestamp = new Date().getTime();
		this.text = text;		
	}
	
	public Message(int senderID, int receiverID, int fileID, String extension, byte[] fileData) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageID = fileID;
		this.text = FileMessage.FILE_IDENTIFIER + extension;
		this.fileData = fileData;
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
	
	public String getTimestampString() {
		return new SimpleDateFormat("HH:mm").format(timestamp);
	}

	public byte[] getFileData() {
		return fileData;
	}
	
	// TODO: Message order with timestamp differences

}
