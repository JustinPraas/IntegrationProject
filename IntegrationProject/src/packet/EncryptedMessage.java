package packet;

import java.util.ArrayList;

public class EncryptedMessage implements Payload {
	
	public static final int MESSAGE_ENCRYPTION_KEY = 4;
	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 2;
	
	private int messageEncryptionKey;
	private int messageID;
	private int messageLength;
	private String encryptedMessage;

	public EncryptedMessage(int messageEncryptionKey, int messageID, int messageLength, String encryptedMessage) {
		this.messageEncryptionKey = messageEncryptionKey;
		this.messageID = messageID;
		this.messageLength = messageLength;
		this.encryptedMessage = encryptedMessage;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message sequence number to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		
		resultList.add((byte) (messageLength >> 8));
		resultList.add((byte) messageLength);
		
		// encryptedMessage to binary
		for (int i = 0; i < encryptedMessage.length(); i++) {
			resultList.add((byte)encryptedMessage.charAt(i));
		}
		
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

	public String getEncryptedMessage() {
		return encryptedMessage;
	}
	
	public int getMessageLength() {
		return messageLength;
	}
	
	public int getMessageEncryptionKey() {
		return messageEncryptionKey;
	}
}
