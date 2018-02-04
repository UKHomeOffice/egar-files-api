package uk.gov.digital.ho.egar.files.config.queue;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SqsConfig {

	@Value("${aws.sqs.region}")
	private String region;

	@Value("${aws.sqs.access.key}")
	private String accessKey;

	@Value("${aws.sqs.secret.key}")
	private String secretKey;
	
}