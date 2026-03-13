package org.esisar.command_control;

import io.javalin.Javalin;
import org.esisar.command_control.encrypt.AES;
import org.esisar.command_control.encrypt.RSA;
import org.esisar.command_control.server_logic.Client;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
       // Javalin app = Javalin.create().start(7000);

        Javalin app = Javalin.create(config -> {
            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setIdleTimeout(Duration.ofHours(2)); // on définit le temps de Timeout à 2heures pour pas etre emmerdé
            });
        }).start(7000);

        ArrayList<Client> clients = new ArrayList<>();

        //j'ai changé d'avis je fais du websocket

        app.ws("/", ws -> {
            ws.onConnect(ctx ->{

                System.out.println("Machine " + ctx.sessionId() + " connected");
                Client client = new Client(ctx);
                clients.add(client);


                // On gère la logique du "HandShake"
                RSA rsa = new RSA();
                AES aes = new AES();


            });

            ws.onMessage(ctx -> {
                //Gestion anti déconection => obligé pour éviter que les rooters nous emmerdent.
                if(ctx.message().equals("Ping")){
                    System.out.println("Pong");
                    ctx.send("Pong");
                }else{
                    System.out.println(ctx.message());
                }

            });

            //
            ws.onClose(ctx ->{
                System.out.println("Machine " + ctx.sessionId() + " disconnected");
                for(Client client : clients){
                    if (ctx.sessionId().equals(client.getSessionId())) {clients.remove(client);}
                    //client... client... CLIENT AAAAAAAAAAAAAAAAAAAA
                }
            });
        });


        // Le parseur  (désolé Béatrice <3)
        Thread consoleReader = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("> Entrer Commande");

            while (true) {
                if (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    for(Client client : clients){
                        client.sendMessage(message);
                    }

                }
            }
        });


        consoleReader.setDaemon(true); // S'arrête si le serveur s'arrête
        consoleReader.start();
    }
}
