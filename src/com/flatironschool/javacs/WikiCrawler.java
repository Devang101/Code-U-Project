
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiCrawler {
    // keeps track of where we started
    private final String source;
    private static int count;
    private static HashSet<String> skipUrls = new HashSet<String>();
    
    
    private static cooliesIndexer index;
    //sprivate static TermCounter tc;
    
    // queue of URLs to be indexed
    private Queue<String> queue = new LinkedList<String>();
    
    // fetcher used to get pages from Wikipedia
    final static WikiFetcher wf = new WikiFetcher();
    
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
    
    public boolean isCrawled(String url)
    {
    	return skipUrls.contains(url);
    }
    public Integer isIndexed(String url)
    {
    	for(Integer urlID : Database.urlDB.keySet())
    	{
    		if(url.equals((String) Database.urlDB.get(urlID).get(0)))
    		{
    			return urlID;
    		}
    	}
    	return null;
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
        url = url.substring(url.lastIndexOf("/")+1);
        System.out.println("Crawling " + url);
        
        Integer urlID = isIndexed(url);
        if (urlID != null) 
        {
            System.out.println("Already indexed." + url);
            //update relevancy score
            int initialRelevancy = (Integer) Database.urlDB.get(urlID).get(1);
            int newRel = initialRelevancy + 1;
            Database.urlDB.get(urlID).add(url);
            Database.urlDB.get(urlID).add(newRel);
            return null;
        }
        
        if(isCrawled(url))
        {
        	System.out.println("Already Crawled" + url);
        	return null;
        }
        else
        {
        	skipUrls.add(url);
        }
        
        //if new url index it if google appears twice
        DataNode stuffOnThisPage = wf.fetchData("https://en.wikipedia.org/wiki/" + url);
        if(stuffOnThisPage == null)
        {
        	return null;
        }
        Elements paragraphs = stuffOnThisPage.getParagraphs();
        int translations = stuffOnThisPage.getTranslations();
        int OutGoingUrls = queueInternalLinks(paragraphs);
        
        TermCounter tc = new TermCounter();
        index.indexPage(url, translations, OutGoingUrls, paragraphs,tc);
        
        
        return url;
    }
    
    /**
     * Parses paragraphs and adds internal links to the queue.
     *
     * @param paragraphs
     */
    // NOTE: absence of access level modifier means package-level
    int queueInternalLinks(Elements paragraphs) {
    	int total = 0;
        for (Element paragraph: paragraphs) {
            total += queueInternalLinks(paragraph);
        }
        return total;
    }
    
    /**
     * Parses a paragraph and adds internal links to the queue.
     *
     * @param paragraph
     */
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
        cooliesIndexer n = new cooliesIndexer();
        WikiCrawler wc = new WikiCrawler(source, n);
        //Database.masterDB = new HashMap<String, HashMap<Integer, Integer>>();
        //Database.urlDB = new HashMap<String, HashMap<Integer, Integer>>();
        Database.populateMasterDB();
        Database.populateUrlDB();
        
        
        // for testing purposes, load up the queue
        Elements paragraphs = wf.fetchData(source).getParagraphs();
        if(paragraphs == null)
        {
        	System.out.println("Check internet connection");
        }
        wc.queueInternalLinks(paragraphs);
        
       
        // loop until you come across 1000 pages you already indexed
        count = 0;
        while(index.UrlsIndexed < 5000 && count <50000)
        {
            
            wc.crawl();       
            count++;
            if(index.UrlsIndexed % 500 == 0)
            {
            	 Database.exportMasterDBToCSV();
                 Database.exportUrlDBToCSV();
            }
        } 
        
        Database.exportMasterDBToCSV();
        Database.exportUrlDBToCSV();
    }
}
