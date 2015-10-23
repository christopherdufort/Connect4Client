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
import java.util.Timer;
import java.util.TimerTask;

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
    
    int[][] board = new int[6][7];

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
    
	public void getNodeByRowColumnIndex() 
    {          	
        ObservableList<Node> childrens = gridPane.getChildren();
        
        for(Node node : childrens) 
        {
            node.setOnMouseClicked
    		(
				new EventHandler<MouseEvent>()
				{
			        @Override
			        public void handle(MouseEvent event) 
			        {
			        	try 
			        	{
							checkColumnValid(node);
						} 
			        	
			        	catch (InterruptedException e) 
			        	{
							e.printStackTrace();
						}
			        	
			            node.setCursor(Cursor.HAND);
			        }
		    	}
    		);
        }
    }
    
	@SuppressWarnings("static-access")
	public void checkColumnValid(Node node) throws InterruptedException
    {
    	int c;
    	
    	if(gridPane.getColumnIndex(node) == null)
    	{
    		c = 0;
    	}
    	
    	else
    	{
    		c = gridPane.getColumnIndex(node);
    	}
    	
    	System.out.println(c);
    	System.out.println(gridPane.getRowIndex(node));
    	
        //TODO: fix this: get 'a' from appropriate place

        int i = validateSlottingPos(c);
        
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
        	System.out.println("error");
        	
        	//TODO: Display Error?
        	label.setVisible(true);
        	label.setText("Wrong type move");
        	label.setStyle("-fx-text-color: red");
        	
        	Timer timer  = new Timer();
        	timer.scheduleAtFixedRate(new TimerTask()
        			{
        				public void run()
        				{
        					if(1000 < 0)
        					{
        						timer.cancel();
        					}
        				}
        			}, 0, 1000);
        	
        	fixLabel();
        }
    }
    
    
	public int validateSlottingPos(int c)
	{
		for(int i = 0; i < 7; i++)
		{
			if(board[i][c] == 0)
			{
				return i;
			}
		}
		
		return -1;
	}
	
    
    public void fixLabel()
    {
    	System.out.println("fix");
    	label.setVisible(false);
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
