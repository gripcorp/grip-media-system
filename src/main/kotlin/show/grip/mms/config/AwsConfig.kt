package show.grip.mms.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ivs.IvsClient
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AwsConfig(
    private val ivsProperties: IvsProperties
) {

    private fun awsRegion(): Region = Region.of(ivsProperties.region)

    private fun credentialsProvider() = DefaultCredentialsProvider.create()

    @Bean
    fun ivsClient(): IvsClient {
        return IvsClient.builder()
            .region(awsRegion())
            .credentialsProvider(credentialsProvider())
            .build()
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(awsRegion())
            .credentialsProvider(credentialsProvider())
            .build()
    }
}
