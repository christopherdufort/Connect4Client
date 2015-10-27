package controller;

import java.io.IOException;
import java.net.Socket;

import datacomm.Network;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class BoardController {

	private Socket socket;
	private byte[] playerMove;
	private int[][] gameBoard = new int[6][7];

	
	private final int SERVERPORT = 50000;
	  
    @FXML
    private Label error;
	private String serverHost;

    public BoardController(){
    	super();
    }
    
    @FXML
    public void moveMade(ActionEvent event) {
    	
    	System.out.println(((Node) event.getSource()).getId());
    	//System.out.println("Player made a move: " + playerMove[0] + " " + playerMove[1]);
    	
    }
    
    @FXML
    public void newGameClicked(ActionEvent event) {

    }

    @FXML
    public void quitClicked(ActionEvent event) throws IOException {
    	System.out.println("Good Bye");
    	byte[] quitRequest = new byte[]{9,0,0};
    	sendMessage(quitRequest);
    	Platform.exit();
    }
    
	public void sendMessage(byte[] message) throws IOException {
		
		socket = new Socket(serverHost, SERVERPORT);
		
		Network.sendMessage(socket, message);
		
		if (message[0] != 9) //FIXME either leave this or get response kill message.
		{
			byte[] reply;
			reply = Network.receiveMessage(socket);
			
			this.handleReply(reply);
		}


		
	}

	private void handleReply(byte[] reply) {
		
		switch(reply[0])
		{
		case 0:
			System.out.println("Reply message started with a zero");
			break;
		case 1:
			System.out.println("Reply message started with a one");
			break;
		case 3:
			System.out.println("Reply message started with a three");
			break;
		default:
			System.out.println("reply defaulted?");
		}
	}

	public void establishConnection(String serverHost) throws IOException {
		this.serverHost = serverHost;
		byte[] sessionRequest = new byte[]{0,0,0};
		
		this.sendMessage(sessionRequest);
				
	}

}