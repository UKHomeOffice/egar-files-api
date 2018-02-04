package uk.gov.digital.ho.egar.files.service.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import uk.gov.digital.ho.egar.files.model.FileDetails;
import uk.gov.digital.ho.egar.files.model.FileStatus;

import javax.persistence.*;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="FILES")
public class FilePersistedRecord implements FileDetails{
    @Id
    @JsonProperty("file_uuid")
    private UUID fileUuid;

    @JsonIgnore
    private UUID userUuid;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private Long fileSize;

    @Column(name = "file_status")
    @Enumerated(EnumType.STRING)
    @JsonProperty("file_status")
    private FileStatus status;
    
    @JsonIgnore
    private Boolean deleted;
    
    @Column
    @JsonProperty("file_last_modified_date")
    private Date fileLastModifiedDate;
    
    @Column
    @JsonProperty("file_link")
    private String fileLink;
}
