package fr.yncrea.scorpion.utils.security;

import android.util.Base64;

import java.security.KeyPair;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

class SecurityKey {
    private static final String AES_MODE_FOR_POST_API_23 = "AES/GCM/NoPadding";

    private final SecretKey secretKey;

    SecurityKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    String encrypt(String token) {
        if (token == null) return null;

        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            byte[] encrypted = cipher.doFinal(token.getBytes());
            return Base64.encodeToString(encrypted, Base64.URL_SAFE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Unable to encrypt Token
        return null;
    }

    String decrypt(String encryptedToken) {
        if (encryptedToken == null) return null;

        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            byte[] decoded = Base64.decode(encryptedToken, Base64.URL_SAFE);
            byte[] original = cipher.doFinal(decoded);
            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Unable to decrypt encrypted Token
        return null;
    }

    private Cipher getCipher(int mode) throws Exception {
        Cipher cipher;

        cipher = Cipher.getInstance(AES_MODE_FOR_POST_API_23);
        cipher.init(mode, secretKey, new GCMParameterSpec(128, AES_MODE_FOR_POST_API_23.getBytes(), 0, 12));
        return cipher;
    }
}