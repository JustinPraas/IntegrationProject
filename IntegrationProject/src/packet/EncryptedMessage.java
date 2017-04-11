package packet;

import java.util.ArrayList;

public class EncryptedMessage implements Payload {
	
	private int messageID;
	private String encryptedMessage;

	public EncryptedMessage(int messageID, String encryptedMessage) {
		this.messageID = messageID;
		this.encryptedMessage = encryptedMessage;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
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

	public int getMessageID() {
		return messageID;
	}

	public String getEncryptedMessage() {
		return encryptedMessage;
	}
}
