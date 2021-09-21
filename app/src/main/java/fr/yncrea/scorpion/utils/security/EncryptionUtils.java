package fr.yncrea.scorpion.utils.security;

import java.security.KeyStore;

public class EncryptionUtils {

    public static String encrypt(String token) {
        SecurityKey securityKey = getSecurityKey();
        return securityKey != null ? securityKey.encrypt(token) : null;
    }

    public static String decrypt(String token) {
        SecurityKey securityKey = getSecurityKey();
        return securityKey != null ? securityKey.decrypt(token) : null;
    }

    private static SecurityKey getSecurityKey() {
        return EncryptionKeyGenerator.generateSecretKey(getKeyStore());
    }

    private static KeyStore getKeyStore() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    public static void clear() {
        KeyStore keyStore = getKeyStore();
        try {
            if (keyStore.containsAlias(EncryptionKeyGenerator.KEY_ALIAS)) {
                keyStore.deleteEntry(EncryptionKeyGenerator.KEY_ALIAS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}