package com.flatironschool.javacs;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiFetcher {
	private long lastRequestTime = -1;
	private long minInterval = 1000;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements and
	 * the number of translations the page has.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public DataNode fetchData(String url) throws IOException {
		sleepIfNeeded();

		// download and parse the document
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();

		// select the content text and pull out the paragraphs.
		Element content = doc.getElementById("mw-content-text");
		
		//select the translations column and pull the number of translations in that list
		Element translationColumn = doc.getElementById("p-lang");
		int translations = translationColumn.select("li").size()-1;

		// TODO: avoid selecting paragraphs from sidebars and boxouts
		Elements paras = content.select("p");
		return new DataNode(paras, translations);
	}

	/**
	 * Reads the contents of a Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements readWikipedia(String url) throws IOException {
		URL realURL = new URL(url);

		// assemble the file name
		String slash = File.separator;
		String filename = "resources" + slash + realURL.getHost() + realURL.getPath();

		// read the file
		InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
		Document doc = Jsoup.parse(stream, "UTF-8", filename);

		// TODO: factor out the following repeated code
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		
		return paras;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					//System.out.println("Sleeping until " + nextRequestTime);
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchContent.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
	
	public static void main (String[] args) throws IOException
	{
		WikiFetcher wf = new WikiFetcher();
		DataNode results = wf.fetchData("https://en.wikipedia.org/wiki/Kobe_Bryant");
		System.out.println("Number of translations: " + results.getTranslations());
		//System.out.println("Paragraphs: " + results.getParagraphs());
	}
}
