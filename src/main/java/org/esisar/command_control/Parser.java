package org.esisar.command_control;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    List<String> result = new ArrayList<>(); //making a list
    String message;

    public static void main(String[] arg){
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


}
