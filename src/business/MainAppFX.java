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
 * Basic class for starting a JavaFX application.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 */
public class MainAppFX extends Application 
{
    // The primary window or frame of this application
    private Stage primaryStage;
    private String serverHost;
    private TextInputDialog dialog;
    
    ClientGameController controller;
    

    /**
     * Constructor for the FX window.
     */
    public MainAppFX() 
    {
        super();
        serverHost = "";
    }
   
    public static void main(String[] args) 
    {
        launch(args);
        //implicit call to init()
        //implicit call to start(javafx.stage.Stage)
        System.exit(0);
    }
    
    /**
     * The application starts here
     *
     * @param primaryStage The Stage that represents the main Java FX pane.
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
            loader.setLocation(MainAppFX.class.getResource("Board.fxml")); 
            
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
