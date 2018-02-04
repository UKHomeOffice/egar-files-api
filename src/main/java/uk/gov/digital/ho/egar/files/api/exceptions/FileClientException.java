package uk.gov.digital.ho.egar.files.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_GATEWAY)
public class FileClientException extends FilesApiException {

    public FileClientException(String message){
        super(message);
    }

}
