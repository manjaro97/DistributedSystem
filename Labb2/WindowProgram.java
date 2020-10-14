import java.awt.EventQueue;

import javax.swing.JFrame;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.listeners.JoinMessageListener;
import se.miun.distsys.listeners.LeaveMessageListener;
import se.miun.distsys.listeners.ActivesMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;
import se.miun.distsys.messages.ActivesMessage;


import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JScrollPane;

//Skeleton code for Distributed systems 9hp, DT050A

public class WindowProgram implements ChatMessageListener, JoinMessageListener, LeaveMessageListener, ActivesMessageListener, ActionListener {

	JFrame frame;
	JTextPane txtpnActive = new JTextPane();
	JTextPane txtpnChat = new JTextPane();
	JTextPane txtpnMessage = new JTextPane();
	String ComputerName = "";
	String UserName = "";
	//ArrayList<String> UserList = new ArrayList<String>();
	LinkedHashMap<String, Integer> UserList = new LinkedHashMap<String, Integer>();
	int UserNumber;

	GroupCommuncation gc = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowProgram window = new WindowProgram();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public WindowProgram() {
		initializeFrame();

		gc = new GroupCommuncation();
		gc.setChatMessageListener(this);
		gc.setJoinMessageListener(this);
		gc.setLeaveMessageListener(this);
		gc.setActivesMessageListener(this);
		System.out.println("Group Communcation Started");
	}

	private void initializeFrame() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		//Active Users
		JScrollPane scrollPaneClients = new JScrollPane();
		frame.getContentPane().add(scrollPaneClients);
		scrollPaneClients.setViewportView(txtpnActive);
		txtpnActive.setEditable(false);
		txtpnActive.setText("--== Active Users ==--");


		//Chat Box
		JScrollPane scrollPaneChat = new JScrollPane();
		frame.getContentPane().add(scrollPaneChat);
		scrollPaneChat.setViewportView(txtpnChat);
		txtpnChat.setEditable(false);
		txtpnChat.setText("--== Group Chat ==--");

		//Write Message
		txtpnMessage.setText("Message");
		frame.getContentPane().add(txtpnMessage);

		//Send Message Button
		JButton btnSendChatMessage = new JButton("Send Chat Message");
		btnSendChatMessage.addActionListener(this);
		btnSendChatMessage.setActionCommand("send");

		frame.getContentPane().add(btnSendChatMessage);

		//Open Window And Set Computer Name
		frame.addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowOpened(WindowEvent winEvt){
				ComputerName = getComputerName();
				UserNumber = getRandomIntegerBetweenRange();
				UserName = ComputerName + "_" + UserNumber;
				gc.sendJoinMessage(UserName );
			}
		});

		//Close Window
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				gc.shutdown();
				gc.sendLeaveMessage(UserName);
			}
		});
	}

	public static int getRandomIntegerBetweenRange(){
		Random rand = new Random();
		return rand.nextInt(10000);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equalsIgnoreCase("send")) {
			UserList.put(UserName, UserList.get(UserName)+1);
			gc.sendChatMessage(UserNumber + ": " + txtpnMessage.getText(), UserName, UserList);
		}
	}

	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {
		if(!UserName.equals(chatMessage.user)){
			UserList.put(UserName, UserList.get(UserName)+1);
		}

		Integer sumOwn = 0;
		for(String user : chatMessage.clientList.keySet()){
			if(!user.equals(UserName) && !user.equals(chatMessage.user)){
				sumOwn += chatMessage.clientList.get(user);
			}
		}
		Integer sumElse = 0;
		for(String user : UserList.keySet()){
			if(!user.equals(UserName) && !user.equals(chatMessage.user)){
				sumElse += UserList.get(user);
			}
		}
		if(sumOwn != sumElse && sumOwn != 0 && sumElse != 0){
			System.out.println("Chatlogg is missing or out of order");
		}


		//Write to chat and update list to highest values;
		txtpnChat.setText(chatMessage.chat + "\n" + txtpnChat.getText());
		for(String user : chatMessage.clientList.keySet()){
			if(UserList.get(user) < chatMessage.clientList.get(user)){
				UserList.put(user, chatMessage.clientList.get(user));
			}
		}

		/*System.out.println("Values are awesome: " + UserNumber);
		for(String user : UserList.keySet()){
			System.out.println("User: " + user + " Value: " + UserList.get(user));
		}*/
	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		txtpnChat.setText(joinMessage.chat + " Joined the Chat\n" + txtpnChat.getText());

		AddActivesList(joinMessage.chat);

		UpdateActivesList();

		gc.sendActivesMessage(UserList);
	}

	@Override
	public void onIncomingActivesMessage(ActivesMessage activesMessage) {
		if(activesMessage.clientList.size() > UserList.size()){
			CopyActivesList(activesMessage.clientList);
		}
	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		txtpnChat.setText(leaveMessage.chat + " Left The Chat\n" + txtpnChat.getText());

		ReduceActivesList(leaveMessage.chat);

		UpdateActivesList();
	}

	public void UpdateActivesList(){
		txtpnActive.setText("--== Active Users ==--");
		for(String user : UserList.keySet()){
			txtpnActive.setText(user + "\n" + txtpnActive.getText());
		}
	}

	public void ReduceActivesList(String username){
		if(UserList.containsKey(username)){
			UserList.remove(username);
		}
		else{
			System.out.println("User does not exist.");
		}
	}

	public void AddActivesList(String username){
		UserList.put(username, 0);
	}

	public void CopyActivesList(LinkedHashMap<String, Integer> clientList){
		UserList.clear();
		for (String user : clientList.keySet()) {
			UserList.put(user , 0);
		}
		UpdateActivesList();
	}

	private String getComputerName()
	{
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME"))
			return env.get("COMPUTERNAME");
		else if (env.containsKey("HOSTNAME"))
			return env.get("HOSTNAME");
		else
			return "Unknown Computer";
	}
}

