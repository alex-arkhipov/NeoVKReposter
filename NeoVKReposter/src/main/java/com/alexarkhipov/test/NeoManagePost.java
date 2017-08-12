package com.alexarkhipov.test;

import java.util.List;

public interface NeoManagePost {
	public void addPost(String title, String description, String link, String pubDate);

	public List<NeoPost> getUnpublishedPosts(String title);

	public String getPostContent(NeoPost p);
}
