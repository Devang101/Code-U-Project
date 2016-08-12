


import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiFetcher{
	private long lastRequestTime = -1;
	private long minInterval = 1000;
	private int TimeOutcount = 0;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements and
	 * the number of translations the page has.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public DataNode fetchData(String url) throws IOException{
		sleepIfNeeded();
		try{
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
			TimeOutcount = 0;
			return new DataNode(paras, translations);
		}
		catch(Exception e){
			sleepIfNeeded();
			System.out.println("Time out error on " + url + " ... trying again");
			TimeOutcount++;
			if (TimeOutcount >= 10){
				return null;
			}
			return fetchData(url);
		}
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
	
	public static void main (String[] args) throws IOException{
		WikiFetcher fetcher = new WikiFetcher();
		DataNode results = fetcher.fetchData("https://en.wikipedia.org/wiki/Larry_Page");
		System.out.println("Number of translations: " + results.getTranslations());
		//System.out.println("Paragraphs: " + results.getParagraphs());
	}
}
