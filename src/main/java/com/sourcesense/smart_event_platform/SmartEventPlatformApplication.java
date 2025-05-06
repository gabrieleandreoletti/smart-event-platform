package com.sourcesense.smart_event_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@EnableCaching
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SmartEventPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartEventPlatformApplication.class, args);
    }

}
