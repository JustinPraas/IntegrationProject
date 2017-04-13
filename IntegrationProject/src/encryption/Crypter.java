package encryption;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Crypter {
	
	public static final String INIT_VECTOR = "0123456789123456";
	
	public static String encrypt(String key, String value) {
		System.out.println("Key at encrypt: " + key);
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    public static String decrypt(String key, String encrypted) {
    	System.out.println("Key at decrypt: " + key);
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return encrypted;
    }
    
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
    
//    public static void main(String[] args) {
//    	int prime = 19;
//    	int generator = 2;
//		EncryptionPair Justin = new EncryptionPair(prime, generator, 11, true);
//		EncryptionPair Tim = new EncryptionPair(prime, generator, 18, true);
//
//		Tim.setRemoteHalfKey((int) (Math.pow(generator, 11) % prime));
//		Justin.setRemoteHalfKey((int) (Math.pow(generator, 18) % prime));
//		
//		String plainText = "Person Justin is se";
//		String encrypted = Crypter.encrypt(Crypter.getKey(Justin, 11), plainText);
//		System.out.println(encrypted);
//
//		String decrypted = Crypter.decrypt(Crypter.getKey(Tim, 18), encrypted);
//		System.out.println(decrypted);
//	}
}
