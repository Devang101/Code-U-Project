import java.util.HashMap;

public  class Database {
	public static HashMap<String, HashMap<Integer, Integer>> masterDB;
	public static  HashMap<String, Integer> urlDB;
	public static int ID = 0;
	public static int count = 0;
	

	/**
	 * Pushes the contents of the TermCounter to Redis.
	 * 
	 * @param tc
	 * @return List of return values from Redis.
	 */
	public void pushToDatabase(TermCounter tc) {

		for (String term: tc.keySet()) {

			int count = tc.get(term);
			if(masterDB.containsKey(term)){
				masterDB.get(term).put(getUrlID(tc.getLabel()), count);
			}else{
				masterDB.put(term, new HashMap<Integer, Integer>());
				masterDB.get(term).put(getUrlID(tc.getLabel()), count);
			}
		}
	}
	
	public void populateFromCSV(String csvFileName){
		
	}
	
	private Integer getUrlID(String url) {
		// TODO Auto-generated method stub
		if(Database.masterDB.containsKey(url)){
			return urlDB.get(url);
		}else{
			Database.urlDB.put(url, Database.ID);
			Database.ID++;
			return Database.ID-1;
		}
	}
	
	

}
