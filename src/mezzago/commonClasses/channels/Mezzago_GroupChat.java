package mezzago.commonClasses.channels;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Mezzago_GroupChat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7736214824001426077L;

	private String chatName;
	private ArrayList<String> members;
	private String adminUser;
	private String localUser;
	
	private boolean blocked;

	private boolean isPublic;
	private String tempMessage = "";
	
	private String allMessages = "";
	private String lastMessage = "";

	private LocalDate lastInteractionDate;


	public Mezzago_GroupChat(boolean isPublic, String chatName, String localUser, boolean localAdmin) {

		this.isPublic = isPublic;
		this.chatName = chatName;
		this.localUser = localUser;
		
		if (localAdmin)
			this.adminUser = localUser;
		else 
			this.adminUser = "";

		members = new ArrayList<>();
		
		// Date of the last interaction with this channel. Is needed for cleanup.
		lastInteractionDate = LocalDate.now();
		
		blocked = false;
	}

	public LocalDate getLastInteractionDate() {
		return lastInteractionDate;
	}
	
	public String getName() {
		return chatName;
	}

	public void setChatroomMembers(ArrayList<String> members) {
		this.members = members;
	}
	
	public ArrayList<String> getChatroomMembers() {
		return members;
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
	

	public void setUserName(String username) {
		members.add(username);
	}

	public String getAdmin() {
		return adminUser;
	}
	
	public boolean getBlocked() {
		return blocked;
	}
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * Remove a user from a groupChat
	 * 
	 * @param userName
	 * @param adminUser
	 */
	public void removeUser(String username, String adminUser) {
		boolean found = false;
		for (int i = 0; i < members.size() && !found; i++) {
			if (members.get(i) == username) {
				members.remove(i);
			}
		}

	}

	/*
	 * Handling for the temp. message - when the user changes the channel, the
	 * message is temporarily saved and when chosen again, the message is shown
	 */
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
	/*
	 * ------------------------------------------------------------------------
	 */

}
