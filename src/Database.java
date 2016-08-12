import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    public static HashMap<String, HashMap<Integer, Integer>> masterDB;
    @SuppressWarnings("rawtypes")
	public static  HashMap<Integer, ArrayList> urlDB;
    public static int ID = 0;
    public static int count = 0;
    private static String csvSplitBy = ";"; 
    private static PrintWriter pw;
    
    public static void populateMasterDB(){
        System.out.println("Populating master DB");
        masterDB = new HashMap<String, HashMap<Integer, Integer>>();
        String csvFile = "MasterDB.csv";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] array = line.split(csvSplitBy);
                masterDB.put(array[0], new HashMap<Integer, Integer>());
                for (int i=1; i<array.length; i++){
                    masterDB.get(array[0]).put(extractID(array[i]), extractRelevancy(array[i]));
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void populateUrlDB(){
        System.out.println("Populating urlDB");
        urlDB = new HashMap<Integer, ArrayList>();
        String csvFile = "urlDB.csv";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] array = line.split(csvSplitBy); 
                Integer urlID = Integer.valueOf(array[0]);
                String url = array[1];
                Integer fixedRel = Integer.valueOf(array[2]);
                urlDB.put(Integer.valueOf(urlID), new ArrayList());
                ArrayList urlAndRelScore = urlDB.get(Integer.valueOf(urlID));
                urlAndRelScore.add(url);
                urlAndRelScore.add(fixedRel);                
            }            
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void printDB(){
        System.out.println(masterDB.toString());
        System.out.println(urlDB.toString());
        
    }
    
    public static Integer extractID(String s){
        try{
            return Integer.valueOf(s.substring(0, s.lastIndexOf('-')));
        } 
        catch (Exception e){
        	return 0;
        }
    }
    
    public static Integer extractRelevancy(String s){
        try{
            return Integer.valueOf(s.substring(s.indexOf('-') + 1));
        }
        catch (Exception e){
        	return 0;
        }
    }
    
    public static void exportMasterDBToCSV(){
        try {
            pw = new PrintWriter(new File("MasterDB.csv"));
            //Ex. term;url1-termcount;url2-termcount;url3-termcount
            for (String word: masterDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(word);
                sb.append(csvSplitBy);
                for (Integer urlID: masterDB.get(word).keySet()){
                	Integer termcount = masterDB.get(word).get(urlID);
                    sb.append(urlID + '-' + termcount);
                    sb.append(csvSplitBy);
                }
                sb.append('\n');
                pw.write(sb.toString());
            }
            pw.close();
            System.out.println("Finished Writing masterDB CSV File!");
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void exportUrlDBToCSV(){
        try {
            pw = new PrintWriter(new File("urlDB.csv"));
            for (Integer urlID: urlDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(urlID);
                sb.append(csvSplitBy);
                String url = (String) urlDB.get(urlID).get(0);
                String fixedRel = urlDB.get(urlID).get(1).toString();
                sb.append(url+csvSplitBy+fixedRel);
                sb.append(csvSplitBy);
                sb.append('\n');
                pw.write(sb.toString());
            }
            pw.close();
            System.out.println("Finished Writing urlDB CSV File!");
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        /*		masterDB = new HashMap<String, HashMap<Integer, Integer>>();
         for(int i=0; i<21;i++){
         masterDB.put("word"+i, new HashMap<Integer, Integer>());
         masterDB.get("word"+i).put(i, i);
         masterDB.get("word"+i).put(i+2, i+2);
         masterDB.get("word"+i).put(i+3, i+3);
         masterDB.get("word"+i).put(i+4, i+4);
         masterDB.get("word"+i).put(i+5, i+5);
         }
         
         System.out.println("about to");
         exportMasterDBToCSV();*/
        populateMasterDB();
        populateUrlDB();
        //printDB();
    }    
}
