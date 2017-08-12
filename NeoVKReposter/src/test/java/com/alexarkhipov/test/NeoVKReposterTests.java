package com.alexarkhipov.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
public class NeoVKReposterTests {

	@Autowired
	private NeoStorage neoStorage;

	@Autowired
	private NeoManagePost neoManagePost;

	@Test
	public void contextLoads() {
	}

	@Test
	public void postSaving() {
		// Create dummy post
		String postTitle = "Test post title";
		String postTitle2 = "Test post title 2";
		NeoPost p = new NeoPost(postTitle, "Desc", "link", "pubDate");
		NeoPost p2 = new NeoPost(postTitle2, "Desc", "link", "pubDate");
		// Save it in storage
		neoStorage.savePost(p);
		neoStorage.savePost(p2);
		// Check saving
		String t = neoStorage.getLastPublishedPostTitle();

		assertNotEquals(postTitle, t);
		assertEquals(postTitle2, t);

	}

	@Test
	public void testPostManager() {
		// Check HTML tags
		String postDesc = " <b>hello</b> ";
		NeoPost p = new NeoPost("title", postDesc, "link", "pubDate");
		NeoPost p1 = new NeoPost("title", " hello ", "link", "pubDate");
		String content = neoManagePost.getPostContent(p);
		String content1 = neoManagePost.getPostContent(p1);
		assertEquals(content, content1);

		// Check '&nbsp;'
		postDesc = "&nbsp;k&nbsp;t&nbsp;";
		p.setDescription(postDesc);
		p1.setDescription(" k t ");
		content = neoManagePost.getPostContent(p);
		content1 = neoManagePost.getPostContent(p1);
		assertEquals(content, content1);

		// Check '<br/>'
		postDesc = "<br />good<br/>";
		p.setDescription(postDesc);
		p1.setDescription("\n\rgood\n\r");
		content = neoManagePost.getPostContent(p);
		content1 = neoManagePost.getPostContent(p1);
		assertEquals(content, content1);
	}

}
