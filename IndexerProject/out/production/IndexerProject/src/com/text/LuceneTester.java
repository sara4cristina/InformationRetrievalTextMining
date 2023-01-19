package com.text;

import java.io.IOException;
import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
//import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
public class LuceneTester
{
    String indexDir;//= "D:\\facultate\\TEXTMining\\ProjectIndexer\\index";
    String dataDir;// = "D:\\facultate\\TEXTMining\\ProjectIndexer\\data";
    Indexer indexer;
    Searcher searcher;

    public LuceneTester(String indexDir, String dataDir){
        this.indexDir = indexDir;
        this.dataDir = dataDir;
    }

    public void createIndex() throws IOException, Exception
    {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: " +(endTime-startTime)+" ms");
    }
    public void search(String searchQuery) throws IOException, ParseException
    {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        //Verificam si cuvantul cu diacritice si cel fara diacritice
        TopDocs hits = searcher.search(searchQuery);
        if(hits.totalHits == 0)
            hits=searcher.search(StringUtils.stripAccents(searchQuery));
        long endTime = System.currentTimeMillis();
        System.out.println("We found the search word: \"" + searchQuery + "\" in " + hits.totalHits + " documents. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs)
        {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
        }
        searcher.close();
    }
}