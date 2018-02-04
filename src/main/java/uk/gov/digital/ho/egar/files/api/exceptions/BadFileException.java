package uk.gov.digital.ho.egar.files.api.exceptions;

/**
 * @author localuser
 *
 */
public class BadFileException extends BadRequestFilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public BadFileException(final String message)
	{
		super(message);
	}
	
}
