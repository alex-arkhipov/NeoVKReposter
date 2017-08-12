/**
 * 
 */
package com.alexarkhipov.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author arkhipov
 *
 */
@Component
public class NeoFileStorage implements NeoStorage {
	private static final Logger logger = LoggerFactory.getLogger(NeoFileStorage.class);
	private String filename;

	/**
	 * 
	 */
	public NeoFileStorage(String filename) {
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.alexarkhipov.test.NeoStorage#checkPost(java.lang.String)
	 */
	@Override
	public boolean checkPost(String title) {
		String titleFromFile = "";
		titleFromFile = readTitle();

		// Check post
		return title.equals(titleFromFile);
	}

	private String readTitle() {
		String titleFromFile = "";
		Path path = Paths.get(filename);
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			titleFromFile = reader.readLine();
		} catch (IOException ex) {
			logger.error("Error during reading: {}", ex.getMessage());
		}

		return titleFromFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.alexarkhipov.test.NeoStorage#savePost(java.lang.String) File is in
	 * current folder
	 */
	@Override
	public void savePost(NeoPost post) {

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"))) {

			writer.write(post.getTitle());
		} catch (IOException ex) {
			logger.error("Error during saving: {}", ex.getMessage());
		}
	}

	@Override
	public String getLastPublishedPostTitle() {
		return readTitle();
	}

}
