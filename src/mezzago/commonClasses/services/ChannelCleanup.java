package mezzago.commonClasses.services;

import mezzago.appClasses.Mezzago_Model;
import mezzago.commonClasses.channels.Mezzago_DirectMessage;
import mezzago.commonClasses.channels.Mezzago_GroupChat;
import mezzago.commonClasses.channels.Mezzago_Channels;
import mezzago.ServiceLocator;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This service is called every time after the user logged in. It checks for every chat, the last time it has been used.
 * If the duration is more than 5d the channel is deleted.
 * @author Mathis Hermann
 *
 */
public class ChannelCleanup extends Thread {

	Mezzago_Model model;
	Mezzago_Channels channels;
	Logger logger = ServiceLocator.getServiceLocator().getLogger();

	public ChannelCleanup(Mezzago_Model model, Mezzago_Channels channels) {
		super("ChannelCleanup");
		this.model = model;
		this.channels = channels;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		
		ArrayList<Mezzago_GroupChat> groupChats = channels.getGroupChats();
		ArrayList<Mezzago_DirectMessage> directMessages = channels.getDirectMessages();
		
			logger.info("Removing old channels");

			for (Mezzago_GroupChat gc : groupChats) {
				Period period = Period.between(LocalDate.now(), gc.getLastInteractionDate());
				int diff = period.getDays();
				if (diff < -5)
					channels.getGroupChats().remove(gc);
				
			}
			for (Mezzago_DirectMessage dm : directMessages) {
				Period period = Period.between(LocalDate.now(), dm.getLastInteractionDate());
				int diff = period.getDays();
				if (diff < -5)
					channels.getDirectMessages().remove(dm);
			}
			

	}

}
