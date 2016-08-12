import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.select.Elements;

public class cooliesIndexer {
    public int UrlsIndexed = 0;

    public void indexPage(String url, int translations, int OutGoingUrls, Elements paragraphs, TermCounter tc){
        // make a TermCounter and count the terms in the paragraphs
        tc.processElements(paragraphs);
        if(checkGoogleCount(tc)){
            System.out.println("Indexing: " + url);
            // push the contents of the TermCounter to Redis
            pushToDatabase(url, translations, OutGoingUrls, tc);
            UrlsIndexed++;
        }
    }

    public void pushToDatabase(String url, int translations, int OutGoingUrls, TermCounter tc){
        for (String term: tc.keySet()){
            int count =tc.get(term);
            if(Database.masterDB.containsKey(term)){
                Database.masterDB.get(term).put(getUrlID(url, translations, OutGoingUrls), count);
            }
            else{
                Database.masterDB.put(term, new HashMap<Integer, Integer>());
                Database.masterDB.get(term).put(getUrlID(url, translations, OutGoingUrls), count);
            }
        }     
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer getUrlID(String url, int translations, int OutGoingUrls){
    	for (Integer urlID : Database.urlDB.keySet()){
    		if (url == (String) Database.urlDB.get(urlID).get(0)){
    			return urlID;
    		}
    	}
    	Integer id = Database.ID;
		Database.urlDB.put(id, new ArrayList());
		ArrayList urlAndRelScore = Database.urlDB.get(id);
		urlAndRelScore.add(url);
		urlAndRelScore.add(translations + OutGoingUrls);
		Database.ID++;
        return id;
    }
    
    public boolean checkGoogleCount(TermCounter tc){
        return tc.get("google") > 2;
    }
}
    
    


