package uk.gov.digital.ho.egar.files.utils.impl;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.files.api.RestConstants;
import uk.gov.digital.ho.egar.files.utils.UriLocationUtilities;

import java.net.URI;
import java.util.UUID;


/**
 * The uri location utilities provide URI's for the provided parameters.
 * Utilises the service name and identifiers found in the files api.
 * These can be used to construct redirection responses
 */
@Component
public class UriLocationUtilitiesImpl implements UriLocationUtilities {

	public static class URIFormatException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public URIFormatException(Exception cause) {super(cause);}
	}

	private URI buildUri(String uriPath)  {
			try {
				return new URI(uriPath);
			} catch (Exception e) {
				throw new URIFormatException(e);
			}
		}

    @Override
    public URI getFilesUri(UUID fileUuid)  {
        return buildUri(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR);
    }
}

