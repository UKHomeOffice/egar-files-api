package uk.gov.digital.ho.egar.files.model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.digital.ho.egar.files.service.repository.model.FilePersistedRecord;

/**
 * Describes all of the information available on a file.s
 */
@JsonDeserialize(as = FilePersistedRecord.class)
public interface FileDetails extends FileEntry {

    UUID getFileUuid();

    String getFileName();

    Long getFileSize();

    FileStatus getStatus();

    UUID getUserUuid();
    
    Boolean getDeleted();
    
    Date getFileLastModifiedDate();
    
    String getFileLink();

}
