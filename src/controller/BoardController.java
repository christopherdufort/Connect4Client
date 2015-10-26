package controller;

import business.C4ClientSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BoardController {

	private byte[] playerMove = new byte[]{5,5};
	private boolean moveNotMadeYet;
	
    @FXML
    private Label error;

    public BoardController(){
    	super();
    	this.moveNotMadeYet = true;
    }
    @FXML
    public void moveMade(ActionEvent event) {
    	playerMove = new byte[]{4,5};
    	System.out.println("Player made a move: " + playerMove[0] + " " + playerMove[1]);
    	moveNotMadeYet = false;
    	
    }
    
    public byte[] getMove(){
    	System.out.println("InGetMove");
    	//do{
    	//	System.out.println("awaiting move");
    	//}while (moveNotMadeYet);
    	//moveNotMadeYet = true;
    	System.out.println("The move that i got was " + playerMove[0] + " " + playerMove[1]);
		return playerMove;
    }
    @FXML
    public void newGameClicked(ActionEvent event) {

    }

    @FXML
    public void quitClicked(ActionEvent event) {
    	System.out.println("Good Bye");
    	//FIXME remove this awful thing
    	C4ClientSession.endSession(true);
    	Platform.exit();
    }

}