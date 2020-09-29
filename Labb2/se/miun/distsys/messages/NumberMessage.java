package se.miun.distsys.messages;


public class NumberMessage extends Message {

    public Integer number;

    public NumberMessage(Integer number) {
        this.number = number;
    }
}