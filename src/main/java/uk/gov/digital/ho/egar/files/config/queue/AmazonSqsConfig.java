package uk.gov.digital.ho.egar.files.config.queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
@Profile("!jms-disabled")
public class AmazonSqsConfig {

    @Autowired
    private SqsConfig sqsConfig;

    @Bean
    public SQSConnectionFactory createConnectionFactory(){

        return SQSConnectionFactory.builder()
                .withAWSCredentialsProvider(createAwsCredentialProvider())
                .withRegionName(sqsConfig.getRegion())
                .build();
    }

    @Bean
    public AmazonSQS createClient(){
        return AmazonSQSClientBuilder.standard().
                withCredentials(createAwsCredentialProvider())
                .withRegion(sqsConfig.getRegion())
                .build();
    }

    private AWSCredentialsProvider createAwsCredentialProvider(){
        return new StaticCredentialsProvider(
                new BasicAWSCredentials(sqsConfig.getAccessKey(), sqsConfig.getSecretKey()));
    }
}