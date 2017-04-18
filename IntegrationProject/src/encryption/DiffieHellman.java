package encryption;

import model.Person;
import model.Session;

public class DiffieHellman {
	
	/**
	 * A (small) set of prime numbers linked to their generator.
	 * If this program were to be a widely distributed application, a set of higher
	 * primes and generators would be chosen. The small numbers are purely for simplicity.
	 */
	public static final int[][] PRIME_GENERATOR = 
			new int[][]{{11, 2}, {13, 2}, {17, 3}, {19, 2}, {23, 5}, {29, 2}};
	
	/**
	 * Produces a random secret key in the range of {5 ... (prime - 1)}.
	 * @param prime the corresponding prime
	 * @return secretKey the random secret key produced from the given prime
	 */
	public static int produceSecretKey(int prime) {
		return (int) (5 + Math.random() * (prime - 1)); //TODO TEST, changed prime-5 to prime-1
	}

	/**
	 * Gets the encryption key (most likely to be sent with an <code>EncryptedMessage</code> 
	 * payload. If 'a' is the secret key of preson A and g and p are the generator and the prime
	 * that are chosen between two contact persons, then this method returns g^a mod p.
	 * @param session the session from which the <code>EncryptionPair</code> 
	 * from the contact person is fetched
	 * @param receiver the contact person for which we use the encryption key
	 * @return encryptionKey the encryption key for any message to this contact person
	 */
	public static int getEncryptionKey(Session session, Person receiver) {
		EncryptionPair ep = session.getKnownPersons().get(receiver.getID()).getPrivateChatPair();
		return (int) (Math.pow(ep.getGenerator(), session.getSecretKeysForPerson().get(receiver.getID())) % ep.getPrime());
	}
}
