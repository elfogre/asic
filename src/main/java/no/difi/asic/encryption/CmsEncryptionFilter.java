package no.difi.asic.encryption;

import no.difi.asic.api.EncryptionFilter;
import no.difi.asic.config.ValueWrapper;
import no.difi.asic.lang.AsicExcepion;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author erlend
 */
public class CmsEncryptionFilter extends CmsCommons implements EncryptionFilter {

    @Override
    public OutputStream createFilter(OutputStream outputStream, ValueWrapper algorithm, List<X509Certificate> certificates)
            throws IOException, AsicExcepion {
        try {
            // Create envelope data
            CMSEnvelopedDataStreamGenerator streamGenerator = new CMSEnvelopedDataStreamGenerator();
            for (X509Certificate certificate : certificates)
                streamGenerator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(certificate));

            // Create encryptor
            OutputEncryptor outputEncryptor = new JceCMSContentEncryptorBuilder(algorithm.getOid())
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build();

            // Return OutputStream for use
            return streamGenerator.open(outputStream, outputEncryptor);
        } catch (CertificateEncodingException | CMSException e) {
            throw new AsicExcepion(e.getMessage(), e);
        }
    }
}
