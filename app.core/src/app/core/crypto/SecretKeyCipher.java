package app.core.crypto;

import java.security.GeneralSecurityException;

public interface SecretKeyCipher {

	byte[] encrypt(byte[] data) throws GeneralSecurityException;

	byte[] decrypt(byte[] data) throws GeneralSecurityException;

}
