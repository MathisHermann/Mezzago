package mezzago.commonClasses.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

import mezzago.ServiceLocator;
import mezzago.appClasses.Mezzago_Model;
import mezzago.commonClasses.socket.Mezzago_Socket;

public class Mezzago_MessagesIn extends Thread {

	Mezzago_Model model;
	Mezzago_Socket mezzago_socket;
	Socket socket;
	public static volatile boolean not = false;
	boolean alive = true;

	static String sessionToken;
	Logger logger = ServiceLocator.getServiceLocator().getLogger();

	public Mezzago_MessagesIn(Mezzago_Model model, Mezzago_Socket mezzago_socket) {
		super("MessageIn");

		this.model = model;
		this.mezzago_socket = mezzago_socket;

		this.setDaemon(true);
		this.start();
	}

	/**
	 * This Thread is listening for incoming messages which are initiated by he
	 * server. The message is split and then sent to the model.
	 */

	@Override
	public void run() {

		String message;
		BufferedReader in;
		
		socket = mezzago_socket.getSocket();

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			logger.info("dataIn ok");

			while (alive) {

				try { // receive the answer from client

					message = in.readLine();
					if (message != null) {
						if (message.length() > 0) {
						//	System.out.println("Received: \t" + message + " \t(messagesIn)");
							identifyMessage(message);
						}
					}

				} catch (IOException e) {

				}
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
				}
			}

			in.close();

		} catch (Exception e) {
			logger.info("Buffered Reader in not initialized.");
		}
	}

	private void identifyMessage(String message) {
		String[] parts = message.split("\\|", 4);

		/*
		 * For testing (shows all message parts) for (String s : parts) {
		 * System.out.println(s); }
		 */

		if (parts[0].equals("Result")) {
			Mezzago_Message msg = new Mezzago_ResultMessage(parts);
			model.messages.add(msg);
			model.newestMessage.set("" + msg.getID());
		} else if (parts[0].equals("MessageError")) {
			Mezzago_Message msg = new Mezzago_MessageError(parts);
			model.messages.add(msg);
			model.newestMessage.set("" + msg.getID());
		} else if (parts[0].equals("MessageText")) {
			Mezzago_Message msg = new Mezzago_MessageText(parts);
			model.messages.add(msg);
			model.newestMessage.set("" + msg.getID());
		}

	}

	

}