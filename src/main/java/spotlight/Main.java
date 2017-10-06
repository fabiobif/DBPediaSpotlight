/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONArray;
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
        HttpClient httpclient = HttpClients.createDefault();
        String text;
        String confidence = "0.5";
        String support = "20";
//        String function = "candidates";
        String function = "annotate";
        String lang = "en";
        List<String> tweetsId = new ArrayList<>();

        BufferedReader br;

        String anotacaoes = "";
        File arquivo;
        URI uri;
//        try {
//            BufferedWriter buffW;
//            //le arq de tweets
//            br = new BufferedReader(new FileReader("src/main/resources/downloaded.tsv"));
//            while (br.ready()) {
//
//                text = br.readLine();
//                System.out.println(text.split("\t")[1]);
//                uri = new URIBuilder()
//                        .setScheme("http")
//                        .setHost("model.dbpedia-spotlight.org/")
//                        .setPath(lang + "/" + function)
//                        .addParameter("text", text.split("\t")[1])
//                        .addParameter("confidence", confidence)
//                        .addParameter("support", support)
//                        .build();
//                HttpGet httpget = new HttpGet(uri);
//                httpget.addHeader("Accept", "application/json");
//
//                CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httpget);
//                HttpEntity entity = response.getEntity();
//
//                //Cria arquivos para gravar anotações
//                arquivo = new File("src/main/resources/Anotações/tweet - " + text.split("\t")[0] + ".json");
//                if (!arquivo.exists()) {
//                    arquivo.createNewFile();
//                }
//
//                JSONObject json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
//                json.put("tweet", text.split("\t")[0]);
//                anotacaoes = json.toString();
////                anotacaoes = json.toString(1);  USAR ESSE CASO QUEIRA ARQUIVO IDENTADO
//
//                buffW = new BufferedWriter(new FileWriter(arquivo));
//                buffW.write(anotacaoes);
//                buffW.close();
//                EntityUtils.consume(entity);
//            }
//
//            br.close();
//        } catch (URISyntaxException | IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //Ler arq com regra ouro
        JSONArray array = null;
        try {
            br = new BufferedReader(new FileReader("src/main/resources/microposts2016-neel-test_neel.gs"));
            array = new JSONArray();
            JSONObject object;
            while (br.ready()) {
                text = br.readLine();
                object = new JSONObject();
                object.put("id", text.split("\t")[0]);
                object.put("start", text.split("\t")[1]);
                object.put("end", text.split("\t")[2]);
                object.put("url", text.split("\t")[3]);
                object.put("confidence", text.split("\t")[4]);
                object.put("type", text.split("\t")[5]);

                if (!tweetsId.contains(text.split("\t")[0])) {
                    tweetsId.add(text.split("\t")[0]);

                }
                array.put(object);
            }
            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String tweet : tweetsId) {
            try {
                br = new BufferedReader(new FileReader("src/main/resources/Anotações/tweet - " + tweet + ".json"));
                while (br.ready()) {
                    JSONObject json = new JSONObject(br.readLine());

                    String comparacao = "Tweet ID:" + tweet + "\nTweet: " + json.getString("@text") + "\n";
                    arquivo = new File("src/main/resources/Anotações/ComparacaoOuro/tweet - " + tweet);
                    for (Object obj : array) {
                        JSONObject jsonouro = (JSONObject) obj;

                        if (!arquivo.exists()) {
                            arquivo.createNewFile();
                        }
                        if (jsonouro.get("id").equals(tweet)) {
                            //TODO: comparar elementos
                            if (json.has("Resources")) {
                                boolean achou = false;
                                int contNaoAchou = 0;
                                JSONArray resources = new JSONArray(json.getJSONArray("Resources").toString());
                                for (Object resource : resources) {
                                    JSONObject jsonResource = (JSONObject) resource;
                                    if (jsonouro.get("start").equals(jsonResource.get("@offset"))
                                            && jsonouro.get("url").equals(jsonResource.get("@URI"))) {

                                        comparacao += "Palavra: " + jsonResource.getString("@surfaceForm") + "\n"
                                                + "OffSet: " + jsonResource.get("@offset") + "\n"
                                                + "ACHOU MESMA URL: " + jsonResource.get("@URI") + "\n\n";
                                        achou = true;
                                    } else if (jsonouro.get("start").equals(jsonResource.get("@offset"))
                                            && !jsonouro.get("url").equals(jsonResource.get("@URI"))) {
                                        comparacao += "Palavra: " + jsonResource.getString("@surfaceForm") + "\n"
                                                + "OffSet: " + jsonResource.get("@offset") + "\n"
                                                + "ACHOU URLs DIFERENTES\n DBPedia-Spotlight: " + jsonResource.get("@URI") + "\n"
                                                + " Regra Ouro:" + jsonouro.get("url") + "\n\n";
                                        achou = true;
                                    }

                                    if (achou == false) {
                                        contNaoAchou++;
                                    }
                                }
                                if(contNaoAchou == resources.length()){
                                    comparacao += "DBPEDIA NÃO ENCONTROU, PORÉM ESTAVA NA REGRA OURO\n"
                                            + "Palavra: " + json.getString("@text").substring(Integer.parseInt(jsonouro.getString("start")), Integer.parseInt(jsonouro.getString("end"))) +"\n"
                                            + "URI na regra ouro: " + jsonouro.get("url") + "\n"
                                            + "Type: " + jsonouro.getString("type")+ "\n\n";
                                }
                            }
                        }
                    }
                    arquivo = new File("src/main/resources/Anotações/ComparacaoOuro/tweet - " + tweet);
                    if (arquivo.exists()) {
                        BufferedReader buffread = new BufferedReader(new FileReader(arquivo));
                        while (buffread.ready()) {
                            comparacao += buffread.readLine() + "\n";
                        }
                        BufferedWriter buffW = new BufferedWriter(new FileWriter(arquivo));
                        buffW.write(comparacao);
                        buffW.close();
                    }
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
