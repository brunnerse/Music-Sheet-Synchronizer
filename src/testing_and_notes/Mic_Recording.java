package testing_and_notes;


import javax.sound.sampled.*;

public class Mic_Recording {
	private static final double sampleRate = 20000d;
	private static final double seconds = 5d;
	private static final int sampleSize = 2;
	
	public static void main(String args[]) {
		TargetDataLine line;
		SourceDataLine outLine;
		byte[] b = new byte[(int)(sampleSize * sampleRate * seconds)];
		AudioFormat format = new AudioFormat(20000, 16, 1, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("ERROR: Das Audioformat " + format.toString() + "\nwird nicht unterstützt.");
			return;
		}
		
		
		try {
			outLine = AudioSystem.getSourceDataLine(format);
			line = AudioSystem.getTargetDataLine(format);
			line.open(format);
			line.start();
			System.out.println("Recording now...");
			line.read(b, 0, b.length);
			System.out.println("Recording finished. Playing audio in 3 seconds...");
			Thread.sleep(3000);
			//playback slightly faster
			outLine.open(new AudioFormat(25000, 16, 1, true, false));
			System.out.println("Playing...");
			outLine.start();
			outLine.write(b, 0, b.length);
			line.close();
			outLine.close();
		} catch (LineUnavailableException ex) {
			System.err.println("line nicht verfügbar: " + ex.getMessage());
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("finished.");
	}
}
