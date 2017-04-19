package packet;

import java.util.ArrayList;

/**
 * A class that stores properties of an <code>EncryptedMessage</code> payload-type.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class EncryptedMessage implements Payload {
	
	/**
	 * The total header length (bytes) of the encrypted message (excluding the cipher length).
	 */
	public static final int ENCRYPTED_MESSAGE_HEADER_LENGTH = 5;
	
	/**
	 * The length (bytes) of the messageID field in the encrypted message.
	 */
	public static final int MESSAGE_ID_LENGTH = 2;
	
	/**
	 * The length (bytes) of the midWayKey field in the encrypted message.
	 */
	public static final int MID_WAY_KEY_LENGTH = 1;
	
	/**
	 * The length (bytes) of the cipherLength field in the encrypted message. 
	 */
	public static final int CIPHER_LENGTH_LENGTH = 2;
	
	/**
	 * The messageID of the <code>EncryptedMessage</code> encapsulated <code>Message</code>.
	 */
	private int messageID;
	
	/**
	 * The midWaykey (a.k.a. the localHalfKey) of the sender of the <code>EncryptedMessage</code>.
	 */
	private int midWayKey;
	
	/**
	 * The length of the cipher (bytes, UTF-8).
	 */
	private int cipherLength;
	
	/**
	 * The cipher of the encrypted plain text of the encapsulated <code>Message</code>.
	 */
	private String cipher;

	/**
	 * Constructs an encrypted message <code>Payload</code>.
	 * @param messageID the messageID of the encapsulated <code>Message</code>
	 * @param midWayKey the midWayKey (localHalfKey) of the sender that is used to encrypt the
	 * message
	 * @param cipherLength the length (bytes, UTF-8) of the cipher
	 * @param cipher the encrypted plain text of the encapsulated <code>Message</code>
	 */
	public EncryptedMessage(int messageID, int midWayKey, int cipherLength, String cipher) {
		this.messageID = messageID;
		this.midWayKey = midWayKey;
		this.cipherLength = cipherLength;
		this.cipher = cipher;
	}

	/**
	 * Returns the byte array of this <code>EncryptedMessage</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message ID to binary
		for (int i = (MESSAGE_ID_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (messageID >> i));
		}
		
		// MidWayKey to binary
		for (int i = (MID_WAY_KEY_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (midWayKey >> i));
		}
		
		// CipherLength to binary
		for (int i = (CIPHER_LENGTH_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (cipherLength >> i));
		}
		
		// EncryptedMessage to binary
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
