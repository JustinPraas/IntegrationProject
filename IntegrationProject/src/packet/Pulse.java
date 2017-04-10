package packet;

import java.util.ArrayList;

public class Pulse implements Payload {
	
	private int senderID;
	private String name;

	public Pulse(int senderID, String name) {
		this.senderID = senderID;
		this.name = name;	
	}

	@Override
	public byte[] getPayload() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Sender ID to binary
		resultList.add((byte) (senderID >> 24));
		resultList.add((byte) (senderID >> 16));
		resultList.add((byte) (senderID >> 8));
		resultList.add((byte) senderID);
		
		// Name to binary
		for (int i = 0; i < name.length(); i++) {
			resultList.add((byte)name.charAt(i));
		}
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;		
	}
}
