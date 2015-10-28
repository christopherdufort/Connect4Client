package controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import business.MainAppFX;
import datacomm.Network;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class BoardController 
{

	private Socket socket;
	private boolean clientsTurn = true;
	private int[][] gameBoard = new int[6][7];
	
    private GridPane gridPane;
	private  ObservableList<Node> children;
    private Label label;

	String y = MainAppFX.class.getResource("y.png").toExternalForm();
	String r = MainAppFX.class.getResource("r.png").toExternalForm();
	
	private final int SERVERPORT = 50000;
	  
    @FXML
    private Label error;
	@SuppressWarnings("unused")
	private String serverHost;

    public BoardController()
    {	
    	super();
    }
    
    @FXML
    public void moveMade(ActionEvent event) throws IOException 
    {
    	
    	System.out.println(((Node) event.getSource()).getId());
    	String id = ((Node) event.getSource()).getId();
    	int column = Integer.parseInt(id.substring(1, 2));
    	
    	if (gameBoard[column][5] != 0)  //5 is actually 6 zero based counting.
    	{
        	//Makes the label visible
        	label.setVisible(true);
        	
        	//Sets an asynchronous timer for 2000 milliseconds
        	new Timer().schedule(new TimerTask()
        	{
        	    @Override
        	    public void run()
        	    {
        	    	fixLabel();
        	    }
        	}, 2000 );
    	}
    	
    	else
    	{
    		int emptyRow = findEmptyPosition(column);
    		updateBoardDisplay(clientsTurn, column, emptyRow);
    		byte[] move = new byte[] {2,(byte) column , (byte) emptyRow};
    		sendMessage(move);
    	}
    	
    	
    }
    

	private int findEmptyPosition(int column) 
	{
		int row = -1;
		
		for(int i = 0; i< gameBoard[column].length; i++)
		{
			if (gameBoard[column][i] == 0)
			{
				row = i;
				break;
			}
		}
		
		return row;
	}
	
    private void updateBoardDisplay(boolean clientsTurn, int column, int emptyRow) 
    {    	
       	String curId;
    	String searchId;
    	Node node = null;    	
    	
    	
    	for(Node child : children)
    	{
    		curId = child.getId();
    		searchId = "_" + column + emptyRow;
    		
    		if(curId.equals(searchId))
    		{
    			node = child;
    			break;
    		}
    	}
    	
    	if (clientsTurn)
    	{
    		gameBoard [column][emptyRow] = 2;
    		
        	node.setStyle("-fx-background-image: url('" + r + "'); " +
      	           "-fx-background-position: center center; " +
      	           "-fx-background-repeat: stretch;");
        	clientsTurn = false;
    	}
    	
    	else
    	{
    		gameBoard[column][emptyRow] = 1;
        	node.setStyle("-fx-background-image: url('" + y + "'); " +
      	           "-fx-background-position: center center; " +
      	           "-fx-background-repeat: stretch;");
        	clientsTurn = true;
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
    public void newGameClicked(ActionEvent event) 
	{

    }

    @FXML
    public void quitClicked(ActionEvent event) throws IOException 
    {
    	System.out.println("Good Bye");
    	byte[] quitRequest = new byte[]{9,0,0};
    	sendMessage(quitRequest);
    	//TODO close socket?
    	Platform.exit();
    }
    
	public void sendMessage(byte[] message) throws IOException 
	{		
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
			updateBoardDisplay(false, reply[1], reply[2]);
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

	public void establishConnection(String serverHost) throws IOException 
	{
		this.serverHost = serverHost;
		
		//FIXME THIS ONLY WORKS FOR A SINGLE MESSAGE WHY!!!?
		//FIXME WHERE DOES THIS GO??!?!?!
		this.socket = new Socket(serverHost, SERVERPORT);
		
		byte[] sessionRequest = new byte[]{0,0,0};
		
		this.sendMessage(sessionRequest);				
	}
	
    public void setLabel(Label label)
    {
    	this.label = label;
    }
    
    public void setGridPane(GridPane gridPane)
    {
    	this.gridPane = gridPane;
    	children =  gridPane.getChildren();
    }
    
    /**
     * Makes the error label disappear.
     */
    private void fixLabel()
    {
    	label.setVisible(false);
    }

}