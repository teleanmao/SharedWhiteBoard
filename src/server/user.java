package server;
//Zelin Mao 1112821 COMP90015 Ass2 

import java.net.Socket;

public class user {
	private Socket socket;
	private String userName;
	private String userStatus = "";
	
	public user(Socket socket, String userName) {
		this.socket = socket;
		this.userName = userName;
	}
	
	public Socket getSocket() {
		return socket;
	} 
	
	public String getUsername() {
		return userName;
	}
	
	public String getStatus() {
		return userStatus;
	}
	
	public void setStatus(String status) {
		userStatus = status;
	}
	

}
