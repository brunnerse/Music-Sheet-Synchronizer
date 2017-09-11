package testing_and_notes;

import javax.sound.sampled.*;

public class Music_Playback{
	private static SourceDataLine line;
	private static LineHandler lh;
	//This only works with .wav Files.
	public static void main(String[] args) throws LineUnavailableException {
		byte sampleList[];
		final String fileName = "all about you.wav";
		System.out.println(ClassLoader.getSystemResource(fileName));
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(fileName));
			line = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
			System.out.println("AudioFormat: " + audioInputStream.getFormat());
			AudioFormat af = audioInputStream.getFormat();
			line.open(audioInputStream.getFormat(), 2600);
			lh = new LineHandler();
			line.addLineListener(lh);
			sampleList = new byte[(int)af.getSampleRate()];
			
			line.start();
			int i = 0;
			while (audioInputStream.read(sampleList, 0, 2600) > 0) {
				line.write(sampleList, 0, 2600);
				++i;
			}
			System.out.println("Finished. Read the buffer " + i + " times.");
			line.close();
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
	
	private static class LineHandler implements LineListener{
		@Override
		public void update(LineEvent e) {
			if (e.getLine().equals(line)) {
				if (e.getType().equals(LineEvent.Type.STOP)) {
					System.out.println("Audio finished playing.");
				}
			}
		}
	}
	
}
