package encryption;

/**
 * A class that stores the encryption details for a contact person.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class EncryptionPair {

	/**
	 * The prime that is used to compute the encryption key.
	 */
	private int prime;
	
	/**
	 * The generator that is used to compute the encryption key.
	 */
	private int generator;
	
	/**
	 * The 'localHalfKey' which is sent with an <code>EncryptedMessage</code>. 
	 * This is simply the value of (generator^secretInteger) % prime.
	 */
	private int localHalfKey;
	
	/**
	 * The 'remoteHalfKey'. The localHalfKey of the contact person.
	 */
	private int remoteHalfKey;
	
	/**
	 * Indicates if the <code>EncryptionPair</code> is acknowledged on the other side.
	 */
	private boolean acknowledged;
	
	/**
	 * Constructs a fresh <code>EncryptionPair</code> object with a prime (with generator)
	 * randomly chosen from the <code>DiffieHellman.PRIME_GENERATOR</code> array. This object
	 * is unacknowledged by default.
	 */
	public EncryptionPair() {
		int[] primeToGenerator = DiffieHellman.PRIME_GENERATOR[(int) (Math.random() * DiffieHellman.PRIME_GENERATOR.length)];
		this.prime = primeToGenerator[0];
		this.generator = primeToGenerator[1];
		this.acknowledged = false;
	}
	
	/**
	 * Constructs an <code>EncryptionPair</code> (most likely as response to a previously received
	 * <code>EncryptionPair</code> from the contact person) that holds the same info as the contact
	 * person's <code>EncryptionPair</code> for this client's user.
	 * @param prime the prime of this encryption pair
	 * @param generator the generator of this encryption pair
	 * @param secretInteger this client's secret integer for the contact person
	 * @param acknowledged true if this object is supposed to be an acknowledgement on the 
	 * previously sent <code>EncryptionPair</code> by the other side (contact person)
	 */
	public EncryptionPair(int prime, int generator, int secretInteger, boolean acknowledged) {
		this.prime = prime;
		this.generator = generator;
		this.localHalfKey = (int) (Math.pow(generator, secretInteger) % prime);
		this.acknowledged = acknowledged;
	}

	/**
	 * Computes and sets the localHalfKey (g^a mod p).
	 * @param secretInteger this client's user's secret integer for the contact person.
	 */
	public void setLocalHalfKey(int secretInteger) {
		this.localHalfKey = (int) (Math.pow(generator, secretInteger) % prime);
	}
	
	public int getPrime() {
		return prime;
	}
	
	public int getGenerator() {
		return generator;
	}

	public boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	
	public void setRemoteHalfKey(int remoteHalfKey) {
		this.remoteHalfKey = remoteHalfKey;
	}

	public int getLocalHalfKey() {
		return localHalfKey;
	}

	public int getRemoteHalfKey() {
		return remoteHalfKey;
	}
	
	/**
	 * For testing purposes. Prints this <code>EncryptionPair</code>'s details.
	 */
	public String toString() {
		return "----------------------------------"
				+ "\nENCRYPTIONPAIR:"
				+ "\nprime: " + prime
				+ "\ngenerator: " + generator 
				+ "\nlocalHalfKey: " + localHalfKey 
				+ "\nremoteHalfKey: " + remoteHalfKey
				+ "\n----------------------------------";
	}
}
