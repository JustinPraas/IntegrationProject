package encryption;

public class EncryptionPair {

	private int prime;
	private int generator;
	private boolean acknowledged;
	
	public EncryptionPair(boolean acknowledged) {
		int[] primeToGenerator = DiffieHellman.PRIME_TO_PRIMITIVE_ROOT[(int) (Math.random() * DiffieHellman.PRIME_TO_PRIMITIVE_ROOT.length)];
		this.prime = primeToGenerator[0];
		this.generator = primeToGenerator[1];
		this.acknowledged = acknowledged;
	}
	
	public EncryptionPair(int prime, int generator, boolean acknowledged) {
		this.prime = prime;
		this.generator = generator;
		this.acknowledged = acknowledged;
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
	

}
