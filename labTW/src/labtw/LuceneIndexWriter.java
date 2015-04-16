/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

/**
 *
 * @author Marcial
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LuceneIndexWriter {

    //nombre indice
    String indexPath;
    Path file;

    //String jsonFilePath;

    IndexWriter indexWriter = null;

    public LuceneIndexWriter(String indexPath) {
        this.indexPath = indexPath;
        this.file = Paths.get("src/",this.indexPath);
        System.out.println("Asignando salida al indice");
    //    this.jsonFilePath = jsonFilePath;
    };
    
    public boolean openIndex(){
        try {
            //salida
            //Path file = Paths.get("src/",this.indexPath);
            System.out.println("Creando indice");
            Directory dir = FSDirectory.open(this.file);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            //Always overwrite the directory
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            
            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            iwc.setRAMBufferSizeMB(256.0);
            this.indexWriter = new IndexWriter(dir, iwc);
            System.out.println("Indice creado");
            return true;
        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    };

    public void addDocuments(JSONArray jsonObjects){
        System.out.println("Agregando documentos");
        int contador=1;
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            System.out.println("Documento numero " + contador);
            contador++;
            Document doc = new Document();
            for(String field : (Set<String>) object.keySet()){
                Class type = object.get(field).getClass();
                if(type.equals(String.class)){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.NO));
                }else if(type.equals(Long.class)){
                    //(Long) not (long). Primitives are not castable to Objects
                    doc.add(new LongField(field, (Long)object.get(field), Field.Store.YES));
                }else if(type.equals(Double.class)){
                    doc.add(new DoubleField(field, (Double)object.get(field), Field.Store.YES));
                }else if(type.equals(Boolean.class)){
                    doc.add(new StringField(field, object.get(field).toString(), Field.Store.YES));
                }
            }
            try {
                this.indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " +  ex.getMessage());
            }
        }
        System.out.println("Documentos agregados correctamente");
    };

    public void finish(){
        try {
            //this.indexWriter.optimize();
            this.indexWriter.commit();
            this.indexWriter.close();
            System.out.println("Indice creado correctamente");
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    };
}
