package uk.gov.digital.ho.egar.files.model;

/**
 * The file status enumeration.
 */
public enum FileStatus {
    UPLOADED, 
    UPLOADING,
    UPLOAD_FAILED,
    AWAITING_VIRUS_SCAN,
    VIRUS_SCANNED,
    QUARANTINED
}
