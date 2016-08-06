import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public  class Database {
    public static HashMap<String, HashMap<Integer, Integer>> masterDB;
    public static  HashMap<Integer, ArrayList> urlDB;
    public static int ID = 0;
    public static int count = 0;
    
    
    public static void populateMasterDB(){
        System.out.println("master DB");
        
        masterDB = new HashMap<String, HashMap<Integer, Integer>>();
        String csvFile = "MasterDB.csv";
        String line = "";
        String csvSplitBy = ";";
        
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
    public static void populateUrlDB(){
        System.out.println("Populating urlDB");
        urlDB = new HashMap<Integer, ArrayList>();
        String csvFile = "urlDB.csv";
        String line = "";
        String csvSplitBy = ";";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                // use comma as separator
                String[] array = line.split(csvSplitBy);
                
                Integer urlID = Integer.valueOf(array[0]);
                String url = array[1];
                Integer fixedRel = Integer.valueOf(array[2]);
                
                //System.out.println(Arrays.toString(array));
        
                
                urlDB.put(Integer.valueOf(urlID), new ArrayList());
                urlDB.get(Integer.valueOf(urlID)).add(url);
                urlDB.get(Integer.valueOf(urlID)).add(fixedRel);
                
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void printDB(){
        System.out.println(masterDB.toString());
        System.out.println(urlDB.toString());
        
    }
    
    public static Integer extractID(String s){
        try{
            return Integer.valueOf(s.substring(0,s.lastIndexOf('-')));
            
        } catch (Exception e){
            //System.out.println(s);
            
            //return Integer.valueOf(s.substring(0,s.lastIndexOf('-')));
            
        }
        return 0;
    }
    
    public static Integer extractRelevancy(String s){
        try{
            return Integer.valueOf(s.substring(s.indexOf('-')+1));
            
        }catch (Exception e){
            //System.out.println(s);
        }
        return 0;
    }
    
    
    public static void exportMasterDBToCSV(){
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("MasterDB.csv"));
            
            for(String word: masterDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(word);
                sb.append(';');
                for(Integer id: masterDB.get(word).keySet()){
                    sb.append(""+id+'-'+masterDB.get(word).get(id));
                    sb.append(';');
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
    
    
    public static void exportUrlDBToCSV(){
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("urlDB.csv"));
            
            for(Integer urlID: urlDB.keySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(urlID);
                sb.append(';');
                String url = (String) urlDB.get(urlID).get(0);
                String fixedRel = urlDB.get(urlID).get(1).toString();
                sb.append(url+';'+fixedRel);
                sb.append(';');
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
        //rintDB();
        
    }
    
    
    
    
}
