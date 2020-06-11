package mezzago.commonClasses.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class PlaySound extends Thread {

	private String sound;
	
	public PlaySound(String sound) {
		super("Notification " + sound);
		this.sound = sound;
		this.start();
	}
	
	@Override
	public void run() {
		InputStream in;
		try {
			in = new FileInputStream("src/main_v2/resources/sounds/Notification_" + sound + ".wav");

			// create an audiostream from the inputstream
			AudioStream audioStream = new AudioStream(in);

			// play the audio clip with the audioplayer class
			AudioPlayer.player.start(audioStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}
