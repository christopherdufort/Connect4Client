package controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ClientGameController {

	private Socket socket;
	private boolean clientsTurn;
	private int[][] gameBoard;
	private boolean gameStarted;

	private ObservableList<Node> children;
	private AnchorPane ap;
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
			gameBoard[column][emptyRow] = 1;
			
    		node.setStyle("-fx-background-image: url('" + y + "'); " +
    		           "-fx-background-position: center center; " +
    		           "-fx-background-repeat: stretch;");
    		
			clientsTurn = true;
		}
	}

	@FXML
	public void newGameClicked(ActionEvent event) throws IOException 
	{
		byte[] newGameRequest = new byte[] {MessageType.NEW_GAME.getCode(), 0, 0 };
		Network.sendMessage(socket, newGameRequest);
		resetGame();
	}

	@FXML
	public void quitClicked(ActionEvent event) throws IOException {
		/*
		 * TODO
		 * This should pop up a dialog box for the user to confirm
		 * before socket.close(); and Platform.exit();
		 * 
		 */
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("you have selected to Quit this game.");
		alert.setContentText("Are you sure you want to quit?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
		{
			// ... user chose OK
			System.out.println("Good Bye");
			byte[] quitRequest = new byte[] { MessageType.END_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, quitRequest);
			socket.close();
			Platform.exit();
		} else 
		{
		    // ... user chose CANCEL or closed the dialog
			//Do nothing.
		}
		
	
	}
	
	private void handleReply() throws IOException {
		
		byte[] reply = Network.receiveMessage(socket);
		String displayMessage;
		switch (MessageType.fromValue(reply[0])) {
		case NEW_GAME:
			gameStarted = true;
			break;
		case MOVE:
			System.out.println("handleReply() : This is a move");
			updateBoardDisplay(false, reply[1], reply[2]);
			break;
		case USER_WIN:
			displayMessage = "You have won the game!";
			System.out.println("handleReply() : User won");
			//Lock GUI
			displayEndDialog(displayMessage);
			//ask to play again
			break;
		case SERVER_WIN:
			displayMessage = "The server has won the game!";
			System.out.println("handleReply() : Server won");
			//Lock GUI
			displayEndDialog(displayMessage);
			//ask to play again
			break;
		case TIE:
			displayMessage = "The game has resulted in a Tie.";
			System.out.println("handleReply() : Tie");
			//Lock GUI
			displayEndDialog(displayMessage);
			//ask to play again
			break;
		default:
			System.out.println("handleReply() : reply defaulted?");
		}
	}

	private void displayEndDialog(String displayMessage) throws IOException {
		
		//TODO disable the board(only the grid leave the buttons)
		this.ap.setDisable(true);
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game has ended Dialog");
		alert.setHeaderText(displayMessage);
		alert.setContentText("Would you like to play again?");

		ButtonType buttonYes = new ButtonType("Yes");
		ButtonType buttonNo = new ButtonType("No");
		ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonYes)
		{
			//TODO RESET GUI AND GAME BOARD method (new empty array, loop through clearing images)
			resetGame();
			System.out.println("Restart the game");
			byte[] newGameRequest = new byte[] {MessageType.NEW_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, newGameRequest);
		}
		else if (result.get() == buttonNo) 
		{
			System.out.println("Good Bye");
			byte[] quitRequest = new byte[] { MessageType.END_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, quitRequest);
			socket.close();
			Platform.exit();
			
		} else {
		    //TODO: What should we do here?
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

	private void resetGame()
	{
		//TODO RESET GUI AND GAME BOARD method (new empty array, loop through clearing images)
		clientsTurn = true;
		gameBoard = new int[7][6];
		
		ap.setDisable(false);
		
		for (Node child : children) 
		{
			if (child.getClass().equals(HBox.class)) 
			{
				child.setStyle("");
			}
			
			else
			{
				break;
			}
		}
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

	public void setAnchorPane(AnchorPane ap) 
	{
		this.ap = ap;
	}

}