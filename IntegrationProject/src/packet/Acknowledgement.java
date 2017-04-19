package packet;

import java.util.ArrayList;

public class Acknowledgement implements Payload {
	
	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int FILE_SEQUENCE_NUMBER = 1;

	private int messageID;
	private int fileSequenceNumber;

	public Acknowledgement(int messageID) {
		this.messageID = messageID;
		this.fileSequenceNumber = -1;
	}
	
	public Acknowledgement(int fileID, int seqNum) {
		this.messageID = fileID;
		this.fileSequenceNumber = seqNum;
	}
	
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message sequence number to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		resultList.add((byte) fileSequenceNumber);
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}

	public int getMessageID() {
		return messageID;
	}
	
	public int getFileSequenceNumber() {
		return fileSequenceNumber;
	}
}
