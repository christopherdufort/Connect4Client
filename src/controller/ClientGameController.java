package controller;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import business.C4ClientAppFX;
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

/**
 * Controller class for GUI. This class maintains all of the methods used to
 * handle events within the GUI and update the state of the display. This class
 * will communicate between the network classes, and the GUI to send and receive
 * messages when appropriate.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * @since JDK 1.8
 */
public class ClientGameController {

	// Image that will set a yellow checker for server moves
	String y = C4ClientAppFX.class.getResource("yellowtoken.png")
			.toExternalForm();

	// Image that will set a red checker for client moves
	String r = C4ClientAppFX.class.getResource("redtoken.png")
			.toExternalForm();

	private Socket socket;
	private boolean clientsTurn;
	// Internal board representation used for light validation.
	private int[][] gameBoard;

	// List of nodes that represent the horizontal
	// boxes that are in every cell of the grid pane
	private ObservableList<Node> children;

	// Anchor pane that will be disabled when the game ends
	private AnchorPane ap;

	// Label that will be manipulated for error message
	private Label label;

	private final int SERVERPORT = 50000;

	@SuppressWarnings("unused")
	private String serverHost;

	/**
	 * Constructor instantiates a new board. First turn is always made by the
	 * client.
	 */
	public ClientGameController() {
		super();
		clientsTurn = true;
		gameBoard = new int[7][6];
	}

	/**
	 * Handles board clicked event. The source of the event is retrieved, and
	 * then it's id isolated. That id is formated in a pattern of: _X Where x is
	 * a number that represents the column number. If that column is full, a
	 * label with an asynchronous error message is displayed for the user.
	 * Otherwise the board is updated.
	 * 
	 * @param event
	 *            Event fired from board click
	 * @throws IOException
	 *             Exception thrown from network code
	 */
	@FXML
	public void onBoardClicked(ActionEvent event)
			throws IOException {
		// Seven buttons where placed on the grid, one over each column of cells

		// Retrieve the id of the node(button) that was clicked
		String id = ((Node) event.getSource()).getId();

		// Depending on the id of the button clicked, we can determine the
		// column that was targeted
		int column = Integer.parseInt(id.substring(1, 2));

		// if the column the user clicked is full
		if (gameBoard[column][5] != 0) {

			// Makes the error label visible
			label.setVisible(true);

			// Sets an asynchronous timer for 2000 milliseconds
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					fixLabel();
				}
			}, 2000);
		}

		// If the user clicked on a valid column
		else {
			int emptyRow = findEmptyPosition(column);

			// Updates the board display
			updateBoardDisplay(clientsTurn, column, emptyRow);

			byte[] move = new byte[] {
					MessageType.MOVE.getCode(), (byte) column,
					(byte) emptyRow };

			try {
				// Sends message to server to tell it about the move
				Network.sendMessage(socket, move);
				handleReply();
			}

			catch (SocketException | SocketTimeoutException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Connect Four Network Error");
				alert.setHeaderText("Connection Interupted");
				alert.setContentText(
						"A Network Error has occured, and the game is unable to continue, please contact your DataCommunication Administrator.");

				alert.showAndWait();
				socket.close();
				Platform.exit();
			}
		}
	}

	/**
	 * Handles the button click on New Game button. The FXML annotation binds
	 * this method to the button click. This will start a new game.
	 * 
	 * @param event
	 *            Event that was fired from the button click
	 * @throws IOException
	 *             That can be thrown due to network code
	 */
	@FXML
	public void newGameClicked(ActionEvent event)
			throws IOException {
		// End the game(if currently in play).
		byte[] newGameRequest = new byte[] {
				MessageType.END_GAME.getCode(), 0, 0 };
		Network.sendMessage(socket, newGameRequest);
		// Begin a new game.
		newGameRequest = new byte[] {
				MessageType.NEW_GAME.getCode(), 0, 0 };
		Network.sendMessage(socket, newGameRequest);
		handleReply();
	}

	/**
	 * Handles the button click on quit button. The FXML annotation binds this
	 * method to the button click. This will ask for confirmation from the user
	 * and if confirmed, the game will end.
	 * 
	 * @param event
	 *            Event that was fired from the button click
	 * @throws IOException
	 *             That can be thrown due to network code
	 */
	@FXML
	public void quitClicked(ActionEvent event)
			throws IOException {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(
				"You have selected to Quit this game.");
		alert.setContentText("Are you sure you want to quit?");

		Optional<ButtonType> result = alert.showAndWait();

		// User selects OK
		if (result.get() == ButtonType.OK) {
			// Send an end game message to the server.
			byte[] quitRequest = new byte[] {
					MessageType.END_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, quitRequest);
			// Send an end session message to the server.
			quitRequest = new byte[] {
					MessageType.END_SESSION.getCode(), 0, 0 };
			Network.sendMessage(socket, quitRequest);
			socket.close();
			Platform.exit();
		}

	}

	/**
	 * Method that establishes connection to the server.
	 * 
	 * @param serverHost
	 *            String serverHost
	 * @return boolean Shows if the connection has been successfully established
	 */
	public boolean establishConnection(String serverHost) {
		try {
			this.serverHost = serverHost;
			this.socket = new Socket(serverHost, SERVERPORT);
			byte[] sessionRequest = new byte[] {
					MessageType.NEW_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, sessionRequest);
			handleReply();
			return true;
		}

		catch (IOException e) {
			return false;
		}
	}

	/**
	 * Setter method that is called from the GUI creation class to set a handle
	 * to the label object that will be manipulated to display error messages.
	 * 
	 * @param label
	 *            Handle to a label object
	 */
	public void setLabel(Label label) {
		this.label = label;
		this.label.setStyle(
				"-fx-text-fill: red; -fx-font-size: 25pt;");
	}

	/**
	 * Setter method to get a handle on the gridPane object from the main GUI
	 * class. The children of the gridPane are the elements of the gameBoard and
	 * are retrieved for further manipulation.
	 * 
	 * @param gridPane
	 *            Handle to a GridPane object
	 */
	public void setGridPaneChildren(GridPane gridPane) {
		children = gridPane.getChildren();
	}

	/**
	 * Setter method for the anchor pane that holds the grid and main gameBoard
	 * GUI elements. Is a high-level parent element and will be used to freeze
	 * and de-freeze the gameBoard when user input wants to be focused somewhere
	 * else.
	 * 
	 * @param ap
	 *            Handle to an Anchor Pane object
	 */
	public void setAnchorPane(AnchorPane ap) {
		this.ap = ap;
	}

	/**
	 * Method that will determine if a column has any more empty rows left by
	 * searching the gameBoard array
	 * 
	 * @param column
	 *            to be checked for empty rows
	 * @return int either the first empty row in the column or a -1 if column is
	 *         full
	 */
	private int findEmptyPosition(int column) {

		int result = -1;

		for (int row = 0; row < 6; row++) {
			// Checks if row is empty
			if (gameBoard[column][row] == 0) {
				result = row;
				return result;
			}
		}
		return result;
	}

	/**
	 * Method that updates the board display
	 * 
	 * @param clientsTurn
	 *            A boolean that specifies whether it is the client's turn or
	 *            the server's
	 * @param column
	 *            Column that the move was played on
	 * @param emptyRow
	 *            Row that the move was played on
	 */
	private void updateBoardDisplay(boolean clientsTurn,
			int column, int emptyRow) {
		// We iterate through the list of horizontal boxes, we have one for
		// every cell in the grid pane
		// Each is given a unique id that is formated in a way of: _XY where X
		// is it's column value and Y it's row value
		// This permits us to determine which horizontal box corresponds to the
		// move made and update it's display

		String curId;

		// String constructed in the aforementioned method pattern of: _XY
		String searchId = "_" + column + emptyRow;
		Node node = null;

		for (Node child : children) {
			// Get the id of the current node we are iterating through
			curId = child.getId();

			// If the id of the node and the one we had built(searchId) where
			// equal, then we know which horizontal box to update
			if (curId.equals(searchId)) {
				// If it is found we save the node and break out of our loop.
				node = child;
				break;
			}
		}

		// Does logic that corresponds to a client move
		if (clientsTurn) {
			// Updates gameBoard
			gameBoard[column][emptyRow] = 2;

			// Sets the background image of the horizontal box to one that
			// corresponds to a user move
			// The background image is set through the style of the node
			node.setStyle(
					"-fx-background-image: url('" + r + "'); "
							+ "-fx-background-position: center center; "
							+ "-fx-background-repeat: stretch;");

			clientsTurn = false;
		}

		// Does logic that corresponds to a server move
		else {
			// Updates gameBoard
			gameBoard[column][emptyRow] = 1;

			// Sets the background image of the horizontal box to one that
			// corresponds to a server move
			// The background image is set through the style of the node
			node.setStyle(
					"-fx-background-image: url('" + y + "'); "
							+ "-fx-background-position: center center; "
							+ "-fx-background-repeat: stretch;");

			clientsTurn = true;
		}
	}

	/**
	 * Method that handles reply messages from server. Each packet has a
	 * starting byte made up of a unique Message Type represented as an enum.
	 * Each case of the message type results in a different handling of the
	 * message.
	 * 
	 * @throws IOException
	 *             Exception can be caused from network code
	 */
	private void handleReply() throws IOException {

		byte[] reply = Network.receiveMessage(socket);
		String displayMessage;
		switch (MessageType.fromValue(reply[0])) {
		case NEW_GAME:
			resetGame();
			System.out.println("New game started");
			break;
		case MOVE:
			updateBoardDisplay(false, reply[1], reply[2]);
			break;
		case USER_WIN:
			displayMessage = "You have won the game!";
			// Lock GUI
			displayEndDialog(displayMessage);
			// ask to play again
			break;
		case SERVER_WIN:
			updateBoardDisplay(false, reply[1], reply[2]);
			displayMessage = "The server has won the game!";
			// Lock GUI
			displayEndDialog(displayMessage);
			// ask to play again
			break;
		case TIE:
			updateBoardDisplay(false, reply[1], reply[2]);
			displayMessage = "The game has resulted in a Tie.";
			// Lock GUI
			displayEndDialog(displayMessage);
			// ask to play again
			break;
		default:
			System.out
					.println("handleReply() : reply defaulted?");
		}
	}

	/**
	 * Displays a dialogue box to end the game
	 * 
	 * @param displayMessage
	 *            Appropriate message displayed to the user
	 * @throws IOException
	 *             Exception can be caused from network code
	 */
	private void displayEndDialog(String displayMessage)
			throws IOException {

		// Disables the board (only the grid)
		this.ap.setDisable(true);

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game has ended Dialog");
		alert.setHeaderText(displayMessage);
		alert.setContentText("Would you like to play again?");

		ButtonType buttonYes = new ButtonType("Yes");
		ButtonType buttonNo = new ButtonType("No");
		ButtonType buttonCancel = new ButtonType("Cancel",
				ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonYes, buttonNo,
				buttonCancel);

		Optional<ButtonType> result = alert.showAndWait();

		// Reconsider end of game
		if (result.get() == buttonYes) {
			byte[] newGameRequest = new byte[] {
					MessageType.NEW_GAME.getCode(), 0, 0 };
			Network.sendMessage(socket, newGameRequest);
			handleReply();
		}

		// Confirm end of game
		else if (result.get() == buttonNo) {
			byte[] quitRequest = new byte[] {
					MessageType.END_SESSION.getCode(), 0, 0 };
			Network.sendMessage(socket, quitRequest);
			socket.close();
			Platform.exit();

		}

	}

	/**
	 * Method that resets the gameBoard and resets the elements of the GUI back
	 * to their default state.
	 */
	private void resetGame() {
		clientsTurn = true;
		gameBoard = new int[7][6];

		ap.setDisable(false);

		for (Node child : children) {
			if (child.getClass().equals(HBox.class)) {
				child.setStyle("");
			}

			else {
				break;
			}
		}
	}

	/**
	 * Makes the error label not visible. Is called so annoying error message
	 * does not linger for too long in GUI.
	 */
	private void fixLabel() {
		label.setVisible(false);
	}

}