package encryption;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Crypter {
	
	// If this were to be a widely distributed application, a random IV would be sent with each message
	// We use a static IV for simplicity
	public static final String INIT_VECTOR = "0123456789123456";
	
	/**
	 * Encrypts a String using AES/CBC/PKCS5PADDING with the given key String.
	 * @param key the key to be used in the encryption process
	 * @param value the String to be encrypted
	 * @return cipherString the cipher that resulted from the encryption process
	 */
	public static String encrypt(String key, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            String cipherString = Base64.encodeBase64String(encrypted);

            return cipherString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

	/**
	 * Decrypts a String (encrypted value) using AES/CBC/PKCS5PADDING with
	 * the given key String.
	 * @param key the key to be used in the decryption process
	 * @param encryptedthe String to be decrypted
	 * @return originalString the original plain text of the message
	 */
    public static String decrypt(String key, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            String originalString = new String(original);
            
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encrypted;
    }
    
    /**
     * Gets the encryption/decryption key, specific for the contact person's 
     * <code>EncryptionPair</code> and the corresponding secretInteger. 
     * @param ep the <code>EncryptionPair</code> of the current contact person
     * @param secretInteger the secretInteger linked to the current contact person
     * @return resultKey.toString() the key to be used by the encryption/decryption method
     */
    public static String getKey(EncryptionPair ep, int secretInteger) {
    	BigInteger remoteHalfKey = new BigInteger(Integer.toString(ep.getRemoteHalfKey()));
    	BigInteger secretInt = new BigInteger(Integer.toString(secretInteger));
    	BigInteger prime = new BigInteger(Integer.toString(ep.getPrime()));
    	BigInteger bigKey = remoteHalfKey.modPow(secretInt, prime);
    	long key = bigKey.intValue();
    	StringBuilder resultKey = new StringBuilder();
    	resultKey.append(Long.toString(key));
    	
    	int length = resultKey.toString().length();
    	for (int i = 0; i < 16 - length; i++) {
    		resultKey.append("0");
    	}
    	
    	return resultKey.toString();
    }
}
