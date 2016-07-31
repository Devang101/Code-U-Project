import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

public  class Database {
    public static HashMap<String, HashMap<Integer, Integer>> masterDB;
    public static  HashMap<String, Integer> urlDB;
    public static int ID = 0;
    public static int count = 0;
    
    
    public static void populateMasterDB(){
        masterDB = new HashMap<String, HashMap<Integer, Integer>>();
        String csvFile = "MasterDB.csv";
        String line = "";
        String csvSplitBy = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            while ((line = br.readLine()) != null) {
                
                // use comma as separator
                String[] array = line.split(csvSplitBy);
                
                masterDB.put(array[0], new HashMap<Integer, Integer>());
                for(int i=1; i<array.length;i++){
                    masterDB.get(array[0]).put(extractID(array[i]), extractRelevancy(array[i]));
                }
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void printDB(){
        System.out.println(masterDB.toString());
        
    }
    
    public static Integer extractID(String s){
        return Integer.valueOf(s.substring(0,s.indexOf('-')));
    }
    
    public static Integer extractRelevancy(String s){
        return Integer.valueOf(s.substring(s.indexOf('-')+1));
    }
    
    
    public static void exportMasterDBToCSV(){
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("MasterDB.csv"));
            
            for(String word: masterDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(word);
                sb.append(',');
                for(Integer id: masterDB.get(word).keySet()){
                    sb.append(""+id+'-'+masterDB.get(word).get(id));
                    sb.append(',');
                }
                sb.append('\n');
                pw.write(sb.toString());
            }
            pw.close();
            System.out.println("Finished Writing masterDB CSV File!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void exportUrlDBToCSV(){
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("UrlDB.csv"));
            
            for(String url: urlDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                sb.append(',');
                sb.append(urlDB.get(url));
                sb.append('\n');
                pw.write(sb.toString());
            }
            pw.close();
            System.out.println("Finished Writing urlDB CSV File!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        printDB();
        
    }
    
    
    
    
}
