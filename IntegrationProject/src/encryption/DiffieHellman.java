package encryption;

import model.Person;
import model.Session;

public class DiffieHellman {
	
	// Prime number mapped to their primitive roots {prime, primitive root} (p, g): 
	public static final int[][] PRIME_TO_PRIMITIVE_ROOT = 
			new int[][]{{11, 2}, {13, 2}, {17, 3}, {19, 2}, {23, 5}, {29, 2}};
			
	public static int produceSecretKey(int prime) {
		return (int) (5 + Math.random() * (prime - 5));
	}

	public static int getEncryptionKey(Session session, Person receiver) {
		EncryptionPair ep = session.getKnownPersons().get(receiver.getID()).getPrivateChatPair();
		return (int) (Math.pow(ep.getGenerator(), session.getSecretKeysForPerson().get(receiver.getID())) % ep.getPrime());
	}
}
