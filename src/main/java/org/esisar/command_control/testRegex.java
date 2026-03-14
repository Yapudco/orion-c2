package org.esisar.command_control;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testRegex {
    // Le fameux "main", c'est le point de départ de ton test
    public static void main(String[] args) {

        // 1. On simule la commande reçue par le WebSocket (avec des espaces un peu partout)
        String fausseCommande = "download \"C:\\Mes Documents Secrets\\passwords.txt\"   /tmp/loot.txt";

        System.out.println("Commande brute reçue : " + fausseCommande);
        System.out.println("--------------------------------------------------");

        // 2. On prépare notre liste vide
        List<String> listeArguments = new ArrayList<>();

        // 3. On prépare la Regex magique
        // Traduction : "Prends les mots sans espaces OU les blocs entiers entre guillemets"
        Pattern motif = Pattern.compile("(\"[^\"]+\"|\\S+)");
        Matcher chercheur = motif.matcher(fausseCommande);

        // 4. La boucle : Tant que la Regex trouve un morceau qui correspond à la règle...
        while (chercheur.find()) {
            // On récupère le bout de texte trouvé
            String morceau = chercheur.group(1);

            // On nettoie : on retire les guillemets s'il y en a
            morceau = morceau.replace("\"", "");

            // On l'ajoute à notre liste
            listeArguments.add(morceau);
        }

        // 5. LA VÉRIFICATION VISUELLE !
        System.out.println("La liste contient " + listeArguments.size() + " éléments :");

        // On parcourt notre liste finale pour afficher chaque case
        for (int i = 0; i < listeArguments.size(); i++) {
            System.out.println("Case n°" + i + " -> [" + listeArguments.get(i) + "]");
        }

        System.out.println(listeArguments);
    }
}
