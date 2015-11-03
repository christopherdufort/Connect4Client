package business;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;
import controller.ClientGameController;

/**
 * This is the starting point for the client Connect 4 application. As the
 * starting point for a JavaFX application this class is responsible for
 * creating the GUI and displaying it. This class will create an instance of a
 * client game controller which will be used to handle GUI events. Client Gui is
 * made of a grid filled with clickable buttons and containers that will change
 * images as needed.
 * 
 * 
 * Resources and assets are currently kept in this package but should be
 * relocated to a resource folder before release.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * @since JDK 1.8
 */
public class C4ClientAppFX extends Application {
	// The primary window or frame of this application
	private Stage primaryStage;
	// Server IP Address
	private String serverHost;
	// Dialog to be displayed requesting info from user.
	private TextInputDialog dialog;

	// Controlling class responsible for handling Gui Events.
	ClientGameController controller;

	/**
	 * Constructor for the FX window.
	 */
	public C4ClientAppFX() {
		super();
		serverHost = "";
	}

	/**
	 * Main method
	 * 
	 * This method is the starting point for every Java application. As a part
	 * of a JavaFX application this method will launch the Gui creation methods.
	 * 
	 * @param args
	 *            List of arguments received from command line
	 */
	public static void main(String[] args) {
		// implicit call to init()
		// implicit call to start(javafx.stage.Stage)
		launch(args);
		System.exit(0);
	}

	/**
	 * The application GUI starts here. This method is called by main to create
	 * a GUI.
	 *
	 * @param primaryStage
	 *            The Stage that represents the main Java FX pane
	 * @throws UnknownHostException
	 *             Can be caused if an incorrect IP is given
	 */
	@Override
	public void start(Stage primaryStage)
			throws UnknownHostException {
		// The Stage comes from the framework
		// so make a copy to use elsewhere
		this.primaryStage = primaryStage;

		// Create the Scene and put it on the Stage
		configureStage();

		// Set the window title
		primaryStage.setTitle("Connect Four");

		// Display a dialog box requesting the server IP.
		// Do not launch the game until a valid
		// connection is established.
		Optional<String> result;
		do {
			result = dialog.showAndWait();
			if (result.isPresent()) {
				serverHost = result.get();
			}
		} while (!controller.establishConnection(serverHost));

		// Display Board
		primaryStage.show();
	}

	/**
	 * Load the FXML and bundle, create a Scene and put the Scene on Stage.
	 *
	 * ConfigureStage method is used to set up additional parameters for the
	 * GUI.
	 * 
	 * Using this approach allows you to use loader.getController() to get a
	 * reference to the fxml's controller should you need to pass data to it.
	 */
	private void configureStage() {
		try {
			// Make a simple test dialog to ask for IP
			dialog = new TextInputDialog("");
			dialog.setTitle("Client Connect Four");
			dialog.setHeaderText("Server IP Address required");
			dialog.setContentText(
					"Please Enter The Connect 4 Server IP Address");
			dialog.setOnCloseRequest(
					new EventHandler<DialogEvent>() {

						@Override
						public void handle(DialogEvent event) {
							String result = dialog.getResult();
							if (result == null) {
								System.exit(0);
							}
						}
					});

			// Instantiate the FXMLLoader
			FXMLLoader loader = new FXMLLoader();

			// Set the location of the FXML file in the FXMLLoader
			loader.setLocation(C4ClientAppFX.class
					.getResource("Board.fxml"));

			// Parent is the base class for all nodes
			// that have children in the
			// scene graph such as AnchorPane and most
			// other containers
			Parent parent = (BorderPane) loader.load();

			this.controller = (ClientGameController) loader
					.getController();

			// Load the parent into a Scene
			Scene scene = new Scene(parent);

			// Creates a list of nodes, each node is
			// a child of the grid-pane
			// each node representing one of it's cells
			ObservableList<Node> bp = (ObservableList<Node>) parent
					.getChildrenUnmodifiable();

			// Traversing the nested panes to get to the grid-pane that
			// represents the gameboard
			AnchorPane ap = (AnchorPane) bp.get(2);

			this.controller.setGridPaneChildren(
					(GridPane) ap.getChildren().get(1));

			this.controller.setAnchorPane(ap);

			// Gets the label that is positioned in the
			// top left corner that
			// will pop up to display an error when necessary.
			for (Node node : bp) {
				if (node.getId() != null) {
					if (node.getId().equals("label")) {
						// Once it finds the label node it
						// will make it not visible by default
						// and set it to the controller
						node.setVisible(false);
						this.controller.setLabel((Label) node);
					}
				}
			}

			// Put the Scene on Stage
			primaryStage.setScene(scene);
		}

		catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}
}
