package uk.gov.digital.ho.egar.files.client.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.files.api.exceptions.FileClientException;
import uk.gov.digital.ho.egar.files.client.FileStorageClient;
import uk.gov.digital.ho.egar.files.config.S3Config;
import uk.gov.digital.ho.egar.files.model.FileStatus;
import uk.gov.digital.ho.egar.files.model.ScannedFileStatus;
import uk.gov.digital.ho.egar.files.model.pojo.FileInfoPojo;

import java.net.URL;
import java.util.Objects;

import javax.validation.Valid;

/**
 * A S3 implementation of a file storage client.
 */
@Component
@Profile("!s3-mocks")
public class S3FileStorageClient implements FileStorageClient {

	public static final String UTF_8 = "UTF-8";
	private final Log logger = LogFactory.getLog(S3FileStorageClient.class);

	@Autowired
	private AmazonS3 s3Client;

	@Autowired
	private S3Config s3Config;

	@Override
	public void delete(final String fileLink) throws FileClientException {

		AmazonS3URI uri = new AmazonS3URI(fileLink);

		try {
			logger.info("Attempting to delete file " + uri.getKey() + " from  bucket " + uri.getBucket() + " on s3");
			s3Client.deleteObject(uri.getBucket(), uri.getKey());
		} catch (AmazonServiceException ase) {
			logger.error(String.format("AmazonServiceException when deleting file '%s' in bucket '%s'", uri.getKey(),
					uri.getBucket()), ase);
			throw new FileClientException(ase.getMessage());
		} catch (AmazonClientException ace) {
			logger.error("Client Error while trying to communicate with S3", ace);
			throw new FileClientException(ace.getMessage());
		}
	}

	@Override
	public FileInfoPojo getFileLinkDetails(String link) throws FileClientException {

		String urlDecodedKey;

		ObjectMetadata metadata;
		try {
			AmazonS3URI uri = new AmazonS3URI(link);

			urlDecodedKey = uri.getKey();
			try {
				urlDecodedKey = URLDecoder.decode(urlDecodedKey, UTF_8);
			} catch (UnsupportedEncodingException e) {
				logger.error(String.format("Unable to decode '%s' with format '%s'", urlDecodedKey, UTF_8), e);
			}

			metadata = s3Client.getObject(uri.getBucket(), urlDecodedKey).getObjectMetadata();
		}
		catch (Exception exception){
			logger.error(String.format("Unable to find file details for file link '%s'", link), exception);
			throw new FileClientException("Unable to find file details from s3");
		}

		String[] keyParts = urlDecodedKey.split("[/\\\\]");

		return FileInfoPojo.builder()
				.fileName(keyParts[keyParts.length-1])
				.fileSize(metadata.getContentLength())
				.fileLink(link).build();
	}

	@Override
	public URL moveToScanFolder(String fileLink, String newFilename) throws FileClientException {

		AmazonS3URI uri = new AmazonS3URI(fileLink);
		logger.info("Copying Object -> " + "Bucket Name " + uri.getBucket() + "File Name: " + uri.getKey()
				+ "Destination bucket: " + s3Config.getScanbucket() + "File Name again: " + newFilename);
		try {
			// copy object
			CopyObjectRequest copyObject = new CopyObjectRequest(uri.getBucket(), uri.getKey(),
					s3Config.getScanbucket(), newFilename);
			s3Client.copyObject(copyObject);
			// delete object
			s3Client.deleteObject(uri.getBucket(), uri.getKey());

			return s3Client.getUrl(s3Config.getScanbucket(), newFilename);
		} catch (AmazonClientException e) {
			logger.info("Error Message: " + e.getMessage());
			throw new FileClientException(String.format("Unable to move file '%s' to scan folder", fileLink));
		}
	}

	@Override
	public URL moveAfterScan(String fileLink, @Valid ScannedFileStatus scanStatus) {
		Objects.requireNonNull(scanStatus, "File Status is null");

		String destinationBucket = null;
		if (scanStatus != ScannedFileStatus.CLEAN)
			destinationBucket = s3Config.getQuarantinebucket();
		else
			destinationBucket = s3Config.getCleanbucket();

		AmazonS3URI uri = new AmazonS3URI(fileLink);
		try {
			CopyObjectRequest copyObject = new CopyObjectRequest(uri.getBucket(), uri.getKey(), destinationBucket,
					uri.getKey());
			s3Client.copyObject(copyObject);
			s3Client.deleteObject(uri.getBucket(), uri.getKey());

		} catch (AmazonClientException e) {
			logger.info("Error Message: " + e.getMessage());

		}
		return s3Client.getUrl(destinationBucket, uri.getKey());
	}

}
