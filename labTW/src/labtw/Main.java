/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
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
        //banderaCrear indica si hay que crear o no un nuevo indice invertido
        //En caso que si, es true, en caso que no, es false
        boolean banderaCrear=true;
        Vector opcionesCrearIndex = new Vector();
        opcionesCrearIndex.add("si");
        opcionesCrearIndex.add("no");
        menu menuTemp=new menu();
        if (menuTemp.obtiene(opcionesCrearIndex,"Crear indice invertido? (si/no)").equals("si")){
            banderaCrear=true;
        }
        else{
            banderaCrear=false;
        }
        if (directoryListing != null) {
            
            parseJSONFile entradaBD;
            JSONArray documentos;
            
            for (File child : directoryListing) {
                
                //Para cada documento
                entradaBD = new parseJSONFile();
                documentos = new JSONArray(); 
                //Se usa esta forma para ignorar un documento dentro de la carpeta bd
                if (child.getName().equals(".DS_Store")){
                continue;}
                documentos = entradaBD.obtieneDocumentos("/bd/"+child.getName());
                index = new LuceneIndexWriter(nombreDirSalida,child.getName());
                index.openIndex(banderaCrear);
                if (banderaCrear){
                    index.addDocuments(documentos);
                }
                index.finish();
                
                if (banderaCrear==false){
                    break;
                }
            }
            
        } else {
            System.out.println("No hay archivos en BD");

    // Handle the case where dir is not really a directory.
    // Checking dir.isDirectory() above would not be sufficient
    // to avoid race conditions with another process that deletes
    // directories.
        }
        ////////////////////////////////////////////////////////////////
        
        LuceneIndexWriterTest indexTest= new LuceneIndexWriterTest(index.file);
        
        if (menuTemp.obtiene(opcionesCrearIndex,"Ejecutar test de existencia indice invertido? (si/no)").equals("si")){
            indexTest.testWriteIndex();
        }
        
        if (menuTemp.obtiene(opcionesCrearIndex,"Ejecutar test de busqueda indice invertido? (si/no)").equals("si")){
            indexTest.testQueryLucene("good","review/text",10);
        }
        
    }

}
