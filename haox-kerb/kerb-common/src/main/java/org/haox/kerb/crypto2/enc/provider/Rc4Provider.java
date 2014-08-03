package org.haox.kerb.crypto2.enc.provider;

import org.haox.kerb.spec.KrbException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class Rc4Provider extends AbstractEncryptProvider {

    public Rc4Provider() {
        super(1, 16, 16);
    }

    @Override
    public void encrypt(byte[] key, byte[] cipherState, byte[] data) throws KrbException {
        try {
            Cipher cipher = Cipher.getInstance("ARCFOUR");
            SecretKeySpec secretKey = new SecretKeySpec(key, "ARCFOUR");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] output = cipher.doFinal(data);
            System.arraycopy(output, 0, data, 0, output.length);
        } catch (GeneralSecurityException e) {
            KrbException ke = new KrbException(e.getMessage());
            ke.initCause(e);
            throw ke;
        }
    }

    @Override
    public void decrypt(byte[] key, byte[] cipherState, byte[] data) {

    }

    @Override
    public byte[] initState(byte[] key, int keyUsage) {
        return new byte[0];
    }
}
