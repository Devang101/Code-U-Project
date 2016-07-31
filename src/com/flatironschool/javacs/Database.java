package com.flatironschool.javacs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

public  class Database {
    public static HashMap<String, HashMap<Integer, Integer>> masterDB;
    public static  HashMap<String, Integer> urlDB;
    public static int ID = 0;
    public static int count = 0;
    
    
    
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
        masterDB = new HashMap<String, HashMap<Integer, Integer>>();
        for(int i=0; i<21;i++){
            masterDB.put("word"+i, new HashMap<Integer, Integer>());
            masterDB.get("word"+i).put(i, i);
            masterDB.get("word"+i).put(i+2, i+2);
            masterDB.get("word"+i).put(i+3, i+3);
            masterDB.get("word"+i).put(i+4, i+4);
            masterDB.get("word"+i).put(i+5, i+5);
        }
        
        System.out.println("about to");
        exportMasterDBToCSV();
        
    }
    
    
    
    
}
