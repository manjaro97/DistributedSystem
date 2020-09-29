package se.miun.distsys.messages;

public class FinalJoinMessage extends Message {

    public String chat = "";

    public FinalJoinMessage(String chat) {
        this.chat = chat;
    }
}
