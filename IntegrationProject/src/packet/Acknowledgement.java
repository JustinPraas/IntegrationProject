package packet;

import java.util.ArrayList;

/**
 * A class that stores properties of an acknowledgement payload-type.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Acknowledgement implements Payload {
	
	/**
	 * The total acknowledgement header length (bytes).
	 */
	public static final int ACK_HEADER_LENGTH = 2;
	
	/**
	 * The length (bytes) of the messageID field in the payload.
	 */
	public static final int ACK_MESSAGE_ID_LENGHT = 2;

	/**
	 * The messageID which this acknowledgement serves to acknowledge.
	 */
	private int messageID;

	/**
	 * Constructs an acknowledgement <code>Payload</code>.
	 * @param messageID the message ID which this <code>Acknowledgement</code> serves to
	 * acknowledge
	 */
	public Acknowledgement(int messageID) {
		this.messageID = messageID;
	}

	/**
	 * Returns the byte array of this <code>Acknowledgement</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message ID to binary
		for (int i = (ACK_MESSAGE_ID_LENGHT - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (messageID >> i));
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
}
