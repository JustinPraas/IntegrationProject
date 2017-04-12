package packet;

import java.util.ArrayList;

public class FileMessage implements Payload {
	
	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 4;
	public static final int FILE_ID_LENGTH = 1;
	public static final int FILE_SEQ_LENGTH = 1;
	
	public static final String FILEMESSAGE_INDICATOR = "FileMessage_";
	
	private int messageID;
	private int messageLength;
	private int fileID;
	private int fileSeq; 
	private byte[] message;

	public FileMessage(int messageID, int messageLength, int fileID, int fileSeq, byte[] message) {
		this.messageID = messageID;
		this.messageLength = messageLength;
		this.fileID = fileID;
		this.fileSeq = fileSeq;
		this.message = message;
	}
	
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		resultList.add((byte) (messageLength >> 24));
		resultList.add((byte) (messageLength >> 16));
		resultList.add((byte) (messageLength >> 8));
		resultList.add((byte) (messageLength));
		resultList.add((byte) fileID);
		resultList.add((byte) fileSeq);
		
		for (int i = 0; i < message.length; i++) {
			resultList.add(message[i]);
		}
		
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}

	public int getMessageID() {
		return messageID;
	}
	
	public int getMessageLength() {
		return messageLength;
	}
	
	public int getFileID() {
		return fileID;
	}

	public int getFileSeq() {
		return fileSeq;
	}
	
	public byte[] getMessage() {
		return message;
	}
}
