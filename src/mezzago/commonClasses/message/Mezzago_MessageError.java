package mezzago.commonClasses.message;

public class Mezzago_MessageError extends Mezzago_Message {
	String command;
	String content;

	public Mezzago_MessageError(String[] parts) {
		super(parts[0]);
		
		this.content = parts[1];
		
		
	}

	public String getContent() {
		return content;
	}
	
	
	public String getCommand() {
		return command;
	}
}
