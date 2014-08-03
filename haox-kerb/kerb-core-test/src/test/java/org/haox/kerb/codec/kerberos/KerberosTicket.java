package org.haox.kerb.codec.kerberos;

import org.bouncycastle.util.Arrays;
import org.haox.kerb.codec.KrbCodec;
import org.haox.kerb.crypto2.EncryptionHandler;
import org.haox.kerb.spec.KrbException;
import org.haox.kerb.spec.type.ap.ApOptions;
import org.haox.kerb.spec.type.common.AuthorizationData;
import org.haox.kerb.spec.type.common.EncryptedData;
import org.haox.kerb.spec.type.common.EncryptionKey;
import org.haox.kerb.spec.type.common.KeyUsage;
import org.haox.kerb.spec.type.ticket.EncTicketPart;
import org.haox.kerb.spec.type.ticket.Ticket;

public class KerberosTicket {
    private String serverPrincipalName;
    private String serverRealm;
    private Ticket ticket;

    public KerberosTicket(Ticket ticket, ApOptions apOptions, EncryptionKey key)
            throws Exception {
        this.ticket = ticket;

        byte[] decrypted = EncryptionHandler.decrypt(
                ticket.getEncryptedEncPart(), key, KeyUsage.KDC_REP_TICKET);

        EncTicketPart encPart = KrbCodec.decode(decrypted, EncTicketPart.class);
        ticket.setEncPart(encPart);

        /**
         * Also test encryption by the way
         */
        EncryptedData encrypted = EncryptionHandler.encrypt(
                decrypted, key, KeyUsage.KDC_REP_TICKET);

        byte[] decrypted2 = EncryptionHandler.decrypt(
                encrypted, key, KeyUsage.KDC_REP_TICKET);
        if (!Arrays.areEqual(decrypted, decrypted2)) {
            throw new KrbException("Encryption checking failed after decryption");
        }
    }

    public String getUserPrincipalName() throws KrbException {
        return ticket.getEncPart().getCname().getName();
    }

    public String getUserRealm() throws KrbException {
        return ticket.getEncPart().getCrealm();
    }

    public String getServerPrincipalName() throws KrbException {
        return ticket.getSname().getName();
    }

    public String getServerRealm() throws KrbException {
        return ticket.getRealm();
    }

    public AuthorizationData getAuthorizationData() throws KrbException {
        return ticket.getEncPart().getAuthorizationData();
    }

    public Ticket getTicket() {
        return ticket;
    }
}
