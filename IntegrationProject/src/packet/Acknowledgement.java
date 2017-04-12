package packet;

import java.util.ArrayList;

public class Acknowledgement implements Payload {
	
	public static final int ACK_PAYLOAD_LENGHT = 2;

	private int messageID;

	public Acknowledgement(int messageID) {
		this.messageID = messageID;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Message sequence number to binary
		resultList.add((byte) (messageID >> 8));
		resultList.add((byte) messageID);
		
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
