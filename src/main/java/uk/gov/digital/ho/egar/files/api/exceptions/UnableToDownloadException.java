package uk.gov.digital.ho.egar.files.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author localuser
 *
 */
@ResponseStatus(value= HttpStatus.BAD_GATEWAY)
public class UnableToDownloadException extends FilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public UnableToDownloadException(final String message)
	{
		super(message);
	}

	public UnableToDownloadException(final String message, final Exception e)
	{
		super(message,e );
	}


}
