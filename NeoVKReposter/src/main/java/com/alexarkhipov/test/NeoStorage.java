package com.alexarkhipov.test;

public interface NeoStorage {

	public boolean checkPost(String title);

	public void savePost(NeoPost post);

	public String getLastPublishedPostTitle();
}
