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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LuceneIndexWriter {

    //nombre indice
    String indexPath;
    String jsonFilePath;
    Path file;

    //String jsonFilePath;

    IndexWriter indexWriter = null;

    public LuceneIndexWriter(String indexPath, String jsonFilePath) {
        this.indexPath = indexPath;
        this.file = Paths.get("src/",this.indexPath);
        this.jsonFilePath=jsonFilePath;
        
    };
    
    public boolean openIndex(){
        try {
            //salida
            //Path file = Paths.get("src/",this.indexPath);
            System.out.println("Creando indice a partir de: "+this.jsonFilePath);
            Directory dir = FSDirectory.open(this.file);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            
            //Verifico si ya existe un indice invertido creado
            //En caso que si, se configura en modo Append
            Directory indexDirectory = FSDirectory.open(this.file);
            if (DirectoryReader.indexExists(indexDirectory)){
                System.out.println("El indice ya existe, se agregara informacion a este");
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            //En caso contrario, se crea uno nuevo
            else{
            //Always overwrite the directory
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            }
            
            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            iwc.setRAMBufferSizeMB(256.0);
            this.indexWriter = new IndexWriter(dir, iwc);
            return true;
        } catch (Exception e) {
            System.err.println("Error abriendo el indice con el archivo: "+this.jsonFilePath +" | "+ e.getMessage());
        }
        return false;
    };

    public void addDocuments(JSONArray jsonObjects){
        System.out.println("Agregando documentos desde: "+this.jsonFilePath);
        int contador=0;
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            Document doc = new Document();
            contador++;
            for(String field : (Set<String>) object.keySet()){
                Class type = object.get(field).getClass();
                if(type.equals(String.class)){
                    doc.add(new StringField(field, (String)object.get(field), Field.Store.YES));
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
                //this.indexWriter.updateDocument(true, doc);
                this.indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
            }
        }
        System.out.println(contador+" Documentos agregados correctamente desde: " +this.jsonFilePath);
    };

    public void finish(){
        try {
            //this.indexWriter.optimize();
            this.indexWriter.commit();
            this.indexWriter.close();
            System.out.println("Cerrando Indice");
        } catch (IOException ex) {
            System.err.println("H surgido un problema cerrando el indice desde: "+this.jsonFilePath + " | "+ex.getMessage());
        }
    };
}
