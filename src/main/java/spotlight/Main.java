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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author arthur.sens
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HttpClient httpclient = HttpClients.createDefault();
        String text = "Olá #Advogado #Correspondente, nova demanda em https://t.co/4tyCFnQmCb: Audiência - Boa Esperança, MG. Candidate-se já!";
        String confidence = "0.2";
        String support = "20";
//        String function = "candidates";
        String function = "annotate";
        String lang = "pt";

        URI uri;
        try {
            File arquivo = new File("src/main/resources/teste.json");
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("model.dbpedia-spotlight.org/")
                    .setPath(lang + "/" + function)
                    .addParameter("text", text)
                    .addParameter("confidence", confidence)
                    .addParameter("support", support)
                    .build();
            HttpGet httpget = new HttpGet(uri);
            httpget.addHeader("Accept", "application/json");

            CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            BufferedWriter buffW = new BufferedWriter(new FileWriter(arquivo));
            String resposta = EntityUtils.toString(entity, "UTF-8");
            
            buffW.write(resposta);
            buffW.close();
            EntityUtils.consume(entity);
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
