package uk.gov.digital.ho.egar.files.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.digital.ho.egar.files.model.pojo.FileEntryPojo;

/**
 * Describes all of the information available on a file.s
 */
@JsonDeserialize(as = FileEntryPojo.class)
public interface FileEntry {

    String getFileName();

    Long getFileSize();

    String getFileLink();
}
