package uk.gov.digital.ho.egar.files.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class FileNotFoundFilesApiException extends DataNotFoundFilesApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFoundFilesApiException(final UUID fileUuid , final UUID userId )
	{
		super(String.format("Can not find file %s for user %s", fileUuid.toString(), userId.toString()));
	}

	public FileNotFoundFilesApiException(UUID fileUuid) {
		super(String.format("Can not find file %s ", fileUuid.toString()));
	}
	
}
