package uk.gov.digital.ho.egar.files.api.rest;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import uk.gov.digital.ho.egar.files.api.FileDetails;
import uk.gov.digital.ho.egar.files.api.RestConstants;
import uk.gov.digital.ho.egar.files.api.exceptions.FilesApiException;
import uk.gov.digital.ho.egar.files.model.FileInfoDetails;
import uk.gov.digital.ho.egar.files.service.FileService;
import uk.gov.digital.ho.egar.shared.auth.api.token.UserValues;

@RestController
@RequestMapping(RestConstants.DETAILS_ROOT_PATH)
@Api(value = RestConstants.DETAILS_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class FileDetailsController implements FileDetails {
	
	@Autowired
	private FileService fileService;

	protected final Log logger = LogFactory.getLog(FileDetailsController.class);

	/* (non-Javadoc)
	 * @see uk.gov.digital.ho.egar.files.api.FileDetails#linkDetails(java.util.UUID, uk.gov.digital.ho.egar.files.model.FileInfoDetails)
	 */
	@Override
	@ApiOperation(value = "Get file details from S3.", notes = "Get File Details")
	@ApiResponses(value = { @ApiResponse(code = 202, message = "accepted"), 
			@ApiResponse(code = 400, message = "fail"),
			@ApiResponse(code = 401, message = "unauthorised"), 
			@ApiResponse(code = 401, message = "forbidden") })
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = RestConstants.PATH_DETAILS)
	public FileInfoDetails  linkDetails(@RequestHeader(UserValues.USERID_HEADER) UUID userUuid,
			@Valid @RequestBody FileInfoDetails fileLink) throws FilesApiException {

		return fileService.getFileLinkDetails(fileLink);
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
