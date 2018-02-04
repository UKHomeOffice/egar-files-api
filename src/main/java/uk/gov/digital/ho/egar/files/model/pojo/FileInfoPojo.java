package uk.gov.digital.ho.egar.files.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uk.gov.digital.ho.egar.files.model.FileInfoDetails;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfoPojo implements FileInfoDetails {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private long fileSize;

    @NotNull
    @JsonProperty("file_link")
    private String fileLink;

}
