package app.core.crypto.impl;

import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import app.core.crypto.PinBlock;

public class JdkDesCipher extends AbstractSecretKeyCipher {

	private static final String ALGORITHM = "DES";

	private byte[] keyMaterial;

	private byte[] iv;

	public JdkDesCipher() {
		super();
	}

	private byte[] getKeyMaterial() {
		return keyMaterial;
	}

	public void setKeyMaterial(final String keyMaterial) {
		if (keyMaterial == null) {
			throw new IllegalArgumentException("keyMaterial cannot be null.");
		}
		this.keyMaterial = new PinBlock(keyMaterial).toByteArray();
	}

	public void setInitializationVector(final String iv) {
		if (iv == null) {
			this.iv = null;
		} else {
			this.iv = new PinBlock(iv).toByteArray();
		}
	}

	@Override
	protected Cipher getCipher(final String transform) throws GeneralSecurityException {
		return Cipher.getInstance(ALGORITHM + transform);
	}

	@Override
	protected SecretKey getSecretKey() throws GeneralSecurityException {
		final DESKeySpec keySpec = new DESKeySpec(getKeyMaterial());
		return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
	}

	@Override
	protected AlgorithmParameterSpec getAlgorithmParameterSpec() {
		IvParameterSpec spec = null;
		if (iv != null) {
			spec = new IvParameterSpec(iv);
		}
		return spec;
	}
}
