package com.alexarkhipov.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NeoVKReposter.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NeoVK.class) })
@EnableAutoConfiguration
@Profile("Test")
public class TestApplicationConfiguration {

	@Autowired
	@Bean
	public String storageFile(@Value("${app.storagefile.test}") String file) {
		return file;
	}

	@Autowired
	@Bean
	public NeoStorage neoStorage(String storageFile) {
		return new NeoFileStorage(storageFile, false);
	}

	@Autowired
	@Bean
	public NeoPostManager neoPostManager(@Value("${app.rss.url}") String url) {
		return new NeoPostManager(neoPostRetriever(url));
	}

	public NeoPostRetriever neoPostRetriever(String url) {
		return new NeoRssParser(url);
	}

}