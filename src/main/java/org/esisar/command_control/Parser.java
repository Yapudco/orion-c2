package org.esisar.command_control;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static void main(String[] arg) {

        String fausseCommande = "download \"C:\\Mes Documents Secrets\\passwords.txt\"   /tmp/loot.txt";
        System.out.println("On envoie au parseur : " + fausseCommande);

        List<String> command = commandSplit(fausseCommande);
        System.out.println("Le parseur a renvoyé : " + command);
    }





    public static List<String> commandSplit(String message){
        List<String> result = new ArrayList<>(); //making a list
        Pattern motif = Pattern.compile("(\"[^\"]+\"|\\S+)");
        Matcher chercheur = motif.matcher(message);

        while (chercheur.find()) {
            String morceau = chercheur.group(1);
            morceau = morceau.replace("\"", "");
            result.add(morceau);
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println("Case n°" + i + " -> [" + result.get(i) + "]");
        }
        return result;
    }
    public static void executeCommand(List<String> command) throws IOException {
        Process execute = new ProcessBuilder(command).start();
        execute.getInputStream();

        java.io.InputStream flux = execute.getInputStream();
        java.io.BufferedReader lecteur = new java.io.BufferedReader(new java.io.InputStreamReader(flux));


        String line;
        do {
            line = lecteur.readLine();
            System.out.println(line);
        }
        while (line != null);


    }


}
