/**
 * 
 */
package com.alexarkhipov.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Alex Arkhipov
 *
 */
@Component
public class NeoCommanLineRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(NeoCommanLineRunner.class);

	@Autowired
	private NeoPostManager neoPostManager;

	@Autowired
	private NeoStorage neoStorage;

	@Autowired
	private NeoVK neoVK;

	/**
	 * 
	 */
	public NeoCommanLineRunner() {
		logger.debug("{} constructor is called.", this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... arg0) throws Exception {
		logger.info("Run method is active...");
		List<NeoPost> newPosts = null;
		try {
			neoPostManager.downloadPosts();
			neoPostManager.printPosts();
			String lastPubTitle = neoStorage.getLastPublishedPostTitle();

			newPosts = neoPostManager.getUnpublishedPosts(lastPubTitle);
		} catch (Exception ex) {
			logger.error("Cannot connect to get feed: {}", ex.getMessage());
		}

		if (newPosts == null || newPosts.isEmpty()) {
			logger.info("No new posts!");
		} else {
			NeoPost lastPost = newPosts.get(0);
			for (NeoPost p : newPosts) {
				neoVK.publishPost(neoPostManager, p);
				lastPost = p;
				logger.info("New post: {}", p.getTitle());
			}

			neoStorage.savePost(lastPost);
			logger.info("Saved latest title: {}", lastPost.getTitle());
		}

	}

}
