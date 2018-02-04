package uk.gov.digital.ho.egar.files.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uk.gov.digital.ho.egar.files.model.FileEntry;
import uk.gov.digital.ho.egar.files.model.FileInfoDetails;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntryPojo implements FileEntry {


    @NotNull
    @JsonProperty("file_name")
    private String fileName;

    @NotNull
    @Min(0)
    @JsonProperty("file_size")
    private Long fileSize;

    @NotNull
    @JsonProperty("file_link")
    private String fileLink;

}
