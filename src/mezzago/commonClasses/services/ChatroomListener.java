package mezzago.commonClasses.services;

import java.util.logging.Logger;

import mezzago.appClasses.Mezzago_Model;
import mezzago.ServiceLocator;

/**
 * Calls every 5 minutes all chat-rooms and according users and stores them
 * locally.
 * 
 * @author MathisHermann
 *
 */
public class ChatroomListener extends Thread {
	Mezzago_Model model;
	Logger logger = ServiceLocator.getServiceLocator().getLogger();

	public ChatroomListener(Mezzago_Model model) {
		super("ChatroomListener");
		this.model = model;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		while (true) {
			logger.info("Reading chatrooms and chatroom users");
			model.getAllUsers();

			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
			}
		}

	}

}
