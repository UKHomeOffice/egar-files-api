package uk.gov.digital.ho.egar.files.api.exceptions;

import uk.gov.digital.ho.egar.shared.util.exceptions.NoCallStackException;

/**
 * A base exception type that does not pick uo the stack trace.
 *
 */
public class FilesApiException extends NoCallStackException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FilesApiException() {
		this(null,null);
	}

	public FilesApiException(String message) {
		this(message,null);
	}

	public FilesApiException(Throwable cause) {
		this(null,cause);
	}

	public FilesApiException(String message, Throwable cause) {
		super(message, cause);
        }

}
