package org.esisar.command_control.download;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class Download {

    public static void main(String fichier) throws Exception {
        // 1. On crée le verrou ici pour qu'il soit neuf à chaque nouveau téléchargement
        CountDownLatch latch = new CountDownLatch(1);

        HttpClient client = HttpClient.newHttpClient();

        // 2. On passe 'fichier' et 'latch' lors de la création du Listener
        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:7000/download"), new ClientWebSocketListener(fichier, latch))
                .join();

        System.out.println("Connecté au serveur en attente du fichier : " + fichier);

        // 3. On bloque ce thread spécifique jusqu'à ce que le verrou atteigne 0
        latch.await();

        System.out.println("Fermeture du thread de téléchargement.");
    }

    private static class ClientWebSocketListener implements WebSocket.Listener {

        private final Path cheminDestination;
        private final CountDownLatch latch;

        // Le constructeur qui reçoit le nom du fichier et le verrou depuis le main
        public ClientWebSocketListener(String nomFichier, CountDownLatch latch) {
            this.cheminDestination = Path.of(nomFichier);
            this.latch = latch;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            WebSocket.Listener.super.onOpen(webSocket);
            webSocket.request(1); // N'oubliez pas de demander le premier message !
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            try (FileChannel channel = FileChannel.open(cheminDestination,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {

                channel.write(data);

                if (last) {
                    System.out.println("Téléchargement terminé avec succès sous : " + cheminDestination.toString());
                    // Le fichier est complet, on libère le verrou !
                    latch.countDown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // En cas d'erreur d'écriture, on libère aussi pour éviter de bloquer à l'infini
                latch.countDown();
            }

            // Demande le prochain message
            webSocket.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("Erreur WebSocket : " + error.getMessage());
            latch.countDown();
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            latch.countDown();
            return null;
        }
    }
}