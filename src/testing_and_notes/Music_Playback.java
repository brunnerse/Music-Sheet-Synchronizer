package testing_and_notes;

import javax.sound.sampled.*;

public class Music_Playback {
	//This only works with .wav Files.
	public static void main(String[] args) throws LineUnavailableException {
		byte sampleList[];
		final String fileName = "Rule the world.wav";
		System.out.println(ClassLoader.getSystemResource(fileName));
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(fileName));
			SourceDataLine line = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
			System.out.println("AudioFormat: " + audioInputStream.getFormat());
			AudioFormat af = audioInputStream.getFormat();
			line.open(audioInputStream.getFormat(), 2600);
			sampleList = new byte[(int)af.getSampleRate()];
			line.start();
			int i = 0;
			while (audioInputStream.read(sampleList, 0, 2600) > 0) {
				line.write(sampleList, 0, 2600);
				++i;
			}
				
			line.close();
			System.out.println("Finished. Read the buffer " + i + " times.");
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
}
