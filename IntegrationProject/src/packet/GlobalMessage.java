package packet;

import java.util.ArrayList;

public class GlobalMessage implements Payload {

	public static final int MESSAGE_ID_LENGTH = 2;
	public static final int MESSAGE_LENGTH_LENGTH = 2;
	
	private int messageID;
	private int messageLength;
	private String message;
	
	public GlobalMessage(int messageID, int messageLength, String message) {
		this.messageID = messageID;
		this.messageLength = messageLength;
		this.message = message;
	}
	
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message sequence number to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		
		// Message length to binary
		resultList.add((byte) (messageLength >> 8));
		resultList.add((byte) messageLength);
		
		// message to binary
		for (int i = 0; i < message.length(); i++) {
			resultList.add((byte)message.charAt(i));
		}
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}
	
	
}
