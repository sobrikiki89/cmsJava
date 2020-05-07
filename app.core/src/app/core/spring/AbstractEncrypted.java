package app.core.spring;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.apache.commons.codec.binary.Base64;

import app.core.crypto.SecretKeyCipher;
import app.core.crypto.impl.JdkDesCipher;

public abstract class AbstractEncrypted {

	protected static final String PREFIX = "crypt:";

	protected static final String ENCODING = "UTF-8";

	protected static final SecretKeyCipher cipher;

	static {
		try {
			JdkDesCipher jdkCipher = new JdkDesCipher();
			jdkCipher.setKeyMaterial("12A03FF999B87C7C");
			jdkCipher.setTransform("/CBC/PKCS5Padding");
			jdkCipher.setInitializationVector("44A038AFE9B0CC7C");
			cipher = jdkCipher;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * @param args
	 *            1st argument is SaltText , 2nd argument is plainText.
	 * @throws Exception
	 */
	public static String encode(String[] args) throws Exception {
		final String saltText = args[0];
		final String plainText = args[1];

		return encode(saltText, plainText);

	}

	/**
	 * 
	 * @param saltText
	 *            SaltText
	 * @param plainText
	 *            plainText
	 * @return
	 * @throws Exception
	 */
	public static String encode(final String saltText, final String plainText) throws Exception {

		final String cipherText = encrypt(saltText, plainText);

		final String encodedText = PREFIX + saltText + "::" + cipherText;
		return encodedText;

	}

	/**
	 * 
	 * @param saltText
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(final String saltText, final String plainText) throws UnsupportedEncodingException,
			GeneralSecurityException {
		final byte[] salt = saltText.getBytes(ENCODING);
		final byte[] plain = plainText.getBytes(ENCODING);

		for (int i = 0; i < salt.length; i++) {
			if (i >= plain.length) {
				break;
			}
			plain[i] = (byte) (salt[i] ^ plain[i]);
		}

		byte[] encrypted = cipher.encrypt(plain);

		final String cipherText = new String(Base64.encodeBase64(encrypted), ENCODING);
		return cipherText;
	}

	/**
	 * 
	 * @param text
	 * @param salt
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws GeneralSecurityException
	 */
	public static String decrypt(String text, String salt) throws UnsupportedEncodingException,
			GeneralSecurityException {
		byte[] encrypted = Base64.decodeBase64(text.getBytes(ENCODING));
		byte[] decrypted = cipher.decrypt(encrypted);
		byte[] saltBytes = salt.getBytes(ENCODING);

		for (int i = 0; i < saltBytes.length; i++) {
			if (i >= decrypted.length) {
				break;
			}
			decrypted[i] = ((byte) (saltBytes[i] ^ decrypted[i]));
		}

		return new String(decrypted, ENCODING);
	}

}
