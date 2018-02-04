package uk.gov.digital.ho.egar.files.api.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.files.api.Files;
import uk.gov.digital.ho.egar.files.api.RestConstants;
import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.model.FileDetails;
import uk.gov.digital.ho.egar.files.model.FileEntry;
import uk.gov.digital.ho.egar.files.service.FileService;
import uk.gov.digital.ho.egar.files.utils.UriLocationUtilities;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;

@RestController
@RequestMapping(RestConstants.FILES_ROOT_PATH)
@Api(value = RestConstants.FILES_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FilesController implements Files {

	protected final Log logger = LogFactory.getLog(FilesController.class);
	private FileService fileService;
	private UriLocationUtilities uriLocationUtilities;

	public FilesController(@Autowired final FileService fileService,
			@Autowired final UriLocationUtilities uriLocationUtilities) {
		this.uriLocationUtilities = uriLocationUtilities;
		this.fileService = fileService;
	}

	// 1. ADD FILE ENTRY
	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.files.api.Files#addFileEntry(java.util.UUID, uk.gov.digital.ho.egar.files.model.FileDetails)
	 */
	@Override
	@ApiOperation(value = "Add A New File Entry To DB.", notes = "Add File Entry")
	@ApiResponses(value = { @ApiResponse(code = 303, message = "see other"), 
			@ApiResponse(code = 400, message = "fail"),
			@ApiResponse(code = 401, message = "unauthorised"), 
			@ApiResponse(code = 403, message = "forbidden") })
	@ResponseStatus(HttpStatus.SEE_OTHER)
	@PostMapping(value = RestConstants.PATH_UPLOAD)
	public ResponseEntity<Void> addFileEntry(@RequestHeader(UserValues.USERID_HEADER) UUID userUuid,
			@RequestBody @Valid FileEntry newFile) throws FilesApiException {


		UUID fileUuid = fileService.addFile(userUuid, newFile);
		URI redirectLocation = uriLocationUtilities.getFilesUri(fileUuid);
		logger.debug("Redirection url for files is: " + redirectLocation);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(redirectLocation);
		return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
	}

	// 3. RETRIEVE (GET) A FILE
	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.files.api.Files#retrieve(java.util.UUID, java.util.UUID)
	 */
	@Override
	@ApiOperation(value = "Retrieve File Details From DB.", notes = "Retrieve file details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "ok"), 
			@ApiResponse(code = 401, message = "unauthorised"), 
			@ApiResponse(code = 403, message = "forbidden") })
	@GetMapping(value = RestConstants.PATH_FILE_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public FileDetails retrieve(@RequestHeader(UserValues.USERID_HEADER) UUID uuidOfUser,
			@PathVariable(RestConstants.PATH_VARIABLE_FILE_UUID) UUID fileUuid) throws FilesApiException {

		return fileService.retrieveFileDetails(fileUuid, uuidOfUser);
	}

	// 6. DELETE A FILE
	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.files.api.Files#delete(java.util.UUID, java.util.UUID)
	 */
	@Override
	@ApiOperation(value = "Delete file from S3.", notes = "Delete a file")
	@ApiResponses(value = { @ApiResponse(code = 202, message = "accepted"), 
			@ApiResponse(code = 400, message = "fail"),
			@ApiResponse(code = 401, message = "unauthorised"), 
			@ApiResponse(code = 403, message = "forbidden") })
	@DeleteMapping(value = RestConstants.PATH_FILE_DETAILS)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<Void> delete(@PathVariable(RestConstants.PATH_VARIABLE_FILE_UUID) UUID fileUuid,
			@RequestHeader(UserValues.USERID_HEADER) UUID uuidOfUser) throws FilesApiException {

		fileService.deleteFile(fileUuid, uuidOfUser);

		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ApiErrors processValidationError(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();

		return new ApiErrors().addFieldErrors(fieldErrors) ;
	}
	

}
