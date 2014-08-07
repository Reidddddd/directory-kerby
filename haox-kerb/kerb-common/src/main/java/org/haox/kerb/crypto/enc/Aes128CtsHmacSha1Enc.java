package org.haox.kerb.crypto.enc;

import org.haox.kerb.crypto.Aes128;
import org.haox.kerb.crypto.enc.provider.Aes128Provider;
import org.haox.kerb.crypto.key.Aes128KeyMaker;
import org.haox.kerb.spec.type.common.CheckSumType;
import org.haox.kerb.spec.type.common.EncryptionType;

public class Aes128CtsHmacSha1Enc extends AesCtsHmacSha1Enc {

    public Aes128CtsHmacSha1Enc() {
        super(new Aes128Provider(), null);
        keyMaker(new Aes128KeyMaker(this));
    }

    public EncryptionType eType() {
        return EncryptionType.AES128_CTS_HMAC_SHA1_96;
    }

    public int confounderSize() {
        return blockSize();
    }

    public CheckSumType checksumType() {
        return CheckSumType.HMAC_SHA1_96_AES128;
    }

    public int checksumSize() {
        return Aes128.getChecksumLength();
    }

    public int blockSize() {
        return 16;
    }

    public int keySize() {
        return 16; // bytes
    }

    // Override default, because our decrypted data does not return confounder
    // Should eventually get rid of EncType.decryptedData and
    // EncryptedData.decryptedData altogether
    public byte[] decryptedData(byte[] data) {
        return data;
    }
}
