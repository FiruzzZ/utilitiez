package utilities.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author FiruzzZ
 */
public class PasswordHash {

    /**
     * 
     * @param password ..
     * @param iterations higher than 75.000 ( &gt; 100.000 for server side!)
     * @param salt ..
     * @param keyLength in <b>bits</b> long
     * @return byte produced by SecretKeyRandomFactory
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public static byte[] getPBKDF2WithHmacSHA512(String password, int iterations, byte[] salt, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return hash;
    }

    /**
     * Genera un salt aleatorio
     * @param saltByteSize ..
     * @return byte of random generated salt
     * @throws NoSuchAlgorithmException  ..
     */
    public static byte[] getSalt(int saltByteSize) throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[saltByteSize];
        sr.nextBytes(salt);
        return salt;
    }

    public static boolean validatePBKDF2WithHmacSHA512(String candidate, byte[] hash, int iterations, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(candidate.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    public static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private PasswordHash() {
    }
}
