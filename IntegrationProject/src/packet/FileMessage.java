package packet;

import java.util.ArrayList;

public class FileMessage implements Payload {
	
	public static final String FILE_IDENTIFIER = "FILE_";
	
	public static final int FILE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 4;
	public static final int SEQUENCE_NUMBER_LENGTH = 1;
	public static final int TOTAL_PACKETS_LENGTH = 1;
	public static final int EXTENSION_LENGTH = 4;
	
	private int fileID;
	private int messageLength;
	private int sequenceNumber;
	private int totalPackets;
	private String extension;
	private byte[]	data;
	
	public FileMessage(int fileID, int seqNum, int msgLen, int totalPkts, String ext, byte[] data) {
		this.fileID = fileID;
		this.sequenceNumber = seqNum;
		this.messageLength = msgLen;
		this.totalPackets = totalPkts;
		this.extension = ext;
		this.data = data;
	}

	public int getFileID() {
		return fileID;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getTotalPackets() {
		return totalPackets;
	}

	public String getExtensiion() {
		return extension;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message sequence number to binary
		resultList.add((byte) (fileID >> 8));
		resultList.add((byte) fileID);
		
		// Message length to binary
		resultList.add((byte) (messageLength >> 24));
		resultList.add((byte) (messageLength >> 16));
		resultList.add((byte) (messageLength >> 8));
		resultList.add((byte) messageLength);
		
		resultList.add((byte) sequenceNumber);
		
		resultList.add((byte) totalPackets);
		
		// extension to binary
		for (int i = 0; i < extension.length(); i++) {
			resultList.add((byte) extension.charAt(i));
		}
		
		for (int i = 0; i < data.length; i++) {
			resultList.add(data[i]);
		}
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}
	

}
