package org.esisar.command_control;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.*;


public class MonClientMachine {
    public static void main(String[] args) throws InterruptedException {  //thows Exeption peut-être à chener pour un try-catch sur le latch
    	CountDownLatch latch = new CountDownLatch(1);
    	
    	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); //créer un timer dans un autre thread pour gérer les pings

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
                        System.out.println("Message reçu : " + data);
                        
                        if(data.toString().equals("Goodbye")) { //à modifer pour fonctionner avec cryptage
                        	latch.countDown();
                        }
                        
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }
                    
                }).join();

        webSocket.sendText("Bonjour serveur !", true);
        
        scheduler.scheduleAtFixedRate(() -> {
        	webSocket.sendText("Ping",true);
        }, 10, 10, TimeUnit.SECONDS);
        
        //Version du Ping avec la fonction sendPing (Pong automatique)
        //scheduler.scheduleAtFixedRate(() -> {
        //	webSocket.sendPing(ByteBuffer.allocate(0));
        //}, 10, 10, TimeUnit.SECONDS);
        
        latch.await();  //enpéche l'arret du programme
        scheduler.shutdown();
    }
}