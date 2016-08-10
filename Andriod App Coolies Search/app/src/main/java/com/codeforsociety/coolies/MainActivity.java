package com.codeforsociety.coolies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity {
    public static HashMap<String, HashMap<Integer, Integer>> wordsDatabase;
    public static HashMap<Integer, ArrayList> urlDatabase;
    public static HashMap<String,Integer> urlToID;
    private static HashMap<Integer, Integer> results = new HashMap<Integer, Integer>();
    public static  String resText = "";
    public static double duration;
    public static String url = "https://en.wikipedia.org/wiki/";

    public static List<String> resultsToDisplayArrayList;
    public TextView tv;
    public SearchView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateMasterDB();
        populateUrlDB();
        sv = (SearchView) findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public void callSearch(String query) {
                resultsToDisplayArrayList = new ArrayList<>();
                long startTime = System.nanoTime();
                doSearch(query);
                long endTime = System.nanoTime();
                double dur = (endTime - startTime)/1000000.0;
                duration = dur;
                setTextRes("");
                Intent intent = new Intent(getBaseContext(), SearchResults.class);
                startActivity(intent);
            }
        });
    }



    public void doSearch(String query){
        results = new HashMap<>();
        StringTokenizer st = new StringTokenizer(query);
        while(st.hasMoreTokens())
        {
            String word = st.nextToken();
            updateResults(word.toLowerCase().trim());
        }
        displayResults(results);
    }

    public void setTextRes(String word){
        if(resultsToDisplayArrayList.size()>1){
            resText = word+ resultsToDisplayArrayList.size()+" results found.  It took "+duration+" secs";

        }else{
            resText = ":( no results found";
        }
    }
    public void updateResults(String word) {
        HashMap<Integer, Integer> IDToTermcount = wordsDatabase.get(word);
    try {
        for (Integer urlID : IDToTermcount.keySet()) {
            Integer TermCount = wordsDatabase.get(word).get(urlID);
            Integer TermCount2 = results.get(urlID);
            Integer newRelevance = (TermCount == null ? 0 : TermCount) + (TermCount2 == null ? 0 : TermCount2);
            results.put(urlID, newRelevance);
        }
    }catch (Exception e){
        setTextRes(word);
    }
    }

    public  List<Map.Entry<String, Integer>> SortMap(HashMap<String, Integer> map)
    {
        Comparator<Map.Entry<String,Integer>> SortByRelevancy = new Comparator<Map.Entry<String,Integer>>()
        {
            public int compare(Map.Entry<String,Integer> e1, Map.Entry<String,Integer> e2)
            {
                return (e1.getValue()<= e2.getValue())? 1 : -1;
            }
        };

        List<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, SortByRelevancy);
        return list;
    }

    public void displayResults(HashMap<Integer, Integer> termcounts)
    {
        HashMap<String, Integer> finalResults = new HashMap<String, Integer>();
        for(Integer urlID : termcounts.keySet())
        {
            Integer pageRankRelevancy = (Integer)urlDatabase.get(urlID).get(1);
            String url = (String) urlDatabase.get(urlID).get(0);
            Integer termCount = results.get(urlID);

            finalResults.put(url, termCount * 3 + pageRankRelevancy);
        }

        resultsToDisplayArrayList.addAll(finalResults.keySet());
        SortMap(finalResults);

    }


    public void populateMasterDB(){
        String csvFile = "MasterDB.csv";
        String line = "";
        String csvSplitBy = ",";
        try {
            wordsDatabase = new HashMap<String, HashMap<Integer, Integer>>();

            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(csvFile)));


            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] array = line.split(csvSplitBy);

                wordsDatabase.put(array[0], new HashMap<Integer, Integer>());
                for(int i=1; i<array.length;i++){
                    wordsDatabase.get(array[0]).put(extractID(array[i]), extractRelevancy(array[i]));
                }
            }

            System.out.println("Master DataB Is Ready!");
            System.out.print("size of "+wordsDatabase.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void populateUrlDB(){
        String csvFile = "urlDB.csv";
        String line = "";
        String csvSplitBy = ",";

        try  {
            urlDatabase = new HashMap<Integer, ArrayList>();
            urlToID = new HashMap<>();

            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(csvFile)));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] array = line.split(csvSplitBy);
                Integer id,rel;

                id = extractID(array[array.length-1]);
                rel = extractRelevancy(array[array.length-1]);

                String url = (array.length==2 ? array[0]:array[0]+array[1]);

                //using a hashmap and not array because url and rel are not of the same type
                urlDatabase.put(id, new ArrayList());
                urlDatabase.get(id).add(url);
                urlDatabase.get(id).add(rel);
                urlToID.put(url,id);
            }
            System.out.println("Url Database Is Ready!...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public  Integer extractID(String s){
        try{
            return Integer.valueOf(s.substring(0,s.lastIndexOf('-')));
        } catch (Exception e){
            System.out.println(s);
        }
        return 0;
    }

    public  Integer extractRelevancy(String s){
        try{
            return Integer.valueOf(s.substring(s.indexOf('-')+1));

        }catch (Exception e){
            System.out.println(s);
            return 0;

        }
    }

}
