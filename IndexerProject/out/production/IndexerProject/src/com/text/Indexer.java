package com.text;

import java.io.*;
import java.text.Normalizer;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;


import java.util.HashSet;

import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
//import org.apache.commons
//import org.apache.lucene.analysis.ro.RomanianAnalyzer;

public class Indexer {

    private IndexWriter writer;
    public Indexer(String indexDirectoryPath) throws IOException
    {
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));

        //create the indexer
        writer = new IndexWriter(indexDirectory,
                new StandardAnalyzer(Version.LUCENE_36),true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }
    public void close() throws CorruptIndexException, IOException
    {
        writer.close();
    }

    public File removeStopWordsAndDiacritics(File textFile) throws Exception {
        String [] romanianStopWords = {"si", "și", "acea","aceasta","această","aceea","acei","aceia","acel","acela","acele","acelea","acest","acesta","aceste","acestea","aceşti","aceştia","acolo","acord","acum","ai","aia","aibă","aici","al","ale","alea","altceva","altcineva","am","ar","are","asemenea","asta","astea","astăzi","asupra","au","avea","avem","aveţi","azi","aş","aşadar","aţi","bine","bucur","bună","ca","care","caut","ce","cel","ceva","chiar","cinci","cine","cineva","contra","cu","cum","cumva","curând","curînd","când","cât","câte","câtva","câţi","cînd","cît","cîte","cîtva","cîţi","că","căci","cărei","căror","cărui","către","da","dacă","dar","datorită","dată","dau","de","deci","deja","deoarece","departe","deşi","din","dinaintea","dintr-","dintre","doi","doilea","două","drept","după","dă","ea","ei","el","ele","eram","este","eu","eşti","face","fata","fi","fie","fiecare","fii","fim","fiu","fiţi","frumos","fără","graţie","halbă","iar","ieri","la","le","li","lor","lui","lângă","lîngă","mai","mea","mei","mele","mereu","meu","mi","mie","mine","mult","multă","mulţi","mulţumesc","mâine","mîine","mă","ne","nevoie","nici","nicăieri","nimeni","nimeri","nimic","nişte","noastre","noastră","noi","noroc","nostru","nouă","noştri","nu","opt","ori","oricare","orice","oricine","oricum","oricând","oricât","oricînd","oricît","oriunde","patra","patru","patrulea","pe","pentru","peste","pic","poate","pot","prea","prima","primul","prin","printr-","puţin","puţina","puţină","până","pînă","rog","sa","sale","sau","se","spate","spre","sub","sunt","suntem","sunteţi","sută","sînt","sîntem","sînteţi","să","săi","său","ta","tale","te","timp","tine","toate","toată","tot","totuşi","toţi","trei","treia","treilea","tu","tăi","tău","un","una","unde","undeva","unei","uneia","unele","uneori","unii","unor","unora","unu","unui","unuia","unul","vi","voastre","voastră","voi","vostru","vouă","voştri","vreme","vreo","vreun","vă","zece","zero","zi","zice","îi","îl","îmi","împotriva","în","înainte","înaintea","încotro","încât","încît","între","întrucât","întrucît","îţi","ăla","ălea","ăsta","ăstea","ăştia","şapte","şase","şi","ştiu","ţi","ţie"};

        CharArraySet stopWords = new CharArraySet(romanianStopWords.length, true);
        stopWords.addAll(Arrays.asList(romanianStopWords));

        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_36, new FileReader(textFile));

        tokenStream = new StopFilter(Version.LUCENE_36, tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term + " ");
        }
        //Remove accents
        String fileContent = StringUtils.stripAccents(sb.toString());
        if(fileContent != null)
            Normalizer.normalize(fileContent, Normalizer.Form.NFKD);

        Files.writeString(Path.of(textFile.getAbsolutePath()),fileContent);
        return textFile;
    }




    private Document getDocument(File file) throws IOException, Exception
    {
        Document document = new Document();

        //Eliminate stop words and diacritics
        file = removeStopWordsAndDiacritics(file);

        //index file contents
        Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file));
        //index file name
        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES,Field.Index.NOT_ANALYZED);
        //index file path
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES,Field.Index.NOT_ANALYZED);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);
        return document;
    }
    private void indexFile(File file) throws IOException, Exception
    {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }
    public int createIndex(String dataDirPath, FileFilter filter) throws IOException, Exception
    {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files)
        {
            if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) )
            {
                indexFile(file);
            }
        }
        return writer.numDocs();
    }
}
