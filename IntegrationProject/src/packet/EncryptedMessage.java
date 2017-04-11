package packet;

import java.util.ArrayList;

public class EncryptedMessage implements Payload {
	
	private int senderID;
	private int receiverID;
	private int messageID;
	private String encryptedMessage;

	public EncryptedMessage(int senderID, int receiverID, int messageID, String encryptedMessage) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageID = messageID;
		this.encryptedMessage = encryptedMessage;
	}

	@Override
	public byte[] getPayload() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Sender ID to binary
		resultList.add((byte) (senderID >> 24));
		resultList.add((byte) (senderID >> 16));
		resultList.add((byte) (senderID >> 8));
		resultList.add((byte) senderID);
		
		// Receiver ID to binary
		resultList.add((byte) (receiverID >> 24));
		resultList.add((byte) (receiverID >> 16));
		resultList.add((byte) (receiverID >> 8));
		resultList.add((byte) receiverID);
		
		// Message sequence number to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		
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

	public int getSenderID() {
		return senderID;
	}

	public int getReceiverID() {
		return receiverID;
	}

	public int getMessageID() {
		return messageID;
	}

	public String getEncryptedMessage() {
		return encryptedMessage;
	}
}
