package edu.hawaii.its.holiday.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "edu.hawaii.its.holiday" },
               excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = Configuration.class) })
public class WebConfig {

}