package uk.gov.digital.ho.egar.files.api;

import uk.gov.digital.ho.egar.constants.ServicePathConstants;

public interface RestConstants {

	String FILES_SERVICE_NAME = "Files";
	String FILES_ROOT_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.ROOT_SERVICE_API
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.SERVICE_VERSION_ONE
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + FILES_SERVICE_NAME;

	String PATH_VARIABLE_FILE_UUID = "file_uuid";

	String PATH_UPLOAD = "/";
	String PATH_DELETE = "/";
	String PATH_DETAILS = "/";

	String PATH_FILE_DETAILS = ServicePathConstants.ROOT_PATH_SEPERATOR + "{" + PATH_VARIABLE_FILE_UUID + "}";

	String FILE_DETAILS_SERVICE_NAME = "FileDetails";
	String DETAILS_ROOT_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.ROOT_SERVICE_API
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.SERVICE_VERSION_ONE
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + FILE_DETAILS_SERVICE_NAME;
	String PATH_BULK = "/Summaries";
}