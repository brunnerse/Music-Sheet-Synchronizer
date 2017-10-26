package TestPrograms;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import AudioFile.WAVWriter;

public class MicToWAV {
	private static final float sampleRate = 44100f;
	
	public static void main(String[] s) {
		try {
			micToFile("testfile1.wav", 206f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void micToFile(String fileName, float seconds) throws IOException {
		AudioFormat f = new AudioFormat(sampleRate, 16, 1, true, false);
		WAVWriter w = new WAVWriter(fileName, f, "Born To Run", "American Authors");
		
		byte[] b1 = new byte[(int)(sampleRate * 2)];
		byte [] b2 = new byte[(int)(sampleRate * 2)];
		
		
		TargetDataLine line;
		
		try {
			line = AudioSystem.getTargetDataLine(f);
			line.open(f);
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
			w.close();
			return;
		}
		
		line.start();
		w.open();
		System.out.println("Reading Line...");
		byte[] b = b1;
		Thread tWriter = new Thread();
		
		for (; seconds > 0; seconds -= 1f) {
			int bytesToRead = (int)(Math.min(1f, seconds) * b.length);
			line.read(b, 0, bytesToRead);
			try {
				tWriter.join();
			} catch (InterruptedException e) {}
			tWriter = new Thread(new WriteToWAV(w, b, bytesToRead));
			tWriter.start();
			if (b == b1)
				b = b2;
			else
				b = b1;
		}
		try {
			tWriter.join();
		} catch (InterruptedException e) {}
		
		line.stop();
		line.close();
		w.close();
		
		System.out.println("Finished.");
	}
	
	private static class WriteToWAV implements Runnable {
		private WAVWriter w;
		private byte[] b;
		private int length;
		
		public WriteToWAV(WAVWriter w, byte[] b, int length) {
			this.w = w;
			this.b = b;
			this.length = length;
		}
		
		@Override
		public void run() {
			try {
				w.write(b, 0, length);
			} catch (IOException e) { }
		}
	}
}
