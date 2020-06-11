package mezzago.commonClasses.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BlockedListHandler {

	/**
	 * Read in the blocked list
	 * 
	 * @param localUser
	 * @return
	 */
	public static ArrayList<String> readBlockedChannelsList(String localUser) {
		ArrayList<String> blockedChannels = new ArrayList<>();

		// load all blocked channels
		String content = "none";
		try {

			String path = "channelStorage" + File.separator + localUser + File.separator + "blockedChannels.txt";

			File blockedList = new File(path);
			boolean exists = blockedList.exists();

			if (exists) {
				content = new String(Files.readAllBytes(Paths.get(path)));

				String[] blocked = content.split("\n");

				if (blocked.length > 0) {
					for (String s : blocked)
						if (s.length() >= 3)
							blockedChannels.add(s);
				}
			}

		} catch (IOException e) {
		}

		return blockedChannels;
	}

	/**
	 * Write the blocked list into the corresponding file according to the local
	 * user
	 * 
	 * @param localUser
	 * @param blockedChannels
	 */
	public static void writeBlockedChannelsList(String localUser, ArrayList<String> blockedChannels) {

		String path = "channelStorage" + File.separator + localUser + File.separator + "blockedChannels.txt";

		try {
			FileWriter myWriter = new FileWriter(path);

			for (String s : blockedChannels)
				myWriter.write(s + "\n");

			myWriter.close();

		} catch (IOException e) {
		}
	}
}
