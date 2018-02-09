package uk.gov.digital.ho.egar.files.utils;

import com.amazonaws.services.s3.AmazonS3URI;

public interface UrlUtilities {

    public static final String UTF_8 = "UTF-8";

    public String urlDecodeKey(AmazonS3URI uri);

    public String urlDecodeValue(String key);
}
