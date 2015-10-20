/**
 * 
 */
package datacomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Christopher , Elliot, Nader
 *
 */
public class Network {
	
	public static void sendMessage(Socket socket, byte[] message) throws IOException
	{
		OutputStream out = socket.getOutputStream();
		out.write(message);
	}
	public static byte[] receiveMessage(Socket socket) throws IOException
	{
		InputStream in = socket.getInputStream();
		// Receive the same string back from the server
		byte[] byteBuffer = new byte[3]; 			// Length of Packet = 3 bytes
	    int totalBytesRcvd = 0;						// Total bytes received so far
	    int bytesRcvd;								// Bytes received in last read
	    
	    while (totalBytesRcvd < byteBuffer.length)
	    {
	      if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
	                        byteBuffer.length - totalBytesRcvd)) == -1)
	        throw new SocketException("Connection close prematurely");
	      totalBytesRcvd += bytesRcvd;
	    }
	    
		return byteBuffer;
	}
}
