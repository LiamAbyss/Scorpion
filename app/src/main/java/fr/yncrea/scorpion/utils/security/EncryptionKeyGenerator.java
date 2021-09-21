package fr.yncrea.scorpion.utils.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;

public class EncryptionKeyGenerator {
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String KEY_ALIAS = "KEY_ALIAS";

    static SecurityKey generateSecretKey(KeyStore keyStore) {
        try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator =
                        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(
                        KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build());
                return new SecurityKey(keyGenerator.generateKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            final KeyStore.SecretKeyEntry entry =
                    (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            return new SecurityKey(entry.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}