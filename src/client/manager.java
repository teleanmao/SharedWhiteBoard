package client;
//Zelin Mao 1112821 COMP90015 Ass2 

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JList;
import javax.swing.JOptionPane;

import whiteboard.Draw;
import whiteboard.whiteboard;

public class manager {
	
	public static void main(String[] args) {
		//Start the manager.
		// Check the input parameters.
		
		
		try {
			
			startGUI login = new startGUI("Manager");
			
			while(!login.getStatus()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			Socket socket = login.getSocket();
			
			DataInputStream input = new DataInputStream(socket.getInputStream());
		    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		    
		    whiteboard whiteBoard = new whiteboard();
		    
		    whiteBoard.input = input;
		    whiteBoard.output = output;
		    whiteBoard.mode = "Manager";
		   
		    whiteBoard.show();
	        
		    Thread t = new Thread(() -> Canvasupdate(whiteBoard, socket));
			t.start();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void Canvasupdate(whiteboard whiteBoard, Socket socket) {
		// Update the white board.
		try {
			Container currentboard = whiteBoard.board;
			String request;
			
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			
			while(true) {
	
				
					
					if(input.available() > 0){ 
						request = input.readUTF();
						
						
						// Check users.
						if(request.contains("CHECK_JOIN")) {
							
							String userName =  request.split("/")[1];
							System.out.println(userName + " wants to join.");
	
							int reply =  JOptionPane.showConfirmDialog(currentboard, "Client "+ userName +  " wants to join.", "Join Request", JOptionPane.YES_NO_OPTION);
	
							if(reply == 0) {
								output.writeUTF("Yes/" + userName);
								System.out.println("Accept " + userName);
							}else {
								output.writeUTF("No/" + userName);
								System.out.println("Reject " + userName);
	
							}
							continue;
						}
						
						// Update userList.
						if(request.contains("Client")) {
							
							JList userList = (JList) currentboard.getComponent(1);
							String[] infoList = request.split("USER_BREAKER");
							String[] currentUserList = new String[infoList.length];
							for (int i = 0; i < infoList.length; i++) {
								currentUserList[i] = infoList[i].split("ACTION_BREAKER")[0];
								currentUserList[i] = currentUserList[i].split(": ")[1];
								infoList[i] = infoList[i].replace("ACTION_BREAKER"," ");
							}
							whiteBoard.currentUsers = currentUserList;
							userList.setListData(infoList);
							continue;
						}
						
						if (request.contains("CHAT")) {
							String msg = request.split("/")[1]+"\n";
							
							
							whiteboard.getTextArea().append(msg);
							
						}
						if (currentboard.getComponentCount() > 5) {
							Draw currentArea = (Draw) currentboard.getComponent(5);
							if (currentArea.g2 == null) {
								Image img = currentArea.createImage(currentArea.getSize().width, currentArea.getSize().height);
								Graphics2D g2 = (Graphics2D) img.getGraphics();
								currentArea.loadImg(img);
								currentArea.loadG2(g2);
								currentArea.clearScreen();
								g2.drawImage(img, 0, 0, null);
							}
							currentArea.drawOp(request, currentArea.g2);
	
						}
					}
			}
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
}
