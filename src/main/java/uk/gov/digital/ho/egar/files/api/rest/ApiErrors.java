package uk.gov.digital.ho.egar.files.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {

    @JsonProperty("message")
    private final List<String> errorMessages = new ArrayList<>();

    public ApiErrors()
    {
    }

    public ApiErrors(Errors errors) {

        addFieldErrors(errors.getFieldErrors());
        addObjectErrors(errors.getGlobalErrors());
    }

    protected ApiErrors addObjectErrors(List<ObjectError> globalErrors) {
        for(final ObjectError error : globalErrors ){
            errorMessages.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return this ;
    }

    protected ApiErrors addFieldErrors(List<FieldError> fieldErrors) {

        for(final FieldError error : fieldErrors ){
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return this ;

    }
}
