/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labtw;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

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
    
}