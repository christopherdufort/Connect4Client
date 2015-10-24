/**
 * 
 */
package business;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import datacomm.Network;
import presentation.MainAppFX;

/**
 * @author Christopher, Elliot, Nader
 *
 */
public class C4App {

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) 
	{
		MainAppFX mafx = new MainAppFX();
		mafx.main(args);
	}
/*
 * When game start type in 3 digit starting with 0 to get into the game, then type 3 digit starting with 1 to keep playing.
 * Anything that is not something like 1** will quit the game.
 * After one game ends, type something like 9** will end the connectiong
 * 
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the IP address: ");
		Socket socket = new Socket(scan.nextLine(), 50000);
		byte[] message = new byte[3];
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
	}*/
}
