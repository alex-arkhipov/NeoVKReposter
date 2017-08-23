package com.alexarkhipov.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:neo.vk.properties")
// @PropertySource(value = { "classpath:resources/application.properties" })
public class NeoVKReposter {
	private static final Logger logger = LoggerFactory.getLogger(NeoVKReposter.class);

	@Autowired
	@Bean
	public NeoPostRetriever neoPostRetriever(@Value("${app.rss.url}") String url) {
		return new NeoRssParser(url);
	}

	@Autowired
	@Bean
	public String storageFile(@Value("${app.storagefile}") String file) {
		return file;
	}

	@Autowired
	@Bean
	public Boolean test(@Value("${neo.test}") Boolean test) {
		return test;
	}

	@Autowired
	@Bean
	public NeoStorage neoStorage(String storageFile, Boolean test) {
		return new NeoFileStorage(storageFile, test);
	}

	@Autowired
	@Bean
	public NeoVK neoVK(@Value("${app.vk.url}") String url, @Value("${app.vk.access_token}") String accesstoken,
			@Value("${app.vk.owner_id}") String ownerid, @Value("${app.vk.services}") String services,
			@Value("${app.vk.test}") Boolean test) {
		return new NeoVK(url, accesstoken, ownerid, services, test);
	}

	@Autowired
	@Bean
	public NeoPostManager neoPostManager(@Value("${app.rss.url}") String url) {
		return new NeoPostManager(neoPostRetriever(url));
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(NeoVKReposter.class, args);
		NeoVK vk = (NeoVK) ctx.getBean("neoVK");
		logger.debug("App name: {} | Bean type: {}", ctx.getApplicationName(), vk.getClass().getName());
	}
}
