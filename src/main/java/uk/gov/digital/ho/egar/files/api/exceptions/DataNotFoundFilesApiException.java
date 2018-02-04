package uk.gov.digital.ho.egar.files.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class DataNotFoundFilesApiException extends FilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public DataNotFoundFilesApiException() {
	}

	/**
	 * @param message
	 */
	public DataNotFoundFilesApiException(String message) {
		super(message);
	}


}