package packet;

import java.util.ArrayList;

/**
 * A class that stores properties of a pulse payload-type.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Pulse implements Payload {
	
	/**
	 * The total pulse header length (bytes).
	 */
	public static final int PULSE_HEADER_LENGTH = 2;
	
	/**
	 * The length (bytes) of the nameLength field in the payload.
	 */
	public static final int NAME_LENGTH_LENGTH = 1;
	
	/**
	 * The length of the level field in the payload.
	 */
	public static final int LEVEL_LENGTH = 1;
	
	/**
	 * The length of the name that comes with this pulse <code>Payload</code>.
	 */
	private int nameLength;
	
	/**
	 * The name that comes with this pulse <code>Payload</code>.
	 */
	private String name;
	
	/**
	 * The level that is linked to the sender that comes with this pulse <code>Payload</code>.
	 */
	private int level;

	/**
	 * Constructs a pulse <code>Payload</code>.
	 * @param nameLength the length of the name that comes with this pulse
	 * @param level the level of the sender that comes with this pulse
	 * @param name the name of the sender that comes with this pulse
	 */
	public Pulse(int nameLength, int level, String name) {
		this.nameLength = nameLength;
		this.name = name;
		this.level = level;
	}

	/**
	 * Returns the byte array of this <code>Pulse</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// NameLength to binary
		for (int i = (NAME_LENGTH_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (nameLength >> i));
		}
		
		// Level to binary
		for (int i = (LEVEL_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (level >> i));
		}
		
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
