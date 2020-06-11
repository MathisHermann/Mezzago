package mezzago.commonClasses.message;

public class Mezzago_Message {
	String type;
	static int index = 0;
	int id = 0;

	public Mezzago_Message(String type) {
		this.type = type;
		this.id = index++;
	}
	
	public int getID() {
		return id;
	}
	
	public String getType() {
		return type;
	}
}
