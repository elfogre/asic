package no.difi.commons.asic.util;

import no.difi.commons.asic.lang.MimeTypeException;
import no.difi.commons.asic.model.MimeType;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author erlend
 */
public class MimeTypeUtil {

    private static FileNameMap fileNameMap = URLConnection.getFileNameMap();

    public static MimeType detect(String filename) throws MimeTypeException {
        String filenameLower = filename.toLowerCase();

        if (filenameLower.endsWith(".xml"))
            return MimeType.APPLICATION_XML;
        else if (filenameLower.endsWith(".asice"))
            return MimeType.APPLICATION_ASICE;

        try {
            // Use Files to find content type
            String mimeType = Files.probeContentType(Paths.get(filename));

            // Use URLConnection to find content type
            if (mimeType == null)
                mimeType = fileNameMap.getContentTypeFor(filename);

            // Throw exception if content type is not detected
            if (mimeType == null)
                throw new MimeTypeException(String.format("Unable to determine MIME type of %s", filename));

            return MimeType.of(mimeType);
        } catch (IOException e) {
            throw new MimeTypeException(e.getMessage(), e);
        }
    }
}