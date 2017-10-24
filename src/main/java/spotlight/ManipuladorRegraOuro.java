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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author arthu
 */
public class ManipuladorRegraOuro {
    
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
