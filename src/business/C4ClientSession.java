package business;
/*
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import controller.BoardController;
import datacomm.Network;

public class C4ClientSession {

	private String serverHost;
	private int serverPort;
	private BoardController myController;
	
	Socket socket;
	byte[] message = new byte[3];
	int packetLength = 3;
	private static boolean keepPlaying;
	
	public C4ClientSession(String serverHost)
	{
		this.serverHost = serverHost;
		this.serverPort = 50000;
		this.myController = new BoardController();
		keepPlaying = true;

	}
	
	public boolean establishSession() throws UnknownHostException, IOException {
		boolean connectionEstablished = false;
		
		socket = new Socket(serverHost, serverPort);
	
		message[0] = 0;
		message[1] = 0;
		message[2] = 0;
		Network.sendMessage(socket, message);
		
		int reply = -1;
		reply = Network.receiveMessage(socket)[0];
		System.out.println("Reply From server: " + reply);

		if (reply == 1){
			connectionEstablished = true;
		}
		System.out.println("Established Connection? " + connectionEstablished);
		
		//socket.close();
		
		return connectionEstablished;
	} 
	
	public void sendValidMove() throws IOException{
		//await valid move from UI
		System.out.println("InValidMove");
		byte[] move = myController.getMove();
		
		byte[] message = new byte[3];
		
		message[0] = 11;
		message[1] = move[0];
		message[2] = move[1];
		
		Network.sendMessage(socket, message);
		System.out.println("Send a move to server " + message[1] + " " + message[2]);
		


	 }

	public void beginSession()throws IOException {
		System.out.println("ClientSession has begun");
		//while (keepPlaying){
			sendValidMove();
			
			//int reply = -1;
			//reply = Network.receiveMessage(socket)[0];
			//System.out.println("Reply From server: " + reply);
		//}
		
	}
	
	public static void endSession(boolean end){
		keepPlaying = end;
	}
		
}
*/

