package client;
//Zelin Mao 1112821 COMP90015 Ass2 
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class startGUI extends JFrame{
	
	private JFrame frmStart;
	private JTextField inputAddr;
	private JTextField inputPort;
	private JButton btnConnect;
	private String address;
	private String username;
	private int port;
	private JTextField inputUserName;
	private boolean connect = false;
	private Socket socket;
	
	
	

// Frame Initialization
	public startGUI(String mode) {
		frmStart = new JFrame();
		frmStart.setTitle("White Board Connection");
		frmStart.setFont(new Font("Arial", Font.PLAIN, 12));
		frmStart.getContentPane().setBackground(Color.WHITE);
		frmStart.setBounds(100, 100, 444, 304);
		frmStart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStart.getContentPane().setLayout(null);
		
		JTextPane txtpnWhiteBoard = new JTextPane();
		txtpnWhiteBoard.setEditable(false);
		txtpnWhiteBoard.setFont(new Font("Arial", Font.PLAIN, 20));
		txtpnWhiteBoard.setForeground(new Color(255, 255, 255));
		txtpnWhiteBoard.setBackground(SystemColor.textInactiveText);
		if (mode.equals("Manager")) {
			
			txtpnWhiteBoard.setText("                    White Board Creation");
		}else {
			txtpnWhiteBoard.setText("                   White Board Connection");
		}
		
		txtpnWhiteBoard.setBounds(0, 0, 438, 62);
		frmStart.getContentPane().add(txtpnWhiteBoard);
		
		JTextPane txtpnAddress = new JTextPane();
		txtpnAddress.setEditable(false);
		txtpnAddress.setFont(new Font("Arial", Font.PLAIN, 12));
		txtpnAddress.setBackground(UIManager.getColor("CheckBox.interiorBackground"));
		txtpnAddress.setText("Server Address:");
		txtpnAddress.setBounds(10, 101, 56, 44);
		frmStart.getContentPane().add(txtpnAddress);
		
		// user name
		JTextPane UserName = new JTextPane();
		UserName.setText("User Name:");
		UserName.setFont(new Font("Arial", Font.PLAIN, 12));
		UserName.setEditable(false);
		UserName.setBackground(Color.WHITE);
		UserName.setBounds(10, 165, 56, 44);
		frmStart.getContentPane().add(UserName);
		
		inputUserName = new JTextField();
		inputUserName.setFont(new Font("Arial", Font.PLAIN, 12));
		inputUserName.setColumns(10);
		inputUserName.setBounds(73, 172, 130, 26);
		frmStart.getContentPane().add(inputUserName);
		
		//input address
		inputAddr = new JTextField("localhost");
		
		inputAddr.setFont(new Font("Arial", Font.PLAIN, 12));
		inputAddr.setBounds(73, 107, 130, 26);
		frmStart.getContentPane().add(inputAddr);
		inputAddr.setColumns(10);
		
		JTextPane txtpnPort = new JTextPane();
		txtpnPort.setEditable(false);
		txtpnPort.setFont(new Font("Arial", Font.PLAIN, 12));
		txtpnPort.setText("Server Port:");
		txtpnPort.setBackground(SystemColor.window);
		txtpnPort.setBounds(224, 101, 56, 44);
		frmStart.getContentPane().add(txtpnPort);
		
		//input port 
		inputPort = new JTextField();
		inputPort.setFont(new Font("Arial", Font.PLAIN, 12));
		inputPort.setColumns(10);
		inputPort.setBounds(289, 107, 130, 26);
		frmStart.getContentPane().add(inputPort);
		
		//connect button
		if (mode.equals("Manager")){
			btnConnect = new JButton("Create");
		}
		else {
			btnConnect = new JButton("Join");
		}
		btnConnect.setFont(new Font("Arial", Font.PLAIN, 12));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					address = inputAddr.getText();
					port = Integer.parseInt(inputPort.getText());
					username = inputUserName.getText();
			          
			        if (!address.equals("localhost") && (!address.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"))){
			        	throw new Exception ("Invalid IP address!");
			        	}
			        else if(port<=1024 || port>=65535) {
			          	throw new Exception("Invalid Port number!");
			          	}
			        else if (username.isEmpty()) {
						throw new Exception("User Name is Empty!");
					}
			        socket = new Socket(address, port);
			        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					DataInputStream input = new DataInputStream(socket.getInputStream());
					output.writeUTF("CONNECT/" + username + "/" + mode);
					System.out.println("Connect " + username + ": " + mode);
			        
					
					while (true) {
						if(input.available()>0) {
							String result = input.readUTF();
							switch (result) {
								case "ACCEPT": 
									
									JOptionPane.showMessageDialog(frmStart, "You are accepted by the manager.");
									connect = true;
									frmStart.setVisible(false);
									break;
								
								case "ACCEPT_MANAGER":
									JOptionPane.showMessageDialog(frmStart, "You are now the manager.");
									connect = true;
									frmStart.setVisible(false);
									break;
								
								case "DUPLICATE":
									JOptionPane.showMessageDialog(frmStart, "User name already exsited.");
									break;
								
								case "TWOMANAGER":
									JOptionPane.showMessageDialog(frmStart, "Manager already exsited.");
									break;
								
								case "REJECTED":{
									JOptionPane.showMessageDialog(frmStart, "Connection rejected by the manager.");
									break;
								}
								case "NO_MANAGER":
									JOptionPane.showMessageDialog(frmStart, "No Manager exsited.");
									break;
								
								
							}
					      break;
						}
					}
					
					
			       
		      	    }
			   
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(frmStart, "Invalid Port Number! Please input digits.");
				}
				catch(ConnectException e) {
					JOptionPane.showMessageDialog(frmStart, "Connection Error.");
				}
				catch (Exception e) {
					if (e.getMessage()!=null) {
						JOptionPane.showMessageDialog(frmStart, e.getMessage());
					}else {
						JOptionPane.showMessageDialog(frmStart, "Invaild address or port!");
						}
					}
				}
			}
		);
		
		btnConnect.setBounds(224, 171, 195, 29);
		frmStart.getContentPane().add(btnConnect);
		
		JTextPane txtpnexplain = new JTextPane();
		txtpnexplain.setEditable(false);
		txtpnexplain.setFont(new Font("Arial", Font.PLAIN, 10));
		txtpnexplain.setText("Please enter the white board server address, port and user name below:");
		txtpnexplain.setBounds(10, 72, 409, 21);
		frmStart.getContentPane().add(txtpnexplain);
		
		frmStart.setVisible(true);
		
		
		
		
	}

	public Socket getSocket() {
		return socket;
	}
	
	public boolean getStatus() {
		return connect;
	}
}
	