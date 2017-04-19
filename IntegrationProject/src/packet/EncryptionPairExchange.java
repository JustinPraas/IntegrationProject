package packet;

import java.util.ArrayList;

public class EncryptionPairExchange implements Payload {

	public static final int ENCRYPTION_PAIR_HEADER_LENGTH = 3;
	public static final int PRIME_LENGTH = 1;
	public static final int GENERATOR_LENGTH = 1;
	public static final int HALF_KEY_LENGTH = 1;
	
	private int prime;
	private int generator;
	private int localHalfKey;
	
	public EncryptionPairExchange(int prime, int generator, int localHalfKey) {
		this.prime = prime;
		this.generator = generator;
		this.localHalfKey = localHalfKey;
	}
	
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		resultList.add((byte) prime);
		resultList.add((byte) generator);
		resultList.add((byte) localHalfKey);
		
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

	public int getLocalHalfKey() {
		return localHalfKey;
	}
}
