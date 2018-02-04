package uk.gov.digital.ho.egar.files.api;

import java.util.UUID;

import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.model.FileInfoDetails;

public interface FileDetails {

	/**
	 * 
	 * @param userUuid
	 * @param fileLink
	 * @return
	 * @throws FilesApiException
	 */

	FileInfoDetails linkDetails(UUID userUuid, FileInfoDetails fileLink) throws FilesApiException;

}