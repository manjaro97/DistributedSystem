package se.miun.distsys.messages;

import java.util.LinkedHashMap;

public class ActivesMessage extends Message {

    public LinkedHashMap<String, Integer> clientList = new LinkedHashMap<String, Integer>();

    public ActivesMessage(LinkedHashMap<String, Integer> clientList) {
        this.clientList = clientList;
    }
}