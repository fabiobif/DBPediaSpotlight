/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotlight;

import org.tensorflow.Tensor;

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
//        ma.criaAnotacoesRdf();
//        ma.criaAnotacoesNTriplets();
        
    }
}
