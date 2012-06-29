package utilities.security;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author FiruzzZ
 */
public class MD5 {

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @see #createChecksum(java.io.File)
     */
    public static byte[] createChecksum(String filename) throws FileNotFoundException, IOException {
        return createChecksum(new File(filename));
    }

    /**
     *
     * @param file
     * @return the array of bytes for the resulting hash value.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] createChecksum(File file) throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        MessageDigest complete = null;
        try {
            complete = MessageDigest.getInstance("MD5");
            is = new DigestInputStream(is, complete);
            int numRead;
            do {
                numRead = is.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
        } catch (NoSuchAlgorithmException ex) {
            //can't happend!
        } finally {
            is.close();
        }
        return complete.digest();
    }

    /**
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @see #getMD5Checksum(java.io.File)
     */
    public static String getMD5Checksum(String filename) throws FileNotFoundException, IOException {
        return getMD5Checksum(new File(filename));
    }

    public static String getMD5Checksum(File file) throws FileNotFoundException, IOException {
        byte[] b = createChecksum(file);
        StringBuilder result = new StringBuilder(b.length);
        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}