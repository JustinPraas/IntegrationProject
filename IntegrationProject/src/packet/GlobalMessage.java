package packet;

import java.util.ArrayList;

/**
 * A class that stores properties of a <code>GlobalMessage</code> payload-type.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class GlobalMessage implements Payload {
	
	/**
	 * The total header length (bytes) of the global message.
	 */
	public static final int GLOBAL_MESSAGE_HEADER_LENGTH = 4;
	
	/**
	 * The length (bytes) of the messageID field in the global message.
	 */
	public static final int MESSAGE_ID_LENGTH = 2;
	
	/**
	 * The length (bytes) of the messageLength field in the global message.
	 */
	public static final int MESSAGE_LENGTH_LENGTH = 2;
	
	/**
	 * The messageID of the <code>Message</code> that is encapsulated by this payload.
	 */
	private int messageID;
	
	/**
	 * The messageLength of the <code>Message</code>'s text that is encapsulated by this payload.
	 */
	private int messageLength;
	
	/**
	 * The text of the <code>Message</code>.
	 */
	private String plainText;
	
	/**
	 * Constructs a global message <code>Payload</code>.
	 * @param messageID
	 * @param messageLength
	 * @param plainText
	 */
	public GlobalMessage(int messageID, int messageLength, String plainText) {
		this.messageID = messageID;
		this.messageLength = messageLength;
		this.plainText = plainText;
	}

	/**
	 * Returns the byte array of this <code>GlobalMessage</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// MessageID to binary
		for (int i = (MESSAGE_ID_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (messageID >> i));
		}
		
		// MessageLength to binary
		for (int i = (MESSAGE_LENGTH_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (messageLength >> i));
		}
		
		// GlobalMessage plain text to binary
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
