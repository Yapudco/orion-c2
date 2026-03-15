package org.esisar.command_control;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static void main(String[] arg) throws IOException {

        String fausseCommande = "whoami";
        System.out.println("On envoie au parseur : " + fausseCommande);

        List<String> command = commandSplit(fausseCommande);
        System.out.println("Le parseur a renvoyé : " + command);
        String resultatC2 = executeCommand(command);


        System.out.println("--- CE QUI SERA ENVOYÉ AU SERVEUR ---");
        System.out.println(resultatC2);
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
    public static String executeCommand(List<String> command) throws IOException {
        Process execute = new ProcessBuilder(command).start();
        java.io.InputStream flux = execute.getInputStream();
        java.io.BufferedReader lecteur = new java.io.BufferedReader(new java.io.InputStreamReader(flux));

        String line;
        StringBuilder responses = new StringBuilder();
        while ((line = lecteur.readLine()) != null) {
            responses.append(line).append("\n");

        }
        return responses.toString();

    }


}
