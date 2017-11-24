package TestPrograms.Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

//This doesnt work yet because the input signal is too delayed.
public class Noise_Canceller {

	public static void main(String args[]) {
		final int sampleSize = 2;
		AudioFormat format = new AudioFormat(44100, sampleSize * 8, 1, true, false);
		byte b[], b1[] = new byte[sampleSize * 50], b2[] = new byte[sampleSize * 50];
		int i;
		try {
			TargetDataLine tLine = AudioSystem.getTargetDataLine(format);
			SourceDataLine sLine = AudioSystem.getSourceDataLine(format);
			
			tLine.open();
			sLine.open();
			
			tLine.start();
			sLine.start();
			
			//float DCOffset = getDCOffset(tLine);
			
			b = b1;
			tLine.read(b, 0, b.length);
			while (true) {
				Thread t = new Thread(new LineWriter(sLine, b));
				t.start();
				if (b == b1)
					b = b2;
				else
					b = b1;
				tLine.read(b, 0,  b.length);
				
				for (int idx = 0; idx < b.length; idx += sampleSize) {
					i = b[idx] | (b[idx + 1] << 8);
					//i = 2 * (int)(DCOffset * b.length / sampleSize) - i; //DCOffset - (i - DCOffset)
					i = -i;
					b[0] = (byte)(i & 0xff);
					b[1] = (byte)((i & 0xff00) >> 8);
					
				}

				try { 
					t.join();
				} catch (InterruptedException e) {}
			}
			
		} catch (LineUnavailableException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	private static class LineWriter implements Runnable {
		private SourceDataLine line;
		private byte[] b;
		public LineWriter(SourceDataLine l, byte[] b) {
			this.line = l;
			this.b = b;
		}
		public void run() {
			line.write(b, 0, b.length);
		}
		
	}
	
	public static float getDCOffset(TargetDataLine line) {
		final int len = 100;
		byte[] b = new byte[len * line.getFormat().getFrameSize()];
		line.read(b,  0,  b.length);
		float sum = 0;
		for (int i = 0; i < len; i += 2) {
			sum += (float)(short)(b[i] | (b[i + 1] << 8));
		}
		return sum / len;
	}
	
}
