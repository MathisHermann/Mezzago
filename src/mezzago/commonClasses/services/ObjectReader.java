package mezzago.commonClasses.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import mezzago.commonClasses.channels.Mezzago_Channels;

/**
 * This class enables to read objects (from a specific file) that are stored on
 * a path that is defined by the name of the local user. This class loads the
 * all channels and the information stored in it (users, members, chat history
 * etc..)
 * 
 * @author Mathis Hermann
 *
 */
public class ObjectReader {

	public static Mezzago_Channels readAllChannels(String localUser) {

		try {
			FileInputStream fi = new FileInputStream(
					new File("channelStorage" + File.separator + localUser + File.separator + "mezzagoObjects.txt"));
			ObjectInputStream oi = new ObjectInputStream(fi);

			// Read objects
			Mezzago_Channels channels = (Mezzago_Channels) oi.readObject();

			oi.close();
			fi.close();

			return channels;

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		return null;

	}

}
