package ie.tcd.singhr3;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class ClassicSimilaritySearch {

    public void querySearch() throws Exception {

        String index = "index";
        String queryString = "";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        //Analyzer analyzer = new SimpleAnalyzer();
        //Analyzer analyzer = new WhitespaceAnalyzer();
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new EnglishAnalyzer();

        String results_path = "results.txt";
        PrintWriter writer = new PrintWriter(results_path, "UTF-8");

        //BM25 Similarity
//        searcher.setSimilarity(new BM25Similarity());

        //Classic Similarity
        searcher.setSimilarity(new ClassicSimilarity());

        //LMDirichletSimilarity
//        searcher.setSimilarity(new LMDirichletSimilarity());

        //Trying a multi similarity model
        //searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(),new ClassicSimilarity()}));

        //Trying another multi similarity model
        //searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(),new LMDirichletSimilarity()}));

        //Trying another multi similarity model
        //searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new ClassicSimilarity(),new LMDirichletSimilarity()}));
        
        //Trying another multi similarity model
//        searcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(),new LMDirichletSimilarity()}));

        String queriesPath = "cran/cran.qry";
        BufferedReader br = Files.newBufferedReader(Paths.get(queriesPath), StandardCharsets.UTF_8);
        QueryParser parser = new QueryParser("work", analyzer);

        String strLine = br.readLine();

        System.out.println("Reading in queries and creating search results.");

        String id = "";
        int i=0;

        while (strLine != null) {
            i++;
            if (strLine.startsWith(".I")) {
                id = Integer.toString(i);
                strLine = br.readLine();
            }
            if (strLine.startsWith(".W")) {
            	strLine = br.readLine();
                while (strLine != null && !strLine.startsWith(".I")) {
                    queryString += strLine + " ";
                    strLine = br.readLine();
                }
            }
            queryString = queryString.trim();
            Query query = parser.parse(QueryParser.escape(queryString));
            queryString = "";
            ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;
            for (int k = 0; k < hits.length; k++) {
                Document doc = searcher.doc(hits[k].doc);
                writer.println(Integer.parseInt(id) + " 0 " + doc.get("id") + " " + k + " " + hits[k].score + " STANDARD");
    	        }
        }

        System.out.println("'results.txt' file done!");
        writer.close();
        reader.close();
    }
	}