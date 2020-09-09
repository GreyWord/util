package auto.util.encry;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author ZhangXu on 2017/10/20.
 */

public enum Encryption {
	sha2 ("SHA-256"),
	sha1("SHA-1"),
	md5("MD5");
    String type;
	Encryption(String type){
		this.type=type;
	}
	public String encrypte(String source){
		return encrypte(source.getBytes());
	}
	public String encrypte(byte[] source){
        MessageDigest digest = getDigest(type);
        digest.update(source);
		return toString(digest);
	}
    public byte[] encryptebytes(byte[] source){
        MessageDigest digest = getDigest(type);
        byte[] bs = digest.digest(source);
        return bs;
    }
    public MessageDigest getInstance(){
        return getDigest(type);
    }
    private static MessageDigest getDigest(String type){
        MessageDigest digest = null;
        try { digest = MessageDigest.getInstance(type);
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return digest;
    }
    public static String toString(MessageDigest digest){
        byte[] bs = digest.digest();
        String md5 = new BigInteger(1,bs).toString(16);
        return String.format("%32s", md5).replace(' ', '0');
    }
}