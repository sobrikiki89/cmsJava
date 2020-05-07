package app.core.spring;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.BeanInitializationException;

public class EncryptedPropertyUtils extends AbstractEncrypted {

	/**
	 * 
	 * 
	 * @param args
	 *            1st argument is SaltText , 2nd argument is plainText.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String encodedText = encode(args);
		System.out.println(encodedText);
	}

	/**
	 * 
	 * @param originalValue
	 * @return
	 */
	public static String convertPropertyValue(final String originalValue) {
		if (!originalValue.startsWith(PREFIX)) {
			return originalValue;
		}
		int i = originalValue.indexOf("::", PREFIX.length());
		String salt = originalValue.substring(PREFIX.length(), i);
		String text = originalValue.substring(i + 2);
		try {
			return decrypt(text, salt);
		} catch (UnsupportedEncodingException e) {
			throw new BeanInitializationException("Cannot convert property.", e);
		} catch (GeneralSecurityException e) {
			throw new BeanInitializationException("Cannot convert property.", e);
		}
	}
}
