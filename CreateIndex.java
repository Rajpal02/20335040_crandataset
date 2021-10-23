package ie.tcd.singhr3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
public class CreateIndex
{
	
	public void indexing() {
		String indexDirectory = "index";
		String cranPath = "cran/cran.all.1400";
		
		final Path cranDir = Paths.get(cranPath);
		
		if (!Files.isReadable(cranDir)) {
            System.out.println("Path doesn\'t exists");
            System.exit(1);
        }
		
        try {
            System.out.println("Indexing it to'" + indexDirectory + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexDirectory));
            
            //Analyzer analyzer = new SimpleAnalyzer();
            //Analyzer analyzer = new WhitespaceAnalyzer();
//            Analyzer analyzer = new StandardAnalyzer();
            Analyzer analyzer = new EnglishAnalyzer();
            IndexWriterConfig iwconfig = new IndexWriterConfig(analyzer);

            iwconfig.setOpenMode(OpenMode.CREATE);

            IndexWriter iw = new IndexWriter(dir, iwconfig);
            cran1400(iw, cranDir);

            iw.forceMerge(1);

            iw.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //indexing cran.all.1400 documents
    static void cran1400(IndexWriter iw, Path file) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            System.out.println("Start Indexing.");

            String strLine = br.readLine();
            String str2 = "";

            while(strLine != null){
                Document doc = new Document();
                if(strLine.startsWith(".I")){
                    
                    doc.add(new StringField("id", strLine.substring(3), Field.Store.YES));
                    strLine = br.readLine();
                }
                if (strLine.startsWith(".T")){
                	strLine = br.readLine();
                    while(!strLine.startsWith(".A")){
                    	str2 += strLine + " ";
                        strLine = br.readLine();
                    }
                    doc.add(new TextField("title", str2, Field.Store.YES));
                    str2 = "";
                }
                if (strLine.startsWith(".A")){
                	strLine = br.readLine();
                    while(!strLine.startsWith(".B")){
                    	str2 += strLine + " ";
                        strLine = br.readLine();
                    }
                    doc.add(new TextField("author", str2, Field.Store.YES));
                    str2 = "";
                }
                if (strLine.startsWith(".B")){
                	strLine = br.readLine();
                    while(!strLine.startsWith(".W")){
                    	str2 += strLine + " ";
                        strLine = br.readLine();
                    }
                    doc.add(new TextField("bibliography", str2, Field.Store.YES));
                    str2 = "";
                }
                if (strLine.startsWith(".W")){
                	strLine = br.readLine();
                    while(strLine != null && !strLine.startsWith(".I")){
                    	str2 += strLine + " ";
                        strLine = br.readLine();
                    }
                    doc.add(new TextField("work", str2, Field.Store.YES));
                    str2 = "";
                }
                iw.addDocument(doc);
            }
        }
    }
}