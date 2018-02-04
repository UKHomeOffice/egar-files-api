package uk.gov.digital.ho.egar.files.api.jms;

import java.io.IOException;

import javax.jms.JMSException;

public interface FileScanService {
    void processScannedFile(String requestJSON) throws IOException, JMSException ;
}
