package com.alexarkhipov.vkrenamer;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class NeoRssParser extends DefaultHandler implements NeoPostRetriever {
	private static final Logger logger = LoggerFactory.getLogger(NeoRssParser.class);

	private String urlString;
	private RssFeed rssFeed;
	private StringBuilder text;
	private Item item;
	private boolean imgStatus;

	public NeoRssParser(String url) {
		this.urlString = url;
		this.text = new StringBuilder();
	}

	public boolean parse() {
		InputStream urlInputStream = null;
		SAXParserFactory spf = null;
		SAXParser sp = null;
		boolean flag = false;

		try {
			URL url = new URL(this.urlString);
			urlInputStream = url.openConnection().getInputStream();
			spf = SAXParserFactory.newInstance();
			if (spf != null) {
				sp = spf.newSAXParser();
				sp.parse(urlInputStream, this);
			}
			flag = true;
		} /*
			 * Exceptions need to be handled MalformedURLException
			 * ParserConfigurationException IOException SAXException
			 */

		catch (Exception ex) {
			logger.error("Exception ex: {}", ex.getMessage());
		} finally {
			try {
				if (urlInputStream != null)
					urlInputStream.close();
			} catch (Exception ex) {
				logger.error("Exception ex: {}", ex.getMessage());
			}
		}
		return flag;
	}

	public RssFeed getFeed() {
		return (this.rssFeed);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equalsIgnoreCase("channel"))
			this.rssFeed = new RssFeed();
		else if (qName.equalsIgnoreCase("item") && (this.rssFeed != null)) {
			this.item = new Item();
			this.rssFeed.addItem(this.item);
		} else if (qName.equalsIgnoreCase("image") && (this.rssFeed != null))
			this.imgStatus = true;
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (this.rssFeed == null)
			return;

		if (qName.equalsIgnoreCase("item"))
			this.item = null;

		else if (qName.equalsIgnoreCase("image"))
			this.imgStatus = false;

		else if (qName.equalsIgnoreCase("title")) {
			setTitle();
		}

		else if (qName.equalsIgnoreCase("link")) {
			setLink();
		}

		else if (qName.equalsIgnoreCase("description")) {
			setDescription();
		}

		else if (qName.equalsIgnoreCase("url") && this.imgStatus)
			this.rssFeed.setImageUrl(this.text.toString().trim());

		else if (qName.equalsIgnoreCase("language"))
			this.rssFeed.setLanguage(this.text.toString().trim());

		else if (qName.equalsIgnoreCase("generator"))
			this.rssFeed.setGenerator(this.text.toString().trim());

		else if (qName.equalsIgnoreCase("copyright"))
			this.rssFeed.setCopyright(this.text.toString().trim());

		else if (qName.equalsIgnoreCase("pubDate") && (this.item != null))
			this.item.pubDate = this.text.toString().trim();

		else if (qName.equalsIgnoreCase("category") && (this.item != null))
			this.rssFeed.addItem(this.text.toString().trim(), this.item);

		this.text.setLength(0);
	}

	private void setTitle() {
		if (this.item != null)
			this.item.title = this.text.toString().trim();
		else if (this.imgStatus)
			this.rssFeed.setImageTitle(this.text.toString().trim());
		else
			this.rssFeed.setTitle(this.text.toString().trim());
	}

	private void setLink() {
		if (this.item != null)
			this.item.link = this.text.toString().trim();
		else if (this.imgStatus)
			this.rssFeed.setImageLink(this.text.toString().trim());
		else
			this.rssFeed.link = this.text.toString().trim();
	}

	private void setDescription() {
		if (this.item != null)
			this.item.description = this.text.toString().trim();
		else
			this.rssFeed.description = this.text.toString().trim();
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		this.text.append(ch, start, length);
	}

	public static class RssFeed {
		private String title;
		private String description;
		private String link;
		private String language;
		private String generator;
		private String copyright;
		private String imageUrl;
		private String imageTitle;
		private String imageLink;

		private ArrayList<Item> items;
		private HashMap<String, ArrayList<Item>> category;

		public void addItem(Item item) {
			if (this.items == null)
				this.items = new ArrayList<>();
			this.items.add(item);
		}

		public void addItem(String category, Item item) {
			if (this.getCategory() == null)
				this.category = new HashMap<>();
			if (!this.getCategory().containsKey(category))
				this.getCategory().put(category, new ArrayList<Item>());
			this.getCategory().get(category).add(item);
		}

		public Map<String, ArrayList<Item>> getCategory() {
			return category;
		}

		public int getItemsSize() {
			return items.size();
		}

		public Item getItem(int i) {
			return items.get(i);
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getGenerator() {
			return generator;
		}

		public void setGenerator(String generator) {
			this.generator = generator;
		}

		public String getCopyright() {
			return copyright;
		}

		public void setCopyright(String copyright) {
			this.copyright = copyright;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public String getImageLink() {
			return imageLink;
		}

		public void setImageLink(String imageLink) {
			this.imageLink = imageLink;
		}

		public String getImageTitle() {
			return imageTitle;
		}

		public void setImageTitle(String imageTitle) {
			this.imageTitle = imageTitle;
		}
	}

	public static class Item {
		private String title;
		private String description;
		private String link;
		private String pubDate;

		public String toString() {
			return (this.title + ": " + this.pubDate + "\n" + this.description);
		}
	}

	public String getLastTitle() {
		String res = "<Empty>";
		RssFeed feed = getFeed();
		if (feed.getItemsSize() > 0) {
			res = feed.getItem(0).title;
		}
		return res;
	}

	public void printRSS() {
		RssFeed feed = getFeed();
		logger.info("Neo: Feed parsed...");
		// Listing all categories & the no. of elements in each category
		if (feed.getCategory() != null) {
			logger.info("Category: ");
			for (String category : feed.getCategory().keySet()) {
				logger.info("{0}: {1}", category, (feed.getCategory().get(category)).size());
			}
		}
		// Listing all items in the feed
		for (int i = 0; i < feed.getItemsSize(); i++)
			logger.info("{0}: {1}", i, feed.getItem(i).title);
	}

	@Override
	public void getPosts(NeoManagePost addPost) {
		if (parse()) {
			RssFeed feed = getFeed();
			// Iterate over all items in the feed
			for (Item it : feed.items) {
				addPost.addPost(it.title, it.description, it.link, it.pubDate);
			}
		}
	}
}
