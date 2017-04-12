package packet;

import java.util.ArrayList;

public class EncryptionPairExchange implements Payload {

	public static final int PRIME_LENGTH = 1;
	public static final int GENERATOR_LENGTH = 1;
	
	private int prime;
	private int generator;
	
	public EncryptionPairExchange(int prime, int generator) {
		this.prime = prime;
		this.generator = generator;
	}
	
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		resultList.add((byte) prime);
		resultList.add((byte) generator);
		
		// Convert ArrayList to byte[]
		byte[] result = new byte[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		
		return result;
	}

	public int getPrime() {
		return prime;
	}

	public int getGenerator() {
		return generator;
	}
}
