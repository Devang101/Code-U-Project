


import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;


public class WikiCrawler {
	// keeps track of where we started
	private final String source;
	private static int count;


	private static cooliesIndexer index;
	private static TermCounter tc;

	// queue of URLs to be indexed
	private Queue<String> queue = new LinkedList<String>();

	// fetcher used to get pages from Wikipedia
	final static WikiFetcher wf = new WikiFetcher();

	private static Database data = new Database();

	/**
	 * Constructor.
	 *
	 * @param source
	 * @param index
	 */
	public WikiCrawler(String source, cooliesIndexer n) {
		index = n;
		this.source = source;
		queue.offer(source);
	}

	/**
	 * Returns the number of URLs in the queue.
	 *
	 * @return
	 */
	public int queueSize() {
		return queue.size();
	}

	/**
	 * Gets a URL from the queue and indexes it.
	 * @param b
	 *
	 * @return Number of pages indexed.
	 * @throws IOException
	 */
	public String crawl() throws IOException {

		if (queue.isEmpty()) {
			return null;
		}
		String url = queue.poll();
		System.out.println("Crawling " + url);


		if (data.urlDB.containsKey(url)) {
			System.out.println("Already indexed.");
			//update fixed relvancy score in URLDB
			return null;
		}

		DataNode data = wf.fetchData(url);
		Elements paragraphs = data.getParagraphs();

		tc.setLabel(url);
		tc.setTranlations(data.getTranslations());

		index.indexPage(url, paragraphs,tc);
		queueInternalLinks(paragraphs);
		return url;
	}

	/**
	 * Parses paragraphs and adds internal links to the queue.
	 *
	 * @param paragraphs
	 */
	// NOTE: absence of access level modifier means package-level
	void queueInternalLinks(Elements paragraphs) {
		for (Element paragraph: paragraphs) {
			queueInternalLinks(paragraph);
		}
	}

	/**
	 * Parses a paragraph and adds internal links to the queue.
	 *
	 * @param paragraph
	 */
	private void queueInternalLinks(Element paragraph) {
		Elements elts = paragraph.select("a[href]");
		for (Element elt: elts) {
			String relURL = elt.attr("href");

			if (relURL.startsWith("/wiki/")) {
				String absURL = "https://en.wikipedia.org" + relURL;
				//System.out.println(absURL);
				queue.offer(absURL);
			}
		}
	}


	public static void main(String[] args) throws IOException {
		data.populateMasterDB();
		String source = "https://en.wikipedia.org/wiki/Google";
		cooliesIndexer n = new cooliesIndexer();
		tc = new TermCounter(source);
		WikiCrawler wc = new WikiCrawler(source, n);
		
		// for testing purposes, load up the queue
		Elements paragraphs = wf.fetchData(source).getParagraphs();
		wc.queueInternalLinks(paragraphs);

		int count = 0;
		// loop until you come across 100 pages you already indexed
		do {
			//System.out.println("KB: " + (double) (Runtime.getRuntime().freeMemory()));
			wc.crawl();

			count ++;
			//System.out.println("KB: " + (double) (Runtime.getRuntime().freeMemory()));
		} while (count<100);

		data.exportMasterDBToCSV();
		data.printDB();




	}
}
