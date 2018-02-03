package Music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import GUI.AbstractFrequencyDisplay;
import GUI.AudioAnalysisWindow;
import AudioFile.WAVReader;
import FourierTransformation.FourierTransform;

public class Characteristic_Analyser {

	private static byte[] b = new byte[2 * (1 << 16)];
	private static float[] f = new float[(1 << 16)], img = new float[(1 << 16)];
	
	private static AudioFormat format;

	public static void main(String[] args) {

		WAVReader w = new WAVReader("piano - a1.wav");
		try {
			w.open();
			w.read(b, 0, b.length);
			format = w.getFormat();
			w.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		/*
		 * try { format = new AudioFormat(41000, 16, 1, true, false); TargetDataLine
		 * line = AudioSystem.getTargetDataLine(format); line.open(); line.start();
		 * line.read(b, 0, b.length); line.stop(); line.close(); } catch
		 * (LineUnavailableException e) { System.out.println(e.getMessage()); }
		 */

		AudioAnalysisWindow window = new AudioAnalysisWindow(
				new StaticFrequencyDisplay(0.5f, 0, 5000, 800, 800, b, format));
		SourceDataLine sLine;
		try {
			sLine = AudioSystem.getSourceDataLine(format);
			sLine.open();
		} catch (LineUnavailableException e) {
			System.out.println(e.getMessage());
			return;
		}
		sLine.start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		int x = 1 | 3;
		while (x != 0) {
			try {
				input = br.readLine();
			} catch (IOException e) {
				continue;
			}

			if (input.contains("play")) {
				sLine.write(b, 0, b.length);
			} else {
				for (int i = 0; i < f.length; ++i) {
					img[i] = 0f;
					f[i] = (float)(b[2 * i] | (b[2 * i + 1] << 8));
				}
				
				FourierTransform.FFT(f, img);
				
				if (input.contains("set")) {
					float val = Float.valueOf(input.substring(input.indexOf('=') + 2));
					int idx = Integer.valueOf(input.substring(4, input.indexOf(' ', 4)));
					int endIdx = input.indexOf("to");
					if (endIdx >= 0) {
						endIdx = Integer.valueOf(input.substring(endIdx + 3, input.indexOf(' ', endIdx + 3)));
					} else {
						endIdx = idx;
					}
					System.out.println("setting values from " + idx + " to " + endIdx + " to Value " + val);
					idx = (int)(idx / window.getFrequencyDisplay().getPrecision());
					endIdx = (int)(endIdx / window.getFrequencyDisplay().getPrecision());
					for (; idx <= endIdx; ++idx) {
						f[idx] = val;
						img[idx] = val;
					}
				} else if (input.contains("read")) {
					int idx = input.indexOf(' ', 5);
					if (idx < 0)
						idx = input.length();
					idx = Integer.valueOf(input.substring(5, idx));
					int endIdx = input.indexOf("to");
					if (endIdx >= 0) {
						endIdx = Integer.valueOf(input.substring(endIdx + 3, input.length()));
					} else {
						endIdx = idx;
					}
					System.out.println("Printing values from " + idx + " to " + endIdx);
					idx = (int)(idx / window.getFrequencyDisplay().getPrecision());
					endIdx = (int)(endIdx / window.getFrequencyDisplay().getPrecision());
					for (; idx <= endIdx; idx++) {
						System.out.printf("%d: %.1f, %.1f\n", idx, f[idx], img[idx]);
					}
				}
				
				FourierTransform.InverseFFT(f,  img);
				for (int i = 0; i < f.length; ++i) {
					b[2 * i] = (byte)((int)f[i] & 0xff);
					b[2 * i + 1] = (byte)(((int)f[i] & 0xff00) >> 8);
				}
				
			}

		}

		sLine.close();
	}

	@SuppressWarnings("serial")
	private static class StaticFrequencyDisplay extends AbstractFrequencyDisplay<byte[]> {

		int dataIdx = 0;

		public StaticFrequencyDisplay(float precision, int minFrequency, int maxFrequency, int width, int height,
				byte[] b, AudioFormat f) {
			super(precision, minFrequency, maxFrequency, AbstractFrequencyDisplay.INTERVAL.LONGEST, width, height);
			this.line = b;
			this.format = f;
		}

		@Override
		protected void readLine(byte[] data, int off, int len) {
			for (int i = 0; i < len; ++i, ++dataIdx) {
				if (dataIdx >= line.length) {
					dataIdx = 0;
				}
				if (i + off >= line.length)
					off = -i;
				data[i + off] = line[dataIdx];
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}

		@Override
		protected void openLine() throws Exception {
		}

		@Override
		protected void closeLine() {
		}

		@Override
		protected void setupLineAndFormat() throws Exception {
		}


	}
}
