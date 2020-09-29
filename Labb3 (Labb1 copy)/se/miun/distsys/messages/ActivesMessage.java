package se.miun.distsys.messages;

import java.util.ArrayList;
import java.util.List;

public class ActivesMessage extends Message {

    public List<String> clientList = new ArrayList<>();

    public ActivesMessage(List<String> clientList) {
        this.clientList = clientList;
    }
}