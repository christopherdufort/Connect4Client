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
	private boolean clientsTurn = true;
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
    public void moveMade(ActionEvent event) throws IOException {
    	
    	System.out.println(((Node) event.getSource()).getId());
    	String id = ((Node) event.getSource()).getId();
    	int column = Integer.parseInt(id.substring(1, 2));
    	
    	if (gameBoard[column][5] !=0)  //5 is actually 6 zero based counting.
    	{
    		//do nothing column is full
    	}
    	else
    	{
    		int emptyRow = findEmptyPosition(column);
    		updateBoardDisplay(clientsTurn, column,emptyRow);
    		byte[] move = new byte[] {2,(byte) column , (byte) emptyRow};
    		sendMessage(move);
    	}
    	
    	
    }
    

	private int findEmptyPosition(int column) {
		for(int i = 0; i< gameBoard[column].length; i++)
		{
			if (gameBoard[column][i] == 0)
				return i;
		}
		return -1; //TODO this should never happen lets avoid 2 returns?
		
	}
	
    private void updateBoardDisplay(boolean clientsTurn, int column, int emptyRow) {
    	if (clientsTurn)
    	{
    		gameBoard [column][emptyRow] = 2;
    		//TODO change gui imageview at that position to yellow marker
    	}
    	else
    	{
    		gameBoard[column][emptyRow] = 1;
    		//TODO change gui imageview at that position to red marker
    	}
    	
    	//TODO remove this after gui updates properly
    	for(int i = 0; i < 6 ; i++)
    	{
    		for (int j = 0; j < 7; j++)
    		{
    			System.out.print(gameBoard[i][j] + " ");
    		}
    		System.out.println();
    	}
  
		
	}
	@FXML
    public void newGameClicked(ActionEvent event) {

    }

    @FXML
    public void quitClicked(ActionEvent event) throws IOException {
    	System.out.println("Good Bye");
    	byte[] quitRequest = new byte[]{9,0,0};
    	sendMessage(quitRequest);
    	//TODO close socket?
    	Platform.exit();
    }
    
	public void sendMessage(byte[] message) throws IOException {
		//FIXME THIS ONLY WORKS FOR A SINGLE MESSAGE WHY!!!?
		this.socket = new Socket(serverHost, SERVERPORT); //FIXME WHERE DOES THIS GO??!?!?!
		
		Network.sendMessage(socket, message);
		//If message sent is not a end game message await response
		if (message[0] != 9) //TODO either leave this or get response kill message.
		{
			byte[] reply;
			reply = Network.receiveMessage(socket);
			
			this.handleReply(reply);
		}

		//socket.close();
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