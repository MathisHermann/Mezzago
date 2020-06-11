package mezzago.commonClasses.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import mezzago.commonClasses.channels.*;

public class ObjectWriter {
	
	public ObjectWriter(Mezzago_Channels channels, String localUser) {
		try {
			FileOutputStream f = new FileOutputStream(new File("channelStorage" + File.separator + localUser + File.separator + "mezzagoObjects.txt"));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			o.writeObject(channels);
			

			o.close();
			f.close();

			

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	
	}
		
	
}

