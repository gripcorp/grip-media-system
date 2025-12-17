package show.grip.mms.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ivs.IvsClient
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AwsConfig {

    @Bean
    fun ivsClient(): IvsClient {
        return IvsClient.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.AP_NORTHEAST_2)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
}
