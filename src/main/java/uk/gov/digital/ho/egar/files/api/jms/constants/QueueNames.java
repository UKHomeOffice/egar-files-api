package uk.gov.digital.ho.egar.files.api.jms.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class QueueNames {

	public static final String VSCAN_REQUEST_QUEUE_REF = "${egar.vscan.req.queue}";

	public static final String VSCAN_RESPONSE_QUEUE_REF = "${egar.vscan.res.queue}";

	@Value(VSCAN_REQUEST_QUEUE_REF)
	private String vscanRequestQueue;

	@Value(VSCAN_RESPONSE_QUEUE_REF)
	private String vscanResponseQueue;
}