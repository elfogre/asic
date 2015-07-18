package no.difi.asic;

import com.sun.xml.bind.api.JAXBRIContext;
import org.etsi.uri._2918.v1_1.ASiCManifestType;
import org.etsi.uri._2918.v1_1.DataObjectReferenceType;
import org.etsi.uri._2918.v1_1.ObjectFactory;
import org.etsi.uri._2918.v1_1.SigReferenceType;
import org.w3._2000._09.xmldsig_.DigestMethodType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

class AsicManifest {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static JAXBContext jaxbContext; // Thread safe

    private MessageDigestAlgorithm messageDigestAlgorithm;

    private ASiCManifestType ASiCManifestType = new ASiCManifestType();

    public AsicManifest(MessageDigestAlgorithm messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;

        try {
            // Creating the JAXBContext is heavy lifting, so do it only once.
            if (jaxbContext == null)
                jaxbContext = JAXBRIContext.newInstance(ASiCManifestType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(String.format("Unable to create JAXBContext: %s ", e.getMessage()), e);
        }
    }

    /**
     *
     * @param filename Filename in container
     * @param mimeType Content type of content
     * @param digest Message digest for content
     */
    public void add(String filename, String mimeType, byte[] digest) {
        DataObjectReferenceType dataObject = new DataObjectReferenceType();
        dataObject.setURI(filename);
        dataObject.setMimeType(mimeType);
        dataObject.setDigestValue(digest);

        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(messageDigestAlgorithm.getUri());
        dataObject.setDigestMethod(digestMethodType);

        ASiCManifestType.getDataObjectReference().add(dataObject);
    }

    public void setSignature(String filename, String mimeType) {
        SigReferenceType sigReferenceType = new SigReferenceType();
        sigReferenceType.setURI(filename);
        sigReferenceType.setMimeType(mimeType);
        ASiCManifestType.setSigReference(sigReferenceType);
    }

    public ASiCManifestType getASiCManifestType() {
        return ASiCManifestType;
    }

    public byte[] toBytes() {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            JAXBElement<ASiCManifestType> jaxbRootElement = objectFactory.createASiCManifest(ASiCManifestType);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(jaxbRootElement, baos);
            return baos.toByteArray();
        } catch (JAXBException e) {
            throw new IllegalStateException("Unable to marshall the ASiCManifest into string output", e);
        }
    }

}
