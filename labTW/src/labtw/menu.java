/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labtw;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

/**
 *
 * @author Marcial
 */
public class menu {
    
        public menu(){
    };
    
     public String obtiene(Vector opciones, String glosa){
        boolean banderaEntradaCorrecta=false;
        Scanner sc = new Scanner(System.in);
        System.out.println(glosa);
        String temp= sc.nextLine();
        Iterator itr = opciones.iterator();
        while(itr.hasNext()){
            if (itr.next().equals(temp)){
                banderaEntradaCorrecta=true;
                return temp;
            } 
        }
        
        while (!banderaEntradaCorrecta){
            
            System.out.println("Entrada incorrecta, "+glosa);
            temp= sc.nextLine();
            itr = opciones.iterator();
            while(itr.hasNext()){
                if (itr.next().equals(temp)){
                banderaEntradaCorrecta=true;
                return temp;
                } 
            }
        }
        return temp;
    };
     
    public String obtieneSimple(String glosa){
        Scanner sc = new Scanner(System.in);
        System.out.println(glosa);
        String temp= sc.nextLine();
        return temp;
    };
    
    public boolean isInt(String str){
    if (str == null) {
            return false;
    }
    int length = str.length();
    if (length == 0) {
            return false;
    }
    int i = 0;
    if (str.charAt(0) == '-') {
            if (length == 1) {
                    return false;
            }
            i = 1;
    }
    for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                    return false;
            }
    }
    return true;
}
    
    public String obtieneNumero(String glosa){
        Scanner sc = new Scanner(System.in);
        System.out.println(glosa);
        String temp= sc.nextLine();
        boolean banderaNumero=isInt(temp);
        
        while (banderaNumero==false){
            
            System.out.println("Entrada incorrecta, "+glosa);
            temp= sc.nextLine();
            banderaNumero=isInt(temp);
        }
       
        return temp;
    
    };
}