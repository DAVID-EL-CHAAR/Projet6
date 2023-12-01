package com.payMyBuddy.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {

    private static final String AES = "AES";
    private static byte[] key;

    // Générer une clé secrète (à faire une seule fois et à stocker en sécurité)
    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            key = secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptId(Long id) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(id.toString().getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static Long decryptId(String encryptedId) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedId));
        return Long.parseLong(new String(decrypted));
    }
}

