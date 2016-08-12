
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiCrawler {
    @SuppressWarnings("unused")
	private final String source;
    private static int count;
    private static HashSet<String> skipUrls = new HashSet<String>();
    private static cooliesIndexer index;
    // queue of URLs to be indexed
    private Queue<String> queue = new LinkedList<String>();
    // fetcher used to get pages from Wikipedia
    final static WikiFetcher fetcher = new WikiFetcher();
    private static int numberOfPagesYouWantToIndex = 15000;
    
    public WikiCrawler(String source, cooliesIndexer indexer) {
        index = indexer;
        this.source = source;
        queue.offer(source);
    }
    
    public int queueSize() {
        return queue.size();
    }
    
    public boolean isCrawled(String url){
    	return skipUrls.contains(url);
    }
    
    public Integer isIndexed(String url){
    	for (Integer urlID : Database.urlDB.keySet()){
    		if (url.equals((String) Database.urlDB.get(urlID).get(0))){
    			return urlID;
    		}
    	}
    	return null;
    }

    @SuppressWarnings("unchecked")
	public String crawl() throws IOException {
        if (queue.isEmpty()) {
            return null;
        }
        String url = queue.poll();
        url = url.substring(url.lastIndexOf("/") + 1);
        System.out.println("Crawling " + url);
        Integer urlID = isIndexed(url);
        if (urlID != null) 
        {
            System.out.println("Already indexed." + url);
            //update relevancy score
            @SuppressWarnings("rawtypes")
			ArrayList urlAndRelScore = Database.urlDB.get(urlID);
            int initialRelevancy = (Integer) urlAndRelScore.get(1);
            int newRel = initialRelevancy + 1;
            urlAndRelScore.add(url);
            urlAndRelScore.add(newRel);
            return null;
        }
        
        if (isCrawled(url)){
        	System.out.println("Already Crawled" + url);
        	return null;
        }
        skipUrls.add(url);
        DataNode stuffOnThisPage = fetcher.fetchData("https://en.wikipedia.org/wiki/" + url);
        if(stuffOnThisPage == null){
        	return null;
        }
        Elements paragraphs = stuffOnThisPage.getParagraphs();
        int translations = stuffOnThisPage.getTranslations();
        int OutGoingUrls = queueInternalLinks(paragraphs);
        TermCounter counter = new TermCounter();
        index.indexPage(url, translations, OutGoingUrls, paragraphs,counter);   
        return url;
    }
    
    // NOTE: absence of access level modifier means package-level
    int queueInternalLinks(Elements paragraphs) {
    	int total = 0;
        for (Element paragraph: paragraphs) {
            total += queueInternalLinks(paragraph);
        }
        return total;
    }
    
    private int queueInternalLinks(Element paragraph) {
        Elements elts = paragraph.select("a[href]");
        int totalUrls = elts.size();
        for (Element elt: elts) {
            String relURL = elt.attr("href");
            if (relURL.startsWith("/wiki/")) {
                String absURL = "https://en.wikipedia.org" + relURL;
                //System.out.println(absURL);
                queue.offer(absURL);
            }
        }
        return totalUrls;
    }
    
    public static void main(String[] args) throws IOException {
        String source = "https://en.wikipedia.org/wiki/Google";
        cooliesIndexer indexer = new cooliesIndexer();
        WikiCrawler crawler = new WikiCrawler(source, indexer);
        Database.populateMasterDB();
        Database.populateUrlDB();
        //load up queue initially
        Elements paragraphs = fetcher.fetchData(source).getParagraphs();
        if (paragraphs == null){
        	System.out.println("Check internet connection");
        }
        crawler.queueInternalLinks(paragraphs);
        count = 0;
        while (index.UrlsIndexed < numberOfPagesYouWantToIndex && count <20000){
            crawler.crawl();       
            count++;
            if (index.UrlsIndexed % 10 == 0){
            	 Database.exportMasterDBToCSV();
                 Database.exportUrlDBToCSV();
            }
        } 
        Database.exportMasterDBToCSV();
        Database.exportUrlDBToCSV();
    }
}
