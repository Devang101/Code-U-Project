import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.select.Elements;

public class cooliesIndexer {
    public int UrlsIndexed = 0;
    
    
    /**
     * Constructor.
     *
     * @param jedis
     */
    public cooliesIndexer() {
    }
    
    
    
    /**
     * Add a page to the index.
     *
     * @param url         URL of the page.
     * @param paragraphs  Collection of elements that should be indexed.
     */
    public void indexPage(String url, int translations, int OutGoingUrls, Elements paragraphs, TermCounter tc) {
        
        // make a TermCounter and count the terms in the paragraphs
        tc.processElements(paragraphs);
        
        if(checkGoogleCount(tc)){
            System.out.println("Indexing: " + url);
            
            // push the contents of the TermCounter to Redis
            pushToDatabase(url, translations, OutGoingUrls, tc);
            UrlsIndexed++;
        }
        
    }
    
    /**
     * Pushes the contents of the TermCounter to Redis.
     *
     * @param tc
     * @return List of return values from Redis.
     */
    public void pushToDatabase(String url, int translations, int OutGoingUrls, TermCounter tc) {
        
        for (String term: tc.keySet()) {
            int count =tc.get(term);
            if(Database.masterDB.containsKey(term)){
                Database.masterDB.get(term).put(getUrlID(url, translations, OutGoingUrls), count);
            }else{
                Database.masterDB.put(term, new HashMap<Integer, Integer>());
                Database.masterDB.get(term).put(getUrlID(url, translations, OutGoingUrls), count);
            }
        }
        
            
    }
    
    public Integer getUrlID(String url, int translations, int OutGoingUrls) {
    	for(Integer urlID : Database.urlDB.keySet())
    	{
    		if(url == (String) Database.urlDB.get(urlID).get(0))
    		{
    			return urlID;
    		}
    	}
    	Integer id = Database.ID;
		Database.urlDB.put(id, new ArrayList());
		Database.urlDB.get(id).add(url);
		Database.urlDB.get(id).add(translations + OutGoingUrls);
		Database.ID++;
        return id;
    }
    
    public boolean checkGoogleCount(TermCounter tc){
        return tc.get("google")>2;
        
    }
}
    
    


