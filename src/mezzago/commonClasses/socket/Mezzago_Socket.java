package mezzago.commonClasses.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;
import mezzago.ServiceLocator;

public class Mezzago_Socket {

	private boolean success = false;
	private String serverAddress; // "http://JavaProjects.ch/";
	private int portNumber; // 3306; // 50001;
	private Socket socket;
	private ServiceLocator sl = ServiceLocator.getServiceLocator();
	private Logger logger = sl.getLogger();

	protected OutputStreamWriter dataOut;
	protected BufferedReader dataIn;

	

	public Mezzago_Socket(String serverAddress, int portNumber) {
		this.serverAddress = serverAddress;
		this.portNumber = portNumber;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setConnection(String serverAddress, int portNumber) {
		this.serverAddress = serverAddress;
		this.portNumber = portNumber;
	}
	
	public boolean connect() {
		try {
			logger.info("Initialize Socket");
			InetSocketAddress address = new InetSocketAddress(serverAddress, portNumber);
			socket = new Socket();

			socket.connect(address, 3000);

			success = true;


			logger.info("Socket initialized");

		} catch (Exception e) {
			logger.info("Socket not initialized");
			success = false;
		}

		return success;

	}

	public boolean connectionSuccessful() {
		return success;
	}

	public void closeSocket() {
		try {
			if (socket.isConnected())
			socket.close();
		} catch (IOException e) {
		}
	}

}
