package mezzago.commonClasses.message;

public enum MessageTypeIn {
	
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
