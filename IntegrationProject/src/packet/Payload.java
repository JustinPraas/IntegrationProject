package packet;

public interface Payload {
	
	public static final int PULSE = 0;
	public static final int PLAIN_MESSAGE = 1;
	public static final int ACKNOWLEDGEMENT = 2;
	public static final int ENCRYPTION_PAIR = 3;
	public static final int ENCRYPTED_MESSAGE = 4;
	public static final int FILE_MESSAGE = 5;
	
	public byte[] getPayloadData();

}
