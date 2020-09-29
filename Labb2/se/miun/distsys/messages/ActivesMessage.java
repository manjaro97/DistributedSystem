package se.miun.distsys.messages;

import java.util.List;

public class ActivesMessage extends Message {

    public List<String> clientList;
    public List<Integer> numberList;
    public String name;

    public ActivesMessage(List<String> clientList, List<Integer> numberList, String name) {
        this.clientList = clientList;
        this.numberList = numberList;
        this.name = name;
    }
}