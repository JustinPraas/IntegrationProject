package packet;

import java.util.ArrayList;

public class Acknowledgement implements Payload {
	
	private int senderID;
	private int receiverID;
	private int messageSeq;

	public Acknowledgement(int senderID, int receiverID, int messageSeq) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.messageSeq = messageSeq;
	}

	@Override
	public byte[] getPayload() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Sender ID to binary
		resultList.add((byte) (senderID >> 24));
		resultList.add((byte) (senderID >> 16));
		resultList.add((byte) (senderID >> 8));
		resultList.add((byte) senderID);
		
		// Receiver ID to binary
		resultList.add((byte) (receiverID >> 24));
		resultList.add((byte) (receiverID >> 16));
		resultList.add((byte) (receiverID >> 8));
		resultList.add((byte) receiverID);
		
		// Message sequence number to binary
		resultList.add((byte) (messageSeq >> 8));
		resultList.add((byte) messageSeq);
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}
}
