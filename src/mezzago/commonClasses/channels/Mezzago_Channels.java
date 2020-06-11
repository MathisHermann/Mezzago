package mezzago.commonClasses.channels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Mezzago_Channels implements Serializable {

	/**
	 * ID to export the channel
	 */
	private static final long serialVersionUID = -1279705079616683379L;
	private ArrayList<Mezzago_GroupChat> groupChats;
	private ArrayList<Mezzago_DirectMessage> directMessages;
	private ArrayList<String> allUsers;

	public Mezzago_Channels() {
		groupChats = new ArrayList<>();
		directMessages = new ArrayList<>();
		allUsers = new ArrayList<>();
	}

	public void createGroupChat(boolean isPublic, String chatName, String localUser, boolean localAdmin) {
		Mezzago_GroupChat tempChat = new Mezzago_GroupChat(isPublic, chatName, localUser, localAdmin);
		groupChats.add(tempChat);
	}

	public void createDirectMessage(String userName, boolean isOnline, String localUser) {
		//Mezzago_DirectMessage tempChat = ;
		directMessages.add(new Mezzago_DirectMessage(userName, isOnline, localUser));
	}

	public void setUser(String userName) {

	}

	public void removeUserFromGroupChat(String chatroomName, String username) {

	}

	public ArrayList<Mezzago_GroupChat> getGroupChats() {
		return groupChats;
	}

	public ArrayList<Mezzago_DirectMessage> getDirectMessages() {
		return directMessages;
	}

	public void setAddUsersToAllUsers(String usersList) {
		String[] users = usersList.split("\\|");
		for (String s : users) {
			if (!allUsers.contains(s))
				allUsers.add(s);
		}
		Collections.sort(allUsers);
	}

	public ArrayList<String> getAllUsers() {
		return allUsers;
	}

	/**
	 * Handle the list of members of a chat room and add them to it
	 * 
	 * @param usersList
	 * @param chatroomName
	 */
	public void setAddUserChatroom(String usersList, String chatroomName) {
		Mezzago_GroupChat gc = this.getSingleGroupChat(chatroomName);
		ArrayList<String> members = new ArrayList<>();

		if (gc != null)
			members = gc.getChatroomMembers();

		members.clear();

		String[] users = null;

		if (usersList != null)
			users = usersList.split("\\|");

		if (users != null)
			for (String s : users) {
				members.add(s);
				if (!allUsers.contains(s))
					allUsers.add(s);
			}

		Collections.sort(allUsers);
		Collections.sort(members);

		if (gc != null)
			gc.setChatroomMembers(members);
	}

	/**
	 * Get the group chat (chat room) profile with the chat room name <br>
	 * If the chat is not found, return is null
	 * 
	 * @param groupChat
	 * @return
	 */
	public Mezzago_GroupChat getSingleGroupChat(String groupChat) {

		boolean found = false;
		Mezzago_GroupChat gc = null;

		for (int counter = 0; counter < groupChats.size() && !found; counter++) {
			gc = groupChats.get(counter);
			if (gc.getName().equals(groupChat)) {
				found = true;
				return gc;
			}
		}

		return null;
	}

	/**
	 * Get the User profile with the username <br>
	 * If the chat is not found, return is null
	 * 
	 * @param userName
	 * @return
	 */
	public Mezzago_DirectMessage getSingleDirectMessage(String username) {
		boolean found = false;
		for (int i = 0; i < directMessages.size() && !found; i++) {
			Mezzago_DirectMessage dm = null;
			dm = directMessages.get(i);
			if (dm.getUserName().equals(username)) {
				found = true;
				return dm;
			}
		}

		return null;
	}

	/**
	 * Delete a group-chat or direct message channel from the application intern
	 * storage.
	 * 
	 * @param b    -> true if group-chat, false if direct message
	 * @param name -> name of the chat to be removed.
	 */
	public boolean remove(boolean b, String name) {
		boolean removed = false;
		if (b) {
			for (int i = 0; i < groupChats.size() && !removed; i++) {

				if (groupChats.get(i).getName().equals(name)) {
					// TODO: remove the directory entries
					groupChats.remove(i);
					removed = true;
				}

			}
		} else {
			for (int i = 0; i < directMessages.size() && !removed; i++) {
				if (directMessages.get(i).getUserName().equals(name)) {
					// TODO: remove the directory entries
					directMessages.remove(i);
					removed = true;
				}
			}
		}

		return removed;

	}

}
