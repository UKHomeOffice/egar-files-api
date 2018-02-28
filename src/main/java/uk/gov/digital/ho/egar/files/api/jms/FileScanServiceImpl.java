package uk.gov.digital.ho.egar.files.api.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.files.api.jms.constants.QueueNames;
import uk.gov.digital.ho.egar.files.model.ScanResults;
import uk.gov.digital.ho.egar.files.service.FileService;

import javax.jms.JMSException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Component
public class FileScanServiceImpl implements FileScanService {

	private static final Logger logger = LoggerFactory.getLogger(FileScanServiceImpl.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FileService fileService;

	@Autowired
	private Validator validator;

	@Override
	@JmsListener(destination = QueueNames.VSCAN_RESPONSE_QUEUE_REF)
	public void processScannedFile(String requestJSON)
			throws JMSException {
		logger.info("Processing Notification from queue");

		ScanResults scanResults = null;
		try {
			scanResults = objectMapper.readValue(requestJSON, ScanResults.class);

			validateRequest(scanResults);

			fileService.updateFileDetails(scanResults);
			
		} catch (Exception e) {
			String errMsg = "Error encountered while attempting to move processed file to the correct folder";
			Objects.requireNonNull(scanResults, errMsg);
			logger.error(errMsg, e);
			logger.error(e.getMessage());
//			throw new JMSException(errMsg);
		}

	}

	/**
	 * Validates the incoming results.
	 * @param results the results
	 */
	private void validateRequest(final ScanResults results){
		Set<ConstraintViolation<ScanResults>> violations = validator.validate(results);

		if (!violations.isEmpty()){
			throw new IllegalArgumentException("File request was invalid. " + getErrorMessage(violations));
		}

	}

	/**
	 * Builds an error message from the violations
	 * @param violations The violations
	 * @return The error message.
	 */
	private String getErrorMessage(Set<ConstraintViolation<ScanResults>> violations) {
		StringBuilder messageBuilder = new StringBuilder();

		boolean first = true;

		for (ConstraintViolation<ScanResults> violation: violations){
			messageBuilder.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());

			if (first) {
				messageBuilder.append(", ");
			}
			first = false;
		}
		return messageBuilder.toString();
	}
	
	

}
