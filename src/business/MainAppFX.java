package business;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import controller.BoardController;

/**
 * Basic class for starting a JavaFX application.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * TODO this class should be moved?
 */
public class MainAppFX extends Application 
{
    // The primary window or frame of this application
    private Stage primaryStage;
    private String serverHost;
    
    private BoardController bc;
    
    //Label label; TODO make me disapear nader do your magic thang
    

    /**
     * Constructor for the FX window.
     * TODO is this needed?
     */
    public MainAppFX() 
    {
        super();
        //BoardController bc = new BoardController(); constructor never called
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
    	
		//Make a simple test dialog to ask for ip
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Client Connect Four");
		dialog.setHeaderText("Server IP Address required");
		dialog.setContentText("Please Enter The Connect 4 Server IP Address");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			serverHost = result.get();
		}
		
    	// The Stage comes from the framework so make a copy to use elsewhere
        this.primaryStage = primaryStage;
        
        // Create the Scene and put it on the Stage
        configureStage();

        // Set the window title
        primaryStage.setTitle("Connect Four");
        
        // Raise the curtain on the Stage
        primaryStage.show();
        
        BoardController bc = new BoardController();  
	    bc.establishConnection(serverHost);
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
        	
            // Instantiate the FXMLLoader
            FXMLLoader loader = new FXMLLoader();

            // Set the location of the fxml file in the FXMLLoader
           //FIXME PATH WONT WORK!!!!
           // loader.setLocation(MainAppFX.class.getResource("/fxml/Board.fxml"));
            loader.setLocation(MainAppFX.class.getResource("Board.fxml"));
            
            // Parent is the base class for all nodes that have children in the
            // scene graph such as AnchorPane and most other containers
            Parent parent = (BorderPane) loader.load();
            
            // Load the parent into a Scene
            Scene scene = new Scene(parent);
                 
            // Put the Scene on Stage
            primaryStage.setScene(scene);
            
        } 
        
        catch (IOException ex) 
        { 
            System.out.println(ex);
            System.exit(1);
        }
    }
    
   
    public static void main(String[] args) 
    {
        launch(args);
        //implicit call to init()
        //implicit call to start(javafx.stage.Stage)
        System.exit(0);
    }
}
