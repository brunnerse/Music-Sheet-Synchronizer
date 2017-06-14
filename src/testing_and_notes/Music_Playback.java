package testing_and_notes;

import javax.sound.sampled.*;

public class Music_Playback {
	//This only works with .wav Files.
	public static void main(String[] args) throws LineUnavailableException {
		byte b[] = new byte[2200];
		int readBytes;
		System.out.println(ClassLoader.getSystemResource("Rule the world.wav"));
		System.out.println(Music_Playback.class.getResource("Rule the world.wav"));
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("Rule the world.wav"));
			SourceDataLine line = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
			System.out.println("AudioFormat: " + audioInputStream.getFormat());
			line.open(audioInputStream.getFormat(), 2200);
			line.start();
			while ((readBytes = audioInputStream.read(b, 0, 2200)) > 0) {
				line.write(b, 0, readBytes);
				
			}
				
			line.close();
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
}
