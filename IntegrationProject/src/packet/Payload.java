package packet;

public interface Payload {
	
	/**
	 * The typeIdentifier of a <code>Pulse</code> payload.
	 */
	public static final int PULSE = 0;
	
	/**
	 * The typeIdentifier of a <code>GlobalMessage</code> payload.
	 */
	public static final int GLOBAL_MESSAGE = 1;
	
	/**
	 * The typeIdentifier of a <code>Acknowledgement</code> payload.
	 */
	public static final int ACKNOWLEDGEMENT = 2;
	
	/**
	 * The typeIdentifier of a <code>EncryptionPair</code> payload.
	 */
	public static final int ENCRYPTION_PAIR = 3;
	
	/**
	 * The typeIdentifier of a <code>EncryptedMessage</code> payload.
	 */
	public static final int ENCRYPTED_MESSAGE = 4;
	public static final int FILE_MESSAGE = 5;
	
	/**
	 * Returns a byte array of the data of the <code>Payload</code>.
	 * @return data the <code>Payload</code> data in a byte array
	 */
	public byte[] getPayloadData();

}
