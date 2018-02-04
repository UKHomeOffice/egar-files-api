package uk.gov.digital.ho.egar.files.client;

import uk.gov.digital.ho.egar.files.api.exceptions.FileClientException;
import uk.gov.digital.ho.egar.files.model.ScannedFileStatus;
import uk.gov.digital.ho.egar.files.model.pojo.FileInfoPojo;

import java.net.URL;

/**
 * A storage client for uploading file input streams.
 */
public interface FileStorageClient {

	FileInfoPojo getFileLinkDetails(final String link) throws FileClientException;

	URL moveToScanFolder(String fileLink, String newFileName) throws FileClientException;

	/**
	 * Deletes a file from a storage service.
	 * @param fileLink
	 * @throws FileClientException if deleting the file was unsuccessful
	 */
	void delete(String fileLink) throws FileClientException;

	URL moveAfterScan(String fileLink, ScannedFileStatus status);
}
