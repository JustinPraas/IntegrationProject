package packet;

import java.util.ArrayList;

public class EncryptedMessage implements Payload {
	

	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int MID_WAY_KEY_LENGTH = 1;
	public static final int CIPHER_LENGTH_LENGTH = 2;
	
	private int messageID;
	private int midWayKey;
	private int cipherLength;
	private String cipher;

	public EncryptedMessage(int messageID, int midWayKey, int cipherLength, String cipher) {
		this.messageID = messageID;
		this.midWayKey = midWayKey;
		this.cipherLength = cipherLength;
		this.cipher = cipher;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// MessageID to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		
		// MidWayKey to binary
		resultList.add((byte) midWayKey);
		
		resultList.add((byte) (cipherLength >> 8));
		resultList.add((byte) cipherLength);
		
		// encryptedMessage to binary
		for (int i = 0; i < cipher.length(); i++) {
			resultList.add((byte)cipher.charAt(i));
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

	public int getMidWayKey() {
		return midWayKey;
	}

	public int getCipherLength() {
		return cipherLength;
	}
	
	public String getCipher() {
		return cipher;
	}
}
