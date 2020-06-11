package mezzago.commonClasses.message;

import mezzago.commonClasses.socket.Mezzago_Socket;

public class Mezzago_MessagesOut {

	private Mezzago_ServerMessages serverMessages;
	private Mezzago_Socket socket;

	public Mezzago_MessagesOut(Mezzago_Socket socket, Mezzago_ServerMessages serverMessages) {
		this.socket = socket;
		this.serverMessages = serverMessages;
	}

	/**
	 * Assemble the message to create a login sent out to the server.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public void createLogin(String username, String password) {
		String createLogin = MessageTypeOut.CREATELOGIN.messageType() + "|" + username + "|" + password;
		 serverMessages.send(createLogin);
		// return true;
	}

	/**
	 * Assemble the message to login.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public void login(String username, String password) {
		String login = MessageTypeOut.LOGIN.messageType() + "|" + username + "|" + password;
		serverMessages.send(login);
	}

	/**
	 * Assemble the message to logout from the server.
	 * 
	 * @param sessionToken
	 * @return
	 */
	public void logout(String sessionToken) {
		String logout = MessageTypeOut.LOGOUT.messageType() + "|" + sessionToken;
		serverMessages.send(logout);
	}

	/**
	 * Assemble the message to delete the local user
	 * 
	 * @param sessionToken
	 * @return
	 */
	public void deleteLogin(String sessionToken) {
		String deleteLogin = MessageTypeOut.DELETELOGIN.messageType() + "|" + sessionToken;
		serverMessages.send(deleteLogin);
	}

	/**
	 * Assemble the message to change the password of the local user.
	 * 
	 * @param sessionToken
	 * @param password
	 * @return
	 */
	public void changePassword(String sessionToken, String password) {
		String changePassword = MessageTypeOut.CHANGEPASSWORD.messageType() + "|" + sessionToken + "|" + password;
		serverMessages.send(changePassword);
	}

	/**
	 * Assemble the message to create a chat room.
	 * 
	 * @param sessionToken
	 * @param chatroomName
	 * @param isPublic
	 */
	public void createChatroom(String sessionToken, String chatroomName, boolean isPublic) {
		String createChatroom = MessageTypeOut.CREATECHATROOM.messageType() + "|" + sessionToken + "|" + chatroomName
				+ "|" + (isPublic ? "true" : "false");
		serverMessages.send(createChatroom);
	}

	/**
	 * Assemble the message to join a chat room.
	 * 
	 * @param sessionToken
	 * @param chatroomName
	 * @param username
	 */
	public void joinChatroom(String sessionToken, String chatroomName, String username) {
		String joinChatroom = MessageTypeOut.JOINCHATROOM.messageType() + "|" + sessionToken + "|" + chatroomName + "|"
				+ username;
		serverMessages.send(joinChatroom);
	}

	/**
	 * Assemble the message to leave a chat room.
	 * 
	 * @param sessionToken
	 * @param chatroomName
	 * @param username
	 */
	public void leaveChatroom(String sessionToken, String chatroomName, String username) {
		String leaveChatroom = MessageTypeOut.LEAVECHATROOM.messageType() + "|" + sessionToken + "|" + chatroomName
				+ "|" + username;
		serverMessages.send(leaveChatroom);
	}

	/**
	 * Assemble the message to delete a chat room.
	 * 
	 * @param sessionToken
	 * @param chatroomName
	 */
	public void deleteChatroom(String sessionToken, String chatroomName) {
		String deleteChatroom = MessageTypeOut.DELETECHATROOM.messageType() + "|" + sessionToken + "|" + chatroomName;
		serverMessages.send(deleteChatroom);
	}

	/**
	 * Assemble the message to list all chat rooms.
	 * 
	 * @param sessionToken
	 * @return
	 */
	public void listChatrooms(String sessionToken) {
		String listChatroom = MessageTypeOut.LISTCHATROOMS.messageType() + "|" + sessionToken;
		 serverMessages.send(listChatroom);
	}

	/**
	 * Assemble the message to ping the server with a token. If there is a user with
	 * this token, the server returns true.
	 * 
	 * @param sessionToken
	 * @return
	 */
	public void ping(String sessionToken) {
		String ping = MessageTypeOut.PING.messageType() + "|" + sessionToken;
		serverMessages.send(ping); // return true;
	}

	/**
	 * Assemble the message to ping without a token. The server always returns true.
	 * 
	 * @return
	 */
	public void ping() {
		String ping = MessageTypeOut.PING.messageType();
		serverMessages.send(ping);
	}

	/**
	 * Assemble the message to send a message.
	 * 
	 * @param sessionToken
	 * @param target
	 * @param messageOut
	 * @return
	 */
	public void sendMessage(String sessionToken, String target, String messageOut) {
		messageOut = MessageTypeOut.SENDMESSAGE.messageType() + "|" + sessionToken + "|" + target + "|" + messageOut;
		// The boolean can be used to evaluate if the wanted operation is successful
		serverMessages.send(messageOut);
	}

	/**
	 * Assemble the message to see if a user is online
	 * 
	 * @param sessionToken
	 * @param username
	 * @return
	 */
	public void userOnline(String sessionToken, String username) {
		String userOnline = MessageTypeOut.USERONLINE.messageType() + "|" + sessionToken + "|" + username;
		 serverMessages.send(userOnline);
	}

	/**
	 * Assemble the message to get all users of a chat room.
	 * 
	 * @param sessionToken
	 * @param chatroomName
	 * @return
	 */
	public void listChatroomUsers(String sessionToken, String chatroomName) {
		String listChatroomUsers = MessageTypeOut.LISTCHATROOMUSERS.messageType() + "|" + sessionToken + "|" + chatroomName;
		serverMessages.send(listChatroomUsers);
	}

}
