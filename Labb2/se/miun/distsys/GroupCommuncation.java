package se.miun.distsys;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.listeners.JoinMessageListener;
import se.miun.distsys.listeners.LeaveMessageListener;
import se.miun.distsys.listeners.ActivesMessageListener;
import se.miun.distsys.listeners.NumberMessageListener;
import se.miun.distsys.listeners.FinalJoinMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.ActivesMessage;
import se.miun.distsys.messages.NumberMessage;
import se.miun.distsys.messages.FinalJoinMessage;
import se.miun.distsys.messages.Message;
import se.miun.distsys.messages.MessageSerializer;

public class GroupCommuncation {
	
	private int datagramSocketPort = 1912; //Birthyear of Alan Turing!
	DatagramSocket datagramSocket = null;	
	boolean runGroupCommuncation = true;	
	MessageSerializer messageSerializer = new MessageSerializer();
	
	//Listeners
	ChatMessageListener chatMessageListener = null;
	JoinMessageListener joinMessageListener = null;
	LeaveMessageListener leaveMessageListener = null;
	ActivesMessageListener activesMessageListener = null;
	NumberMessageListener numberMessageListener = null;
	FinalJoinMessageListener finaljoinMessageListener = null;

	public GroupCommuncation() {			
		try {
			runGroupCommuncation = true;				
			datagramSocket = new MulticastSocket(datagramSocketPort);
						
			ReceiveThread rt = new ReceiveThread();
			rt.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		runGroupCommuncation = false;		
	}
	

	class ReceiveThread extends Thread{
		
		@Override
		public void run() {
			byte[] buffer = new byte[65536];		
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			
			while(runGroupCommuncation) {
				try {
					datagramSocket.receive(datagramPacket);										
					byte[] packetData = datagramPacket.getData();					
					Message receivedMessage = messageSerializer.deserializeMessage(packetData);					
					handleMessage(receivedMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
				
		private void handleMessage (Message message) {

			if(message instanceof ChatMessage) {
				ChatMessage chatMessage = (ChatMessage) message;
				if(chatMessageListener != null){
					chatMessageListener.onIncomingChatMessage(chatMessage);
				}
			}else if(message instanceof JoinMessage) {
				JoinMessage joinMessage = (JoinMessage) message;
				if(joinMessageListener != null){
					joinMessageListener.onIncomingJoinMessage(joinMessage);
				}
			} else if(message instanceof LeaveMessage) {
				LeaveMessage leaveMessage = (LeaveMessage) message;
				if(leaveMessageListener != null){
					leaveMessageListener.onIncomingLeaveMessage(leaveMessage);
				}
			} else if(message instanceof ActivesMessage) {
				ActivesMessage activesMessage = (ActivesMessage) message;
				if(activesMessageListener != null){
					activesMessageListener.onIncomingActivesMessage(activesMessage);
				}
			} else if(message instanceof NumberMessage) {
				NumberMessage numberMessage = (NumberMessage) message;
				if(numberMessageListener != null){
					numberMessageListener.onIncomingNumberMessage(numberMessage);
				}
			} else if(message instanceof FinalJoinMessage) {
				FinalJoinMessage finaljoinMessage = (FinalJoinMessage) message;
				if(finaljoinMessageListener != null){
					finaljoinMessageListener.onIncomingFinalJoinMessage(finaljoinMessage);
				}
			} else {
				System.out.println("Unknown message type");
			}			
		}		
	}	
	
	public void sendChatMessage(String chat) {
		try {
			ChatMessage chatMessage = new ChatMessage(chat);
			byte[] sendData = messageSerializer.serializeMessage(chatMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void sendJoinMessage(String chat) {
		try {
			JoinMessage joinMessage = new JoinMessage(chat);
			byte[] sendData = messageSerializer.serializeMessage(joinMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendLeaveMessage(String chat) {
		try {
			LeaveMessage leaveMessage = new LeaveMessage(chat);
			byte[] sendData = messageSerializer.serializeMessage(leaveMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendActivesMessage(List<String> clientList, List<Integer> numberList, String name) {
		try {
			ActivesMessage activesMessage = new ActivesMessage(clientList, numberList, name);
			byte[] sendData = messageSerializer.serializeMessage(activesMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendNumberMessage(Integer number) {
		try {
			NumberMessage numberMessage = new NumberMessage(number);
			byte[] sendData = messageSerializer.serializeMessage(numberMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendFinalJoinMessage(String chat) {
		try {
			FinalJoinMessage finaljoinMessage = new FinalJoinMessage(chat);
			byte[] sendData = messageSerializer.serializeMessage(finaljoinMessage);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					InetAddress.getByName("255.255.255.255"), datagramSocketPort);
			datagramSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setChatMessageListener(ChatMessageListener listener) {
		this.chatMessageListener = listener;		
	}

	public void setJoinMessageListener(JoinMessageListener listener) {
		this.joinMessageListener = listener;
	}

	public void setLeaveMessageListener(LeaveMessageListener listener) {
		this.leaveMessageListener = listener;
	}

	public void setActivesMessageListener(ActivesMessageListener listener) {
		this.activesMessageListener = listener;
	}

	public void setNumberMessageListener(NumberMessageListener listener) {
		this.numberMessageListener = listener;
	}

	public void setFinalJoinMessageListener(FinalJoinMessageListener listener) {this.finaljoinMessageListener = listener;}
}
