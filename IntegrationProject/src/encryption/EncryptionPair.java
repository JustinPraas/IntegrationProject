package encryption;

public class EncryptionPair {

	private int prime;
	private int generator;

	public EncryptionPair(int prime, int generator) {
		this.prime = prime;
		this.generator = generator;
	} 
	
	public EncryptionPair() {
		int[] primeToGenerator = DiffieHellman.PRIME_TO_PRIMITIVE_ROOT[(int) (Math.random() * DiffieHellman.PRIME_TO_PRIMITIVE_ROOT.length)];
		this.prime = primeToGenerator[0];
		this.generator = primeToGenerator[1];
	}
	
	public int getPrime() {
		return prime;
	}
	
	public int getGenerator() {
		return generator;
	}
	

}
