package liaison.linkit.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${AWS_S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_S3_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
//    @Bean
//    public AmazonS3 S3Client() {
//        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//
//        return AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();
//    }
}
