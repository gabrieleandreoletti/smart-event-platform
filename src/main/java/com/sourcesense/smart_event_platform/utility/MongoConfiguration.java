package com.sourcesense.smart_event_platform.utility;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableMongoAuditing
@Configuration
@EnableScheduling
public class MongoConfiguration {
}
