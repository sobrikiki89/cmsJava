package app.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Used to handle encryption of passwords..
 */
public class MD5Utils {

	/**
	 * return a new random salt value.
	 */
	public static String getNewSaltValue() {

		char[] chars = new char[10];
		for (int i = 0; i < chars.length; i++) {
			int bv = (int) ('a' + (Math.random() * ((double) ('z' - 'a'))));
			chars[i] = (char) bv;
		}
		return new String(chars);
	}

	/**
	 * Uses MD5 encryption to generate a password to be saved in the DB.
	 * 
	 * @param password
	 * @return an encrypted version of the password
	 */
	public static String encrypt(String salt, String password) {

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			String source = password + salt;
			byte[] input = source.getBytes();
			// dp("input "+input.length+" inputString["+new String(input)+"]");

			byte[] result = md.digest(input);
			// dp("result"+result.length+" resultString["+ new
			// String(result)+"]");

			// we need to convert some of the bytes... the reason is that
			// the jdbc driver takes the String and persists it to a DB, and
			// some of those characters don't get into the database correctly??

			// the workaround is to convert some interesting bytes to more
			// normal bytes in the standard character set.
			for (int i = 0; i < result.length; i++) {

				result[i] = makeSafeChar(result[i]);
			}

			String s = new String(result);
			// dp("["+s+"]");
			return s;

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static byte makeSafeChar(int inChar) {
		if (inChar < -174) {
			// dp(" ["+inChar+"]");
			return (byte) 32; // space
		} else if (inChar < -100) {
			return (byte) (224 + inChar);

		} else if (inChar < -26) {
			return (byte) (148 + inChar);

		} else if (inChar < 48) {
			return (byte) (74 + inChar);// ((int)result[i] + 160);

		} else if (inChar < 122) {
			// in our valid range of characters...
			// result[i] = (byte)32;
			return (byte) inChar;
		} else {
			return (byte) 32;
		}
	}

	public static String encrypt(String password) {
		return encrypt(MD5Utils.getNewSaltValue(), password);
	}
}
