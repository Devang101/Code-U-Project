# Code-U-Project

Code U is a invite only development program hosted by Google, where I was given an online curriculum, a Google Engineer as a mentor, and a capstone project where my partner and I designed and developed a search engine from starter code to present at the Code U summit at Google’s HQ in Mountain View. We took the components of a rudamentary search engine, put them together, and created our own search engine with improvements. The orginal program was a wikipedia search engine that could only perform 1 word queries.

Key improvements that we implemented include

  -Ignoring Common Words as a factor in the relevancy score given to webpages
  
  -Multiple word/line queries
  
  -Count only page that have the word “Google” more than 2 times
  
    -Therefore search results are only google related
    
    -Implemented to make our search engine special in that it was a search engine that only displayed Wikipedia urls related to Google
    
  -Timing of search results
  
  -Friendly Android User Interface
  
  -Excel database
  
  -Desgined our own page rank algorithm
  
    +1/4 - user clicks link
    
    +1/4 - each outgoing link
    
    +1/4 - each incoming link
    
    +1/4 - number of translations on a page (more tranlations on a wikipedia page indicate that someone took the time to translate this article and therefore it must be important.
    
    +3 - search term found on page
    
Some pages orginally timeout during indexing. We think this is because Wikipedia has some sort of security for certain pages that they don't want to be hacked. We thus implemented a try catch method that would skip the url if it could not index it on the 10th try.

The biggest improvement we made was ditching the orginal program's redis database. On the free version of redis, we were only able to index 20 pages, because redis had a certain mb limit. Instead we stored our application's data in a .csv file which was imported and exported when our application was opened and closed. Making this change allowed us to index 2,000 wikipedia pages as opposed to 20.
Thus we improved the storage capacity of the search engine that was given to us by 100X, while maintaining an average 0.006 sec search speed.
