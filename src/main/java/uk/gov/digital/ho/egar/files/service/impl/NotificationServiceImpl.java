package uk.gov.digital.ho.egar.files.service.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.digital.ho.egar.files.api.exceptions.NotificationFilesApiException;
import uk.gov.digital.ho.egar.files.api.jms.constants.QueueNames;
import uk.gov.digital.ho.egar.files.model.FileRequestNotification;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	private final Log logger = LogFactory.getLog(NotificationServiceImpl.class);

    private final JmsTemplate defaultJmsTemplate;

    private final ObjectMapper objectMapper;

    private final QueueNames queueNames;

	public NotificationServiceImpl(@Autowired JmsTemplate defaultJmsTemplate,
                                   @Autowired ObjectMapper objectMapper,
                                   @Autowired QueueNames queueNames) {
        this.defaultJmsTemplate = defaultJmsTemplate;
        this.objectMapper = objectMapper;
        this.queueNames = queueNames;
	}

    @Override
	public void requestScan(final FileRequestNotification notification) throws NotificationFilesApiException {
    	
    	logger.info("Sending message to SQS queue: " + queueNames.getVscanRequestQueue());
    	try {
            String notifyText = objectMapper.writeValueAsString(notification);

            defaultJmsTemplate.convertAndSend(queueNames.getVscanRequestQueue(),
            		notifyText);
        }catch (JsonProcessingException e){
    	    logger.error("Unable to convert notification to a string.");
    	    throw new NotificationFilesApiException("Unable to convert notification to string.");
        }
    }
}
