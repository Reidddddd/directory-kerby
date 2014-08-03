package org.haox.kerb.crypto2.enc;

import org.haox.kerb.crypto2.Aes128;
import org.haox.kerb.crypto2.Confounder;
import org.haox.kerb.crypto2.cksum.HashProvider;
import org.haox.kerb.crypto2.dk.AesDkCrypto;
import org.haox.kerb.crypto2.key.KeyMaker;
import org.haox.kerb.spec.KrbException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public abstract class AesCtsHmacSha1Enc extends AbstractEncryptionTypeHandler {
    private AesDkCrypto CRYPTO;

    public AesCtsHmacSha1Enc(EncryptProvider encProvider,
                             HashProvider hashProvider, KeyMaker keyMaker) {
        super(encProvider, hashProvider, keyMaker);

        CRYPTO = new AesDkCrypto(encProvider.keySize() * 8);
    }

    @Override
    public int paddingSize() {
        return 0;
    }

    @Override
    protected void encryptWith(byte[] workBuffer, int[] workLens,
                               byte[] key, byte[] iv, int usage) throws KrbException {
        int confounderLen = workLens[0];
        int checksumLen = workLens[1];
        int inputLen = workLens[2];
        int paddingLen = workLens[3];

        byte[] Ke, Ki, Kc;
        byte[] constant = new byte[5];
        constant[0] = (byte) ((usage>>24)&0xff);
        constant[1] = (byte) ((usage>>16)&0xff);
        constant[2] = (byte) ((usage>>8)&0xff);
        constant[3] = (byte) (usage&0xff);
        constant[4] = (byte) 0xaa;
        try {
            Ke = CRYPTO.dk(key, constant);
            constant[4] = (byte) 0x55;
            Ki = CRYPTO.dk(key, constant);
            constant[4] = (byte) 0x99;
            Kc = CRYPTO.dk(key, constant);
        } catch (GeneralSecurityException e) {
            KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }

        /**
         * Instead of E(Confounder | Checksum | Plaintext | Padding),
         * E(Confounder | Plaintext | Padding) | Checksum,
         * so need to adjust the workBuffer arrangement
         */

        byte[] tmpEnc = new byte[confounderLen + inputLen + paddingLen];
        // confounder
        byte[] confounder = Confounder.bytes(confounderLen);
        System.arraycopy(confounder, 0, tmpEnc, 0, confounderLen);

        // data
        System.arraycopy(workBuffer, confounderLen + checksumLen,
                tmpEnc, confounderLen, inputLen);

        // padding
        for (int i = confounderLen + inputLen; i < paddingLen; ++i) {
            tmpEnc[i] = 0;
        }

        // checksum
        byte[] checksum;
        try {
            checksum = getHmac(Ki, tmpEnc, checksumLen);
            encProvider().encrypt(Ke, iv, tmpEnc);
        } catch (GeneralSecurityException e) {
            KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }

        System.arraycopy(tmpEnc, 0, workBuffer, 0, tmpEnc.length);
        System.arraycopy(checksum, 0, workBuffer, tmpEnc.length, checksum.length);
    }

    public byte[] decrypt(byte[] cipher, byte[] key, byte[] ivec, int usage)
            throws KrbException {
        try {
            return Aes128.decrypt(key, usage, ivec, cipher, 0, cipher.length);
        } catch (GeneralSecurityException e) {
            KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }
    }

    protected byte[] getHmac(byte[] key, byte[] msg, int hashSize)
            throws GeneralSecurityException {

        SecretKey keyKi = new SecretKeySpec(key, "HMAC");
        Mac m = Mac.getInstance("HmacSHA1");
        m.init(keyKi);

        // generate hash
        byte[] hash = m.doFinal(msg);

        // truncate hash
        byte[] output = new byte[hashSize];
        System.arraycopy(hash, 0, output, 0, hashSize);
        return output;
    }
}
