package packet;

import java.util.ArrayList;

public class FileMessage implements Payload {
	
	public static final String FILE_INDICATOR = "FILE_";
	
	public static final int FILE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 4;
	public static final int TOTAL_PACKETS = 1;
	public static final int SEQUENCE_NUMBER = 1;
	
	private int fileID;
	private int messageLength;
	private int totalPackets;
	private int sequenceNumber;
	private byte[] fileData;
	
	public FileMessage(int fileID, int messageLength, int totalPackets, int sequenceNumber, byte[] fileData) {
		this.fileID = fileID;
		this.messageLength = messageLength;
		this.totalPackets = totalPackets;
		this.sequenceNumber = sequenceNumber;
		this.fileData = fileData;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message ID to binary
		resultList.add((byte) (fileID >> 8));
		resultList.add((byte) fileID);
		
		resultList.add((byte) (messageLength >> 24));
		resultList.add((byte) (messageLength >> 16));
		resultList.add((byte) (messageLength >> 8));
		resultList.add((byte) messageLength);
		
		resultList.add((byte) totalPackets);
		
		resultList.add((byte) sequenceNumber);
		
		// fileData added to the list
		for (int i = 0; i < fileData.length; i++) {
			resultList.add((byte) fileData[i]);
		}
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}

	public int getFileID() {
		return fileID;
	}
	
	public int getMessageLength() {
		return messageLength;
	}

	public int getTotalPackets() {
		return totalPackets;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public byte[] getFileData() {
		return fileData;
	}
}
