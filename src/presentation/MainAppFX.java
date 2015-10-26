package presentation;

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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import business.C4App;
import business.C4ClientSession;

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
    private C4ClientSession clientSession;
    
    GridPane gridPane;
    Label label;
    
    //The game board
    int[][] board = new int[6][7];

    /**
     * Constructor for the FX window.
     * TODO is this needed?
     */
    public MainAppFX() 
    {
        super();
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
		
		clientSession = new C4ClientSession(serverHost);
		
		if (clientSession.establishSession())
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
		
		clientSession.beginSession();
		
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
            loader.setLocation(MainAppFX.class.getResource("Board.fxml"));

            // Parent is the base class for all nodes that have children in the
            // scene graph such as AnchorPane and most other containers
            Parent parent = loader.load();
            
            // Load the parent into a Scene
            Scene scene = new Scene(parent);

            /*
            //Creates a list of nodes, each node is a child of the grid-pane, each node representing one of it's cells
            ObservableList<Node> bp = (ObservableList<Node>) parent.getChildrenUnmodifiable();
            
            //Traversing the nested panes to get to the grid-pane that represents the gameboard
            AnchorPane ap = (AnchorPane) bp.get(1);   
            
            //Gets the label that is positioned in the top left corner that will pop up to display an error when necessary.
            label = (Label) bp.get(1);
            
            gridPane = (GridPane) ap.getChildren().get(1);
            
           
            
            //Set to false by default
            label.setVisible(false);
            
            */
            
            // Put the Scene on Stage
            primaryStage.setScene(scene);
            
            

           // getNodeByRowColumnIndex();
            
            //TODO: Get the position that was just changed and send it as a byte array
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
    
    /**
     * Traverses an arrayList of all the nodes that are the grid-pane's cells
     * and attaches an onMouseClickListener.
     */
    /*
	public void getNodeByRowColumnIndex() 
    {          	
        ObservableList<Node> childrens = gridPane.getChildren();
        
        //Traverses the arrayList of nodes and sets a proper clickable cursor on all grid cells
        for(Node node : childrens)
        {
    		node.setCursor(Cursor.HAND);        	
        }
        
        for(Node node : childrens) 
        {
            node.setOnMouseClicked
    		(
				new EventHandler<MouseEvent>()
				{
			        @Override
			        public void handle(MouseEvent event) 
			        {
		        		//Calls method that checks the validity of a user click
						checkColumnValid(node);
			        }
		    	}
    		);
        }
    }
    */
    
	/**
	 * Method that checks the grid that is clicked.
	 * If the grid has already been clicked, it will display an error message.
	 * If the grid has not been clicked it will set the chip in that grid and update the array.
	 * 
	 * @param node The node that should be checked
	 */
    /*
	@SuppressWarnings("static-access")
	public void checkColumnValid(Node node) 
    {
    	int c;
    	int r;
    	
    	//Retrieves the column index of the node in the grid
    	if(gridPane.getColumnIndex(node) == null)
    	{
    		c = 0;
    	}
    	
    	else
    	{
    		c = gridPane.getColumnIndex(node);
    	}
    	
    	//Retrieves the row index of the node in the grid
    	if(gridPane.getRowIndex(node) == null)
    	{
    		r = 0;
    	}
    	
    	else
    	{
    		r = gridPane.getRowIndex(node);
    	}
    	
    	//Checks if a position is taken yet or not
    	//i will contain a 1 if grid is free, or -1 if it is not
        int i = validateSlottingPos(c, r);
        
        //If position is not taken, place a chip in the appropriate grid on the GUI
        if(i == 1)
        {
        	//TODO: Check for who is playing the move and change the code appropriately

        	//Places number representing the client or server in the array of the game board 
        	board[r][c] = 1;
        	
        	//Sets the image on the GUI
        	String image = MainAppFX.class.getResource("y.png").toExternalForm();
        	node.setStyle("-fx-background-image: url('" + image + "'); " +
        	           "-fx-background-position: center center; " +
        	           "-fx-background-repeat: stretch;");
        	
        	
        	//TODO: Strictly to test win logic, eventually remove
        	C4App app = new C4App();
        	
        	//TODO: Should not hard-code player(fourth parameter)
        	if(app.validateWin(board, r, c, 1))
        	{
        		label.setText("Someone won");
        	}
        }
        
        //If position is taken, then display an error message in the top left corner for 2 seconds
        else
        {
        	//Makes the label visible
        	label.setVisible(true);
        	
        	//Sets an asynchronous timer for 200 milliseconds
        	new Timer().schedule(new TimerTask()
        	{
        	    @Override
        	    public void run()
        	    {
        	    	fixLabel();
        	    }
        	}, 2000 );
        	
        }
    }
    */
    /**
     * Checks if a position on the grid has already been taken.
     * 
     * @param c Column of the node to be validated
     * @param r Row of the node to be validated
     * @return An integer that is 1 if the grid is free or -1 if it is not
     */
	public int validateSlottingPos(int c, int r)
	{
		if(board[r][c] == 0)
		{
			return 1;
		}
		
		return -1;
	}
	
    /**
     * Makes the error label disappear.
     */
	/*
    public void fixLabel()
    {
    	label.setVisible(false);
    }
    */
    /**
     * Where it all begins
     *
     * @param args command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
        //implicit call to init()
        //implicit call to start(javafx.stage.Stage)
        System.exit(0);
    }
}
