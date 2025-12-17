package show.grip.mms.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(
    "classpath:config/common.properties",
    "classpath:config/database.properties",
    "classpath:config/ivs.properties"
)
class PropertiesConfig
