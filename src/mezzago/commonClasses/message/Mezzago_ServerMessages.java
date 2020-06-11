package mezzago.commonClasses.message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

import mezzago.ServiceLocator;
import mezzago.commonClasses.socket.Mezzago_Socket;

public class Mezzago_ServerMessages {

	private Socket socket;
	private Mezzago_Socket mezzago_socket;
	private OutputStreamWriter dataOut;
	private Logger logger = ServiceLocator.getServiceLocator().getLogger();

	// Process the messages -> incoming messages go to messages in and returns go via message out to the model.

	public Mezzago_ServerMessages(Mezzago_Socket mezzago_socket) {
		this.mezzago_socket = mezzago_socket;
		
		this.initializeDataOut();
		
	}
	
	/**
	 * Start the output stream
	 */
	public void initializeDataOut() {
		
		socket = mezzago_socket.getSocket();
		
		try {
			dataOut = new OutputStreamWriter(socket.getOutputStream());
			logger.info("dataOut ok");
		} catch (IOException e) {
			logger.info("dataOut not initialized");
		}
	}




	public void closeDataOut() {
		try {
			dataOut.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * Send a message to the server
	 * <p>
	 * The message is assembled in the Class Mezzago_MessagesOut
	 * @param s - the message that is sent
	 */
	public void send(String s) {
		logger.info("Message sent");
		try {
			dataOut.write(s + "\n");
			dataOut.flush();
		//	System.out.println("Sent: \t\t" + s + "\t(serverMessages)");
		} catch (IOException e) {
			logger.info("Message not sent: " + e.toString());
		}
	}
}

