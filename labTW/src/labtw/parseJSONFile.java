/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.json.simple.*;

/**
 *
 * @author Marcial
 */
public class parseJSONFile{

    public parseJSONFile(){
    };

    JSONArray obtieneDocumentos(String jsonFilePath){
        System.out.println("obteniendo Datos");
        InputStream jsonFile =  getClass().getResourceAsStream(jsonFilePath);
        Reader readerJson = new InputStreamReader(jsonFile);
        //Parse the json file using simple-json library
        System.out.println("Parseando Jsons");
        Object fileObjects= JSONValue.parse(readerJson);
        JSONArray arrayObjects=(JSONArray)fileObjects;
        System.out.println("Parseo completado");
        return arrayObjects;
    };

};
