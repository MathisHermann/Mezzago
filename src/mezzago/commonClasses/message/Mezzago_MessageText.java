package mezzago.commonClasses.message;

public class Mezzago_MessageText extends Mezzago_Message {
	String name;
	String target;
	String text;

	public Mezzago_MessageText(String[] parts) {
		super(parts[0]);
		
		this.name = parts[1];
		this.target = parts[2];	
		this.text = parts[3];

		
	}

	public String getName() {
		return name;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getText() {
		return text;
	}
}
