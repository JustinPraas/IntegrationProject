package packet;

import java.util.ArrayList;

public class Pulse implements Payload {
	
	private String name;

	public Pulse(String name) {
		this.name = name;	
	}

	@Override
	public byte[] getPayload() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
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

	public String getName() {
		return name;
	}
}
