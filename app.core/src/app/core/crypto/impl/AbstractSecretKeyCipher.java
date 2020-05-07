package app.core.crypto.impl;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import app.core.crypto.SecretKeyCipher;

public abstract class AbstractSecretKeyCipher implements SecretKeyCipher {

	public static final String DEFAULT_TRANSFORM = "/ECB/NoPadding";

	private String transform = DEFAULT_TRANSFORM;

	public String getTransform() {
		return transform;
	}

	public void setTransform(final String transform) {
		this.transform = transform == null ? DEFAULT_TRANSFORM : transform;
	}

	public byte[] encrypt(final byte[] data) throws GeneralSecurityException {
		final String currentTransform = getTransform();
		final Cipher cipher = getCipher(currentTransform);
		final SecretKey secretKey = getSecretKey();
		final AlgorithmParameterSpec params = getAlgorithmParameterSpec();
		if (params == null) {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
		}
		return cipher.doFinal(data);
	}

	public byte[] decrypt(final byte[] data) throws GeneralSecurityException {
		final String transform = getTransform();
		final Cipher cipher = getCipher(transform);
		final SecretKey secretKey = getSecretKey();
		final AlgorithmParameterSpec params = getAlgorithmParameterSpec();
		if (params == null) {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
		}
		return cipher.doFinal(data);
	}

	protected abstract Cipher getCipher(final String transform) throws GeneralSecurityException;

	protected abstract SecretKey getSecretKey() throws GeneralSecurityException;

	protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
		return null;
	}
}
