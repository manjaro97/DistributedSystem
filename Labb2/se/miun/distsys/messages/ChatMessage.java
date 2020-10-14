package se.miun.distsys.messages;

import java.util.LinkedHashMap;

public class ChatMessage extends Message {

	public String chat = "";
	public String user = "";
	public LinkedHashMap<String, Integer> clientList = new LinkedHashMap<String, Integer>();

	public ChatMessage(String chat, String user,  LinkedHashMap<String, Integer> clientList) {
		this.chat = chat;
		this.user = user;
		this.clientList = clientList;
	}
}

