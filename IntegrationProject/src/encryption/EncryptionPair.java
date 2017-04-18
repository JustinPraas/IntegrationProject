package encryption;

public class EncryptionPair {

	private int prime;
	private int generator;
	private int localHalfKey;
	private int remoteHalfKey;
	private boolean acknowledged;
	
	public EncryptionPair() {
		int[] primeToGenerator = DiffieHellman.PRIME_TO_PRIMITIVE_ROOT[(int) (Math.random() * DiffieHellman.PRIME_TO_PRIMITIVE_ROOT.length)];
		this.prime = primeToGenerator[0];
		this.generator = primeToGenerator[1];
		this.acknowledged = false;
	}
	
	public EncryptionPair(int prime, int generator, int secretInteger, boolean acknowledged) {
		this.prime = prime;
		this.generator = generator;
		this.localHalfKey = (int) (Math.pow(generator, secretInteger) % prime);
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
	
	public void setRemoteHalfKey(int remoteHalfKey) {
		this.remoteHalfKey = remoteHalfKey;
	}

	public int getLocalHalfKey() {
		return localHalfKey;
	}

	public void setLocalHalfKey(int secretInteger) {
		this.localHalfKey = (int) (Math.pow(generator, secretInteger) % prime);
	}

	public int getRemoteHalfKey() {
		return remoteHalfKey;
	}
	
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
