/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotlight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONObject;

/**
 *
 * @author arthur.sens
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ManipuladorArquivos ma = new ManipuladorArquivos();
//        ma.criaAnotacoes();
//        ma.criaAnotacoesIdentado();
        ma.criaOpenAnnotations();
        

    }
}
