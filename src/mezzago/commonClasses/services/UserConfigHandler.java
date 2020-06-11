package mezzago.commonClasses.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserConfigHandler {

	/**
	 * Read in the blocked list
	 * 
	 * @param localUser
	 * @return
	 */
	public static String[] readUserConfig(String localUser) {
		String[] userConfig = new String[2];

		// load all blocked channels
		String contentAll = "none";
		try {

			String path = "channelStorage" + File.separator + localUser + File.separator + "userConfig.txt";

			File userConfigFile = new File(path);
			boolean exists = userConfigFile.exists();

			if (exists) {
				contentAll = new String(Files.readAllBytes(Paths.get(path)));
				String[] content = contentAll.split("\n");
				

				// usually the order of the items is correct, but to be sure, it is checked
				// -> first enable/disable then sound type
				if (content[0].equals("true") | content[0].equals("false")) {
					userConfig[0] = content[0];
					userConfig[1] = content[1];
				} else {
					userConfig[0] = content[1];
					userConfig[1] = content[0];
				}
			}

		} catch (IOException e) {
		}

		return userConfig;
	}

	/**
	 * Write the blocked list into the corresponding file according to the local
	 * user
	 * 
	 * @param localUser
	 * @param blockedChannels
	 */
	public static void writeUserConfig(String localUser, String[] userConfig) {

		String path = "channelStorage" + File.separator + localUser + File.separator + "userConfig.txt";

		try {
			FileWriter myWriter = new FileWriter(path);

			for (String s : userConfig)
				myWriter.write(s + "\n");

			myWriter.close();

		} catch (IOException e) {
		}
	}

}
