package uk.gov.digital.ho.egar.files.utils;

import java.net.URI;
import java.util.UUID;


/**
 * The uri location utilities provide URI's for the provided parameters.
 * These can be used to construct redirection responses
 */
public interface UriLocationUtilities {

    /**
     * Gets the Files URI from the provided files id
     * @param filesUuid the files uuid.
     * @return The Files URI
     * @throws RuntimeException When unable to construct a valid URI
     */
    URI getFilesUri(final UUID filesUuid);

}
