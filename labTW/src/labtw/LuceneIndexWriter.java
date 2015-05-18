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
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
    
    //entrada boleana, True=indica crear, False=No crea, pero verifica si existe
    public boolean openIndex(boolean indicaCreacion){
        try {
            //salida
            //Path file = Paths.get("src/",this.indexPath);
            System.out.println("Creando indice a partir de: "+this.jsonFilePath);
            Directory dir = FSDirectory.open(this.file);
            Analyzer analyzer = new StopAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            
            
            
            //Verifico si ya existe un indice invertido creado
            //En caso que si, se configura en modo Append
            Directory indexDirectory = FSDirectory.open(this.file);
            if (DirectoryReader.indexExists(indexDirectory)){
                System.out.println("El indice ya existe");
                if (indicaCreacion){
                    System.out.println("Se agregara informacion a este");
                    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                }
            }
            //En caso contrario, se crea uno nuevo
            else{
                if (indicaCreacion){
                //Always overwrite the directory
                    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                }
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
        String actual = "";
        String compilado = "";
        float score = 0; 
        int cantidad = 0;
        String price = "";
        Document doc = new Document();
        String id = "";
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            doc = new Document();
            contador++;
         //   String anterior = "";
            if(object.get("product/title").toString().equals(actual)){
                compilado = compilado +"{{ "+object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString() +" }}";
                cantidad++;
                score = valor(score, object.get("review/score").toString(), cantidad);
            }
            else{
                if(!actual.equals("")){
                    doc.add(new TextField("product/title", actual, Field.Store.YES));
                    doc.add(new TextField("review/text", compilado, Field.Store.YES));
                    doc.add(new StringField("review/score", (score + ""), Field.Store.YES));
                    doc.add(new TextField("product/price", price, Field.Store.YES));
                    doc.add(new TextField("product/productId", id, Field.Store.YES));
                
                    try {
                        //this.indexWriter.updateDocument(true, doc);
                        this.indexWriter.addDocument(doc);
                    } catch (IOException ex) {
                        System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
                    }   
                }
               
                actual = object.get("product/title").toString();
                compilado = "{{ "+object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString() +" }}";        
                cantidad = 1;
                price = object.get("product/price").toString();
                score = valor(0, object.get("review/score").toString(), cantidad);
                id = object.get("product/productId").toString();
                
            }
        }
        
        doc.add(new TextField("product/title", actual, Field.Store.YES));
        doc.add(new TextField("review/text", compilado, Field.Store.YES));
        doc.add(new StringField("review/score", (score + ""), Field.Store.YES));
        doc.add(new TextField("product/price", price, Field.Store.YES));
        doc.add(new TextField("product/productId", id, Field.Store.YES));

        try {
            //this.indexWriter.updateDocument(true, doc);
            this.indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
        }   
        
        
        System.out.println(contador+" Documentos agregados correctamente desde: " +this.jsonFilePath);
    };
    
    public float valor(float anterior, String nuevo, int cantidad){
        return ((anterior * (cantidad - 1) + Integer.parseInt(nuevo.charAt(0) + "")) / cantidad); 
    }

    public void finish(){
        try {
            //this.indexWriter.optimize();
            this.indexWriter.commit();
            this.indexWriter.close();
            System.out.println("Cerrando Indice");
        } catch (IOException ex) {
            System.err.println("Ha surgido un problema cerrando el indice desde: "+this.jsonFilePath + " | "+ex.getMessage());
        }
    };
}
