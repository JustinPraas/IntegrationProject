package packet;

import java.util.ArrayList;

public class FileMessage implements Payload {
	
	/**
	 * Indicator used to separate normal chat messages from file messages.
	 */
	public static final String FILE_INDICATOR = "FILE_MESSAGE";
	
	/**
	 * The total header length (bytes) of the file message (excluding file data).
	 */
	public static final int FILE_MESSAGE_HEADER_LENGTH = 8;
	
	/**
	 * The length (bytes) of the fileID field in the file message.
	 */
	public static final int FILE_ID_LENGTH = 2;
	
	/**
	 * The length (bytes) of the messageLength field in the file message.
	 */
	public static final int MESSAGE_LENGTH_LENGTH = 4;
	
	/**
	 * The length (bytes) of the totalPackets field in the file message.
	 */
	public static final int TOTAL_PACKETS_LENGTH = 1;
	/**
	 * The length (bytes) of the sequenceNumber field in the file message.
	 */
	public static final int SEQUENCE_NUMBER_LENGTH = 1;
	
	/**
	 * The fileID of the <code>FileMessage</code> that is encapsulated by this payload.
	 */
	private int fileID;
	
	/**
	 * The messageLength of the <code>FileMessage</code>'s data that is encapsulated by this payload.
	 */
	private int messageLength;
	
	/**
	 * The total packets that is needed to send the <code>File</code> that is (partly) encapsulated by this payload.
	 */
	private int totalPackets;
	
	/**
	 * The sequence number of this <code>FileMessage</code> that is encapsulated by this payload.
	 */
	private int sequenceNumber;
	
	/**
	 * The data of the <code>FileMessage</code> that is encapsulated by this payload.
	 */
	private byte[] fileData;
	
	/**
	 * Constructs a file message <code>Payload</code>.
	 * @param fileID
	 * @param messageLength
	 * @param totalPackets
	 * @param sequenceNumber
	 * @param fileData
	 */
	public FileMessage(int fileID, int messageLength, int totalPackets, int sequenceNumber, byte[] fileData) {
		this.fileID = fileID;
		this.messageLength = messageLength;
		this.totalPackets = totalPackets;
		this.sequenceNumber = sequenceNumber;
		this.fileData = fileData;
	}

	/**
	 * Returns the byte array of this <code>FileMessage</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// FileID to binary
		for (int i = (FILE_ID_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (fileID >> i));
		}
		
		// messageLength to binary
		for (int i = (MESSAGE_LENGTH_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (messageLength >> i));
		}
		
		// totalPackets to binary
		for (int i = (TOTAL_PACKETS_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (totalPackets >> i));
		}
		
		// sequenceNumber to binary
		for (int i = (SEQUENCE_NUMBER_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (sequenceNumber >> i));
		}
		
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
