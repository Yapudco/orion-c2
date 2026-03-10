package org.esisar.command_control;

import io.javalin.Javalin; // Import propre
import org.esisar.command_control.auth.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        //Le serveur vérifie l'authentification A CHAQUE FOIS
        //Sauf pour l'accès au endpoint /auth... sinon c'est con.
        app.before(new CheckAuth());

        //Tous les endpoints de l'API
        app.get("/auth", ctx -> {new Authenticate();});
        app.get("/test", ctx -> ctx.result("t'as surprenament bien codé ça! Bravo"));
    }
}
