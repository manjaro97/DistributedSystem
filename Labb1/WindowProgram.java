import java.awt.EventQueue;

import javax.swing.JFrame;

import se.miun.distsys.GroupCommuncation;
import se.miun.distsys.listeners.ChatMessageListener;
import se.miun.distsys.listeners.JoinMessageListener;
import se.miun.distsys.listeners.LeaveMessageListener;
import se.miun.distsys.messages.ChatMessage;
import se.miun.distsys.messages.JoinMessage;
import se.miun.distsys.messages.LeaveMessage;

import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

//Skeleton code for Distributed systems 9hp, DT050A

public class WindowProgram implements ChatMessageListener, JoinMessageListener, LeaveMessageListener, ActionListener {

	JFrame frame;
	JTextPane txtpnActive = new JTextPane();
	JTextPane txtpnChat = new JTextPane();
	JTextPane txtpnMessage = new JTextPane();
	String ComputerName = new String();
	ArrayList<String> UserList = new ArrayList<String>();
	
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
				gc.sendChatMessage(ComputerName + " Joined The Chat");
			}

		});

		//Close Window
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				gc.shutdown();
				gc.sendChatMessage(ComputerName + " Left The Chat");
				gc.sendLeaveMessage(ComputerName);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equalsIgnoreCase("send")) {
			gc.sendChatMessage(txtpnMessage.getText());
		}		
	}
	
	@Override
	public void onIncomingChatMessage(ChatMessage chatMessage) {	
		txtpnChat.setText(chatMessage.chat + "\n" + txtpnChat.getText());
	}

	@Override
	public void onIncomingJoinMessage(JoinMessage joinMessage) {
		txtpnActive.setText(joinMessage.chat + "\n" + txtpnActive.getText());
		UserList.add(joinMessage.chat);
	}

	@Override
	public void onIncomingLeaveMessage(LeaveMessage leaveMessage) {
		txtpnChat.setText(leaveMessage.chat + "\n" + txtpnChat.getText());

		txtpnActive.setText("");
		for (int i = 0; i < UserList.size()-1; i++) {
			if(leaveMessage.chat.equals(UserList.get(i))){
				UserList.remove(i);
				i = UserList.size();
			}
		}
		for (int i = 0; i < UserList.size()-1; i++) {
			txtpnActive.setText(UserList.get(i) + "\n" + txtpnActive.getText());
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
