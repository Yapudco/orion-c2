package org.esisar.command_control;

import io.javalin.Javalin;




public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        //j'ai changé d'avis je fais du websocket
        //et celui qui m'en empèche est un creuvard
        app.ws("/", ws -> {
            ws.onConnect(ctx ->{
                System.out.println("Machine " + ctx.sessionId() + " connected");
                ctx.send("ceci est un test");
            });
            ws.onClose(ctx ->{
                System.out.println("Machine " + ctx.sessionId() + " closed");
            });
        });



    }
}
