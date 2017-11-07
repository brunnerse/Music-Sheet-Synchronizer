package TestPrograms;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import AudioFile.WAVWriter;

public class MicToWAVFile {
	
	private static final float sampleRate = 44100f;
	
	private static WAVWriter writer;
	
	private static TargetDataLine line;
	private static boolean isOpen = false;
	private static byte[] b1, b2;
	
	private static RecorderThread recorder;
	
	
	public static void openFile(String fileName) throws IOException {
		openFile(fileName, null, null);
	}
	
	public static void openFile(String fileName, String songName) throws IOException {
		openFile(fileName, songName, null);
	}
	
	
	public static void openFile(String fileName, String songName, String artist) throws IOException {
		if (isOpen)
			closeFile();
		AudioFormat f = new AudioFormat(sampleRate, 16, 1, true, false);
		writer = new WAVWriter(fileName, f, songName, artist);
		
		//always record 1/4th second
		b1 = new byte[(int)(sampleRate / 4) * f.getFrameSize()];
		b2 = new byte[(int)(sampleRate / 4) * f.getFrameSize()];
		
		try {
			line = AudioSystem.getTargetDataLine(f);
			line.open(f);
		} catch (LineUnavailableException e) {
			System.err.println(e.getMessage());
			writer.close();
			return;
		}
		
		line.start();
		writer.open();
		
		isOpen = true;	
	}
	
	public static void closeFile() throws IOException {
		if (!isOpen)
			return;
		stopRecording();
		line.stop();
		line.close();
		writer.close();
		
		isOpen = false;
	}
	
	public static void startRecording() throws Exception {
		if (!isOpen)
			throw new Exception("Must call openFile() before starting to record.");
		recorder = new RecorderThread();
		recorder.start();
	}
	
	public static void stopRecording() {
		if (recorder != null)
			recorder.stopRecording();
	}
	
	public static void recordTime(float secs, boolean asynchronous) throws Exception {
		if (!isOpen)
			throw new Exception("Must call openFile() before starting to record.");
		startRecording();
		if (asynchronous) {
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep((long)(secs * 1000));
					} catch (InterruptedException e) {}
					stopRecording();
				}
			}.start();
		} else {
			try {
				Thread.sleep((long)(secs * 1000));
			} catch (InterruptedException e) {}
			stopRecording();
		}
	}
	
	//record 1/4th of a second, then checks if stopRecording() has been called.
	private static class RecorderThread extends Thread {
		private volatile boolean stop = true;
		public void stopRecording() {
			//wait shortly stop is true; it might mean that run() hasn't been executed yet
			if(stop) {
				try { Thread.sleep(50); } catch (InterruptedException e) {}
			}
			this.stop = true;
		}
		
		@Override
		public void run() {;
			stop = false;
			
			byte[] b = b1;
			Thread tWriter = new Thread();
			while (!stop) {
				line.read(b, 0, b.length);
				try {
					tWriter.join();
				} catch (InterruptedException e) {}
				tWriter = new Thread(new WriteToWAV(writer, b, b.length));
				tWriter.start();
				if (b == b1)
					b = b2;
				else
					b = b1;
			}
			try {
				tWriter.join();
			} catch (InterruptedException e) {}
		}
		
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
