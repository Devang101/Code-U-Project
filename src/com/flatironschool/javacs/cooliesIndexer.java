import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.FileWriter;
import java.util.Arrays;

import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class cooliesIndexer {
	private static final char DEFAULT_SEPARATOR = ',';
	private  HashMap<String, HashMap<Integer, Integer>> masterDB;
	private  HashMap<String, Integer> urlDB;
	private int ID = 0;


	/**
	 * Constructor.
	 * 
	 * @param jedis
	 */
	public cooliesIndexer() {
		masterDB = new HashMap<String, HashMap<Integer, Integer>>();
		urlDB  = new HashMap<String, Integer>();

	}



	/**
	 * Add a page to the index.
	 * 
	 * @param url         URL of the page.
	 * @param paragraphs  Collection of elements that should be indexed.
	 */
	public void indexPage(String url, Elements paragraphs, TermCounter tc) {

		// make a TermCounter and count the terms in the paragraphs
		tc.processElements(paragraphs);

		if(checkGoogleCount(tc)){
			System.out.println("Indexing: " + url);

			// push the contents of the TermCounter to Redis
			pushToDatabase(tc);
		}

	}

	/**
	 * Pushes the contents of the TermCounter to Redis.
	 * 
	 * @param tc
	 * @return List of return values from Redis.
	 */
	public void pushToDatabase(TermCounter tc) {

		for (String term: tc.keySet()) {

			int count = tc.get(term);
			if(Database.masterDB.containsKey(term)){
				Database.masterDB.get(term).put(getUrlID(tc.getLabel()), count);
			}else{
				Database.masterDB.put(term, new HashMap<Integer, Integer>());
				Database.masterDB.get(term).put(getUrlID(tc.getLabel()), count);
			}
		}


	}


	private Integer getUrlID(String url) {
		// TODO Auto-generated method stub
		if(Database.masterDB.containsKey(url)){
			return getUrlDB().get(url);
		}else{
			Database.urlDB.put(url, Database.ID);
			Database.ID++;
			return Database.ID-1;
		}
	}

	public boolean checkGoogleCount(TermCounter tc){
		return tc.get("google")>5;

	}





	public HashMap<String, Integer> getUrlDB() {
		return urlDB;
	}


	public static Writer writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

		boolean first = true;

		//default customQuote is empty

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());
		return w;


	}
	private static String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}


}
