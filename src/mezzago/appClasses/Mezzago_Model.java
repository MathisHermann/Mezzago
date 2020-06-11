package mezzago.appClasses;

import mezzago.commonClasses.socket.Mezzago_Socket;

import mezzago.commonClasses.message.Mezzago_MessagesOut;
import mezzago.commonClasses.message.Mezzago_ResultMessage;
import mezzago.commonClasses.message.Mezzago_Message;
import mezzago.commonClasses.message.Mezzago_MessageText;
import mezzago.commonClasses.message.Mezzago_MessagesIn;
import mezzago.commonClasses.message.Mezzago_ServerMessages;
import mezzago.commonClasses.Translator;
import mezzago.commonClasses.channels.Mezzago_Channels;
import mezzago.commonClasses.channels.Mezzago_DirectMessage;
import mezzago.commonClasses.channels.Mezzago_GroupChat;

import mezzago.commonClasses.services.PlaySound;
import mezzago.commonClasses.services.UserConfigHandler;
import mezzago.commonClasses.services.ObjectWriter;
import mezzago.commonClasses.services.BlockedListHandler;
import mezzago.commonClasses.services.ChannelCleanup;
import mezzago.commonClasses.services.ChatroomListener;
import mezzago.commonClasses.services.CheckChatrooms;
import mezzago.commonClasses.services.ObjectReader;

import mezzago.ServiceLocator;
import mezzago.abstractClasses.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class Mezzago_Model extends Model {

	ServiceLocator serviceLocator;
	Logger logger;

	/**
	 * If testing is true
	 */
	public boolean isTesting = false;

	Mezzago_Socket socket;
	private String serverAddress = "127.0.0.1"; // "javaprojects.ch"; //
	private int portNumber = 50001;

	Mezzago_MessagesOut messagesOut;
	Mezzago_MessagesIn messagesIn;
	Mezzago_ServerMessages serverMessages;
	Mezzago_Channels channels;

	boolean socketConnected;
	boolean pingSuccessful;
	boolean loggedIn;
	boolean stayLoggedIn;
	String localUser;
	String sessionToken;

	String notificationSound;
	boolean notificationSoundEnabled;

	String createChatRoom = "";
	boolean chatRoomCreated = false;

	String[] chatroomList;
	String[] chatroomUsersList;
	ArrayList<String> blockedChannels;
	String[] userConfig = new String[2];
	public ArrayList<Mezzago_Message> messages;
	public SimpleStringProperty newestMessage = new SimpleStringProperty();

	String currentChat;
	Mezzago_GroupChat currentGroupChat;
	Mezzago_DirectMessage currentDirectMessage;

	// the number of characters is restricted to 999
	int maxMessageLenght = 999;

	String[] validDomain = { "ch", "com", "org", "net" };

	String[] temp = { "steve", "andrea", "angie", "frodo", "sam" };
	String[] tempGroupChats = { "fish_007", "olive_oil_123", "eat_sushi", "lol_cart", "stay_hydrated" }; // Array of
																											// chatrooms
	String[] tempDirectMessages = { "one_wow", "lost_my_mind", "empirie", "doughnut_coffee", "lol" };

	public Mezzago_Model() {

		serviceLocator = ServiceLocator.getServiceLocator();
		logger = serviceLocator.getLogger();

		// this.loadSettings();

		sessionToken = "sdrgjbasdfgfaoube43oj3345j";
		localUser = "";

		blockedChannels = new ArrayList<>();
		loggedIn = stayLoggedIn;

		// Stores all incoming messages
		messages = new ArrayList<>();

		// Create the connection to the Server
		try {
			socket = new Mezzago_Socket(serverAddress, portNumber);
			socketConnected = socket.connect();

		} catch (Exception e) {
			socketConnected = false;
		}

		messagesIn = new Mezzago_MessagesIn(this, socket);
		serverMessages = new Mezzago_ServerMessages(socket);
		messagesOut = new Mezzago_MessagesOut(socket, serverMessages);

		// Ping the server. If the server is not available, it is not possible to log in

		if (socketConnected) {

			// allMessages = new ArrayList<>();

			if (!isTesting) {
				pingSuccessful = this.ping(false);
				if (pingSuccessful) {
					logger.info("Server Pinged");
				}
			}

		}

		logger.info("Application model initialized");
	}

	/**
	 * Set which chat is currently shown.
	 * 
	 * @param currentChat
	 * @param isGroupchat
	 */
	public void setCurrentChat(String currentChat, Mezzago_GroupChat gc, Mezzago_DirectMessage dm) {
		this.currentChat = currentChat;

		if (gc != null && dm == null) {
			currentGroupChat = gc;
			currentDirectMessage = null;
		} else if (gc == null && dm != null) {

			currentGroupChat = null;
			currentDirectMessage = dm;
		}
	}

	public boolean isTesting() {
		return isTesting;
	}

	public String getCurrentChat() {
		return currentChat;
	}

	public String getLocalUser() {
		return localUser;
	}

	public void setLocalUser(String username, String password) {
		localUser = username;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public int getMaxMessageLength() {
		return maxMessageLenght;
	}

	public String getNotificationSound() {
		return notificationSound;
	}

	public boolean isValidDomain(String domain) {
		for (String s : validDomain)
			if (s.equals(domain))
				return true;

		return false;
	}

	/**
	 * Add or remove a channel to or from the blocked list
	 * 
	 * @param name - name of the channel
	 */
	public void setBlockedChannel(String name) {

		boolean remove = false;
		boolean add = true;

		if (channels.getSingleGroupChat(name) != null) {

			if (channels.getSingleGroupChat(name).getBlocked()) {
				channels.getSingleGroupChat(name).setBlocked(false);
				remove = true;
			} else {
				channels.getSingleGroupChat(name).setBlocked(true);
				add = true;
			}

		}

		if (channels.getSingleDirectMessage(name) != null) {

			if (channels.getSingleDirectMessage(name).getBlocked()) {
				channels.getSingleDirectMessage(name).setBlocked(false);
				remove = true;
			} else {
				channels.getSingleDirectMessage(name).setBlocked(true);
				add = true;
			}
		}

		if (channels.getSingleGroupChat(name) == null && channels.getSingleDirectMessage(name) == null) {
			add = true;
		}

		if (blockedChannels.contains(name) && remove)
			blockedChannels.remove(name);
		else if (!blockedChannels.contains(name) && add)
			blockedChannels.add(name);

	}

	/**
	 * Remove a channel from the blocked list
	 * 
	 * @param name
	 */
	public void removeBlockedChannel(String name) {
		boolean found = false;
		for (int i = 0; i < blockedChannels.size() && !found; i++)
			if (blockedChannels.get(i).equals(name))
				blockedChannels.remove(i);

		channels.getSingleGroupChat(name).setBlocked(false);
	}

	/**
	 * Remove all chats from the blocked list
	 */
	public void clearBlockedChannel() {
		blockedChannels.clear();
	}

	/**
	 * Get the list of all blocked channels, not sorted (chat rooms and dm's are
	 * mixed)
	 * 
	 * @return ArrayList
	 */
	public ArrayList<String> getBlockedChannels() {
		return blockedChannels;
	}

	/**
	 * Get the information about the channel if it is blocked
	 * 
	 * @param channelName
	 * @return boolean - true if blocked
	 */
	public boolean getIsBlocked(String channelName) {
		boolean blocked = false;

		for (int i = 0; i < blockedChannels.size() && !blocked; i++) {
			if (blockedChannels.get(i).equals(channelName)) {
				blocked = true;
			}
		}

		return blocked;
	}

	/**
	 * check if the message is an incoming chat message. Identify which channel is
	 * relevant
	 * 
	 * @param id
	 * @return
	 */
	public String[] addNewMessage(String id) {

		boolean found = false;

		// pos 0: message; pos 1: dm/gc/new; pos 2 dm->target/gc->sender; pos 3:
		// gc->target
		String[] message = new String[4];

		// Get the message out of the newest messages List
		for (int i = 0; i < messages.size() && !found; i++) {
			Mezzago_Message m = messages.get(i);
			if (m.getType().equals("MessageText")) {

				// If the message is a messagetext add the content of the newest message to the
				// corresponding chat

				boolean playSound = false;

				String name = ((Mezzago_MessageText) m).getName();
				String target = ((Mezzago_MessageText) m).getTarget();
				String text = ((Mezzago_MessageText) m).getText().replace("&#124;", "|");
				message[0] = name + ": \n \t" + text + "\n \n";
				message[2] = name;

			//	System.out.println("Sender: " + name);
			//	System.out.println("Target: " + target);

				boolean gcFound = false;
				for (int j = 0; j < channels.getGroupChats().size() && !gcFound; j++) {
					if (channels.getGroupChats().get(j).getName().equals(target)) {
						gcFound = true;
					}
				}

				boolean isRemoteChatroom = false;
				if (chatroomList != null)
					for (String s : chatroomList)
						if (s.equals(target))
							isRemoteChatroom = true;

				// check if the user or chatroom is blocked
				boolean blocked = false;
				for (String s : blockedChannels)
					if (s.equals((gcFound ? target : name)))
						blocked = true;

				if (!blocked) {

					if (target.equals(localUser)) {

						if (channels.getSingleDirectMessage(name) != null) {

							channels.getSingleDirectMessage(name).addMessage(message[0]);
							message[1] = "dm";
							messages.remove(m);
						} else if (!isRemoteChatroom) {
							message[1] = "new";
							this.createDirectMessage(name, true);
							channels.getSingleDirectMessage(name).addMessage(message[0]);
							messages.remove(m);
						}

						playSound = true;

					} else {

						if (channels.getSingleGroupChat(target) != null) {

							if (!name.equals(localUser)) {
								channels.getSingleGroupChat(target).addMessage(message[0]);
								playSound = true;
							}

							message[1] = "gc";
							message[3] = target;
							messages.remove(m);
						} else {
							message[1] = "new";
							this.createGroupChannel(false, target, false);

							if (!name.equals(localUser))
								channels.getSingleGroupChat(target).addMessage(message[0]);

							messages.remove(m);
						}
					}

					if (notificationSoundEnabled && playSound)
						new PlaySound(notificationSound);

				} else {
					messages.remove(m);
					message[0] = null;
				}

				found = true;
			}

		}

		return message;

	}

	/**
	 * Save settings method (different settings can be stored)
	 * 
	 * @param option
	 * @param settings
	 */
	public void saveSettings(String option, String settings) {
		switch (option) {
		case "NotificationSound":
			notificationSound = settings;
			userConfig[1] = settings;
			break;
		case "NotificationSoundEnabled":
			notificationSoundEnabled = settings.equals("true");
			userConfig[0] = settings;
			break;
		case "Language":
			serviceLocator.getConfiguration().setLocalOption(option, settings);
			serviceLocator.setTranslator(new Translator(settings));
			break;
		}

	}

	/**
	 * Is called when the user is logged in
	 */
	private void loadSettings() {
		try {
			if (userConfig[1] != null) {
				notificationSoundEnabled = userConfig[0].equals("true");
				notificationSound = userConfig[1];
			} else {
				notificationSoundEnabled = true;
				notificationSound = "Swoosh";
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * All users possible from all public chat-rooms
	 */
	public void getAllUsers() {
		this.listChatrooms();
		if (chatroomList != null)
			for (String s : chatroomList) {
				this.listChatroomUsers(s);
			}
	}

	/**
	 * Needed to save the messaging information
	 * 
	 * @return
	 */
	public Mezzago_GroupChat getCurrentGroupChat() {
		return currentGroupChat;
	}

	/**
	 * Needed to save the messaging information
	 * 
	 * @return
	 */
	public Mezzago_DirectMessage getCurrentDirectMessage() {
		return currentDirectMessage;
	}

	/**
	 * Get the name of the current chat (group chat or direct message)
	 * 
	 * @return
	 */
	public String getCurrentChatName() {
		if (currentGroupChat != null)
			return currentGroupChat.getName();
		else if (currentDirectMessage != null)
			return currentDirectMessage.getUserName();
		else
			return "";
	}

	/**
	 * Create a new direct message object
	 * 
	 * @param username
	 * @param isOnline
	 * @return
	 */
	public boolean createDirectMessage(String username, boolean isOnline) {
		if (channels.getSingleDirectMessage(username) == null && username != localUser) {
			channels.createDirectMessage(username, isOnline, localUser);
			logger.info("Direct message connected: " + username);
			return true;
		}
		return false;
	}

	/**
	 * Create a new group chat object (is called manually by the user or if a
	 * message of a new channel is incoming)
	 * 
	 * @param isPublic
	 * @param chatName
	 * @param localAdmin
	 */
	public void createGroupChannel(boolean isPublic, String chatName, boolean localAdmin) {
		// Restrict the creation of a channel - cannot create if there is already a
		// channel with this name
		if (channels.getSingleGroupChat(chatName) == null)
			channels.createGroupChat(isPublic, chatName, localUser, localAdmin);

	}

	public boolean checkIsAdmin(String chatname) {
		return channels.getSingleGroupChat(chatname).getAdmin().equals(localUser);
	}

	/**
	 * If the user wants a different connection than the standard connection
	 * 
	 * @param serverAddress
	 * @param portNumber
	 */
	public boolean connectSocket(String serverAddress, int portNumber) {
		// socket.closeSocket();

		try {
			socket.setConnection(serverAddress, portNumber);
			socketConnected = socket.connect();

		} catch (Exception e) {
			socketConnected = false;
		}

		if (socketConnected) {
			this.serverAddress = serverAddress;
			this.portNumber = portNumber;

			messagesIn = new Mezzago_MessagesIn(this, socket);
			serverMessages = new Mezzago_ServerMessages(socket);
			messagesOut = new Mezzago_MessagesOut(socket, serverMessages);

			pingSuccessful = this.ping(false);
		}
		return socketConnected;
	}

	/**
	 * Close the socket connection
	 */
	public void closeSocket() {
		socket.closeSocket();
	}

	/*
	 * All possible interactions with the server
	 * -----------------------------------------------------------------------------
	 */

	/**
	 * Send the message and clear the temporary message in the according channel
	 * 
	 * @param message
	 * @param string
	 */
	public boolean sendMessage(String message) {
		boolean sent = false;

		// clean message
		String cleanMessage = message.replace("|", "&#124;");

		if (!isTesting && (currentDirectMessage != null || currentGroupChat != null)) {
			messagesOut.sendMessage(sessionToken, currentChat, cleanMessage);
			// messagesInThread.notifyAll();
		}

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}

		sent = this.checkMessage("Result", "SendMessage");

		if (sent) {

			if (currentDirectMessage != null)
				currentDirectMessage.addMessage(localUser + ":\n \t" + message + "\n \n");
			else if (currentGroupChat != null)
				currentGroupChat.addMessage(localUser + ":\n \t" + message + "\n \n");

			if (currentGroupChat != null) {
				currentGroupChat.clearTempMessage();
			} else if (currentDirectMessage != null) {
				currentDirectMessage.clearTempMessage();
				// currentDirectMessage.writeMessage(message, true); --> find #553
			}
		}

		return sent;
	}

	/**
	 * Create a login.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean createLogin(String username, String password) {

		boolean created = false;

		messagesOut.createLogin(username, password);

		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
		}

		created = this.checkMessage("Result", "CreateLogin");

		return created;
	}

	/**
	 * Login to the application and the server
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password) {

		boolean check = true;

		messagesOut.login(username, password);

		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
		}

		while (check) {
			for (int i = 0; i < messages.size() && check; i++) {
				Mezzago_Message m = messages.get(i);
				if (m.getType().equals("Result")) {
					if (((Mezzago_ResultMessage) m).getCommand().equals("Login")) {
						if (((Mezzago_ResultMessage) m).getResult()) {
							setLocalUser(username, password);
							sessionToken = ((Mezzago_ResultMessage) m).getContent();
							loggedIn = true;
						}
						messages.remove(m);
						check = false;
					}
				}
			}

		}

		// Initialize the channels - get the objects from the local stored files
		if (loggedIn) {
			File localChannelStorage = new File("channelStorage" + File.separator + localUser);
			localChannelStorage.mkdirs();

			// load settings of the local user
			blockedChannels = BlockedListHandler.readBlockedChannelsList(localUser);
			userConfig = UserConfigHandler.readUserConfig(localUser);

			this.loadSettings();
			new ChatroomListener(this);

			this.listChatrooms();

			channels = ObjectReader.readAllChannels(localUser);
			if (channels == null)
				channels = new Mezzago_Channels();

		}

		return loggedIn;

	}

	public boolean changePassword(String newPassword) {

		boolean passwordChanged = false;

		messagesOut.changePassword(sessionToken, newPassword);

		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}

		passwordChanged = this.checkMessage("Result", "ChangePassword");

		if (passwordChanged)
			setLocalUser(localUser, newPassword);
		// TODO User info, PW not changed

		return passwordChanged;

	}

	/**
	 * Remove the account from the server.
	 * 
	 * @return boolean (true if succeeded)
	 */
	public boolean deleteLogin() {

		messagesOut.deleteLogin(sessionToken);

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}

		loggedIn = !this.checkMessage("Result", "DeleteLogin");

		return !loggedIn;
	}

	/**
	 * Logout of the server and the application
	 * 
	 * @return boolean (true if succeeded and logged out)
	 */
	public boolean logout() {

		messagesOut.logout(sessionToken);

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
		}

		loggedIn = !this.checkMessage("Result", "Logout");

		UserConfigHandler.writeUserConfig(localUser, userConfig);

		// Update the blocked list
		this.writeBlockedList();

		// update the local storage of all channel information
		new ObjectWriter(channels, localUser);

		return !loggedIn;
	}

	/**
	 * Method to store the list of blocked channels locally - accesses the Service
	 * 'BlockedListHandler'
	 */
	private void writeBlockedList() {
		for (Mezzago_GroupChat gc : channels.getGroupChats())
			if (gc.getBlocked())
				if (!blockedChannels.contains(gc.getName()))
					blockedChannels.add(gc.getName());

		for (Mezzago_DirectMessage dm : channels.getDirectMessages())
			if (dm.getBlocked())
				if (!blockedChannels.contains(dm.getUserName()))
					blockedChannels.add(dm.getUserName());

		BlockedListHandler.writeBlockedChannelsList(localUser, blockedChannels);
	}

	/**
	 * Create a chat-room: (creator becomes admin). The server yet needs users to
	 * join. Chat-room can be public or hidden
	 * 
	 * @param chatroomName
	 * @param isPublic
	 * @return
	 */
	public boolean createChatroom(String chatRoomName, boolean isPublic) {
		boolean created = false;

		if (channels.getSingleGroupChat(chatRoomName) == null) {

			chatRoomCreated = false;
			createChatRoom = chatRoomName;

			messagesOut.createChatroom(sessionToken, chatRoomName, isPublic);

			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
			}

			created = this.checkMessage("Result", "CreateChatroom");

			if (created) {
				this.createGroupChannel(isPublic, chatRoomName, true);
				createChatRoom = "";
				new CheckChatrooms(this);
			}

		}
		return created;
	}

	/**
	 * Join a chat-room. If the chat-room is public everyone can join. If the
	 * chat-room is not public, only the admin can add members
	 * 
	 * @param chatroomName
	 * @param username     - name of the user to be added (only admin of a chat can
	 *                     add other members)
	 */
	public boolean joinChatroom(String chatroomName, String username) {
		boolean joined = false;

		messagesOut.joinChatroom(sessionToken, chatroomName, username);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		joined = this.checkMessage("Result", "JoinChatroom");

		if (joined)
			this.createGroupChannel(true, chatroomName, false);

		return joined;
	}

	/**
	 * Leave the chat-room: Local user can leave himself. Admin can kick anyone
	 * 
	 * @param chatroomName
	 * @param username
	 */
	public boolean leaveChatroom(String chatroomName, String username) {
		boolean leave = false;

		messagesOut.leaveChatroom(sessionToken, chatroomName, username);

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}

		leave = this.checkMessage("Result", "LeaveChatroom");

		channels.removeUserFromGroupChat(chatroomName, username);
		this.listChatroomUsers(chatroomName);

		return leave;
	}

	/**
	 * Leave the chat-room on the server. If successfull, all local information is
	 * removed
	 * 
	 * @param chatroomName
	 */
	public boolean leaveChatroom(String chatroomName) {
		boolean leave = false;

		messagesOut.leaveChatroom(sessionToken, chatroomName, localUser);

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}

		leave = this.checkMessage("Result", "LeaveChatroom");
		if (leave)
			channels.remove(true, chatroomName);

		// TODO: User information - not successfully left chatroom. try again
		return leave;
	}

	/**
	 * Check if the local user is the administrator of the chat-room.
	 * <p>
	 * If yes, the chat-room is deleted. If the deletion on the server is
	 * successful, the chat-room is locally deleted
	 * 
	 * @param name
	 */
	public boolean deleteChatroom(String name) {
		boolean deleted = false;

		messagesOut.deleteChatroom(sessionToken, name);

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}

		deleted = this.checkMessage("Result", "DeleteChatroom");

		channels.remove(true, name);

		return deleted;
	}

	/**
	 * List all chat-rooms available from the server. Enables to join the chat rooms
	 * The list is automatically imported.
	 */
	public void listChatrooms() {

		String list = "empty";

		messagesOut.listChatrooms(sessionToken);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		boolean check = true;

		while (check) {
			for (int i = 0; i < messages.size() && check; i++) {
				Mezzago_Message m = messages.get(i);
				if (m.getType().equals("Result")) {
					if (((Mezzago_ResultMessage) m).getCommand().equals("ListChatrooms")) {
						if (((Mezzago_ResultMessage) m).getResult()) {
							list = ((Mezzago_ResultMessage) m).getContent();
						}
						messages.remove(m);
						check = false;
					}
				}
			}
		}

		this.setChatroomList(list);

	}

	public void setChatroomList(String list) {
		if (list != null)
			chatroomList = list.split("\\|");
	}

	public String[] getChatroomList() {
		return chatroomList;
	}

	/**
	 * List all users of a specific chat-room. The list is automatically imported.
	 * 
	 * @param chatroomName
	 */
	public void listChatroomUsers(String chatroomName) {

		messagesOut.listChatroomUsers(sessionToken, chatroomName);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		String list = "empty";
		boolean check = true;

		try {
			while (check) {
				for (int i = 0; i < messages.size() && check; i++) {
					Mezzago_Message m = messages.get(i);
					if (m.getType().equals("Result")) {
						if (((Mezzago_ResultMessage) m).getCommand().equals("ListChatroomUsers")) {
							if (((Mezzago_ResultMessage) m).getResult()) {
								list = ((Mezzago_ResultMessage) m).getContent();
							}
							messages.remove(m);
							check = false;
						}
					}
				}
			}
		} catch (Exception e) {
		}

		this.setChatroomUsersList(list, chatroomName);

	}

	/**
	 * Send the list to channels, where it is split up in single users and stored in
	 * an Arraylist
	 */
	public void setChatroomUsersList(String usersList, String chatroomName) {
		channels.setAddUserChatroom(usersList, chatroomName);
	}

	/**
	 * Check if one (or more) user is online. If more users need to be checked,
	 * multiple requests need to be done. If a single user is online, the online
	 * status is changed in the corresponding direct message
	 * 
	 * @param username
	 * @param users
	 */
	public boolean checkUserOnline(String username) {
		boolean isOnline = false;

		if (username.length() > 0) {
			messagesOut.userOnline(sessionToken, username);
		}

		try {
			Thread.sleep(75);
		} catch (InterruptedException e) {
		}

		isOnline = this.checkMessage("Result", "UserOnline");

		return isOnline;

	}

	/**
	 * The server can be pinged:
	 * <p>
	 * With sessionToken: success if the token is valid. W/o sessionToken: always
	 * successful
	 * 
	 * @param withSessionToken
	 * @return
	 */
	public boolean ping(boolean withSessionToken) {

		boolean ping = false;

		if (withSessionToken) {
			messagesOut.ping(sessionToken);
		} else {
			messagesOut.ping();
			logger.info("Server pinged - " + serverAddress + ": " + portNumber);
		}

		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}

		ping = this.checkMessage("Result", "Ping");

		return ping;
	}

	/**
	 * Evaluate the message (answer of the server)
	 * 
	 * @param type
	 * @param command
	 * @return
	 */
	private boolean checkMessage(String type, String command) {
		boolean answerTrue = false;
		boolean check = true;

		while (check) {
			for (int i = 0; i < messages.size() && check; i++) {
				Mezzago_Message m = messages.get(i);
				if (m.getType().equals(type)) {
					if (((Mezzago_ResultMessage) m).getCommand().equals(command)) {
						if (((Mezzago_ResultMessage) m).getResult()) {
							answerTrue = true;
						}
						messages.remove(m);
						check = false;
					}
				}
			}
		}

		return answerTrue;
	}

}
