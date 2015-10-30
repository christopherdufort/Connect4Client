package controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import business.MainAppFX;
import datacomm.MessageType;
import datacomm.Network;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ClientGameController {

	private Socket socket;
	private boolean clientsTurn;
	private int[][] gameBoard;
	private boolean gameStarted;

	private ObservableList<Node> children;
	private Label label;

	String y = MainAppFX.class.getResource("y.png").toExternalForm();
	String r = MainAppFX.class.getResource("r.png").toExternalForm();

	private final int SERVERPORT = 50000;

	@SuppressWarnings("unused")
	private String serverHost;

	public ClientGameController() 
	{
		super();
		clientsTurn = true;
		gameStarted = false;
		gameBoard = new int[7][6];
	}

	@FXML
	public void onBoardClicked(ActionEvent event) throws IOException 
	{

		String id = ((Node) event.getSource()).getId();
		int column = Integer.parseInt(id.substring(1, 2));
		System.out.println("Column clicked: " + column);

		//if the column the user clicked is full
		if (gameBoard[column][5] != 0) 
		{

			// Makes the error label visible
			label.setVisible(true);

			// Sets an asynchronous timer for 2000 milliseconds
			new Timer().schedule(new TimerTask() 
			{
				@Override
				public void run() 
				{
					fixLabel();
				}
			}, 2000);
		} 
		
		else 
		{

			int emptyRow = findEmptyPosition(column);

			updateBoardDisplay(clientsTurn, column, emptyRow);

			byte[] move = new byte[] {MessageType.MOVE.getCode(), (byte) column, (byte) emptyRow };

			Network.sendMessage(socket, move);
			handleReply();
		}
	}

	private int findEmptyPosition(int column) 
	{

		int result = -1;

		for (int row = 0; row < 6; row++) 
		{
			if (gameBoard[column][row] == 0) 
			{
				result = row;
				return result;
			}
		}
		return result;
	}

	private void updateBoardDisplay(boolean clientsTurn, int column, int emptyRow) 
	{
		String curId;
		String searchId;
		Node node = null;

		for (Node child : children) 
		{
			curId = child.getId();
			searchId = "_" + column + emptyRow;

			if (curId.equals(searchId)) 
			{
				node = child;
				break;
			}
		}

		System.out.println("COLUMN: " + column);
		System.out.println("ROW: " + emptyRow);
		
		if (clientsTurn) 
		{
			System.out.println("\nupdateBoardDisplay() : CLIENT's move.");
			gameBoard[column][emptyRow] = 2;
			
    		node.setStyle("-fx-background-image: url('" + r + "'); " +
	           "-fx-background-position: center center; " +
	           "-fx-background-repeat: stretch;");
 	
			
			clientsTurn = false;
		} 
		
		else 
		{
			System.out.println("\nupdateBoardDisplay() : SERVER's move.");
			System.out.println("\nAI move: " + column + ", " + emptyRow);
			
    		node.setStyle("-fx-background-image: url('" + y + "'); " +
    		           "-fx-background-position: center center; " +
    		           "-fx-background-repeat: stretch;");
			
			gameBoard[column][emptyRow] = 1;
			clientsTurn = true;
		}

		// TODO remove this after gui updates properly
		for (int i = 5; i > -1; i--) 
		{
			for (int j = 0; j < 7; j++) 
			{
				System.out.print(gameBoard[j][i] + " ");
			}
			
			System.out.println();
		}
	}

	@FXML
	public void newGameClicked(ActionEvent event) {

	}

	@FXML
	public void quitClicked(ActionEvent event) throws IOException {
		/*
		 * TODO
		 * This should pop up a dialog box for the user to confirm
		 * before socket.close(); and Platform.exit();
		 * 
		 */
		System.out.println("Good Bye");
		byte[] quitRequest = new byte[] { MessageType.END_GAME.getCode(), 0, 0 };
		Network.sendMessage(socket, quitRequest);
		socket.close();
		Platform.exit();
	}
	
	private void handleReply() throws IOException {
		
		byte[] reply = Network.receiveMessage(socket);
		switch (MessageType.fromValue(reply[0])) {
		case NEW_GAME:
			gameStarted = true;
			break;
		case MOVE:
			System.out.println("handleReply() : This is a move");
			updateBoardDisplay(false, reply[1], reply[2]);
			break;
		case USER_WIN:
			System.out.println("handleReply() : User won");
			break;
		case SERVER_WIN:
			System.out.println("handleReply() : Server won");
			break;
		case TIE:
			System.out.println("handleReply() : Tie");
			break;
		default:
			System.out.println("handleReply() : reply defaulted?");
		}
	}

	public boolean establishConnection(String serverHost)
	{
		try
		{
			this.serverHost = serverHost;
			this.socket = new Socket(serverHost, SERVERPORT);
			byte[] sessionRequest = new byte[] {MessageType.NEW_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, sessionRequest);
			handleReply();
			return true;
		}
		
		catch(IOException e)
		{
			return false;
		}
	}
	
	public boolean isStarted()
	{
		return gameStarted;
	}

	public void setLabel(Label label) 
	{
		this.label = label;
		this.label.setStyle("-fx-text-fill: red;");
	}

	public void setGridPaneChildren(GridPane gridPane) 
	{
		children = gridPane.getChildren();
	}

	/**
	 * Makes the error label disappear.
	 */
	private void fixLabel() 
	{
		label.setVisible(false);
	}

}