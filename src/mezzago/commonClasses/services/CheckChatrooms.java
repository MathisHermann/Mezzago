package mezzago.commonClasses.services;

import mezzago.appClasses.Mezzago_Model;
import mezzago.ServiceLocator;

import java.util.logging.Logger;

/**
 * Calls on demand all chat-rooms and their users and stores them locally.
 * 
 * @author Mathis Hermann
 *
 */
public class CheckChatrooms extends Thread {
	Mezzago_Model model;
	Logger logger = ServiceLocator.getServiceLocator().getLogger();

	public CheckChatrooms(Mezzago_Model model) {
		super("ChatroomListener");
		this.model = model;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		logger.info("Reading chatrooms and chatroom users");
		model.getAllUsers();
	}

}
