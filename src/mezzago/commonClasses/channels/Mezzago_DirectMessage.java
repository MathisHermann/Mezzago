package mezzago.commonClasses.channels;

import java.io.Serializable;
import java.time.LocalDate;

import mezzago.appClasses.Mezzago_Model;

public class Mezzago_DirectMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1286743677073159992L;

	boolean isTesting;

	Mezzago_Model model;
	private String username;
	private boolean userOnline;
	private String localUser;
	private LocalDate lastInteractionDate;
	

	private boolean blocked;

	private String tempMessage = "";
	private String allMessages = "";
	private String lastMessage = "";

	public Mezzago_DirectMessage(String username, boolean isOnline, String localUser) {
		this.username = username;
		this.userOnline = isOnline;
		this.localUser = localUser;
		
		// Date of the last interaction with this channel. Is needed for cleanup.
		lastInteractionDate = LocalDate.now();
		
		blocked = false;

	}

	public LocalDate getLastInteractionDate() {
		return lastInteractionDate;
	}
	
	public boolean getBlocked() {
		return blocked;
	}
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public String getAllMessages() {
		return allMessages;
	}

	public String getLastMessage() {
		return lastMessage;
	}
	
	public void addMessage(String message) {
		this.lastMessage = message;
		allMessages += lastMessage;
		lastInteractionDate = LocalDate.now();
	}

	public String getUserName() {
		return username;
	}

	public boolean getOnline() {
		return userOnline;
	}

	public void setOnline(boolean userOnline) {
		this.userOnline = userOnline;
	}

	public void setTempMessage(String tempMessage) {
		lastInteractionDate = LocalDate.now();
		
		if (tempMessage.length() != 0)
			this.tempMessage = tempMessage;
	}

	public String getTempMessage() {
		lastInteractionDate = LocalDate.now();
		
		String m = tempMessage;
		tempMessage = "";
		return m;
	}

	public void clearTempMessage() {
		tempMessage = "";
	}

}
