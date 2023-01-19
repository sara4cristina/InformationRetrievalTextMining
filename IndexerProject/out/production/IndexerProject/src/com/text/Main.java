package com.text;

import com.text.LuceneTester;
import org.apache.lucene.queryParser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.Normalizer;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException {

         //Please insert the path to the folder where the documents are placed!
         String dataDir = "D:\\facultate\\TEXTMining\\ProjectIndexer\\data";
         String indexDir = dataDir + "\\index";
         new File(indexDir).mkdirs();

         //Insert the word that you want to search
         String searchWord = "casuță";

        LuceneTester tester;
        try
        {
            tester = new LuceneTester(indexDir, dataDir);
            tester.createIndex();
            tester.search("căsuță");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
