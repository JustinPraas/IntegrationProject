package packet;

import java.util.ArrayList;

/**
 * A class that stores properties of an <code>EncryptionPairExchange</code> payload-type.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class EncryptionPairExchange implements Payload {

	/**
	 * The total header length (bytes) of the encryption pair exchange (excluding the cipher length).
	 */
	public static final int ENCRYPTION_PAIR_HEADER_LENGTH = 3;
	
	/**
	 * The length (bytes) of the prime field in the encryption pair exchange.
	 */
	public static final int PRIME_LENGTH = 1;
	
	/**
	 * The length (bytes) of the generator field in the encryption pair exchange.
	 */
	public static final int GENERATOR_LENGTH = 1;
	
	/**
	 * The length (bytes) of the halfway key field in the encryption pair exchange.
	 */
	public static final int HALF_KEY_LENGTH = 1;
	
	/**
	 * The prime that is used between the correspondents to encrypt/decrypt a message.
	 */
	private int prime;
	
	/**
	 * The generator that is used between the correspondents to encrypt/decrypt a message.
	 */
	private int generator;
	
	/**
	 * The localHalfKey of the sender that is used to encrypt/decrypt the message.
	 */
	private int localHalfKey;
	
	/**
	 * Constructs a encryption pair exchange <code>Payload</code>.
	 * @param prime the prime used to encrypt/decrypt a message
	 * @param generator the generator used to encrypt/decrypt a message
	 * @param localHalfKey the localHalfKey of the sender that is used to encrypt/decrypt a message
	 */
	public EncryptionPairExchange(int prime, int generator, int localHalfKey) {
		this.prime = prime;
		this.generator = generator;
		this.localHalfKey = localHalfKey;
	}
	
	/**
	 * Returns the byte array of this <code>EncryptedMessage</code> payload.
	 */
	@Override
	public byte[] getPayloadData() {
		ArrayList<Byte> resultList = new ArrayList<>();
		
		// Prime to binary
		for (int i = (PRIME_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (prime >> i));
		}
		
		// Generator to binary
		for (int i = (GENERATOR_LENGTH - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (generator >> i));
		}
		
		// LocalHalfKey to binary
		for (int i = (localHalfKey - 1) * 8; i >= 0; i -= 8) {
			resultList.add((byte) (localHalfKey >> i));
		}
		
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
