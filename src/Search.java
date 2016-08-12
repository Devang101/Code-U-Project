import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class Search {
	private static HashMap<Integer, Integer> results = new HashMap<Integer, Integer>();
	
	private static void updateResults(String word){
		HashMap<Integer, Integer> idToTermCount = Database.masterDB.get(word);
		if (idToTermCount == null){
			return;
		}
		for (Integer urlID : idToTermCount.keySet()){
			Integer termCount = Database.masterDB.get(word).get(urlID);
			Integer termCount2 = results.get(urlID);
			Integer newRelevance = (termCount == null? 0:termCount) + (termCount2 == null? 0:termCount2);
			results.put(urlID, newRelevance);
		}
	}
	
	 private static List<Entry<String, Integer>> sortMap(HashMap<String, Integer> map){
		  Comparator<Entry<String,Integer>> SortByRelevancy = new Comparator<Entry<String,Integer>>(){
			  public int compare(Entry<String,Integer> e1, Entry<String,Integer> e2) {
				  return (e1.getValue()<= e2.getValue())? 1 : -1;
			  }
		  };
		  List<Entry<String,Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		  Collections.sort(list, SortByRelevancy);
		  return list;
	  }
	
	private static void displayResults(HashMap<Integer, Integer> termcounts){
		HashMap<String, Integer> finalResults = new HashMap<String, Integer>();
		for (Integer urlID : termcounts.keySet()){
			@SuppressWarnings("rawtypes")
			ArrayList urlAndRelScore = Database.urlDB.get(urlID);
			Integer pageRankRelevancy = (Integer) urlAndRelScore.get(1);
			String url = (String) urlAndRelScore.get(0);
			Integer termCount = results.get(urlID);
			finalResults.put(url, termCount * 3 + pageRankRelevancy/4);
		}
		System.out.println(sortMap(finalResults));
	}
	
	public static void main(String[] args){
		Database.populateMasterDB();
		Database.populateUrlDB();
		Scanner scan = new Scanner(System.in);
		System.out.println("Search: ");
		String input = scan.nextLine();
		try{
		StringTokenizer st = new StringTokenizer(input);
		while(st.hasMoreTokens())
		{
			String word = st.nextToken();
			updateResults(word.toLowerCase().trim());
		}
	
		
		displayResults(results);
		System.out.println("Got " + results.size() + " results!");
		}
		catch(Exception e){
			System.out.println("No results found :(");
		}
		scan.close();
	}
}
