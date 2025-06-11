package com.sourcesense.smart_event_platform.utility;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableMongoAuditing
@org.springframework.context.annotation.Configuration
@EnableScheduling
public class ConfigurationCustom {
}
