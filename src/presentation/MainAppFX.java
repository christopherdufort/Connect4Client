package presentation;

import static javafx.application.Application.launch;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Basic class for starting a JavaFX application
 */
@SuppressWarnings("unused")
public class MainAppFX extends Application 
{
    // The primary window or frame of this application
    private Stage primaryStage;
    GridPane gridPane;
    Label label;

    /**
     * Constructor
     */
    public MainAppFX() 
    {
        super();
    }

    /**
     * The application starts here
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
    	// The Stage comes from the framework so make a copy to use elsewhere
        this.primaryStage = primaryStage;
        
        // Create the Scene and put it on the Stage
        configureStage();

        // Set the window title
        primaryStage.setTitle("Connect Four");
        
        // Raise the curtain on the Stage
        primaryStage.show();
    }

    /**
     * Load the FXML and bundle, create a Scene and put the Scene on Stage.
     *
     * Using this approach allows you to use loader.getController() to get a
     * reference to the fxml's controller should you need to pass data to it.
     * Not used in this archetype.
     */
    private void configureStage() 
    {
        try 
        {
            // Instantiate the FXMLLoader
            FXMLLoader loader = new FXMLLoader();

            // Set the location of the fxml file in the FXMLLoader
            loader.setLocation(MainAppFX.class.getResource("Board.fxml"));

            // Parent is the base class for all nodes that have children in the
            // scene graph such as AnchorPane and most other containers
            Parent parent = loader.load();
            
            // Load the parent into a Scene
            Scene scene = new Scene(parent);

            ObservableList<Node> bp = (ObservableList<Node>) parent.getChildrenUnmodifiable();
            
            AnchorPane ap = (AnchorPane) bp.get(1);
            
            gridPane = (GridPane) ap.getChildren().get(1);
            label = (Label) ap.getChildren().get(2);
            
            label.setText("Wrong move bud");
            
            
            // Put the Scene on Stage
            primaryStage.setScene(scene);
            
            
            
            getNodeByRowColumnIndex();
            //TODO: This code maybe needed to place onClickListeners in the columns, how do we retrieve the column "nodes" though?
            //Source: http://stackoverflow.com/questions/24131618/javafx-how-to-create-a-clickable-area-around-node
            
            //TODO: 2. Get the position that was just changed and send it as a byte array into...something?
            //byte[] byteBuffer;
            //byteBuffer[0] = 11;
            //byteBuffer[1] = i; //row
            //byteBuffer[2] = c; //column

        } 
        
        catch (IOException ex) 
        { 
            System.out.println(ex);
            System.exit(1);
        }
    }
    
    //@SuppressWarnings("static-access")
	public void getNodeByRowColumnIndex() 
    {          	
    	//TODO: why children only has one node?
    	//TODO: should we even be doing children? is there a getCells? or maybe spam hboxes in every cell and change that?
        ObservableList<Node> childrens = gridPane.getChildren();
        System.out.println(childrens);
        
        for(Node node : childrens) 
        {
        	System.out.println("loop");
            //if(gridPane.getColumnIndex(node) != null) 
            {
            	System.out.println("system");
                node.setOnMouseClicked
        		(
    				new EventHandler<MouseEvent>()
    				{
    			        @Override
    			        public void handle(MouseEvent event) 
    			        {
    			        	//TODO: Event handling
    			        	checkColumnValid(node);
    			            node.setCursor(Cursor.HAND);
    			        }
    		    	}
        		);
            }
        }
    }
    
    public void checkColumnValid(Node node)
    {
    	//TODO: NO HARDCODING
        int c = 0;
        //TODO: fix this: get 'a' from appropriate place
        int[][] a = new int[6][7];
        //TODO: remove following line, only for testing purposes
        a[0][0] = 1;
        int i = validateSlottingPos(c, a);
        
        if(i != -1)
        {
        	node.setStyle("-fx-background-color: red");
        	//do logic to slot
        	//Get the row: i
        	//Get the column: c
        	//TODO: Set the red or yellow checker depending on who puts it in: Can we set an image to a gridPane node?
        	//Do this by retrieving the grid and setting background image to image of appropriate checker
        }
        
        else
        {
        	//TODO: Display Error?
        	
        }
    }
    
	//first array 'c' represents the columns on the game-board, and the one that was clicked on by the user
	//the second array 'a' represents the full game-board
	public int validateSlottingPos(int c, int[][] a)
	{
		for(int i = 0; i < 7; i++)
		{
			//the first empty spot in the column that it finds will 
			//be returned as the empty spot in which the chip will be slotted
			if(a[i][c] == 0)
			{
				return i;
			}
		}
		
		//if it does not find any empty slots in the entire column returns -1 because move is not valid.
		return -1;
	}
    
    
    /**
     * Where it all begins
     *
     * @param args command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
        System.exit(0);
    }
}
