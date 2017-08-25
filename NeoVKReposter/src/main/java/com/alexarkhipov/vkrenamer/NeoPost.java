package com.alexarkhipov.vkrenamer;

public class NeoPost {

	private String title;
	private String description;
	private String link;
	private String pubDate;

	public String toString() {
		return (this.getTitle() + ": " + this.getPubDate() + "\n" + this.getDescription());
	}

	public NeoPost(String title, String description, String link, String pubDate) {
		this.setTitle(title);
		this.setDescription(description);
		this.setLink(link);
		this.setPubDate(pubDate);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPubDate() {
		return pubDate;
	}

	private void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

}
