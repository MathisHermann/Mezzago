package mezzago.commonClasses.message;

public enum MessageType {

	// client -> server
	CREATELOGIN {
		public String messageType() {
			return "CreateLogin";
		}
	},
	LOGIN {
		public String messageType() {
			return "Login";
		}
	},
	CHANGEPASSWORD {
		public String messageType() {
			return "ChangePassword";
		}
	},
	DELETELOGIN {
		public String messageType() {
			return "DeleteLogin";
		}
	},
	LOGOUT {
		public String messageType() {
			return "Logout";
		}
	},
	CREATECHATROOM {
		public String messageType() {
			return "CreateChatroom";
		}
	},
	JOINCHATROOM {
		public String messageType() {
			return "JoinChatroom";
		}
	},
	LEAVECHATROOM {
		public String messageType() {
			return "LeaveChatroom";
		}
	},
	DELETECHATROOM {
		public String messageType() {
			return "DeleteChatroom";
		}
	},
	LISTCHATROOMS {
		public String messageType() {
			return "ListChatrooms";
		}
	},
	PING {
		public String messageType() {
			return "Ping";
		}
	},
	SENDMESSAGE {
		public String messageType() {
			return "SendMessage";
		}
	},
	USERONLINE {
		public String messageType() {
			return "UserOnline";
		}
	},
	LISTCHATROOMUSERS {
		public String messageType() {
			return "ListChatroomUsers";
		}
	},
	RESULT{
		@Override
		public String messageType() {
			return "Result";
		}
	},
	MESSAGEERROR{
		@Override
		public String messageType() {
			return "MessageError";
		}
	},
	MESSAGETEXT{
		@Override
		public String messageType() {
			return "MessageText";
		}
	};
	
	public abstract String messageType();
	
}