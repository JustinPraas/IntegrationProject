package packet;

import java.util.ArrayList;

public class GlobalMessage implements Payload {
	
	public static final int GLOBAL_MESSAGE_HEADER_LENGTH = 4;
	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 2;
	
	private int messageID;
	private int messageLength;
	private String plainText;

	public GlobalMessage(int messageID, int messageLength, String plainText) {
		this.messageID = messageID;
		this.messageLength = messageLength;
		this.plainText = plainText;
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
		for (int i = 0; i < plainText.length(); i++) {
			resultList.add((byte)plainText.charAt(i));
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

	public String getPlainText() {
		return plainText;
	}
	
	public int getMessageLength() {
		return messageLength;
	}
}
