/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
    
    public static void main(String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException, FileNotFoundException, ClassNotFoundException {
        // TODO code application logic here
        
        //String nombreDirSalida="C:/Users/Odds/Documents/NetBeansProjects/BuscadorWeb/src/java/moviesIndex";
        //String directorioRaiz="src/bd";
        
        String nombreDirSalida="src/moviesIndex";
        String directorioRaiz="src/bd";
        
        File dir = new File(directorioRaiz);
        File[] directoryListing = dir.listFiles();
        System.out.println("debug1= "+ dir.exists());
        LuceneIndexWriter index = null;
        //banderaCrear indica si hay que crear o no un nuevo indice invertido
        //En caso que si, es true, en caso que no, es false
        boolean banderaCrear=true;
        Vector opcionesCrearIndex = new Vector();
        opcionesCrearIndex.add("si");
        opcionesCrearIndex.add("no");
        Vector opcionesFields = new Vector();
        opcionesFields.add("review/text");
        opcionesFields.add("product/title");
        
        malletTrain clasificador = new malletTrain();
        
        if (clasificador.loadClassifier()==true){
            System.out.println("Clasificador cargado con exito");
                
        }
        else{
            System.out.println("No se ha encontrado el clasificador D:");
    
        }
        
        clasificador.creaArchivoTemporal("singleInstance"); 
        //opcionesFields.add("review/summary");
    
        banderaCrear=true;
        int cantMax = 0;
        int cant = 0;
        
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
                index.openIndex(banderaCrear,1);
                if (banderaCrear){
                    cant = index.addDocuments2(documentos,clasificador);
                    if (cant > cantMax){
                        cantMax = cant;
                    }
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
        index = new LuceneIndexWriter(nombreDirSalida);
        index.openIndex(true,0);
        index.updateScore(cantMax);
        index.finish();
        
        LuceneIndexWriterTest indexTest = new LuceneIndexWriterTest(index.file);
                
        menu menuTemp=new menu();
        
       if (menuTemp.obtiene(opcionesCrearIndex,"Ejecutar test de existencia indice invertido? (si/no)").equals("si")){
            indexTest.testWriteIndex();
        }
        
        if (menuTemp.obtiene(opcionesCrearIndex,"Ejecutar busqueda en indice invertido? (si/no)").equals("si")){
            String fieldABuscar =menuTemp.obtiene(opcionesFields,"Indique field a buscar, opciones:\nproduct/title\nreview/text");
            String palabraABuscar=menuTemp.obtieneSimple("Indique que palabra desea buscar:");
            String cantidadResultadosAMostrar=menuTemp.obtieneNumero("Ingrese la cantidad de resultados que quiere obtener");
            indexTest.testQueryLucene(palabraABuscar,fieldABuscar,Integer.parseInt(cantidadResultadosAMostrar));
        }
        
    }

}
