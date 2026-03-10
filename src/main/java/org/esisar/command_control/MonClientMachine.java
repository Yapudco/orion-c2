package org.esisar.command_control;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class MonClientMachine {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:7000/"), new WebSocket.Listener() {
                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        System.out.println("Message reçu : " + data);
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }
                }).join();

        webSocket.sendText("Bonjour serveur !", true);
    }
}