package org.esisar.command_control.server_logic;

import io.javalin.websocket.WsContext;

public class Client {

    private WsContext ctx;

    public Client(WsContext ctx) {
        this.ctx = ctx;
    }

    public String getSessionId(){
        return ctx.sessionId();
    }

    public void sendMessage(String message){
        ctx.send(message);
    }


}
