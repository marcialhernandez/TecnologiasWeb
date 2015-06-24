/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

/**
 *
 * @author Marcial
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
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
        this.file = Paths.get(this.indexPath);
        this.jsonFilePath = jsonFilePath;
    };
    
    public LuceneIndexWriter(String indexPath){
        this.indexPath = indexPath;
        this.file = Paths.get(this.indexPath);
    }
    
    //entrada boleana, True=indica crear, False=No crea, pero verifica si existe
    public boolean openIndex(boolean indicaCreacion, int modo){
        try {
            //salida
            //Path file = Paths.get("src/",this.indexPath);
            if(modo == 1){
                System.out.println("Creando indice a partir de: "+this.jsonFilePath);
            }
            Directory dir = FSDirectory.open(this.file);
            Analyzer analyzer = new StopAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            
            
            
            //Verifico si ya existe un indice invertido creado
            //En caso que si, se configura en modo Append
            Directory indexDirectory = FSDirectory.open(this.file);
            if(modo == 1){
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
            }
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

    public int addDocuments(JSONArray jsonObjects,malletTrain clasificador) throws IOException, FileNotFoundException, ClassNotFoundException{
        System.out.println("Agregando documentos desde: "+this.jsonFilePath);
        int contador=0;
        String actual = "";
        String compilado = "";
        float score = 0; 
        int cantidad = 0;
        int cantMax = 0;
        String price = "";
        Document doc = new Document();
        String id = "";
        Map<String, String> clasificacionSingle = new HashMap<String,String>();;    
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            doc = new Document();
            contador++;
         //   String anterior = "";
            if(object.get("product/title").toString().equals(actual)){
                clasificacionSingle = clasificador.obtieneClasificacionSingle(contador+"", object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString(), clasificacionSingle);

                compilado = compilado +"{{ "+object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString() +" }}";
                cantidad++;
                score = score + valor(0,clasificacionSingle.get("positivo"),clasificacionSingle.get("neutral"), object.get("review/score").toString());

            }
            else{
                if(!actual.equals("")){
                    //Aqui se tiene que ejecutar el clasificador Mallet

                    if(cantidad > cantMax){
                        cantMax = cantidad;
                    }
                    
                    doc.add(new TextField("product/title", actual, Field.Store.YES));
                    doc.add(new TextField("review/text", compilado, Field.Store.YES));
                    doc.add(new FloatField("review/score", (score/(5*cantidad)),  Field.Store.YES));
                    doc.add(new TextField("product/price", price, Field.Store.YES));
                    doc.add(new TextField("product/productId", id, Field.Store.YES));
                    doc.add(new StringField("cantidadReviews", cantidad+ "", Field.Store.YES));
                        
                    try {    
                        this.indexWriter.addDocument(doc);
                    } catch (IOException ex) {
                        System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
                    }   
                }
               
                actual = object.get("product/title").toString();
                compilado = "{{ "+object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString() +" }}";        
                cantidad = 1;
                price = object.get("product/price").toString();
                clasificacionSingle = clasificador.obtieneClasificacionSingle(contador+"", object.get("review/summary").toString().toUpperCase() + " @@ " + object.get("review/text").toString(), clasificacionSingle);
                score = valor(0,clasificacionSingle.get("positivo"),clasificacionSingle.get("neutral"), object.get("review/score").toString());
                id = object.get("product/productId").toString();
                
            }
        }
        //Aqui se tiene que ejecutar el clasificador Mallet
        
        doc.add(new TextField("product/title", actual, Field.Store.YES));
        doc.add(new TextField("review/text", compilado, Field.Store.YES));
        doc.add(new FloatField("review/score", (score/(5*cantidad)),  Field.Store.YES));
        doc.add(new TextField("product/price", price, Field.Store.YES));
        doc.add(new TextField("product/productId", id, Field.Store.YES));
        doc.add(new StringField("cantidadReviews", cantidad + "", Field.Store.YES));
        
        if(cantidad > cantMax){
            cantMax = cantidad;
        }   
        
        try {
            //this.indexWriter.updateDocument(true, doc);
            this.indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
        }   
        
        
        System.out.println(contador+" Documentos agregados correctamente desde: " +this.jsonFilePath);
        
        return cantMax;
    };
    
    public int addDocuments2(JSONArray jsonObjects,malletTrain clasificador) throws IOException, FileNotFoundException, ClassNotFoundException{
        
        //Se agrega un nuevo 
        
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("product/title","NULLL");
        jsonObjects.add(jsonObj);
        System.out.println("Agregando documentos desde: "+this.jsonFilePath);
        
        int contador=0;
        String actual = "";
        String compilado = "";
        String temp="";
        float score = 0; 
        int cantidadComentarios=0;
        int cantMax=0;
        
        String price = "";
        Document doc;
        String id = "";
        Map<String, Float> clasificacionSingle=new HashMap<String,Float>();
        
        
        for(JSONObject object : (List<JSONObject>) jsonObjects){
            
            if(object.get("product/title").toString().equals("NULLL")){
                
                System.out.println("Ultimo documento alcanzado!!");
                doc = new Document();
                score=(float)(score/(cantidadComentarios*5));
                doc.add(new TextField("product/title", actual, Field.Store.YES));
                doc.add(new TextField("review/text", compilado, Field.Store.YES));
                doc.add(new FloatField("review/score", score , Field.Store.YES));
                doc.add(new TextField("product/price", price, Field.Store.YES));
                doc.add(new TextField("product/productId", id, Field.Store.YES));
                doc.add(new StringField("cantidadReviews", cantidadComentarios+ "", Field.Store.YES));
                if(cantidadComentarios > cantMax){
                        cantMax = cantidadComentarios;
                    }

        try {
            this.indexWriter.addDocument(doc);
        } catch (IOException ex) {
            System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
        }
                break;
            }
            
            contador++;

            if(object.get("product/title").toString().equals(actual)){
                cantidadComentarios++;
                temp=object.get("review/summary").toString().toUpperCase() + " "+ object.get("review/text").toString()+" ";
                compilado += temp;
                clasificacionSingle = clasificador.obtieneClasificacionSingle3(contador+"",temp,clasificacionSingle);
                score+=Float.parseFloat(object.get("review/score").toString())*(clasificacionSingle.get("positivo")+(float)clasificacionSingle.get("neutral")/4);
                continue;
            }
            
            else{
                if(!actual.equals("")){
                    doc = new Document();
                    score=(float)(score/(cantidadComentarios*5));
                    //Aqui se tiene que ejecutar el clasificador Mallet
                    doc.add(new TextField("product/title", actual, Field.Store.YES));
                    doc.add(new TextField("review/text", compilado, Field.Store.YES));
                    doc.add(new FloatField("review/score", score , Field.Store.YES));
                    //valoracion
                    doc.add(new TextField("product/price", price, Field.Store.YES));
                    doc.add(new TextField("product/productId", id, Field.Store.YES));
                    doc.add(new StringField("cantidadReviews", cantidadComentarios+ "", Field.Store.YES));
                    System.out.println("Pelicula: "+actual+" id:"+id+" score: "+score);
                    if(cantidadComentarios > cantMax){
                        cantMax = cantidadComentarios;
                    }

                    cantidadComentarios=0;

                    
                    try {
                        //this.indexWriter.updateDocument(true, doc);                        
                        this.indexWriter.addDocument(doc);
                        
                    } catch (IOException ex) {
                        System.err.println("Error al agregar los documentos de: "+this.jsonFilePath +" | "+  ex.getMessage());
                    }   
                }
            }
            
                actual = object.get("product/title").toString();
                cantidadComentarios++;
                temp=object.get("review/summary").toString().toUpperCase() + " " + object.get("review/text").toString()+" ";
                compilado += temp;
                price = object.get("product/price").toString();
                id = object.get("product/productId").toString();
                //Campos de mallet
                
                clasificacionSingle = clasificador.obtieneClasificacionSingle3(contador+"", temp,clasificacionSingle);
                score=Float.parseFloat(object.get("review/score").toString())*(clasificacionSingle.get("positivo")+(float)clasificacionSingle.get("neutral")/4);

        }// Fin for   
        
        System.out.println(contador+" Documentos agregados correctamente desde: " +this.jsonFilePath);
        return cantMax;
    };
    
    public float valor(float anterior, String malletP, String malletN, String stars){

        return (float) (anterior + Integer.parseInt(stars.charAt(0) + "")*(Double.parseDouble(malletP) + Double.parseDouble(malletN)/4));
        
    }
    
    public void updateScore(int maximo){
        try {
            //LuceneIndexWriter lw = new LuceneIndexWriter(INDEX_PATH, JSON_FILE_PATH);
            //lw.createIndex();

            //Check the index has been created successfully
            System.out.println("Verificando existencia de indice");
            Directory indexDirectory = FSDirectory.open(this.file);
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            int numDocs = indexReader.numDocs();
            System.out.println("Cantidad de documentos almacenados: "+numDocs);
            //assertEquals(numDocs, 3);
            System.out.println("Actualizando scores\n");
            for ( int i = 0; i < numDocs; i++)
            {
                //openIndex(true,1);
                Document document = indexReader.document(i);
                String score = document.getField("review/score").toString().substring(20);
                String cant = document.getField("cantidadReviews").toString().substring(69);
                String id = document.getField("product/productId").toString().substring(43);
                float scoreFloat = Float.parseFloat(score.substring(0, score.length()-1));
                float cantFloat = Float.parseFloat(cant.substring(0, cant.length()-1));
                id = id.substring(0, id.length()-1);
                
                System.out.println("Score= "+score+" cantidad = "+cantFloat+" maximo = "+maximo);
                document.removeField("review/score");
                document.add(new FloatField("review/score", (float)(0.9*scoreFloat+0.1*(float)cantFloat/maximo),  Field.Store.YES));
                
                //indexWriter.up.updateDocValues(new Term("product/productId", id) ,new StringField("review/score",(0.5* scoreFloat + 0.5* cantFloat/maximo)+"", Field.Store.YES));
                QueryParser parser = new QueryParser("product/productId", new StandardAnalyzer());
                Query query = parser.parse(id);
                
                //indexWriter.deleteDocuments(query);
                indexWriter.updateDocument(new Term("product/productId", id) ,document);
                //finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("MAXIMO = "+maximo);
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
