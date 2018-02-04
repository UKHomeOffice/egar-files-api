package uk.gov.digital.ho.egar.files.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ScanResults {
    @NotNull
    @JsonProperty("file_uuid")
    private UUID fileUuid;

    @NotNull
    @JsonProperty("file_status")
    private ScannedFileStatus fileStatus;
}
