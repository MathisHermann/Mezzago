package mezzago.commonClasses.message;

public class Mezzago_ResultMessage extends Mezzago_Message {
	String command;
	boolean result;
	String content;

	public Mezzago_ResultMessage(String[] parts) {
		super(parts[0]);
		
		this.command = parts[1];
		this.result = parts[2].equals("true");
		
		if (parts.length > 3) {
			this.content = parts[3];
		}
		
	//	System.out.println("New ResultMessage: " + command + ", " + result + ", " + content);
		
	}

	public String getContent() {
		return content;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public String getCommand() {
		return command;
	}
	
	@Override
	public String toString() {
		return type + ": " + command + ", " + result + (content == null ? "" : content);
	}

}
