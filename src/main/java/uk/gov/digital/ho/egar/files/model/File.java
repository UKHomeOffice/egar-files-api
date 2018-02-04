package uk.gov.digital.ho.egar.files.model;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class File {
	
    @JsonProperty("file_uuid")
    private UUID fileUuid;
    
    @JsonProperty("file_link")
    private String fileLink;
    
	private boolean clean;
	
	private LocalDate date;
	
}