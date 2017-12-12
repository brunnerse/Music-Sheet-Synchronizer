package AudioFile;

import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class WAVPlayer {
	private static final int BUFSIZE = 20000;

	public static void play(String fileName) throws LineUnavailableException, IOException {
		int bytesRead;
		WAVReader wp = new WAVReader(fileName);
		wp.open();
		SourceDataLine line = AudioSystem.getSourceDataLine(wp.getFormat());
		byte[] b, b1 = new byte[BUFSIZE], b2 = new byte[BUFSIZE];
		line.open();
		line.start();
		System.out.println("Playing Song " + wp.getName() + 
				" from " + wp.getArtist() + "\nwith Format " + wp.getFormat());

		b = b1;
		bytesRead = wp.read(b, 0, b.length);
		while (bytesRead > 0) {
			Thread tWrite = new Thread(new LineWriterThread(line, b, bytesRead));
			tWrite.start();
			if (b == b1)
				b = b2;
			else
				b = b1;
			bytesRead = wp.read(b, 0, b.length);
			try {
				tWrite.join();
			} catch (InterruptedException e) {
			}
		}
		line.drain();
		System.out.println("Song finished.");
		line.close();
		wp.close();
	}

	private static class LineWriterThread implements Runnable {
		private byte[] b;
		private SourceDataLine l;
		private int len;

		public LineWriterThread(SourceDataLine l, byte[] b, int len) {
			this.l = l;
			this.b = b;
			this.len = len;
		}

		@Override
		public void run() {
			l.write(b, 0, len);
		}
	}

}
