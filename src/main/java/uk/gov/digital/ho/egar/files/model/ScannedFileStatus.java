package uk.gov.digital.ho.egar.files.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ScannedFileStatus {
    CLEAN,
    INFECTED;

    @JsonCreator
    public static ScannedFileStatus forValue(String value) {
        if (value == null) {
            return null;
        }

        return ScannedFileStatus.valueOf(StringUtils.upperCase(value));
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
