import java.awt.EventQueue;

import javax.swing.JFrame;

import se.miun.distsys.GroupCommuncation;
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


import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JScrollPane;

//Skeleton code for Distributed systems 9hp, DT050A

public class WindowProgram implements ChatMessageListener, JoinMessageListener, LeaveMessageListener, ActivesMessageListener, NumberMessageListener, FinalJoinMessageListener, ActionListener {

	JFrame frame;
	JTextPane txtpnActive = new JTextPane();
	JTextPane txtpnChat = new JTextPane();
	JTextPane txtpnMessage = new JTextPane();
	String ComputerName = new String();
	ArrayList<String> UserList = new ArrayList<String>();
	String User = new String();
	ArrayList<Integer> NumberList = new ArrayList<Integer>();
	
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
		gc.setNumberMessageListener(this);
		gc.setFinalJoinMessageListener(this);
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
				gc.sendJoinMessage(ComputerName);
			}
		});

		//Close Window
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				gc.shutdown();
				gc.sendLeaveMessage(ComputerName + "_" + id);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equalsIgnoreCase("send")) {
			gc.sendChatMessage(id + ": " + txtpnMessage.getText());
		}		
	}
	
	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {	
		txtpnChat.setText(chatMessage.chat + "\n" + txtpnChat.getText());
	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		gc.sendActivesMessage(UserList, NumberList, joinMessage.chat);
	}

	final int UnassignedId = -1;
	private int id = UnassignedId;

	@Override
	public void onIncomingActivesMessage(ActivesMessage activesMessage) {
		if(activesMessage.numberList.size() > NumberList.size()){
			NumberList = (ArrayList<Integer>) activesMessage.numberList;
		}

		if(UserList.size() != 1){
			if(id == UnassignedId) {
				if (NumberList.isEmpty()) {
					id = 0;
				} else {
					id = (NumberList.get(NumberList.size()-1)) + 1;
				}
				NumberList.add(id);
				gc.sendNumberMessage(id);

				gc.sendChatMessage(ComputerName + "_" + id + " Joined the Chat");
				txtpnActive.setText(ComputerName + "_" + id  + "\n" + txtpnActive.getText());
				gc.sendFinalJoinMessage(ComputerName + "_" + id);
				UserList.add(ComputerName + "_" + id );
			}
		}

		gc.sendFinalJoinMessage(ComputerName + "_" + id);
		//NewJoinMessage(ComputerName, id){
		// 	if (!UserList.contains(ComputerName + "_" + id)){
		//			UserList.add(activesMessage.name);
		//			txtpnActive.setText(ComputerName + "_" + id + "\n" + txtpnActive.getText());
		//		}
		// }

	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		txtpnChat.setText(leaveMessage.chat + " Left The Chat\n" + txtpnChat.getText());

		txtpnActive.setText("--== Active Users ==--");
		for (int i = 0; i < UserList.size(); i++) {
			if(leaveMessage.chat.equals(UserList.get(i))){
				UserList.remove(i);
				i = UserList.size();
			}
		}
		for (int i = 0; i < UserList.size(); i++) {
			txtpnActive.setText(UserList.get(i) + "\n" + txtpnActive.getText());
		}
	}

	@Override
	public void onIncomingNumberMessage(NumberMessage numberMessage) {
		if(!NumberList.contains(numberMessage.number)){
			NumberList.add(numberMessage.number);
		}
	}

	@Override
	public void onIncomingFinalJoinMessage(FinalJoinMessage finaljoinMessage) {
		if (!UserList.contains(finaljoinMessage.chat)){
			UserList.add(finaljoinMessage.chat);
			txtpnActive.setText(finaljoinMessage.chat + "\n" + txtpnActive.getText());
		}
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
