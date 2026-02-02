package com.lab.config;

import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

@Configuration
public class QueryCountConfig {
    
    // This configuration is handled by datasource-proxy-spring-boot-starter
    // We'll just add a bean to print query count per request
    // The starter already configures the proxy, so we don't need to do it manually
}
