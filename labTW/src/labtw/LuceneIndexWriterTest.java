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
            int numDocs = indexReader.numDocs();
            System.out.println("Cantidad de documentos almacenados: "+numDocs);
            //assertEquals(numDocs, 3);
            System.out.println("Imprimiendo documentos almacenados\n");
            for ( int i = 0; i < numDocs; i++)
            {
                Document document = indexReader.document( i);
                System.out.println( (i) +".- "+document.getField("product/title").toString());
                System.out.println( document.getField("product/price").toString());
                System.out.println( document.getField("review/summary").toString());
                System.out.println( document.getField("review/text").toString());
                System.out.println( document.getField("review/score").toString());
                System.out.println( "*******************************************************************************");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testQueryLucene(String textoABuscar,String enField,int cantidadResultados) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
        Directory indexDirectory = FSDirectory.open(this.ubicacionIndex);
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        System.out.println("Verificando existencia de indice");
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //indexSearcher.//setDefaultFieldSortScoring(true, true);
        System.out.println("Verificando querys");

        //Creamos la consulta 
        QueryParser parser = new QueryParser(enField, new StandardAnalyzer());
        Query query = parser.parse(textoABuscar);
        
        //Realizamos la busqueda
        TopDocs topBusqueda = indexSearcher.search(query, cantidadResultados);
        
        ScoreDoc[] filterScoreDosArray = topBusqueda.scoreDocs;
        
        if (filterScoreDosArray.length ==0){
            System.out.println("No existe el termino buscado");
        }
        
        int docId =0;
        for (int i = 0; i < filterScoreDosArray.length; ++i) {
            docId = filterScoreDosArray[i].doc;
            Document document = indexReader.document( docId);
            System.out.println((i + 1) + ".- idDoc: "+docId +" <"+ document.getField("product/title").toString().split(":")[1] +" Score: "+ filterScoreDosArray[i].score);
        }
        
        //Term t = new Term(enAtributo, textoSearch);
        //Query query = new TermQuery(t);
        
        //TopDocs topDocs = indexSearcher.search(query, 10);
        //assertEquals(1, topDocs.totalHits);
    }
}