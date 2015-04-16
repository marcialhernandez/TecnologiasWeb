/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package labtw;

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
    public static void main(String[] args) throws IOException, ParseException {
        // TODO code application logic here
        parseJSONFile entradaBD = new parseJSONFile();
        JSONArray documentos = new JSONArray(); 
        documentos = entradaBD.obtieneDocumentos("test.txt");
        LuceneIndexWriter index = new LuceneIndexWriter("moviesIndex");
        index.openIndex();
        index.addDocuments(documentos);
        index.finish();
        LuceneIndexWriterTest indexTest= new LuceneIndexWriterTest(index.file);
        indexTest.testWriteIndex();
        indexTest.testQueryLucene();
        //LuceneIndexWriterTest indexTest= new LuceneIndexWriterTest(index.file);
    }

}
