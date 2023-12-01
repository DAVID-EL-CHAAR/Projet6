package com.payMyBuddy.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    @Test
    void testEncryptId() throws Exception {
        Long id = 12345L;
        String encryptedId = CryptoUtil.encryptId(id);
        assertNotNull(encryptedId);
        assertNotEquals(id.toString(), encryptedId);
    }

    @Test
    void testDecryptId() throws Exception {
        Long originalId = 12345L;
        String encryptedId = CryptoUtil.encryptId(originalId);
        Long decryptedId = CryptoUtil.decryptId(encryptedId);
        assertEquals(originalId, decryptedId);
    }

    @Test
    void testDecryptIdWithInvalidInput() {
        String invalidEncryptedId = "invalid";
        assertThrows(Exception.class, () -> {
            CryptoUtil.decryptId(invalidEncryptedId);
        });
    }
}
