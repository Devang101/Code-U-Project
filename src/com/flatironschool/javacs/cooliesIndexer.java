package com.flatironschool.javacs;

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
            int relevancyScore =tc.get(term)+tc.getTranslations();
            if(Database.masterDB.containsKey(term)){
                Database.masterDB.get(term).put(getUrlID(tc.getLabel()), relevancyScore);
            }else{
                Database.masterDB.put(term, new HashMap<Integer, Integer>());
                Database.masterDB.get(term).put(getUrlID(tc.getLabel()), relevancyScore);
            }
        }
        
        
    }
    
    
    private Integer getUrlID(String url) {
        // TODO Auto-generated method stub
        if(Database.masterDB.containsKey(url)){
            return Database.urlDB.get(url);
        }else{
            Database.urlDB.put(url, Database.ID);
            Database.ID++;
            return Database.ID-1;
        }
    }
    
    public boolean checkGoogleCount(TermCounter tc){
        return tc.get("google")>5;
        
    }
    
    
    
}
