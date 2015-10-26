/**
 * 
 */
package business;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Scanner;

import datacomm.Network;
import javafx.scene.control.TextInputDialog;
import presentation.MainAppFX;

/**
 * Main start point for the client application.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 */

@SuppressWarnings("unused")
public class C4App 
{
	static String serverHost;
	/**
	 * Main starting method that calls the MainAppFX to start up the GUI window.
	 * 
	 * @param args
	 */
/*
	@SuppressWarnings("static-access")
	public static void main(String[] args) 
	{
		MainAppFX mafx = new MainAppFX();
		mafx.main(args);
	}
	
*/
	
	//TODO: Method only checks vertical wins for now but it does not work, behaving very badly, can be removed
	/**
	 * 
	 * 
	 * @param board The full array of the board
	 * @param r The row in which the latest move was played
	 * @param c The column in which the latest move was played
	 * @param player An integer representing if the move was played by the client or the server
	 * @return A boolean value if there is a win condition
	 */
	/*
	public boolean validateWin(int[][] board, int r, int c, int player)
	{
		int ctr = 1;
		
		System.out.println("\n\n\n\n\n");
		
		System.out.println("Starting loop: ");
		System.out.println("Row: " + r);
		System.out.println("Column: " + c);
		
		try
		{
			for(int i = 1; i < 5; i++)
			{
				if(board[r+i][c] == player)
				{
					System.out.println("To the right, we ++");
					ctr++;
					System.out.println("Counter: " + ctr);
				}
				
				System.out.println("halfway through check");
				
				if(board[r-i][c] == player)
				{
					System.out.println("To the right, we ++");
					ctr++;
					System.out.println("Counter: " + ctr);
				}
			}
			
			if(ctr >= 4)
			{
				System.out.println("winner winner chicken dinner");
				return true;
			}
		}
		
		catch(ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		return false;
	}
	
	/*
	/*
	 * When game start type in 3 digit starting with 0 to get into the game, then type 3 digit starting with 1 to keep playing.
	 * Anything that is not something like 1** will quit the game.
	 * After one game ends, type something like 9** will end the connectiong
	 * 
 	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the IP address: ");
		
		
		Socket socket = new Socket(scan.nextLine(), 50000);
		//Socket socket = new Socket(serverHost, 50000);
		
		byte[] message = new byte[3];
		
		//MainAppFX mafx = new MainAppFX();
		//mafx.main(args);
		
		String input;
		do{
			input = scan.nextLine();
			message[0] = Byte.parseByte(input.substring(0, 1));
			message[1] = Byte.parseByte(input.substring(1, 2));
			message[2] = Byte.parseByte(input.substring(2, 3));
			Network.sendMessage(socket, message);
			System.out.println("From server:" + Network.receiveMessage(socket)[0]);
		}while(message[0] != 9);
		scan.close();
		socket.close();
	}
	*/
}

