package mezzago.appClasses;

import mezzago.ServiceLocator;
import mezzago.abstractClasses.View;
import mezzago.commonClasses.Translator;
import mezzago.commonClasses.channels.Mezzago_DirectMessage;
import mezzago.commonClasses.channels.Mezzago_GroupChat;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class Mezzago_View extends View<Mezzago_Model> {

	boolean test = false;
	private String os;

	public InputStream input;

	private Translator t;
	private Logger logger;

	protected MenuBar menuBar;
	protected Menu menuEdit;
	protected Menu menuEditLanguage;
	protected MenuItem menuLogout;
	protected MenuItem menuSettings;

	protected VBox loginVbox;
	protected Scene loginScene;
	protected BorderPane loginPane;
	protected GridPane loginGridPane;
	protected TextField inUserName;
	protected Label lblHint;
	protected PasswordField inPassword;
	protected Label lblPassword;
	protected Button login;
	protected Button createLogin;
	protected Label newLogin;

	private Tooltip inUserNameToolTip;
	private Tooltip inPasswordToolTip;
	
	Hyperlink mezzagoLink;

	Button btnChangeConnection;
	Stage connectionWindow;
	Scene connectionScene;
	TextField inPort;
	TextField inAddress;
	Button btnConnect;
	Label lblAddress;
	Label lblPort;
	Label lblConnectionInfo;

	protected Scene chatScene;
	protected BorderPane MasterPane;
	protected BorderPane ChatPane;
	protected BorderPane ChatTopPane;
	protected HBox chatBox;
	protected VBox SidebarVBox;
	protected Button newDmConnection;
	protected Button createChatroom;
	protected Button joinChatroom;

	protected HBox AccountHBox;
	protected Button btnLogout;
	protected HBox ChannelHBox;

	protected BorderPane LogoBox;
	protected BorderPane LogoBoxLogin;
	protected VBox channelBox;
	protected ImageView imgLogo;
	protected Label lblChannel;
	protected ComboBox<String> allUsersFromChatRoom;

	protected TextFlow textFlow;
	protected ScrollPane scrollPane;
	protected TextArea textArea;

	protected Circle onlineStatus;

	protected ArrayList<Button> groupChatButtons;
	protected ArrayList<Button> directMessageButtons;

	ContextMenu channelContextMenu;
	MenuItem deleteChannel;
	MenuItem leaveChannel;
	MenuItem blockChannel;
	MenuItem addUserToChatroom;
	MenuItem removeUserFromChatroom;

	// Inputfield
	protected TextField textIn;
	protected Button btnSend;
	protected Label lblCounter;

	// Window for settings
	Stage settingsWindow;
	Scene settingsScene;

	BorderPane settingsPane;
	BorderPane accountPane;
	BorderPane blockPane;

	TabPane tabPane;
	Tab settingsTab;
	Tab accountTab;
	Tab blockTab;

	ComboBox<String> soundChoice;
	CheckBox disableAudio;
	Label lblSettingsAudio;
	Button exitGeneralSettings;
	Button saveGeneralSettings;

	Label lblChangePassword;
	PasswordField inNewPassword;
	Button btnExitAccountSettings;
	Button btnSaveAccountSettings;
	Button btnDeleteAccount;
	Label lblAccountSettings;

	Label lblSettingsLanguage;
	ComboBox<String> languageChoice;

	Label lblAccountBlock;
	ComboBox<String> blockedChannelsList;
	Button btnRemoveFromBlockedList;
	Button btnExitBlockSettings;
	Button btnRemoveAll;
	TextField inBlockName;
	Button btnSubmitInBlockName;
	Label lblManualBlock;

	HBox settingsBox;

	Alert alertDeleteAccount;

	// Seperate window (add dm connection)
	Stage addDmWindow;
	Scene addDmScene;

	Label addDmLabel;
	TextField addDmIn;
	Button btnAddDm;
	ComboBox<String> allUsers;

	// Seperate window (join chat room)
	Stage addGcJoinWindow;
	Scene addGcJoinScene;

	Label joinGcLabel;
	TextField joinGcIn;
	Button btnJoinGc;
	ComboBox<String> chatroomListChoice;

	Stage addGcWindow;
	Scene addGcScene;

	Label addGcLabel;
	TextField addGcIn;
	CheckBox gcHidden;
	Button btnAddGc;

	// Seperate window ( add user to chat room)
	Stage addUserWindow;
	Scene addUserScene;

	Label addUserLabel;
	TextField addUserIn;
	Button btnAddUser;
	ComboBox<String> addUserList;

	// Seperate window (remove user from chat room)
	Stage removeUserWindow;
	Scene removeUserScene;

	Label removeUserLabel;
	Button btnRemoveUser;
	ComboBox<String> removeUserList;

	public Mezzago_View(Stage stage, Mezzago_Model model) {
		super(stage, model);
		ServiceLocator.getServiceLocator().getLogger().info("Application view initialized");
	}

	@Override
	protected Scene create_GUI() {
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		logger = sl.getLogger();
		t = sl.getTranslator();

		os = System.getProperty("os.name");

		menuBar = new MenuBar();
		this.makeMenuBar();

		// Create the seperate windows, that are shown when selected
		this.makeSettingsWindow();
		this.makeDmWindow();
		this.makeGcJoinWindow();
		this.makeGcWindow();
		this.makeAddUserWindow();
		this.makeRemoveUserWindow();

		// Create Scene loginScene
		this.makeLoginScene();

		// Create Scene chatScene -> is called when login successful
		this.makeChatScene();

		// Initially, the login scene is set
		this.setLoginScene();

		for (Locale locale : sl.getLocales()) {
			MenuItem language = new MenuItem(locale.getLanguage());
			menuEditLanguage.getItems().add(language);
			language.setOnAction(event -> {
				sl.getConfiguration().setLocalOption("Language", locale.getLanguage());
				sl.setTranslator(new Translator(locale.getLanguage()));
				t = sl.getTranslator();
				updateTexts();
			});
		}

		this.updateTexts();

		return loginScene;
	}

	/**
	 * Set the chat scene according to the local user
	 * 
	 * @param username
	 */
	protected void setChatScene(String username) {
		try {
			lblHint.setText("");

			this.getChannels("all");

			stage.setResizable(true);

			stage.setScene(chatScene);

			stage.setMinHeight(450);
			stage.setMinWidth(600);

			stage.setX((Screen.getPrimary().getBounds().getWidth() - MasterPane.getWidth()) / 2);
			stage.setY((Screen.getPrimary().getBounds().getHeight() - MasterPane.getHeight()) / 2 - 15);

			// Update all texts
			this.updateTexts();

		} catch (Exception e) {
			logger.warning(e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Set the login scene
	 */
	protected void setLoginScene() {
		stage.setTitle("MEZZAGO");
		stage.setMinHeight(450);
		stage.setMinWidth(300);
		stage.setScene(loginScene);
		stage.setX((Screen.getPrimary().getBounds().getWidth() - 400) / 2);
		stage.setY((Screen.getPrimary().getBounds().getHeight() - 600) / 2 - 15);
		stage.setResizable(false);

		inUserName.requestFocus();
		
		textArea.clear();
		this.setChannel(null, false, false);
	}

	/**
	 * Creation of the login scene which is the first window
	 */
	private void makeLoginScene() {

		this.makeConnectionWindow();

		inUserNameToolTip = new Tooltip("Username - min. 3 Chars: 'A-Z', 'a-z', '0-9', '_'");
		inPasswordToolTip = new Tooltip("Password - min. 3 Chars: 'A-Z', 'a-z', '0-9'");

		loginPane = new BorderPane();
		loginGridPane = new GridPane();
		loginVbox = new VBox();

		
		btnChangeConnection = new Button("Change Connection");
		btnChangeConnection.getStyleClass().add("btn-create-login");

		inUserName = new TextField();
		inUserName.setPromptText("username");
		inUserName.setTooltip(inUserNameToolTip);
		inUserName.setMaxWidth(230);
		inUserName.getStyleClass().add("in-username");

		lblHint = new Label("Enter login credentials:");
		lblHint.setWrapText(true);
		lblHint.setTextAlignment(TextAlignment.CENTER);
		lblHint.setPrefHeight(50);
		
		inPassword = new PasswordField();
		inPassword.setTooltip(inPasswordToolTip);
		inPassword.setPromptText("password");
		inPassword.setMaxWidth(230);
		inPassword.getStyleClass().add("in-password");
		login = new Button("Log In");
		login.getStyleClass().add("btn-login");
		login.setDisable(!model.isTesting);

		createLogin = new Button();
		createLogin.getStyleClass().add("btn-create-login");
		createLogin.setDisable(true);
		newLogin = new Label("");
		newLogin.setWrapText(true);
		newLogin.setTextAlignment(TextAlignment.CENTER);
		newLogin.getStyleClass().add("lbl-newlogin");

		loginVbox.getChildren().addAll(lblHint, inUserName, inPassword, login, newLogin, createLogin);
		loginVbox.setPadding(new Insets(20));
		loginVbox.setSpacing(5);
		loginVbox.setAlignment(Pos.CENTER);
		
		mezzagoLink = new Hyperlink("mezzago.ch");
		mezzagoLink.setAlignment(Pos.CENTER);
		mezzagoLink.getStyleClass().add("mezzago-link");
		
		BorderPane.setAlignment(mezzagoLink, Pos.CENTER);
		loginPane.setCenter(loginVbox);
		loginPane.setBottom(mezzagoLink);

		loginScene = new Scene(loginPane);
		loginScene.getStylesheets().add(getClass().getResource("Mezzago_styles.css").toExternalForm());

	}

	/**
	 * Set the hint label above the username field according to the action that was
	 * taken
	 * 
	 * @param s
	 */
	public void setHintLabel(String s) {
		if (s.length() > 0)
			s = "." + s;

		lblHint.setText(t.getString("program.login.label.hint" + s));
	}

	/**
	 * Window to choose a different connection than standard (127.0.0.1, 50001)
	 */
	private void makeConnectionWindow() {
		lblAddress = new Label("Address");
		lblPort = new Label("Port number");
		inPort = new TextField();
		inPort.setPromptText("1024-65535");
		inAddress = new TextField();
		inAddress.setPromptText("e.g. 127.0.0.1");
		btnConnect = new Button("Connect");
		lblConnectionInfo = new Label();
		btnConnect.setDisable(true);

		GridPane grid = new GridPane();
		grid.setVgap(20);
		grid.add(lblAddress, 0, 0, 2, 1);
		grid.add(inAddress, 0, 1, 2, 1);
		grid.add(lblPort, 0, 3, 2, 1);
		grid.add(inPort, 0, 4, 2, 1);
		grid.add(btnConnect, 0, 5);
		grid.add(lblConnectionInfo, 1, 5);
		grid.setPadding(new Insets(50));

		connectionScene = new Scene(grid);

		connectionWindow = new Stage();
		connectionWindow.setScene(connectionScene);
		connectionWindow.initModality(Modality.WINDOW_MODAL);
		connectionWindow.setResizable(false);
		connectionWindow.setTitle("New Connection");
		connectionWindow.initOwner(stage);

	}

	public void addBtnChangeConnection() {
		loginVbox.getChildren().add(btnChangeConnection);
		btnChangeConnection.requestFocus();
	}

	public void removeBtnChangeConnection() {
		if (loginVbox.getChildren().contains(btnChangeConnection))
			loginVbox.getChildren().remove(btnChangeConnection);
	}

	public void showConnectionWindow() {
		connectionWindow.show();
	}

	public void hideConnectionWindow() {
		inPort.clear();
		inAddress.clear();
		connectionWindow.hide();
	}

	public void disableConnectionButton(boolean disable) {
		btnConnect.setDisable(disable);
	}

	public void setLabelConnectionInfo(String text) {
		switch (text) {
		case "failed":
			lblConnectionInfo.setText(t.getString("program.login.newconnection.label.info." + text));
			break;
		default:
			lblConnectionInfo.setText("");
		}
	}

	/**
	 * Creation of the main scene for the chat application
	 */
	private void makeChatScene() {
		// create Master BorderPane and Scene
		MasterPane = new BorderPane();

		// create LogoBox
		LogoBox = new BorderPane();
		LogoBoxLogin = new BorderPane();
		this.makeLogoBox();
		this.makeLogoBoxLogin();

		// List of the buttons in the sibebar vbox
		groupChatButtons = new ArrayList<>();
		directMessageButtons = new ArrayList<>();

		newDmConnection = new Button("Connect dm");
		newDmConnection.getStyleClass().addAll("select-option-channel", "select-option-channel-wide");
		createChatroom = new Button("Create");
		createChatroom.getStyleClass().add("select-option-channel");
		joinChatroom = new Button("Join");
		joinChatroom.getStyleClass().add("select-option-channel");

		// create Sidebar VBox
		SidebarVBox = new VBox();
		this.makeSidebarVBox();

		// Context menu for the channels
		channelContextMenu = new ContextMenu();
		// contextMenu.getItems().add(itemOne);
		deleteChannel = new MenuItem();
		leaveChannel = new MenuItem();
		blockChannel = new MenuItem();
		addUserToChatroom = new MenuItem();
		removeUserFromChatroom = new MenuItem();
		channelContextMenu.getItems().addAll(deleteChannel, leaveChannel, blockChannel, addUserToChatroom,
				removeUserFromChatroom);

		// create ChatPane
		ChatPane = new BorderPane();
		this.makeChatPane();

		// create ChatTopPane
		ChatTopPane = new BorderPane();
		this.makeChatTopPane();

		// create chatPane bottom
		chatBox = new HBox();
		this.makeChatBox();

		// create Account Box
		AccountHBox = new HBox();
		this.makeAccountBox();

		// create Channel Box
		ChannelHBox = new HBox();
		this.makeChannelHBox();

		// set the elements in the masterPane
		MasterPane.setTop(menuBar);
		MasterPane.setCenter(ChatPane);

		// Scene
		chatScene = new Scene(MasterPane, 1536, 864, Color.WHITE);
		chatScene.getStylesheets().add(getClass().getResource("Mezzago_styles.css").toExternalForm());
	}

	private void makeMenuBar() {

		// If the OS is Mac OS, the System Menubar is used
		if (os != null && os.startsWith("Mac"))
			Platform.runLater(() -> menuBar.setUseSystemMenuBar(true));

		menuEditLanguage = new Menu("Language");
		menuLogout = new MenuItem("Logout");

		menuSettings = new MenuItem("Settings");

		if (os != null && os.startsWith("Mac")) {
			menuSettings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN));
			menuLogout.setAccelerator(
					new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN));
		} else {
			menuSettings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.CONTROL_DOWN));
			menuLogout.setAccelerator(
					new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));

		}

		menuEdit = new Menu("Edit");

		menuBar.getMenus().addAll(menuEdit);
		menuEdit.getItems().addAll(menuLogout, menuSettings, menuEditLanguage);

	}

	private void makeChannelHBox() {
		ChannelHBox.setPrefWidth(150);
		ChatTopPane.setCenter(ChannelHBox);

		// ChatTopPane.setLeft(ChannelHBox); -> this or the other..?

		ChannelHBox.getStyleClass().add("ChannelHBox");
		ChannelHBox.setSpacing(5);

		onlineStatus = new Circle(4);
		onlineStatus.getStyleClass().add("default-fill-black");

		lblChannel = new Label("Placeholder");
		lblChannel.setAlignment(Pos.CENTER);

		allUsersFromChatRoom = new ComboBox<>();

		ChannelHBox.getChildren().addAll(onlineStatus, lblChannel);
		ChannelHBox.setAlignment(Pos.CENTER);
	}

	private void makeAccountBox() {
		AccountHBox.setPrefWidth(150);
		btnLogout = new Button("AccountHBox");
		btnLogout.getStyleClass().add("account-button");
		AccountHBox.getChildren().add(btnLogout);
		ChatTopPane.setRight(AccountHBox);
		AccountHBox.getStyleClass().add("AccountHBox");
	}

	private void makeChatBox() {
		chatBox.getStyleClass().add("chat-box");
		chatBox.setSpacing(10);

		textIn = new TextField();
		textIn.getStyleClass().add("text-in");
		textIn.setPrefHeight(20);
		textIn.setDisable(true);

		btnSend = new Button("Send");
		btnSend.getStyleClass().add("btn-send");
		btnSend.setPrefWidth(55);
		btnSend.setMinWidth(55);

		lblCounter = new Label();
		lblCounter.getStyleClass().add("lbl-counter");
		lblCounter.setPrefWidth(80);
		lblCounter.setMinWidth(80);
		lblCounter.setAlignment(Pos.CENTER_RIGHT);

		textIn.prefWidthProperty().bind(chatBox.widthProperty().subtract(btnSend.prefWidthProperty()));

		chatBox.getChildren().addAll(textIn, lblCounter, btnSend);
		ChatPane.setBottom(chatBox);
	}

	private void makeChatTopPane() {
		ChatPane.setTop(ChatTopPane);
		ChatTopPane.setPrefHeight(60);
		ChatTopPane.getStyleClass().add("ChatTopPane");
	}

	private void makeChatPane() {

		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setFocusTraversable(false);
		textArea.setMouseTransparent(false);

		ChatPane.setCenter(textArea);
		ChatPane.getStyleClass().add("ChatPane");
	}

	/**
	 * Add all channels to the according position
	 */
	public void getChannels(String select) {
		channelBox.getChildren().clear();
		groupChatButtons.clear();
		directMessageButtons.clear();

		select = "all";

		if (model.channels.getGroupChats().size() != 0) {

			if (select.equals("all") || select.equals("gc")) {
				for (Mezzago_GroupChat gc : model.channels.getGroupChats()) {
					Button tempButton = new Button(gc.getName());
					tempButton.getStyleClass().add("group-chat-channel");
					channelBox.getChildren().add(tempButton);
					groupChatButtons.add(tempButton);
				}
			}
		}

		channelBox.getChildren().add(new HBox(createChatroom, joinChatroom));
		channelBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

		if (model.channels.getDirectMessages().size() != 0) {
			if (select.equals("all") || select.equals("dm")) {
				for (Mezzago_DirectMessage dm : model.channels.getDirectMessages()) {
					Button tempButton = new Button(dm.getUserName());
					tempButton.getStyleClass().add("direct-message-channel");
					channelBox.getChildren().add(tempButton);
					directMessageButtons.add(tempButton);
				}
			}
		}

		channelBox.getChildren().add(newDmConnection);

	}

	public void setNewMessage(String s) {
		for (Button b : groupChatButtons)
			if (b.getText().equals(s) && !b.getStyleClass().contains("new-message"))
				b.getStyleClass().add("new-message");

		for (Button b : directMessageButtons)
			if (b.getText().equals(s) && !b.getStyleClass().contains("new-message"))
				b.getStyleClass().add("new-message");
	}

	public void removeNewMessage(String s) {
		for (Button b : groupChatButtons)
			if (b.getText().equals(s))
				b.getStyleClass().remove("new-message");

		for (Button b : directMessageButtons)
			if (b.getText().equals(s))
				b.getStyleClass().remove("new-message");
	}

	/**
	 * Creation of the logo-box for the login scene
	 */
	private void makeLogoBoxLogin() {
		LogoBoxLogin.getStyleClass().add("LogoBoxLogin");

		// Add Logo
		try {
			String path = "/mezzago/resources/Mezzago_Logo_blue.png";
			imgLogo = new ImageView(new Image(getClass().getResourceAsStream(path)));
			imgLogo.setFitHeight(120);
			imgLogo.setSmooth(true);
			imgLogo.setPreserveRatio(true);
		} catch (Exception e) {
			logger.info(e.toString());
		}

		LogoBoxLogin.setCenter(imgLogo);
		loginPane.setTop(LogoBoxLogin);
	}

	private void makeLogoBox() {
		LogoBox.getStyleClass().add("LogoBox");

		// Add Logo
		try {
			String path = "/mezzago/resources/Mezzago_Logo.png";
			imgLogo = new ImageView(new Image(getClass().getResourceAsStream(path)));
			imgLogo.setFitHeight(60);
			imgLogo.setSmooth(true);
			imgLogo.setPreserveRatio(true);
		} catch (Exception e) {
			logger.info(e.toString());
		}

		LogoBox.setCenter(imgLogo);
	}

	private void makeSidebarVBox() {
		MasterPane.setLeft(SidebarVBox);
		channelBox = new VBox();
		ScrollPane scrollChannels = new ScrollPane(channelBox);
		scrollChannels.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		SidebarVBox.getStyleClass().add("SidebarVBox");
		SidebarVBox.getChildren().add(LogoBox);
		SidebarVBox.getChildren().add(scrollChannels);
	}

	public void setChannel(String chatName, boolean isOnline, boolean groupChat) {
		if (chatName != null) {
			if (groupChat) {

				if (!ChannelHBox.getChildren().contains(allUsersFromChatRoom))
					ChannelHBox.getChildren().add(allUsersFromChatRoom);

				lblChannel.setText(chatName
						+ ((model.getIsBlocked(chatName) ? " " + t.getString("program.chat.label.channel.blocked") + " "
								: "")));
				onlineStatus.getStyleClass().removeAll("fill-grey", "default-fill-black");
				onlineStatus.getStyleClass().add("fill-transparent");
				textIn.setDisable(false);
				textIn.requestFocus();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						textIn.end();
					}
				});
			} else {
				lblChannel.setText(chatName);

				if (ChannelHBox.getChildren().contains(allUsersFromChatRoom))
					ChannelHBox.getChildren().remove(allUsersFromChatRoom);

				if (isOnline) {
					onlineStatus.getStyleClass().removeAll("fill-grey", "fill-transparent", "default-fill-black");
					onlineStatus.getStyleClass().add("fill-green");
					textIn.setDisable(false);
					textIn.requestFocus();
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							textIn.end();
						}

					});
				} else {
					onlineStatus.getStyleClass().removeAll("fill-green", "fill-transparent", "default-fill-black");
					onlineStatus.getStyleClass().add("fill-grey");
					textIn.setDisable(true);
				}
			}
		} else {
			lblChannel.setText("");

			if (ChannelHBox.getChildren().contains(allUsersFromChatRoom))
				ChannelHBox.getChildren().remove(allUsersFromChatRoom);

			onlineStatus.getStyleClass().removeAll("fill-green", "fill-grey", "default-fill-black");
			onlineStatus.getStyleClass().add("fill-transparent");
			textIn.setDisable(true);
		}

	}

	public void setAllUsersFromChatRoom(ArrayList<String> users) {
		allUsersFromChatRoom.getItems().clear();
		allUsersFromChatRoom.getItems().addAll(users);
	}

	/**
	 * Create the settings window
	 */
	private void makeSettingsWindow() {

		// Create the content for the account tab

		lblChangePassword = new Label("Enter here the new Password:");
		inNewPassword = new PasswordField();
		btnExitAccountSettings = new Button("Exit");
		btnSaveAccountSettings = new Button("Save");
		Separator separator = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		btnDeleteAccount = new Button("Delete the account!");
		lblAccountSettings = new Label("Account");

		GridPane accountGridPane = new GridPane();
		accountGridPane.setPadding(new Insets(50));
		accountGridPane.setHgap(50);
		accountGridPane.setVgap(20);
		accountGridPane.add(lblChangePassword, 0, 0, 2, 1);
		accountGridPane.add(inNewPassword, 0, 1, 2, 1);
		accountGridPane.add(btnSaveAccountSettings, 0, 2);
		accountGridPane.add(btnExitAccountSettings, 1, 2);
		accountGridPane.add(separator, 0, 3, 2, 1);
		accountGridPane.add(btnDeleteAccount, 0, 4);

		accountPane = new BorderPane(accountGridPane);
		accountPane.setTop(lblAccountSettings);
		BorderPane.setAlignment(lblAccountSettings, Pos.TOP_CENTER);
		accountPane.setPadding(new Insets(10));

		accountTab = new Tab("Account");
		accountTab.setClosable(false);
		accountTab.setContent(accountPane);

		// Create the content for the settings tab

		exitGeneralSettings = new Button("Exit");
		saveGeneralSettings = new Button("Save");
		lblSettingsAudio = new Label("Audio");
		soundChoice = new ComboBox<>();
		soundChoice.setPromptText("[choose sound]");
		disableAudio = new CheckBox("Disable");
		lblSettingsLanguage = new Label("Language");
		languageChoice = new ComboBox<>();
		languageChoice.setPromptText("[choose language]");
		languageChoice.getItems().addAll("en", "de");

		File resources = new File("src/mezzago/resources/sounds/");
		File[] listOfFiles = resources.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String tempFile = listOfFiles[i].getName().substring("Notification_".length()).replace("_", " ");
				tempFile = tempFile.replace(".wav", "");
				soundChoice.getItems().add(tempFile);
			}
		}

		GridPane settingsGridPane = new GridPane();
		settingsGridPane.setHgap(50);
		settingsGridPane.setVgap(20);
		settingsGridPane.add(lblSettingsAudio, 0, 0);
		settingsGridPane.add(soundChoice, 0, 1);
		settingsGridPane.add(disableAudio, 1, 1);
		settingsGridPane.add(new Separator(Orientation.HORIZONTAL), 0, 2, 2, 1);
		settingsGridPane.add(lblSettingsLanguage, 0, 3);
		settingsGridPane.add(languageChoice, 0, 4);

		settingsPane = new BorderPane();
		settingsPane.setPadding(new Insets(20));
		settingsPane.setCenter(settingsGridPane);
		settingsPane.setBottom(new HBox(50, saveGeneralSettings, exitGeneralSettings));

		settingsTab = new Tab("Settings");
		settingsTab.setClosable(false);
		settingsTab.setContent(settingsPane);

		// Tab for blocked channels
		lblAccountBlock = new Label();
		btnRemoveFromBlockedList = new Button();
		btnExitBlockSettings = new Button();
		btnRemoveAll = new Button();
		blockedChannelsList = new ComboBox<>();
		inBlockName = new TextField();
		btnSubmitInBlockName = new Button();
		lblManualBlock = new Label();

		GridPane gridBlocked = new GridPane();
		gridBlocked.setHgap(50);
		gridBlocked.setVgap(20);
		gridBlocked.add(blockedChannelsList, 0, 0);
		gridBlocked.add(btnRemoveFromBlockedList, 0, 1);
		gridBlocked.add(btnRemoveAll, 1, 1);
		gridBlocked.add(new Separator(Orientation.HORIZONTAL), 0, 2, 2, 1);
		gridBlocked.add(lblManualBlock, 0, 3);
		gridBlocked.add(inBlockName, 0, 4);
		gridBlocked.add(btnSubmitInBlockName, 0, 5);

		blockPane = new BorderPane();
		blockPane.setTop(lblAccountBlock);
		BorderPane.setAlignment(lblAccountBlock, Pos.TOP_CENTER);
		blockPane.setPadding(new Insets(20));
		blockPane.setCenter(gridBlocked);
		blockPane.setBottom(btnExitBlockSettings);

		blockTab = new Tab();
		blockTab.setClosable(false);
		blockTab.setContent(blockPane);

		tabPane = new TabPane(accountTab, settingsTab, blockTab);
		tabPane.setSide(Side.LEFT);

		settingsBox = new HBox(tabPane);

		settingsScene = new Scene(settingsBox);

		// New window (Stage)
		settingsWindow = new Stage();
		settingsWindow.setTitle("Settings");
		settingsWindow.setScene(settingsScene);

		settingsWindow.initModality(Modality.WINDOW_MODAL);
		settingsWindow.setResizable(false);

		// Specify the owner Window (parent) for new window
		settingsWindow.initOwner(stage);

		alertDeleteAccount = new Alert(AlertType.CONFIRMATION, "Are you sure, you want to delete account ?",
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

	}

	/**
	 * Add items to the combo-box where all the blocked channels are listed.
	 * 
	 * @param channels List of all currently blocked channels
	 */
	public void addChannelsToBlockedComboBox(ArrayList<String> channels) {
		blockedChannelsList.getItems().clear();
		blockedChannelsList.getItems().addAll(channels);
	}

	/**
	 * Get the chosen value of blocked channel
	 * 
	 * @return String (null if nothing chosen
	 */
	public String getBlockedChannel() {
		return blockedChannelsList.getValue();
	}

	/**
	 * Show the settings window
	 */
	public void showSettingsWindow() {
		settingsWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
		settingsWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);
		settingsWindow.show();
	}

	/**
	 * Hide the settings window
	 */
	public void hideSettingsWindow() {
		if (settingsWindow.isShowing())
			settingsWindow.hide();
	}

	/**
	 * Create the stage that is shown when connecting to a direct message
	 */
	private void makeDmWindow() {
		addDmLabel = new Label("Add a new direct message connection:");
		addDmIn = new TextField();
		addDmIn.setPromptText("username");
		btnAddDm = new Button("Go");
		allUsers = new ComboBox<>();
		allUsers.setPromptText("[find user]");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		grid.add(addDmLabel, 0, 0);
		grid.add(addDmIn, 0, 1);
		grid.add(new Separator(Orientation.HORIZONTAL), 0, 2, 2, 1);
		grid.add(allUsers, 0, 3);
		grid.add(btnAddDm, 1, 3);

		addDmScene = new Scene(grid);

		// New window (Stage)
		addDmWindow = new Stage();
		addDmWindow.setTitle("New direct message connection");
		addDmWindow.setScene(addDmScene);
		addDmWindow.setResizable(false);

		// Specify the modality for new window.
		addDmWindow.initModality(Modality.WINDOW_MODAL);

		// Specify the owner Window (parent) for new window
		addDmWindow.initOwner(stage);

	}

	/**
	 * Add Users to the list, to choose from and connect directly
	 * 
	 * @param users
	 */
	public void addUsersToAllUsers(ArrayList<String> users) {
		allUsers.getItems().clear();
		allUsers.getItems().addAll(users);

		addUserList.getItems().clear();
		addUserList.getItems().addAll(users);

		// the local user doesn't need to be shown
		if (allUsers.getItems().contains(model.getLocalUser()))
			allUsers.getItems().remove(model.getLocalUser());

		if (addUserList.getItems().contains(model.getLocalUser()))
			addUserList.getItems().remove(model.getLocalUser());
	}

	/**
	 * Create the stage to join chat rooms
	 */
	private void makeGcJoinWindow() {
		joinGcLabel = new Label("Enter the name of the chat room:");
		joinGcIn = new TextField();
		joinGcIn.setPromptText("room name");
		btnJoinGc = new Button("Go");
		chatroomListChoice = new ComboBox<>();
		chatroomListChoice.setPromptText("[chat-room]");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		grid.add(joinGcLabel, 0, 0);
		grid.add(chatroomListChoice, 0, 1);
		grid.add(btnJoinGc, 1, 1);

		addGcJoinScene = new Scene(grid);

		// New window (Stage)
		addGcJoinWindow = new Stage();
		addGcJoinWindow.setTitle("Join a chat room");
		addGcJoinWindow.setScene(addGcJoinScene);
		addGcJoinWindow.setResizable(false);
		// Specify the modality for new window.
		addGcJoinWindow.initModality(Modality.WINDOW_MODAL);

		// Specify the owner Window (parent) for new window
		addGcJoinWindow.initOwner(stage);

	}

	public void makeChatroomListChoice() {
		String[] list = model.getChatroomList();
		if (list != null) {
			chatroomListChoice.getItems().clear();
			chatroomListChoice.getItems().addAll(list);
		}
	}

	public void showJoinWindow(String chatType) {
		if (chatType.equals("connectDm")) {
			addDmWindow.show();
			addDmWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
			addDmWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);
		} else if (chatType.equals("joinGc")) {
			addGcJoinWindow.show();
			addGcJoinWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
			addGcJoinWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);
		}
	}

	public void hideJoinWindow() {
		if (addDmWindow.isShowing())
			addDmWindow.hide();
		else if (addGcJoinWindow.isShowing())
			addGcJoinWindow.hide();
	}

	/**
	 * Make the window to create a new chat-room
	 */
	private void makeGcWindow() {
		addGcLabel = new Label("Add a new group chat room:");
		addGcIn = new TextField();
		addGcIn.setPromptText("chatroom name");
		gcHidden = new CheckBox("Hidden chat");
		btnAddGc = new Button("Go");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		grid.add(addGcLabel, 0, 0);
		grid.add(addGcIn, 0, 1);
		grid.add(gcHidden, 1, 1);
		grid.add(btnAddGc, 0, 2);

		addGcScene = new Scene(grid);

		// New window (Stage)
		addGcWindow = new Stage();
		addGcWindow.setTitle("Create a new chatroom");
		addGcWindow.setScene(addGcScene);
		addGcWindow.setResizable(false);
		// Specify the modality for new window.
		addGcWindow.initModality(Modality.WINDOW_MODAL);

		// Specify the owner Window (parent) for new window
		addGcWindow.initOwner(stage);

	}

	public void showGcWindow() {

		addGcWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
		addGcWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);

		if (!addGcWindow.isShowing())
			addGcWindow.show();
	}

	public void hideGcWindow() {
		if (addGcWindow.isShowing())
			addGcWindow.hide();
	}

	/**
	 * Make the window where the admin can add users to a chatroom
	 */
	public void makeAddUserWindow() {
		addUserLabel = new Label();
		addUserIn = new TextField();
		btnAddUser = new Button();
		addUserList = new ComboBox<>();

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		grid.add(addUserLabel, 0, 0);
		grid.add(addUserIn, 0, 1);
		grid.add(new Separator(Orientation.HORIZONTAL), 0, 2, 2, 1);
		grid.add(addUserList, 0, 3);
		grid.add(btnAddUser, 1, 3);

		addUserScene = new Scene(grid);

		addUserWindow = new Stage();
		addUserWindow.setScene(addUserScene);
		addUserWindow.setResizable(false);
		// Specify the modality for new window.
		addUserWindow.initModality(Modality.WINDOW_MODAL);

		// Specify the owner Window (parent) for new window
		addUserWindow.initOwner(stage);

	}

	public void showAddUserWindow(String chatroomName) {

		addUserWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
		addUserWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);

		addUserWindow.show();
		addUserWindow.setTitle(t.getString("program.chat.adduser.stage") + ": " + chatroomName);
	}

	public void hideAddUserWindow() {
		if (addUserWindow.isShowing())
			addUserWindow.hide();
	}

	/**
	 * Make the window where the admin can remove users from a chat-room
	 */
	public void makeRemoveUserWindow() {

		removeUserLabel = new Label();
		btnRemoveUser = new Button();
		removeUserList = new ComboBox<>();

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30));
		grid.setHgap(30);
		grid.setVgap(30);
		grid.add(removeUserLabel, 0, 0);
		grid.add(removeUserList, 0, 1);
		grid.add(btnRemoveUser, 1, 1);

		removeUserScene = new Scene(grid);

		removeUserWindow = new Stage();
		removeUserWindow.setScene(removeUserScene);

		// Specify the modality for new window.
		removeUserWindow.initModality(Modality.WINDOW_MODAL);
		removeUserWindow.setResizable(false);
		// Specify the owner Window (parent) for new window
		removeUserWindow.initOwner(stage);
	}

	public void showRemoveUserWindow(String chatroomName) {

		removeUserWindow.setX(stage.getX() + (stage.getWidth() / 2) - 160);
		removeUserWindow.setY(stage.getY() + (stage.getHeight() / 2) - 160);

		removeUserWindow.show();
		removeUserWindow.setTitle(t.getString("program.chat.adduser.stage") + ": " + chatroomName);
	}

	public void hideRemoveUserWindow() {
		if (removeUserWindow.isShowing())
			removeUserWindow.hide();
	}

	public void addUsersToRemoveUserList(ArrayList<String> users) {
		removeUserList.getItems().clear();
		removeUserList.getItems().addAll(users);
		if (removeUserList.getItems().contains(model.getLocalUser()))
			removeUserList.getItems().remove(model.getLocalUser());
	}

	/**
	 * Update all texts that are multilingual
	 */
	protected void updateTexts() {

		t = ServiceLocator.getServiceLocator().getTranslator();
		// add here all elements, that need an update

		// Login Scene
		inUserName.setPromptText(t.getString("program.login.prompt.username"));
		inPassword.setPromptText(t.getString("program.login.prompt.password"));
		login.setText(t.getString("program.login.button.login"));
		createLogin.setText(t.getString("program.login.button.create"));
		newLogin.setText(t.getString("program.login.label.create"));
		inUserNameToolTip.setText(t.getString("program.login.tooltip.username"));
		inPasswordToolTip.setText(t.getString("program.login.tooltip.password"));
		btnChangeConnection.setText(t.getString("program.login.newconnection.button.open"));
		connectionWindow.setTitle(t.getString("program.login.newconnection.stage.title"));
		inPort.setPromptText(t.getString("program.login.newconnection.textfield.port"));
		inAddress.setPromptText(t.getString("program.login.newconnection.textfield.address"));
		btnConnect.setText(t.getString("program.login.newconnection.button.connect"));
		lblAddress.setText(t.getString("program.login.newconnection.label.address"));
		lblPort.setText(t.getString("program.login.newconnection.label.port"));

		// lblHint.setText(t.getString("program.login.label.hint"));

		// Chat Scene
		lblChannel.setText(t.getString("program.chat.label.channel"));
		btnSend.setText(t.getString("program.chat.button.send"));
		allUsersFromChatRoom.setPromptText(t.getString("program.chat.combobox.allusers.prompt"));
		btnLogout.setText(t.getString("program.chat.button.logout"));

		if (stage.getScene() != loginScene) {
			stage.setTitle(t.getString("program.name") + " - " + t.getString("program.welcome") + " "
					+ model.getLocalUser() + "!");
		}

		// Context menu
		deleteChannel.setText(t.getString("program.chat.contextmenu.delete"));
		leaveChannel.setText(t.getString("program.chat.contextmenu.leave"));
		blockChannel.setText(t.getString("program.chat.contextmenu.block"));
		addUserToChatroom.setText(t.getString("program.chat.contextmenu.adduser"));
		removeUserFromChatroom.setText(t.getString("program.chat.contextmenu.removeuser"));

		// new connections in chat scene
		newDmConnection.setText(t.getString("program.chat.button.newdm"));
		createChatroom.setText(t.getString("program.chat.button.createchatroom"));
		joinChatroom.setText(t.getString("program.chat.button.joinchatroom"));

		// new dm
		addDmWindow.setTitle(t.getString("program.chat.newdm.stage"));
		addDmLabel.setText(t.getString("program.chat.newdm.label"));
		addDmIn.setPromptText(t.getString("program.chat.newdm.textfield.prompt"));
		btnAddDm.setText(t.getString("program.chat.newdm.button.send"));

		// join chat room
		addGcJoinWindow.setTitle(t.getString("program.chat.joinchatroom.stage"));
		joinGcLabel.setText(t.getString("program.chat.joinchatroom.label.header"));
		joinGcIn.setPromptText(t.getString("program.chat.joinchatroom.textfield.prompt"));
		btnJoinGc.setText(t.getString("program.chat.joinchatroom.button.send"));

		// create chat room
		addGcWindow.setTitle(t.getString("program.chat.newgc.stage"));
		addGcLabel.setText(t.getString("program.chat.newgc.label.header"));
		addGcIn.setPromptText(t.getString("program.chat.newgc.textfield"));
		gcHidden.setText(t.getString("program.chat.newgc.checkbox.hidden"));
		btnAddGc.setText(t.getString("program.chat.newgc.button.send"));

		// add user window
		addUserLabel.setText(t.getString("program.chat.adduser.label"));
		addUserIn.setPromptText(t.getString("program.chat.adduser.textfield.prompt"));
		btnAddUser.setText(t.getString("program.chat.adduser.button.send"));
		addUserList.setPromptText(t.getString("program.chat.adduser.combobox.prompt"));
		addUserWindow.setTitle(t.getString("program.chat.adduser.stage"));

		// remove user window
		removeUserLabel.setText(t.getString("program.chat.removeuser.label"));
		removeUserList.setPromptText(t.getString("program.chat.removeuser.combobox.prompt"));
		btnRemoveUser.setText(t.getString("program.chat.removeuser.button.send"));
		removeUserWindow.setTitle(t.getString("program.chat.adduser.stage"));

		// The menu entries
		menuLogout.setText(t.getString("program.menu.file.logout"));
		menuSettings.setText(t.getString("program.menu.file.settings"));
		menuEdit.setText(t.getString("program.menu.edit"));
		menuEditLanguage.setText(t.getString("program.menu.edit.language"));

		// Settings window
		settingsWindow.setTitle(t.getString("program.settings"));
		accountTab.setText(t.getString("program.settings.tab.account"));
		settingsTab.setText(t.getString("program.settings.tab.settings"));
		blockTab.setText(t.getString("program.settings.tab.block"));

		exitGeneralSettings.setText(t.getString("program.settings.tab.settings.exit"));
		saveGeneralSettings.setText(t.getString("program.settings.tab.settings.save"));

		disableAudio.setText(t.getString("program.settings.tab.settings.audio.disable"));
		lblSettingsAudio.setText(t.getString("program.settings.tab.settings.audio.label"));

		lblChangePassword.setText(t.getString("program.settings.tab.settings.account.label.password"));
		btnExitAccountSettings.setText(t.getString("program.settings.tab.settings.account.button.exit"));
		btnSaveAccountSettings.setText(t.getString("program.settings.tab.settings.account.button.save"));
		btnDeleteAccount.setText(t.getString("program.settings.tab.settings.account.button.delete"));
		lblAccountSettings.setText(t.getString("program.settings.tab.settings.account.label.header"));

		alertDeleteAccount
				.setContentText(t.getString("program.settings.tab.settings.account.button.delete.alert.content"));
		alertDeleteAccount.setTitle(t.getString("program.settings.tab.settings.account.button.delete.alert.title"));

		lblSettingsLanguage.setText(t.getString("program.settings.tab.settings.account.label.language"));

		lblAccountBlock.setText(t.getString("program.settings.tab.settings.block.label.header"));
		btnRemoveFromBlockedList.setText(t.getString("program.settings.tab.settings.block.button.remove"));
		btnExitBlockSettings.setText(t.getString("program.settings.tab.settings.block.button.exit"));
		btnRemoveAll.setText(t.getString("program.settings.tab.settings.block.button.remove.all"));
		blockedChannelsList.setPromptText(t.getString("program.settings.tab.settings.block.combobox.prompt"));
		btnSubmitInBlockName.setText(t.getString("program.settings.tab.settings.block.button.add"));
		lblManualBlock.setText(t.getString("program.settings.tab.settings.block.label.manual"));

	}

	/**
	 * If a channel is blocked, the label and the context menu text change
	 * 
	 * @param blocked     - true if the channel is blocked
	 * @param notChatroom - true if the channel is not a chat-room - some
	 *                    functionalities need to be disabled
	 * @param isAdmin     - true if the local user is the admin of the chat -
	 *                    enables adding other users
	 */
	public void updateContextMenuTexts(boolean blocked, boolean notChatroom, boolean isAdmin) {

		addUserToChatroom.setDisable((notChatroom || !isAdmin));
		removeUserFromChatroom.setDisable((notChatroom || !isAdmin));
		deleteChannel.setDisable(!isAdmin);
		leaveChannel.setDisable(notChatroom);

		if (blocked)
			blockChannel.setText(t.getString("program.chat.contextmenu.unblock"));
		else
			blockChannel.setText(t.getString("program.chat.contextmenu.block"));
	}

	public void refreshView() {
		textIn.clear();
		textArea.setScrollTop(Double.MAX_VALUE);
	}

	public void scrollDown() {

		/*
		 * textArea.setScrollTop(Double.MAX_VALUE);
		 *
		 * Does not work, therefore ugly workaround below. Please don't punish me. I'm
		 * not happy either..
		 */

		textArea.appendText("");
		Platform.runLater(() -> textArea.setScrollTop(Double.MAX_VALUE));

	}

}