/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import org.json.simple.JSONArray;

/**
 *
 * @author Marcial
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
        // TODO code application logic here
        
        String nombreDirSalida="moviesIndex";
        String directorioRaiz="src/bd";
        
        File dir = new File(directorioRaiz);
        File[] directoryListing = dir.listFiles();
        LuceneIndexWriter index = null;
        
        if (directoryListing != null) {
            
            parseJSONFile entradaBD;
            JSONArray documentos;
            
            for (File child : directoryListing) {
                
                //Para cada documento
                entradaBD = new parseJSONFile();
                documentos = new JSONArray(); 
                documentos = entradaBD.obtieneDocumentos("/bd/"+child.getName());
                index = new LuceneIndexWriter(nombreDirSalida,child.getName());
                index.openIndex();
                index.addDocuments(documentos);
                index.finish();
            }
            
        } else {
            System.out.println("No hay archivos en BD");

    // Handle the case where dir is not really a directory.
    // Checking dir.isDirectory() above would not be sufficient
    // to avoid race conditions with another process that deletes
    // directories.
        }
        ////////////////////////////////////////////////////////////////
        //Test
        LuceneIndexWriterTest indexTest= new LuceneIndexWriterTest(index.file);
        indexTest.testWriteIndex();
        indexTest.testQueryLucene();
    }

}
