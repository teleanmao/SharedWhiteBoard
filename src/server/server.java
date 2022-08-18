package server;

// Zelin Mao 1112821 COMP90015 Ass2 



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class server {

	// the number of user connected
	private static int counter = 0;
    // Log of requests 
	public File logs;
	// current canvas
	public File currentCanvas;
    // current user list
	public volatile ArrayList<user> userlist = new  ArrayList<user>();
	
	public volatile JSONObject managerreply = new JSONObject();

	public server(String logPath) {
		try {
		logs = new File(logPath);
		if(!logs.exists()){
			logs.createNewFile();
           }
		}catch(Exception e) {
			
		}
		
	}
	
	public static void main(String[] args) {
		// Check the input parameters.
		if(args.length!=1) {
			System.out.println("Please input the port number.");
			return;
		}
		server Server = new server("logs.txt");
		ServerSocket s;
		
		try {
			int port = Integer.parseInt(args[0]);
			
			
			s = new ServerSocket(port);
			
			if(port<=1024 || port>=65535) {
	          	throw new Exception("Invalid Port number!");
	          	}
			System.out.println("Server is running!");
			
		}catch(NumberFormatException e) {
			System.out.println("The port number must be an integer.");
			return;
		}catch (BindException e) {
			System.out.println("The address is already in use.");
			return;
		}
		
		catch (Exception e) {
			System.out.println("Invalid port number.");
			return;
		}
		
	
			// Wait for connections.
			while(true){
				
				String userName = "";
				try { 
					Socket client = s.accept();
					String mode = "";
					DataInputStream input = new DataInputStream(client.getInputStream());
					DataOutputStream output = new DataOutputStream(client.getOutputStream());
			
					while(true) {
						// Wait for connections.
						if(input.available() > 0){ 
								String loginCommand =  input.readUTF();
								userName = loginCommand.split("/")[1];
								mode = loginCommand.split("/")[2];
								break;
								
						}
					}
					
					
					if (counter==0)  {
						if(mode.equals("Manager")) {
							output.writeUTF("ACCEPT_MANAGER");
						}else {
							output.writeUTF("NO_MANAGER");
							client.close();
							continue;
						}
					}
					else
					{
						// Send to manager and check whether it can be accepted
						
						// Duplicate check.


						boolean duplicate = false;
						for (user user: Server.userlist) {
							if (user.getUsername().equals(userName)) {
								duplicate = true;
								output.writeUTF("DUPLICATE");
								client.close();
								break;
							}
						}
						if(duplicate)continue;
						if(mode.equals("Manager")) {
							output.writeUTF("TWOMANAGER");
							client.close();
							continue;
						}
						
						//Manager acceptance check.
						Socket manager = Server.userlist.get(0).getSocket();
						
						DataOutputStream managerOutput = new DataOutputStream(manager.getOutputStream());
						
						managerOutput.writeUTF("CHECK_JOIN/" + userName);
						while(!Server.managerreply.containsKey(userName)) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
						}
						String reply = (String) Server.managerreply.get(userName);

						
						if(reply.equals("No")) {
							output.writeUTF("REJECTED");
							client.close();
							Server.managerreply.remove(userName);
							continue;
						}else {
							output.writeUTF("ACCEPT");
							Server.managerreply.remove(userName);
						}
						
					}
					
					
					Server.userlist.add(new user(client, userName));
					// Broadcast new user.
					Server.updateuserlist();
					
					// load current canvas and image back.
					
					
					Server.loadCanvas(output);
					
					Server.loadLogs(output);
				
				// Start a new thread for a connection
				final String socketUserName = userName;
				Thread t = new Thread(() -> Canvasupdate(client, socketUserName, Server));
				t.start();
				counter++;
				System.out.println("Client "+userName+" connected.");

			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Client connection failed.");
				continue;
			}
			}
		}

		
	
	private static void Canvasupdate(Socket client, String userName, server Server ) {
		try {
			DataInputStream input = new DataInputStream(client.getInputStream());
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			while(true) {
					if(input.available() > 0){ 
						

						String request = input.readUTF();

						// client closes.
						if(request.equals("QUIT")) {
							int userid = 0;
							for (int i = 0; i < Server.userlist.size(); i++) {
								user currentUser = Server.userlist.get(i);
								if(currentUser.getUsername().equals(userName)) {
									userid = i;  
									break;
								}
							}
							// Not manager closes.
							if (userid>0) {
								client.close();
								Server.userlist.remove(userid);
								Server.updateuserlist();
								System.out.println("Client " + userName + " closed.");
								return;
							}else {
								// manager closes.
								
								for (int i = 1; i < Server.userlist.size(); i++) {
									Socket currentSocket =  Server.userlist.get(i).getSocket();
									DataOutputStream currentOutput = new DataOutputStream(currentSocket.getOutputStream());
									currentOutput.writeUTF("MANAGER_QUIT");
								}
								
								System.out.println("Manager Closed.");
								PrintWriter writer = new PrintWriter(Server.logs);
								writer.print("");
								writer.close();
							    Server.logs.delete();
							    Server.logs = new File("logs.txt");
							    Server.logs.createNewFile();
								
								
								
								if(Server.currentCanvas!=null) {
									Server.currentCanvas.delete();
									Server.currentCanvas = null;
								}
								Server.counter = 0;
								client.close();
								Server.userlist = new  ArrayList<user>();
								break;
								
							}
						}
						// update status.
						if(request.contains("STATUS")) {
							JSONParser parser = new JSONParser();
							try {
								JSONObject statusCommand = (JSONObject) parser.parse(request);
								String currentStatus = (String) statusCommand.get("STATUS");
								for (int i = 0; i < Server.userlist.size(); i++) {
									user currentuser = Server.userlist.get(i);
									if(currentuser.getUsername().equals(userName)) {
										currentuser.setStatus(currentStatus); 
										Server.updateuserlist();
										break;
									}
	
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							
						}
						
						// Manager acceptance 
						if(request.contains("Yes") || request.contains("No")) {
							
							String resp = request.split("/")[0];
							String corspuser = request.split("/")[1];
							Server.managerreply.put(corspuser,resp);
								
						}
						
						//Kick out 
						if(request.contains("KICK")) {
							String kickuserName = request.split("/")[1];
							
							for (int i = 1; i < Server.userlist.size(); i++) {
								user currentuser = Server.userlist.get(i);
								if(currentuser.getUsername().equals(kickuserName)) {
									
									Socket kickSocket = currentuser.getSocket();
									DataOutputStream kickOutput = new DataOutputStream(kickSocket.getOutputStream());
									kickOutput.writeUTF("KICKED");
									kickSocket.close();
									Server.userlist.remove(i);
									Server.updateuserlist();
									break;
								}
							}
							System.out.println("Client " + kickuserName + " is kicked out.");
							continue;		
						}
						
						// Chat
						
						if (request.contains("CHAT")) {
							String msg = request.split("/")[1];
							String reply = "CHAT/" + userName +": " + msg;
							//Broadcast to other users.
							for(int i = 0; i<Server.userlist.size(); i++){
								
								
								Socket user = Server.userlist.get(i).getSocket();
								DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
								userOutput.writeUTF(reply);
								
							}
							Server.updateRecord(reply);
						}
						// File request.
						if(request.equals("NEW_FILE")) {
							
							PrintWriter writer = new PrintWriter(Server.logs);
							writer.print("");
							writer.close();
							if(Server.currentCanvas!=null) {
								Server.currentCanvas.delete();
								Server.currentCanvas = null;
							}
							 for(int i = 1; i<Server.userlist.size(); i++){
									Socket user = Server.userlist.get(i).getSocket();
									DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
									userOutput.writeUTF("NEW_FILE");
								}
						}
						
						if(request.contains("OPEN_IMG")) {
							PrintWriter writer = new PrintWriter(Server.logs);
							writer.print("");
							writer.close();
							
							long fileSize = Long.parseLong(request.split("/")[1]);
							long remain = fileSize;
							int chunkSize = setChunkSize(fileSize);
							byte[] receiveBuffer = new byte[chunkSize];
							int num;
							byte[] imgByte = null;
							RandomAccessFile openedfile = new RandomAccessFile("image.jpg", "rw");
							while((num=input.read(receiveBuffer))>0){
									openedfile.write(Arrays.copyOf(receiveBuffer, num));
									
	    							remain-=num;

	    							chunkSize = setChunkSize(remain);
	    							receiveBuffer = new byte[chunkSize];

	    							if(remain==0){
	    								break;
	    							}
								}
							openedfile.close();
							Server.currentCanvas = new File("image.jpg");

							 for(int i = 1; i<Server.userlist.size(); i++){
									Socket user = Server.userlist.get(i).getSocket();
									DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
									Server.loadCanvas(userOutput);
								}
						}
						
						
						if(!request.contains("Type"))continue;
						
						Server.updateRecord(request);

						//Broadcast the new request to other users.
						for(int i = 0; i<Server.userlist.size(); i++){
							String currentUserName = Server.userlist.get(i).getUsername();
							if(currentUserName.equals(userName))continue;
							Socket user = Server.userlist.get(i).getSocket();
							DataOutputStream userOutput = new DataOutputStream(user.getOutputStream());
							 userOutput.writeUTF(request);
						}

					}
			}
			
		}catch(Exception e) {
			System.out.println("User " + userName + " disconnect.");
		}
		
	}
	
	public void updateRecord(String request) {
		try {
			
			
			PrintWriter pw = new PrintWriter(new FileWriter(logs, true));
			pw.println(request);
			pw.flush();
			pw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//Load logs of operations to the new user.
	public void loadLogs(DataOutputStream output) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(logs));
			String s = null;
	        while((s = br.readLine())!=null){
	        	output.writeUTF(s);
	    
        	}
	        br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
    //update current user list for each user.
	public void updateuserlist() {
		
		String userStr = "";
		for(user user: userlist) {
			userStr += "Client: " + user.getUsername() +"ACTION_BREAKER" +  user.getStatus() +  "USER_BREAKER";
		}
		for(user user: userlist) {
			try {
				DataOutputStream userOutput = new DataOutputStream(user.getSocket().getOutputStream());
				userOutput.writeUTF(userStr);	
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

	}
	
	public void loadCanvas(DataOutputStream output) {
		try {
			if(currentCanvas !=null && currentCanvas.exists()) {
				output.writeUTF("OPEN_IMG/" + Long.toString(currentCanvas.length()));
				 byte[] sendingBuffer = new byte[1024*1024];
				 RandomAccessFile byteFile = new RandomAccessFile(currentCanvas,"r");
				 int num;
					
					while((num = byteFile.read(sendingBuffer)) > 0){
						output.write(Arrays.copyOf(sendingBuffer, num));
					}
					byteFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static int setChunkSize(long filesize){
		// set the chunkSize
		int chunkSize=1024*1024;
		if(filesize<chunkSize){
			chunkSize=(int) filesize;
		}
		
		return chunkSize;
	}
	

		
	
	
}
