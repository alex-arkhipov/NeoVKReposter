package com.alexarkhipov.vkrenamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NeoPostManager implements NeoManagePost {
	private static final Logger logger = LoggerFactory.getLogger(NeoPostManager.class);

	private NeoPostRetriever neoPostRetriever;

	public final List<NeoPost> posts = new ArrayList<>();

	public NeoPostManager(NeoPostRetriever neoPostRetriever) {
		this.neoPostRetriever = neoPostRetriever;
		logger.debug("{} constructor is called.", this.getClass().getName());
	}

	public boolean downloadPosts() {
		neoPostRetriever.getPosts(this);
		return true;
	}

	@Override
	public void addPost(String title, String description, String link, String pubDate) {
		NeoPost post = new NeoPost(title, description, link, pubDate);
		posts.add(post);
	}

	public void printPosts() {
		Integer i = 0;
		for (NeoPost p : posts) {
			logger.debug("{}. {}", i, p.getTitle());
			i++;
		}
	}

	public String getLatestTitle() {
		return (posts.isEmpty()) ? "" : posts.get(0).getTitle();
	}

	@Override
	public List<NeoPost> getUnpublishedPosts(String title) {
		List<NeoPost> pList = new ArrayList<>();
		int i = 0;
		for (NeoPost p : posts) {
			if (p.getTitle().equals(title)) {
				break;
			}
			i++;
		}

		if (i != 0) {
			// Found unpublished posts
			pList.addAll(posts.subList(0, i));
			Collections.reverse(pList); // From old to new
		}

		return pList;
	}

	@Override
	public String getPostContent(NeoPost p) {
		StringBuilder s = new StringBuilder(p.getTitle());
		s.append("\n");
		// Replace "&nbsp;" with " "
		String desc = p.getDescription().replaceAll("&nbsp;", " ");

		// Replace "<br/>" with "\n"
		desc = desc.replaceAll("<br\\s*/>", "\n\r");

		// Remove HTML tags
		String d = Jsoup.clean(desc, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
		s.append(d);
		logger.debug("Description = {}", d);
		return s.toString();
	}

}
