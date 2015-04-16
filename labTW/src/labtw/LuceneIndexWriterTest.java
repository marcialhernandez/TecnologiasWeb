/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labtw;


import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import static junit.framework.Assert.assertEquals;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import static sun.nio.cs.Surrogate.is;
//import org.junit.Test;

/**
 *
 * @author Marcial
 */
public class LuceneIndexWriterTest {
    
    Path ubicacionIndex;
    //static final String JSON_FILE_PATH = "/test.json";

    public LuceneIndexWriterTest(Path ubicacionIndex) {
        this.ubicacionIndex=ubicacionIndex;
    }

    //@Test
    public void testWriteIndex(){
        try {
            //LuceneIndexWriter lw = new LuceneIndexWriter(INDEX_PATH, JSON_FILE_PATH);
            //lw.createIndex();

            //Check the index has been created successfully
            System.out.println("Verificando existencia de indice");
            Directory indexDirectory = FSDirectory.open(this.ubicacionIndex);
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            System.out.println("Verificando cantidad de documentos almacenados");
            int numDocs = indexReader.numDocs();
            //assertEquals(numDocs, 3);
            System.out.println("Imprimiendo documentos almacenados");
            for ( int i = 0; i < numDocs; i++)
            {
                Document document = indexReader.document( i);
                System.out.println( "d=" +document);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testQueryLucene() throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
        Directory indexDirectory = FSDirectory.open(this.ubicacionIndex);
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        System.out.println("Verificando existencia de indice");
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //indexSearcher.//setDefaultFieldSortScoring(true, true);
        System.out.println("Verificando querys");
        
        //Palabra a buscar
        String textoSearch = "id2";
        
        //En este atributo 
        String enAtributo = "name";
        
        //Creamos la consulta 
        QueryParser parser = new QueryParser(enAtributo, new StandardAnalyzer());
        Query query = parser.parse(textoSearch);
        
        //Realizamos la busqueda
        TopDocs topBusqueda = indexSearcher.search(query, 10);
        
        ScoreDoc[] filterScoreDosArray = topBusqueda.scoreDocs;
        
        if (filterScoreDosArray.length ==0){
            System.out.println("Verificando existencia de indice");
        }
        
        int docId =0;
        for (int i = 0; i < filterScoreDosArray.length; ++i) {
            docId = filterScoreDosArray[i].doc;
            System.out.println((i + 1) + ". idDoc: "+docId +" Score: "+ filterScoreDosArray[i].score);
        }
        
        //Term t = new Term(enAtributo, textoSearch);
        //Query query = new TermQuery(t);
        
        //TopDocs topDocs = indexSearcher.search(query, 10);
        //assertEquals(1, topDocs.totalHits);
    }
}