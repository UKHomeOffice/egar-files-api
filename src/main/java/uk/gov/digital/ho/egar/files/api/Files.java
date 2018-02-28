package uk.gov.digital.ho.egar.files.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.model.FileDetails;
import uk.gov.digital.ho.egar.files.model.FileEntry;

public interface Files {

	// 1. ADD FILE ENTRY
	/**
	 * 
	 * @param userUuid - user UUid
	 * @param newFile - new file details
	 * @return HTTP Entity
	 * @throws FilesApiException
	 */
	ResponseEntity<Void> addFileEntry(UUID userUuid, FileEntry newFile) throws FilesApiException;

	// 3. RETRIEVE (GET) A FILE
	/**
	 * 
	 * @param uuidOfUser - user UUID
	 * @param fileUuid - file UUID
	 * @return File Details
	 * @throws FilesApiException
	 */
	FileDetails retrieve(UUID uuidOfUser, UUID fileUuid) throws FilesApiException;

	// 6. DELETE A FILE
	/**
	 * 
	 * @param fileUuid - file UUID
	 * @param uuidOfUser - user UUID
	 * @return HTTP Entity
	 * @throws FilesApiException
	 */
	ResponseEntity<Void> delete(UUID fileUuid, UUID uuidOfUser) throws FilesApiException;

	/**
	 * Retrieve a list of file details
	 * @param uuidOfUser
	 * @param fileUuids
	 * @return list of file details
	 */
	FileDetails[] bulkRetrieveFiles(final UUID uuidOfUser, final List<UUID> fileUuids);

	

}