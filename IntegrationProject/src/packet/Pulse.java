package packet;

import java.util.ArrayList;

public class Pulse implements Payload {
	
	public static final int NAME_LENGTH_LENGTH = 1;
	public static final int LEVEL_LENGTH = 1;
	
	private int nameLength;
	private String name;
	private int level;

	public Pulse(int nameLength, int level, String name) {
		this.nameLength = nameLength;
		this.name = name;
		this.level = level;
	}

	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		resultList.add((byte) nameLength);
		resultList.add((byte) level); 
		
		// Name to binary
		for (int i = 0; i < name.length(); i++) {
			resultList.add((byte) name.charAt(i));
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
	
	public int getNameLength() {
		return nameLength;
	}
	
	public int getLevel() {
		return level;
	}
}
