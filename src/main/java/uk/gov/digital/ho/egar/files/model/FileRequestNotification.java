package uk.gov.digital.ho.egar.files.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
@EqualsAndHashCode
@Builder
public class FileRequestNotification {

    @NotNull
    @JsonProperty("file_uuid")
    private UUID fileUuid;

    @NotNull
    @JsonProperty("file_link")
    private String fileLink;

}
