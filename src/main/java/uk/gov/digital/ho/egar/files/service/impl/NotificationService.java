package uk.gov.digital.ho.egar.files.service.impl;

import uk.gov.digital.ho.egar.files.api.exceptions.NotificationFilesApiException;
import uk.gov.digital.ho.egar.files.model.FileRequestNotification;

public interface NotificationService {

	void requestScan(FileRequestNotification notification) throws NotificationFilesApiException;

}