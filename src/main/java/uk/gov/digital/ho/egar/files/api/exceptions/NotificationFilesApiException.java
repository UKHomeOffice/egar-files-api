package uk.gov.digital.ho.egar.files.api.exceptions;
/**
 *  
 */
public class NotificationFilesApiException extends FilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public NotificationFilesApiException() {
	}

	/**
	 * @param message
	 */
	public NotificationFilesApiException(String message) {
		super(message);
	}

	public NotificationFilesApiException(String message, Exception e) {
		super(message, e);
	}


}
