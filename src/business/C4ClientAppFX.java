package business;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
 * C4ClientAppFX is the starting point for the Connect 4 Client application.
 * This class contain the main method which is the starting program.
 * As the client is GUI based this class creates and starts the GUI.
 * 
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * @version Java 1.8
 */
public class C4ClientAppFX extends Application 
{
    // The primary window or frame of this application
    private Stage primaryStage;
    private String serverHost;
    private TextInputDialog dialog;
    
    ClientGameController controller;
    
    /**
     * Constructor for the FX window.
     */
    public C4ClientAppFX() 
    {
        super();
        serverHost = "";
    }
   
    /**
     * The application starts here, this main method will launch the GUI component of this application.
     * 
	 * @param args
	 *            Command line arguments that may be accepted when running.
     */
    public static void main(String[] args) 
    {
        //implicit call to init() and start(javafx.stage.Stage)
        launch(args);
        System.exit(0);
    }
    
    /**
     * The start method is used to create and show the GUI component of this application.
     * 
     * @param primaryStage 
     * 			The Stage that represents the main Java FX pane.
     * @throws IOException 
     * @throws UnknownHostException 
     */
    @Override
    public void start(Stage primaryStage) throws UnknownHostException, IOException
    {    	
    	// The Stage comes from the framework so make a copy to use elsewhere
        this.primaryStage = primaryStage;
        // Create the Scene and put it on the Stage
        configureStage();
        // Set the window title
        primaryStage.setTitle("Connect Four");
        
        //Pop up a dialog box that requests the Server IP Address and will not continue until entered correctly or quit.
		Optional<String> result;
		do{
			result = dialog.showAndWait();
			serverHost = result.get();
			
		}while(!controller.establishConnection(serverHost));
		
		if(controller.isStarted())
			primaryStage.show();
    }

    /**
     * Load the FXML and bundle, create a Scene and put the Scene on Stage.
     * The Client Game controller associated with this class is instaciated alongside the creation of the GUI
     *
     * Using this approach allows you to use loader.getController() to get a
     * reference to the fxml's controller should you need to pass data to it.
     */
    private void configureStage() 
    {
        try 
        {
        	//Make a simple test dialog to ask for ip
    		dialog = new TextInputDialog("");
    		dialog.setTitle("Client Connect Four");
    		dialog.setHeaderText("Server IP Address required");
    		dialog.setContentText("Please Enter The Connect 4 Server IP Address");
        	
            // Instantiate the FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            
            // Set the location of the fxml file in the FXMLLoader
            loader.setLocation(C4ClientAppFX.class.getResource("Board.fxml")); 
            
            // Parent is the base class for all nodes that have children in the
            // scene graph such as AnchorPane and most other containers
            Parent parent = (BorderPane) loader.load();
            
            this.controller = (ClientGameController) loader.getController();
            
            // Load the parent into a Scene
            Scene scene = new Scene(parent);
                                   
            //Creates a list of nodes, each node is a child of the grid-pane, each node representing one of it's cells
            ObservableList<Node> bp = (ObservableList<Node>) parent.getChildrenUnmodifiable();
            
            //Traversing the nested panes to get to the grid-pane that represents the gameboard
            AnchorPane ap = (AnchorPane) bp.get(2);            
                  
            this.controller.setGridPaneChildren((GridPane) ap.getChildren().get(1));    
            
            this.controller.setAnchorPane(ap);
            
            //Gets the label that is positioned in the top left corner that will pop up to display an error when necessary.
            for(Node node : bp)
            {
            	if(node.getId() != null)
            	{
	            	if(node.getId().equals("label"))
	            	{
	            		node.setVisible(false);
	            		this.controller.setLabel((Label) node);
	            	}
            	}
            }            
            
            // Put the Scene on Stage
            primaryStage.setScene(scene);            
        } 
        
        catch (IOException ex) 
        { 
            System.out.println(ex);
            System.exit(1);
        }
    }
}
