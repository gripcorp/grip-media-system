package show.grip.mms.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws.ivs")
data class IvsProperties(
    var region: String = "ap-northeast-2",
    var accountId: String = "",
    var channelType: String = "STANDARD",
    var recording: Recording = Recording()
) {
    data class Recording(
        var enabled: Boolean = true,
        var s3: S3 = S3(),
        var thumbnail: Thumbnail = Thumbnail()
    ) {
        data class S3(
            var bucket: String = ""
        )

        data class Thumbnail(
            var interval: Int = 60,
            var storage: String = "LATEST"
        )
    }

    /**
     * Channel ARN 생성
     * 형식: arn:aws:ivs:ap-northeast-2:123456789012:channel/abcd1234EFGH
     */
    fun buildChannelArn(channelId: String): String {
        return "arn:aws:ivs:$region:$accountId:channel/$channelId"
    }

    /**
     * Recording Configuration ARN 생성
     */
    fun buildRecordingConfigArn(configId: String): String {
        return "arn:aws:ivs:$region:$accountId:recording-configuration/$configId"
    }
}
