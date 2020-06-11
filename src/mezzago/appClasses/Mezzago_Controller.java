package mezzago.appClasses;

import mezzago.ServiceLocator;
import mezzago.abstractClasses.Controller;
import mezzago.commonClasses.channels.Mezzago_DirectMessage;
import mezzago.commonClasses.channels.Mezzago_GroupChat;
import mezzago.commonClasses.services.ChannelCleanup;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class Mezzago_Controller extends Controller<Mezzago_Model, Mezzago_View> {
	ServiceLocator serviceLocator;

	private boolean isTesting;
	boolean portNumberGood = false;
	boolean serverAddressGood = false;
	private boolean createNotSet = true;
	protected String currentChat;
	private String chatroomName = "";

	private Logger logger = ServiceLocator.getServiceLocator().getLogger();

	public Mezzago_Controller(Mezzago_Model model, Mezzago_View view) {
		super(model, view);

		isTesting = model.isTesting;

		// Login form

		/*
		 * when the first ping in the model is not successfull (standard 127.0.0.1,
		 * 50001) a new connection can be made
		 */
		if (!model.pingSuccessful) {
			view.addBtnChangeConnection();
			view.setHintLabel("fail");
		}

		view.inPassword.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER) && !view.login.isDisabled() && model.pingSuccessful) {
				this.login();
			}
		});

		view.inUserName.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER) && !view.login.isDisabled() && model.pingSuccessful) {
				this.login();
			}
		});

		view.login.setOnAction(login -> {
			if (model.pingSuccessful || isTesting)
				this.login();
		});

		view.inUserName.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() < 3 || view.inPassword.getText().length() < 3 || newValue.contains("|")) {
					view.login.setDisable(true);
					view.createLogin.setDisable(true);
				} else {
					view.login.setDisable(false);
					view.createLogin.setDisable(false);
				}
			}
		});

		view.inPassword.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() < 3 || view.inUserName.getText().length() < 3 || newValue.contains("|")) {
					view.login.setDisable(true);
					view.createLogin.setDisable(true);
				} else {
					view.login.setDisable(false);
					view.createLogin.setDisable(false);
				}
			}
		});

		view.createLogin.setOnAction(createNewLogin -> {
			if (!isTesting) {
				createLogin();
			} else {
				view.setChatScene("TestingUser");
			}
		});

		view.mezzagoLink.setOnAction(goToMezzzzzzago -> {
			openMezzagoInBrowser();
		});

		view.btnChangeConnection.setOnAction(newConnection -> {
			view.showConnectionWindow();
		});

		view.inAddress.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				String[] partsOfWebAdd = newValue.split("\\.");
				int lengthOfParts = partsOfWebAdd.length;
				boolean check = true;

				if (lengthOfParts == 4) {
					for (int i = 0; i < partsOfWebAdd.length && check; i++) {
						try {
							int part = Integer.parseInt(partsOfWebAdd[i]);
							if (part < 0 || part > 255) {
								check = false;
								serverAddressGood = false;
							}

						} catch (Exception e) {
							serverAddressGood = false;
							check = false;
						}
					}
				}

				if (lengthOfParts == 4 && check) {
					serverAddressGood = true;
				} else if (lengthOfParts > 1 && model.isValidDomain(partsOfWebAdd[lengthOfParts - 1])) {
					serverAddressGood = true;
				} else
					serverAddressGood = false;

				if (serverAddressGood && portNumberGood)
					view.disableConnectionButton(false);
				else
					view.disableConnectionButton(true);
			}
		});

		view.inPort.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int portNumber = 0;

				try {
					portNumber = Integer.parseInt(newValue);
				} catch (Exception e) {
					portNumber = 0;
				}

				if (portNumber < 1024 || portNumber > 65535) {
					portNumberGood = false;
				} else {
					portNumberGood = true;
				}

				if (portNumberGood && serverAddressGood)
					view.disableConnectionButton(false);
				else
					view.disableConnectionButton(true);

			}
		});

		view.inPort.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER) && !view.btnConnect.isDisabled()) {
				this.newConnection();
			} else if (enter.getCode().equals(KeyCode.ESCAPE)) {
				view.hideConnectionWindow();
			}
		});

		view.inAddress.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ESCAPE)) {
				view.hideConnectionWindow();
			}
		});

		view.btnConnect.setOnAction(connect -> {
			this.newConnection();
		});

		// When in the actual application
		view.btnSend.setOnAction(send -> {
			this.handleMessage();
		});

		view.textIn.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER) && !view.btnSend.isDisabled()) {
				this.handleMessage();
			}
		});

		view.btnLogout.setOnAction(logout -> {
			this.logout();
		});

		// Settings

		view.btnExitAccountSettings.setOnAction(exit -> {
			view.inNewPassword.clear();
			view.hideSettingsWindow();
		});

		view.btnSaveAccountSettings.setOnAction(save -> {
			String newPassword = view.inNewPassword.getText();
			view.inNewPassword.clear();
			if (!isTesting && !newPassword.contains("|") && newPassword.length() > 3)
				model.changePassword(newPassword);
			// TODO user notification
		});

		view.saveGeneralSettings.setOnAction(saveSettings -> {
			String soundSettings = view.soundChoice.getValue();
			String languageSettings = view.languageChoice.getValue();
			if (soundSettings != null)
				model.saveSettings("NotificationSound", soundSettings.replace(" ", "_"));

			if (languageSettings != null) {
				model.saveSettings("Language", languageSettings);
				view.updateTexts();
			}

			model.saveSettings("NotificationSoundEnabled", (view.disableAudio.isSelected() ? "false" : "true"));

		});

		view.exitGeneralSettings.setOnAction(exitSettings -> {
			view.hideSettingsWindow();
		});

		view.btnRemoveFromBlockedList.setOnAction(removeChannelFromBlockedList -> {
			String channel = view.getBlockedChannel();
			if (channel != null) {
				model.removeBlockedChannel(channel);
				view.addChannelsToBlockedComboBox(model.getBlockedChannels());
			}
		});

		view.btnRemoveAll.setOnAction(removeAllFromBlockedList -> {
			model.clearBlockedChannel();
			view.addChannelsToBlockedComboBox(model.getBlockedChannels());
		});

		view.btnSubmitInBlockName.setOnAction(submit -> {
			String name = view.inBlockName.getText();
			if (name.length() > 3) {
				model.setBlockedChannel(name);
				view.inBlockName.clear();
			}

		});

		view.btnExitBlockSettings.setOnAction(exitBlockedList -> {
			view.hideSettingsWindow();
		});

		// delete the local account
		view.btnDeleteAccount.setOnAction(deleteAccount -> {

			view.alertDeleteAccount.showAndWait();

			if (view.alertDeleteAccount.getResult() == ButtonType.YES) {
				if (!isTesting && model.deleteLogin())
					view.hideSettingsWindow();
				view.setLoginScene();
			}
		});

		// Add a listener to the menu-bar
		view.menuLogout.setOnAction(logout -> {
			this.logout();
		});

		view.menuSettings.setOnAction(settings -> {
			view.addChannelsToBlockedComboBox(model.getBlockedChannels());
			view.showSettingsWindow();
		});

		// Restrict the number of characters for the message to type in. The number is
		// defined in the model as "maxMessageLenght"
		view.textIn.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int maxMessageLenght = model.getMaxMessageLength();
				view.lblCounter.setText("(" + newValue.length() + "/" + maxMessageLenght + ")");
				if (newValue.length() <= maxMessageLenght) {
					view.btnSend.setDisable(false);
				} else {
					view.btnSend.setDisable(true);
				}
			}
		});

		// Buttons to create a new direct message connection or chat room
		// direct message
		view.newDmConnection.setOnAction(spezi -> {
			// model.getAllUsers();
			view.addUsersToAllUsers(model.channels.getAllUsers());
			view.showJoinWindow("connectDm");
		});

		view.btnAddDm.setOnAction(addDM -> {
			this.connectChannel("dm");
		});

		view.addDmIn.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER))
				this.connectChannel("dm");
			else if (enter.getCode().equals(KeyCode.ESCAPE))
				view.hideJoinWindow();

		});

		// Join existing chatroom
		view.joinChatroom.setOnAction(spezii -> {
			model.listChatrooms();

			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
			}

			view.makeChatroomListChoice();
			view.showJoinWindow("joinGc");
		});

		view.btnJoinGc.setOnAction(addDM -> {
			this.connectChannel("gc");
		});

		view.chatroomListChoice.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER))
				this.connectChannel("gc");
			else if (enter.getCode().equals(KeyCode.ESCAPE))
				view.hideJoinWindow();
		});

		view.joinGcIn.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER)) {
				this.connectChannel("gc");
			} else if (enter.getCode().equals(KeyCode.ESCAPE)) {
				view.hideJoinWindow();
			}
		});

		// Add a new chat room and join it
		view.createChatroom.setOnAction(spezii -> {
			view.createChatroom.getText();
			view.showGcWindow();
		});

		view.btnAddGc.setOnAction(addGC -> {
			this.addGC("new");
		});

		view.addGcIn.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER)) {
				this.addGC("new");
			} else if (enter.getCode().equals(KeyCode.ESCAPE)) {
				view.hideGcWindow();
			}
		});

		// When the user clicks on an item in the drop-down, the
		// add-dm-connection-window is opened
		view.allUsersFromChatRoom.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					view.showJoinWindow("connectDm");
					view.addDmIn.setText(newValue);
					Platform.runLater(() -> view.addDmIn.positionCaret(newValue.length()));
				}
			}
		});

		// add users to a chat room
		view.btnAddUser.setOnAction(addUser -> {
			this.addUserToChatroom();
		});

		view.addUserIn.setOnKeyPressed(enter -> {
			if (enter.getCode().equals(KeyCode.ENTER))
				this.addUserToChatroom();
			else if (enter.getCode().equals(KeyCode.ESCAPE))
				view.hideAddUserWindow();
		});

		// remove a user from a chat-room
		view.btnRemoveUser.setOnAction(removeUser -> {
			String username = view.removeUserList.getValue();
			if (username != null)
				model.leaveChatroom(chatroomName, username);
			model.listChatroomUsers(chatroomName);
			view.addUsersToRemoveUserList(model.channels.getSingleGroupChat(chatroomName).getChatroomMembers());
			view.setAllUsersFromChatRoom(model.channels.getSingleGroupChat(chatroomName).getChatroomMembers());
		});

		view.removeUserList.setOnKeyPressed(exit -> {
			if (exit.getCode().equals(KeyCode.ESCAPE))
				view.hideRemoveUserWindow();
		});
		// register ourselves to handle window-closing event
		view.getStage().setOnCloseRequest(closeWindow -> {

			if (model.loggedIn) {
				this.logout();
			}

			model.closeSocket();
			Platform.runLater(() -> {
				view.hideSettingsWindow();
				Platform.exit();
			});

		});

		/**
		 * Listen to new messages that are incoming (especially server initiated)
		 */
		model.newestMessage.addListener((o, oldValue, newValue) -> {
			logger.info("New message: " + newValue);
		
			if (newValue != null) {
				String[] message = model.addNewMessage(newValue);

				// If the message is not null, there is a relevant incoming message.
				if (message[0] != null) {
					switch (message[1]) {
					case "dm":


						// set the styling of the button to indicate new message
						if (!model.getCurrentChatName().equals(message[2]))
							view.setNewMessage(message[2]);

						if (!message[1].equals("new") && model.getCurrentChatName().equals(message[2]))
							view.textArea.appendText(message[0]);

						break;

					case "gc":

						// set the styling of the button to indicate new message
						if (!model.getCurrentChatName().equals(message[3]))
							view.setNewMessage(message[3]);

						if (!message[1].equals("new") && model.getCurrentChatName().equals(message[3])
								&& !model.getLocalUser().equals(message[2]))
							view.textArea.appendText(message[0]);

						break;

					default:
						Platform.runLater(() -> {
							view.getChannels("all");
							this.attachListenerDirectMessageButtons();
							this.attachListenerGroupChatButtons();
						});
						break;
					}

				}
			}
		});

		// logger info
		logger.info("Application controller initialized");

	}

	private void openMezzagoInBrowser() {
		String url = "https://mezzago.ch";

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (Exception e) {
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Add a new user to a chat-room (possible as chat-room admin)
	 */
	private void addUserToChatroom() {
		String usernameListChoice = view.addUserList.getValue();
		String usernameIn = view.addUserIn.getText();
		String userJoin = "";
		boolean userOnline = false;

		if (usernameIn.length() >= 3) {
			userOnline = model.checkUserOnline(usernameIn);
			userJoin = usernameIn;
		} else if (usernameListChoice != null) {
			userOnline = model.checkUserOnline(usernameListChoice);
			userJoin = usernameListChoice;
		}

		if (userOnline && userJoin != null) {
			model.joinChatroom(chatroomName, userJoin);
			view.addUserIn.clear();
		}
	}

	private void newConnection() {
		String serverAddress = view.inAddress.getText();
		int portNumber = Integer.parseInt(view.inPort.getText());

		boolean connected = model.connectSocket(serverAddress, portNumber);

		if (model.pingSuccessful) {
			view.hideConnectionWindow();
			view.removeBtnChangeConnection();
			view.setHintLabel("success");
		}

		if (connected)
			view.setLabelConnectionInfo("");
		else
			view.setLabelConnectionInfo("failed");
	}

	/**
	 * Load settings and set the according settings visually
	 */
	private void loadSettings() {
		view.disableAudio.setSelected(!model.notificationSoundEnabled);
		view.soundChoice.setValue(model.getNotificationSound());
	}

	private void addGC(String status) {
		if (status.equals("new")) {
			String chatName = view.addGcIn.getText();
			if (chatName.length() >= 3 && chatName.length() < 25 && !chatName.contains("|")) { // TODO check symbols and
																								// add tooltip
				// when the check-box is selected, the chat-room is hidden
				if (model.createChatroom(chatName, !view.gcHidden.isSelected())) {
					if (model.joinChatroom(chatName, model.getLocalUser())) {
						view.addGcIn.clear();
						view.getChannels("gc");
						view.addGcWindow.close();
						this.attachListenerGroupChatButtons();
						this.attachListenerDirectMessageButtons();
					}
				}
			}
		} else if (status.equals("join")) {
			view.addGcWindow.close();
			view.addGcJoinWindow.close();

			Platform.runLater(() -> {
				view.getChannels("all");
				this.attachListenerGroupChatButtons();
				this.attachListenerDirectMessageButtons();
			});
		}
	}

	private void connectChannel(String chatType) {

		if (chatType.equals("dm")) {
			String name = view.addDmIn.getText();
			if (name.length() < 1)
				name = view.allUsers.getValue();

			boolean isOnline = false;
			if (name != null && !model.getLocalUser().equals(name))
				isOnline = model.checkUserOnline(name);
			if (isOnline) {
				if (model.createDirectMessage(name, isOnline)) {
					view.addDmIn.clear();
					view.getChannels("dm");
					view.addDmWindow.close();
					this.attachListenerGroupChatButtons();
					this.attachListenerDirectMessageButtons();
				}
			}
			// Connect to an existing chat-room
		} else if (chatType.equals("gc")) {
			String name = view.chatroomListChoice.getValue();
			if (name != null) {
				model.joinChatroom(name, model.getLocalUser());
				this.addGC("join");
			}
		}
	}

	/**
	 * Messages entered in the main text field. Either messages sent to a channel or
	 * messages to create a new connection (new chat room or dm) see comment below
	 */
	private void handleMessage() {
		String message = view.textIn.getText().trim();
		if (message.length() > 0) {
			// message += "\n";

			/*
			 * Catching the error that can occur if the user wrote a wrong creation
			 * statement
			 * 
			 * 
			 * Cuz users sometimes are not that intelligent...
			 */
			try {

				/*
				 * If the user id too lazy to click on the create button, he or she can simply
				 * type e.g. "#room:hubbabubba:hidden" in the main text field.
				 * 
				 * After pressing enter, a hidden chat room called "hubbabubba" is created and
				 * automatically listed.
				 */
				String[] parts = message.split(":");

			//	System.out.println(message.charAt(0) == '#');
			
				if (message.charAt(0) == '#' && parts[1].length() >= 3) {

					parts[0] = parts[0].replace("#", "");

		
					switch (parts[0]) {
					case "room":

						boolean hidden = true;

						if (parts[2] != null)
							hidden = !parts[2].equals(parts[2]);

						if (model.createChatroom(parts[1], hidden)) {
							view.getChannels("gc");

							this.attachListenerGroupChatButtons();
							this.attachListenerDirectMessageButtons();
						}

						break;

					case "dm":
						model.createDirectMessage(parts[1], false);
						break;
					}

				} else if (model.sendMessage(message)) {
					String appendMessage = "";

					if (model.getCurrentDirectMessage() != null)
						appendMessage = model.getCurrentDirectMessage().getLastMessage();
					else if (model.getCurrentGroupChat() != null)
						appendMessage = model.getCurrentGroupChat().getLastMessage();

					view.textArea.appendText(appendMessage);
				}

			} catch (Exception e) {
			}

		}
		view.refreshView();
		view.scrollDown();
	}

	/**
	 * When the login is not successful, the user can create a new account
	 */
	private void createLogin() {
		String username = view.inUserName.getText();
		String password = view.inPassword.getText();
		model.createLogin(username, password);
	}

	/**
	 * Login handling in controller
	 */
	private void login() {
		if (!isTesting) {
			String username = view.inUserName.getText();

			if (model.login(username, view.inPassword.getText())) {

				new ChannelCleanup(model, model.channels);

				// set all selected values
				this.loadSettings();

				view.hideConnectionWindow();
				view.removeBtnChangeConnection();

				view.inUserName.clear();
				view.inPassword.clear();

				view.setChatScene(model.getLocalUser());

				// Attach listeners to the buttons
				this.attachListenerDirectMessageButtons();
				this.attachListenerGroupChatButtons();

				createNotSet = true;

			} else if (createNotSet) {

				view.setHintLabel("wrong");

				createNotSet = false;
			}

		} else {
			view.setChatScene("Test User");
		}
	}

	/**
	 * Logout handling in controller
	 */
	private void logout() {
		if (model.logout() || isTesting) {
			view.hideSettingsWindow();
			view.setLoginScene();
		}
	}

	/**
	 * Event Listeners for buttons (direct message)
	 */
	private void attachListenerDirectMessageButtons() {
		for (Button b : view.directMessageButtons) {
			b.setOnAction(selectDirectMessage -> {
				this.selectDirectMessage(b, "");
			});

			b.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

				@Override
				public void handle(ContextMenuEvent event) {

					view.channelContextMenu.show(b, event.getScreenX(), event.getScreenY());

					view.updateContextMenuTexts(model.channels.getSingleDirectMessage(b.getText()).getBlocked(), true,
							false);

					view.deleteChannel.setOnAction(deleteChannel -> {

						model.channels.remove(false, b.getText());

						Platform.runLater(() -> {
							view.getChannels("all");

							String nextChannel = null;

							if (model.channels.getDirectMessages().size() > 0)
								nextChannel = model.channels.getDirectMessages().get(0).getUserName();
							else if (model.channels.getGroupChats().size() > 0)
								nextChannel = model.channels.getGroupChats().get(0).getName();

							Mezzago_Controller.this.selectGroupchat(null, nextChannel);

							Mezzago_Controller.this.attachListenerDirectMessageButtons();
							Mezzago_Controller.this.attachListenerGroupChatButtons();
						});
					});

					view.blockChannel.setOnAction(blockChannel -> {
						model.setBlockedChannel(b.getText());
						boolean bla = model.channels.getSingleDirectMessage(b.getText()).getBlocked();
						Platform.runLater(() -> {
							view.updateContextMenuTexts(bla, true, false);
						});
					});
				}

			});
		}
	}

	/**
	 * Handling when the user clicks on a direct message on the left side
	 * 
	 * @param b button if the user clicks on a button to choose a certain dm channel
	 * @param s string to let the program easily open a dm channel
	 */
	private void selectDirectMessage(Button b, String s) {

		String username = "";

		if (b != null)
			username = b.getText();
		else if (s != null)
			username = s;
		else
			username = "empty";

		if (!view.lblChannel.getText().equals(username)) {

			Mezzago_DirectMessage dm = model.channels.getSingleDirectMessage(username);
			if (dm != null) {

				view.removeNewMessage(dm.getUserName());

				if (currentChat != null) {

					if (model.channels.getSingleDirectMessage(currentChat) != null)
						model.channels.getSingleDirectMessage(currentChat).setTempMessage(view.textIn.getText());
					else if (model.channels.getSingleGroupChat(currentChat) != null)
						model.channels.getSingleGroupChat(currentChat).setTempMessage(view.textIn.getText());

					view.textIn.clear();
				}

				dm.setOnline(model.checkUserOnline(dm.getUserName()));
				view.textArea.clear();
				currentChat = username;
				model.setCurrentChat(currentChat, null, dm);
				view.setChannel(dm.getUserName(), dm.getOnline(), false);
				// insert the chat history in the text area
				view.textArea.appendText(dm.getAllMessages());
				// if there is a tepmorary mesage it is called
				view.textIn.setText(dm.getTempMessage());
				// TODO does not work...

				view.scrollDown();

			}
		}

	}

	/**
	 * Event Listeners for buttons (group chat)
	 */
	private void attachListenerGroupChatButtons() {
		for (Button b : view.groupChatButtons) {
			b.setOnAction(selectGroupChat -> {
				this.selectGroupchat(b, "");
			});

			b.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

				@Override
				public void handle(ContextMenuEvent event) {
					view.channelContextMenu.show(b, event.getScreenX(), event.getScreenY());

					chatroomName = b.getText();

					view.updateContextMenuTexts(model.channels.getSingleGroupChat(b.getText()).getBlocked(), false,
							model.checkIsAdmin(chatroomName));
					view.addUsersToRemoveUserList(model.channels.getSingleGroupChat(chatroomName).getChatroomMembers());

					view.leaveChannel.setOnAction(leaveChannel -> {

						if (model.leaveChatroom(chatroomName)) {
							Platform.runLater(() -> {

								String nextChannel = null;

								if (model.channels.getGroupChats().size() > 0)
									nextChannel = model.channels.getGroupChats().get(0).getName();
								else if (model.channels.getDirectMessages().size() > 0)
									nextChannel = model.channels.getDirectMessages().get(0).getUserName();

								Mezzago_Controller.this.selectGroupchat(null, nextChannel);

								view.getChannels("all");
								Mezzago_Controller.this.attachListenerDirectMessageButtons();
								Mezzago_Controller.this.attachListenerGroupChatButtons();

							});
						}
					});

					view.deleteChannel.setOnAction(deleteChannel -> {
						String name = chatroomName;

						if (model.deleteChatroom(name)) {
							Platform.runLater(() -> {

								String nextChannel = "";

								if (model.channels.getGroupChats().size() > 0)
									nextChannel = model.channels.getGroupChats().get(0).getName();
								else if (model.channels.getDirectMessages().size() > 0)
									nextChannel = model.channels.getDirectMessages().get(0).getUserName();
								else
									nextChannel = null;

								Mezzago_Controller.this.selectGroupchat(null, nextChannel);

								view.getChannels("all");
								Mezzago_Controller.this.attachListenerDirectMessageButtons();
								Mezzago_Controller.this.attachListenerGroupChatButtons();

							});

						}
					});

					view.blockChannel.setOnAction(blockChannel -> {
						model.setBlockedChannel(chatroomName);
						// view.updateContextMenuTexts(model.channels.getSingleGroupChat(b.getText()).getBlocked(),
						// false,
						// model.checkIsAdmin(b.getText()));
					});

					view.addUserToChatroom.setOnAction(addUser -> {
						// model.getAllUsers();
						view.addUsersToAllUsers(model.channels.getAllUsers());
						view.showAddUserWindow(chatroomName);
					});

					view.removeUserFromChatroom.setOnAction(removeUser -> {
						view.showRemoveUserWindow(chatroomName);
					});
				}

			});

		}
	}

	/**
	 * Handling when the user clicks on a groupchat (chatroom) on the left side
	 * 
	 * @param b button if the user clicks on a button to choose a certain gc channel
	 * @param s string to let the program easily open a gc channel
	 */
	private void selectGroupchat(Button b, String s) {
		String groupChat = "";
		view.scrollDown();
		if (b != null)
			groupChat = b.getText();
		else if (s != null)
			groupChat = s;
		else
			groupChat = "empty";

		if (!view.lblChannel.getText().equals(groupChat)) {

			Mezzago_GroupChat gc = model.channels.getSingleGroupChat(groupChat);
			// Only if there is a valid chat, the chat is changed
			if (gc != null) {

				view.removeNewMessage(gc.getName());

				model.listChatroomUsers(groupChat);

				// Platform.runLater(() ->
				view.setAllUsersFromChatRoom(gc.getChatroomMembers()); // );

				if (currentChat != null) {

					if (model.channels.getSingleDirectMessage(currentChat) != null)
						model.channels.getSingleDirectMessage(currentChat).setTempMessage(view.textIn.getText());
					else if (model.channels.getSingleGroupChat(currentChat) != null)
						model.channels.getSingleGroupChat(currentChat).setTempMessage(view.textIn.getText());

					view.textIn.clear();
				}

				currentChat = groupChat;
				model.setCurrentChat(currentChat, gc, null);
				view.setChannel(gc.getName(), false, true);

				// if there is a tepmorary mesage it is called
				view.textIn.setText(model.channels.getSingleGroupChat(currentChat).getTempMessage());
				view.textArea.clear();
				// insert the chat history in the text area
				view.textArea.appendText(gc.getAllMessages());
				// TODO does not work...
				view.scrollDown();

			}
		}
	}

}
