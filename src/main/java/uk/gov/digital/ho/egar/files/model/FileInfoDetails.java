package uk.gov.digital.ho.egar.files.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.digital.ho.egar.files.model.pojo.FileInfoPojo;

@JsonDeserialize(as = FileInfoPojo.class)
public interface FileInfoDetails {
	String getFileName();
	long getFileSize();
	String getFileLink();
}
