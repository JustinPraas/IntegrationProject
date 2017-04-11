package packet;

import java.util.ArrayList;

public class Acknowledgement implements Payload {
	
	private int messageID;

	public Acknowledgement(int messageSeq) {
		this.messageID = messageSeq;
	}

	@Override
	public byte[] getPayload() {
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
}
