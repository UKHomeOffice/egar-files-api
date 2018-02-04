package uk.gov.digital.ho.egar.files.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class BadRequestFilesApiException extends FilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public BadRequestFilesApiException() {
	}

	/**
	 * @param message
	 */
	public BadRequestFilesApiException(String message) {
		super(message);
	}

	public BadRequestFilesApiException(String message, Exception e) {
		super(message, e);
	}



}