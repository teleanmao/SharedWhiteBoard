package client;


//Zelin Mao 1112821 COMP90015 Ass2 
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JOptionPane;

import whiteboard.Draw;
import whiteboard.whiteboard;



public class user {
	
	public static void main(String[] args) {
		//Start the manager.
	
		try {
			startGUI login = new startGUI("User");
			
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
		    whiteBoard.mode = "User";
		    whiteBoard.show();
	    
			Thread t = new Thread(() -> Canvasupdate(whiteBoard, socket));
			t.start();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void Canvasupdate(whiteboard whiteBoard, Socket socket) {
		
		try {
			Container currentboard = whiteBoard.board;
			
			
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		while(true) {

			
				
				if(input.available() > 0){ 
					String request = input.readUTF();
					
					//Update user list.
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
					//Manager quits.
					if(request.equals("MANAGER_QUIT")) {
						JOptionPane.showMessageDialog(currentboard, "Manger has left.");
						System.exit(0);
					}
					// Kick by admin.
					if(request.equals("KICKED")) {
						JOptionPane.showMessageDialog(currentboard, "You are kicked out by the manager.");
						
						System.exit(0);
					}
					// NewFile
					if(request.equals("NEW_FILE")) {
						if (currentboard.getComponentCount() > 5) {
							Draw currentArea = (Draw) currentboard.getComponent(5);
							currentArea.clearScreen();
							}
						continue;
					}
					
					if(request.contains("OPEN_IMG")) {
						long fileSize = Long.parseLong(request.split("/")[1]);
						long fileSizeRemaining = Long.parseLong(request.split("/")[1]);
						int chunkSize = setChunkSize(fileSizeRemaining);
						byte[] receiveBuffer = new byte[chunkSize];
						int num;
						byte[] imgByte = null;
						RandomAccessFile downloadingFile = new RandomAccessFile("rec.jpg", "rw");
						while((num=input.read(receiveBuffer))>0){
								downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
								// Reduce the file size left to read..
    							fileSizeRemaining-=num;

    							chunkSize = setChunkSize(fileSizeRemaining);
    							receiveBuffer = new byte[chunkSize];

    							if(fileSizeRemaining==0){
    								break;
    							}
							}
						downloadingFile.close();
						File imgFile = new File("rec.jpg");
						BufferedImage imgBuffer = ImageIO.read(imgFile);
						BufferedImage jpgBuffer = new BufferedImage(imgBuffer.getWidth(), imgBuffer.getHeight(), BufferedImage.TYPE_INT_RGB);
						jpgBuffer.createGraphics().drawImage(imgBuffer, 0, 0, null);
					
						 
						if (currentboard.getComponentCount() > 5) {
								Draw currentArea = (Draw) currentboard.getComponent(5);
								Color orginColor = currentArea.getG2().getColor();
								Graphics2D g2 = (Graphics2D) jpgBuffer.getGraphics();
								g2.setColor(orginColor);
								currentArea.loadImg(jpgBuffer);
								currentArea.loadG2(g2);
								currentArea.repaint();	
							}
						imgFile.delete();
						continue;
					}
					if (request.contains("CHAT")) {
						String msg = request.split("/")[1] +"\n";
						
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
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		
	}
	
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=1024*1024;
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		return chunkSize;
	}
	
	public static byte[] combineByte(byte[] b1, byte[] b2){
		if(b1 == null) return b2;
		byte[] newb = new byte[b1.length+b2.length];
		for (int i = 0; i < b1.length; i++) {
			newb[i] = b1[i];
		}
		for (int i = b1.length; i < newb.length; i++) {
			newb[i] = b2[i - b1.length];
		}
		return newb;
		
	}

}

