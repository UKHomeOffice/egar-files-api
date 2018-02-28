package uk.gov.digital.ho.egar.files.service;

import uk.gov.digital.ho.egar.files.api.exceptions.FileClientException;
import uk.gov.digital.ho.egar.files.api.exceptions.FileNotFoundFilesApiException;
import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.api.exceptions.UnableToDownloadException;
import uk.gov.digital.ho.egar.files.model.FileDetails;
import uk.gov.digital.ho.egar.files.model.FileEntry;
import uk.gov.digital.ho.egar.files.model.FileInfoDetails;
import uk.gov.digital.ho.egar.files.model.ScanResults;

import java.util.List;
import java.util.UUID;

/**
 * A file service that allows for uploading, downloading and retrieving file information.
 */
public interface FileService {

      /**
     * Retrieves details about an already existing file.
     * @param fileUuid The file uuid
     * @param userUuid The user uuid
     * @return The file details
     * @throws FileNotFoundFilesApiException Is thrown when the file details requested does not exist.
     */
    FileDetails retrieveFileDetails(final UUID fileUuid, final UUID userUuid) throws FileNotFoundFilesApiException;

    /**
     * Delete file from S3.
     * @param userUuid The user uuid
     * @param fileUuid The file uuid
     * @return VOID
     * @throws FileNotFoundFilesApiException Is thrown when the file requested does not exist.
     * @throws UnableToDownloadException 
     */
	void deleteFile(UUID userUuid, UUID fileUuid) throws FileNotFoundFilesApiException, UnableToDownloadException;
	

	/**
     * Add file to DB.
     * @param userUuid The user uuid
     * @param newFile file details
     * @return UUID
     * @throws FilesApiException
     */

	UUID addFile(UUID userUuid, FileEntry newFile) throws FilesApiException;


	/**
     * Update File Details.
     * @param scanResults The scan results
     * @return FileDetails
     * @throws FileNotFoundFilesApiException
     */
	FileDetails updateFileDetails(ScanResults scanResults) throws FileNotFoundFilesApiException;


	/**
	 * Get Link Details
	 * @param fileLink
	 * @return
	 * @throws FileNotFoundFilesApiException
	 * @throws FileClientException
	 */

	FileInfoDetails getFileLinkDetails(FileInfoDetails fileLink) throws FileNotFoundFilesApiException, FileClientException;

	/**
	 * Retrieve a list of file details
	 * @param uuidOfUser
	 * @param fileUuids
	 * @return list of file details
	 */
	FileDetails[] getBulkfiles(final UUID uuidOfUser, final List<UUID> fileUuids);


}
