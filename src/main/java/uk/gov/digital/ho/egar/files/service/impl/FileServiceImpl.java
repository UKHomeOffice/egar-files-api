package uk.gov.digital.ho.egar.files.service.impl;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.files.api.exceptions.FileClientException;
import uk.gov.digital.ho.egar.files.api.exceptions.FileNotFoundFilesApiException;
import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.client.FileStorageClient;
import uk.gov.digital.ho.egar.files.model.*;
import uk.gov.digital.ho.egar.files.service.FileService;
import uk.gov.digital.ho.egar.files.service.repository.FilePersistedRecordRepository;
import uk.gov.digital.ho.egar.files.service.repository.model.FilePersistedRecord;
import uk.gov.digital.ho.egar.files.utils.UrlUtilities;

@Service
public class FileServiceImpl implements FileService {

	protected final Log logger = LogFactory.getLog(FileServiceImpl.class);

	/**
	 * The file storage client to persist the binary file.
	 */
	private FileStorageClient fileStorageClient;

	/**
	 * The file repo to persist the metadata and file status.
	 */
	private FilePersistedRecordRepository repo;

	/**
	 * The notification service.
	 */
	private NotificationService notifyService;

	private final UrlUtilities urlUtilities;

	public FileServiceImpl(@Autowired final FileStorageClient fileStorageClient,
						   @Autowired final FilePersistedRecordRepository repo,
						   @Autowired NotificationService notifyService,
						   @Autowired UrlUtilities urlUtilities) {
		this.fileStorageClient = fileStorageClient;
		this.repo = repo;
		this.notifyService = notifyService;
		this.urlUtilities = urlUtilities;
	}

	@Override
	/**
	 * 1. ADD file details to DB
	 * @param userUuid - user UUID
	 * @param newFile - file details to add
	 * @throws FilesApiException
	 *            
	 * @return file UUID.
	 */
	public UUID addFile(final UUID userUuid, final FileEntry newFile) throws FilesApiException {
		
		String link = newFile.getFileLink();
		logger.info("Adding file from link: " + link);

		FilePersistedRecord filePersistedRecord = createFile(UUID.randomUUID(), userUuid, newFile);
		logger.debug("File ID: " + filePersistedRecord.getFileUuid() + " added to DB - " + "File Link: " + link );
		filePersistedRecord.setStatus(FileStatus.AWAITING_VIRUS_SCAN);

		logger.info("Moving file to scanning folder: " + link);

		// move file to scanning bucket with new filename
		URL newLink = fileStorageClient.moveToScanFolder(link, constructFilename(filePersistedRecord));
		filePersistedRecord.setFileLink(newLink.toString());

		repo.save(filePersistedRecord);

		FileRequestNotification notification = FileRequestNotification.builder().fileLink(filePersistedRecord.getFileLink()).fileUuid(filePersistedRecord.getFileUuid()).build();

		notifyService.requestScan(notification);

		return filePersistedRecord.getFileUuid();
	}

	@Override
	/**
	 * 2. UPDATE a file's meta data
	 * TEMP - As this may end up being a SQS subscriber. 
	 */
	public FileDetails updateFileDetails(ScanResults scanResults) throws FileNotFoundFilesApiException {
		logger.debug("Updating File with ID " + scanResults.getFileUuid());

		// use file UUID to find file details.
		FilePersistedRecord fileDetails = repo.findOneByFileUuid(scanResults.getFileUuid());

		if (fileDetails == null) {
			throw new FileNotFoundFilesApiException(scanResults.getFileUuid());
		}

		// move file to correct bucket
		URL url = fileStorageClient.moveAfterScan(fileDetails.getFileLink(), scanResults.getFileStatus());

		// update DB with File Link
		fileDetails.setFileLink(url.toString());
		fileDetails.setStatus(scanResults.getFileStatus()==ScannedFileStatus.CLEAN?FileStatus.VIRUS_SCANNED:FileStatus.QUARANTINED);
		return repo.save(fileDetails);
	}

	@Override
	/**
	 * 3. RETRIEVE file from DB
	 * @param fileUUid - file UUID
	 * @param userUuid - user UUID
	 * @throws FileNotFoundFilesApiException
	 *            
	 * @return file details.
	 */
	public FileDetails retrieveFileDetails(UUID fileUuid, UUID userUuid) throws FileNotFoundFilesApiException {
		FilePersistedRecord fileDetails = repo.findOneByFileUuidAndUserUuidAndDeletedIsFalse(fileUuid, userUuid);

		if (fileDetails == null) {
			throw new FileNotFoundFilesApiException(fileUuid, userUuid);
		}

		logger.info("Retrieving file details for: " + fileDetails.getFileName() + " from DB");
		return fileDetails;
	}


	@Override
	/**
	 * 6. DELETE file from S3. Update DB
	 * @param fileUuid - file UUID
	 * @param  userUuid - user UUID
	 * @throws FileNotFoundFilesApiException
	 *            
	 * @return nothing.
	 */
	public void deleteFile(UUID fileUuid, UUID userUuid) throws FileNotFoundFilesApiException {

		FileDetails fileDetails = repo.findOneByFileUuidAndUserUuidAndDeletedIsFalse(fileUuid, userUuid);

		if (fileDetails == null || fileDetails.getFileLink() == null)
			throw new FileNotFoundFilesApiException(fileUuid, userUuid);

		logger.info("Deleting file: " + fileDetails.getFileName() + " from S3 ");
		try {
			fileStorageClient.delete(fileDetails.getFileLink());
		} catch (FileClientException e) {
			throw new FileNotFoundFilesApiException(userUuid, fileUuid);
		}

		// Update DB - Delete Column to true
		repo.updateDeleteStatus(fileUuid, true);
	}
	
	
	
	@Override
	/**
	 * 1a - FileDetailsController. GET File information from S3.
	 * @param file UUID
	 * @throws FileNotFoundFilesApiException, UnableToDownloadException
	 *            
	 * @return nothing.
	 */
	public FileInfoDetails getFileLinkDetails(FileInfoDetails fileLink) throws FileClientException {


		return fileStorageClient.getFileLinkDetails(fileLink.getFileLink());
	}
	
	@Override
	public FileDetails[] getBulkfiles(UUID uuidOfUser, List<UUID> fileUuids) {
		List<FilePersistedRecord> fileList = repo.findAllByUserUuidAndFileUuidIn(uuidOfUser,fileUuids);
		FileDetails[] filesArray = new FileDetails[fileList.size()];
		filesArray = fileList.toArray(filesArray);
		return filesArray;
	}


	/**
	 * Constructs the fileName by combining the uuid and original fileName
	 * 
	 * @param details
	 *           
	 * @return The new fileName.
	 */
	private String constructFilename(FileDetails details) {
		String filename =  details.getFileUuid().toString() + "/" + details.getFileName();
		return urlUtilities.urlDecodeValue(filename);
	}

	/**
	 * Creates a file from the details provided
	 * @param fileUuid The file uuid.
	 * @param userUuid The user uuid.
	 * @param newFile The file details
	 * @return The persisted record
	 */
	private FilePersistedRecord createFile(UUID fileUuid, UUID userUuid, FileEntry newFile) {
		return FilePersistedRecord.builder().fileName(newFile.getFileName()).userUuid(userUuid)
				.fileSize(newFile.getFileSize()).fileUuid(fileUuid).deleted(false).fileLastModifiedDate(new Date()).build();
	}


}
