package datacomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Network class that sends and receives messages. This class is shared between
 * both the Client and Server Applications. The methods implement blocking IO to
 * create a synchronous dance of packets.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * @since JDK 1.8
 */
public class Network {

	/**
	 * Method that sends byte message across the connection. Custom message is
	 * created in the form of a byte array.
	 * 
	 * @param socket
	 *            Socket at which the message is being sent
	 * @param message
	 *            Byte array to be sent
	 * @throws IOException
	 *             Can be thrown by the network
	 */
	public static void sendMessage(Socket socket, byte[] message)
			throws IOException {
		OutputStream out = socket.getOutputStream();
		out.write(message);
	}

	/**
	 * Method that receives message across the connection. This method will
	 * block the application from continuing while waiting until the full packet
	 * is received.
	 * 
	 * @param socket
	 *            Socket at which the message is being received
	 * @return byte[] Byte array to be received
	 * @throws IOException
	 *             Can be thrown by the network
	 */
	public static byte[] receiveMessage(Socket socket)
			throws IOException {
		InputStream in = socket.getInputStream();
		// Receive the same string back from the server
		byte[] byteBuffer = new byte[3]; // Length of Packet = 3 bytes
		int totalBytesRcvd = 0; // Total bytes received so far
		int bytesRcvd; // Bytes received in last read

		while (totalBytesRcvd < byteBuffer.length) {
			if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
					byteBuffer.length - totalBytesRcvd)) == -1)
				throw new SocketException(
						"Connection close prematurely");
			totalBytesRcvd += bytesRcvd;
		}

		return byteBuffer;
	}
}
