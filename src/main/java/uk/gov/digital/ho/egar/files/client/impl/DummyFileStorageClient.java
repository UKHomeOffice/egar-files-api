package uk.gov.digital.ho.egar.files.client.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import uk.gov.digital.ho.egar.files.api.exceptions.FileClientException;
import uk.gov.digital.ho.egar.files.client.FileStorageClient;
import uk.gov.digital.ho.egar.files.model.FileStatus;
import uk.gov.digital.ho.egar.files.model.ScannedFileStatus;
import uk.gov.digital.ho.egar.files.model.pojo.FileInfoPojo;

/**
 * A mock implementation of a storage client.
 * Doesn't actually store anything.
 */
@Component
@Profile("s3-mocks")
public class DummyFileStorageClient implements FileStorageClient {
    protected final Log logger = LogFactory.getLog(DummyFileStorageClient.class);

    @Value("${mocks.fileSize:131072}")
    private long fileSize;

	private boolean deleteException=false;

	private boolean moveToScanFolderException=false;

	private boolean fileLinkException=false;

	@Override
	public void delete(String fileLink) throws FileClientException {
		if (deleteException){
			throw new FileClientException("Error deleting file");
		}
	}

	@Override
	public FileInfoPojo getFileLinkDetails(String link) throws FileClientException {

		if (fileLinkException){
			throw new FileClientException("Unable to open file");
		}

		String[] parts = link.split("[/\\\\]");
		return FileInfoPojo.builder()
				.fileLink(link)
				.fileName(parts[parts.length-1])
				.fileSize(fileSize)
				.build();
	}

	@Override
	public URL moveToScanFolder(String fileLink, String newFileName) throws FileClientException {

		if (moveToScanFolderException){
			throw new FileClientException("unable to move file to scan folder");
		}

		try {
			return new URL(fileLink);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Filelink not the correct format");
		}
	}

	public void setDeleteException(boolean deleteException) {
		this.deleteException = deleteException;
	}

	public void setMoveToScanFolderException(boolean moveToScanFolderException) {
		this.moveToScanFolderException = moveToScanFolderException;
	}
	public void setFileLinkException(boolean fileLinkException) {
		this.fileLinkException = fileLinkException;
	}

	@Override
	public URL moveAfterScan(String fileLink, ScannedFileStatus status) {
		if (moveToScanFolderException){
			try {
				throw new FileClientException("unable to move file to respective folder");
			} catch (FileClientException e) {
				
				e.printStackTrace();
			}
		}

		try {
			return new URL(fileLink);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Filelink not the correct format");
		}
	}

}
