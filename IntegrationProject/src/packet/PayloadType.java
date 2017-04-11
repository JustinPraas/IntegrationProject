package packet;

public enum PayloadType {
	
	PULSE(0),
	ENCRYPTED_MESSAGE(1),
	ACKNOWLEDGEMENT(2),
	ENCRYPTION_PAIR(3);
	
	public final int type;
	
	PayloadType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
}
