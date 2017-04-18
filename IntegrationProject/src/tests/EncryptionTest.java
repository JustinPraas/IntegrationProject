package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import connection.TransportLayer;
import encryption.Crypter;
import encryption.DiffieHellman;
import encryption.EncryptionPair;

public class EncryptionTest {
	
	@Test
	public void encryptionDecryptionTest() {
		int[] randomPair = DiffieHellman.PRIME_TO_PRIMITIVE_ROOT[(int) Math.random() * DiffieHellman.PRIME_TO_PRIMITIVE_ROOT.length];
		int prime = randomPair[0];
		int generator = randomPair[1];
		
		int secretIntA = DiffieHellman.produceSecretKey(prime);
		int secretIntB = DiffieHellman.produceSecretKey(prime);
		
		EncryptionPair encryptionPairA = new EncryptionPair(prime, generator, secretIntA, true);
		EncryptionPair encryptionPairB = new EncryptionPair(prime, generator, secretIntB, true);
		
		encryptionPairA.setRemoteHalfKey(encryptionPairB.getLocalHalfKey());
		encryptionPairB.setRemoteHalfKey(encryptionPairA.getLocalHalfKey());
		
		String plainText = "Person A is sending this to B.";
		String cipher = Crypter.encrypt(Crypter.getKey(encryptionPairA, secretIntA), plainText);
		System.out.println(cipher);
		
		String decryptedText = Crypter.decrypt(Crypter.getKey(encryptionPairB, secretIntB), cipher);
		System.out.println(decryptedText);
		
		assertEquals(plainText, decryptedText);
	}

}