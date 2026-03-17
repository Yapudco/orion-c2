package org.esisar.command_control.auth;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;

import java.util.HashMap;

//Permet de vérifier que le client n'est pas un "gentil" qui essaye de comprendre comment le C2 fonctionne.
public class CheckAuth implements Handler {
    private HashMap<String, String> ids = new HashMap<>();

    public void handle(Context ctx) {
        if(ctx.path().equals("/auth")){
            return;
        }
        else{
            if(ids.containsKey(ctx.queryParam("id"))){
                return;
            }
            else{
                ctx.status(401);
                System.out.println("erreur d'authentification");
                throw new UnauthorizedResponse("Vous n'êtes pas autorisé à effectuer cette action");
            }
        }
    }
}