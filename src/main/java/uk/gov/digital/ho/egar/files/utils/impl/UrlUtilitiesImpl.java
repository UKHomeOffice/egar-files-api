package uk.gov.digital.ho.egar.files.utils.impl;

import com.amazonaws.services.s3.AmazonS3URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.files.api.RestConstants;
import uk.gov.digital.ho.egar.files.client.impl.S3FileStorageClient;
import uk.gov.digital.ho.egar.files.utils.UriLocationUtilities;
import uk.gov.digital.ho.egar.files.utils.UrlUtilities;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.UUID;

@Component
public class UrlUtilitiesImpl implements UrlUtilities {

	private final Log logger = LogFactory.getLog(UrlUtilitiesImpl.class);


	public String urlDecodeKey(AmazonS3URI uri){
		String key = uri.getKey();
		return urlDecodeValue(key);
	}

	public String urlDecodeValue(String key){
		String urlDecodedKey = key;
		try {
			urlDecodedKey = URLDecoder.decode(urlDecodedKey, UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("Unable to decode '%s' with format '%s'", urlDecodedKey, UTF_8), e);
		}
		return urlDecodedKey;
	}
}

