package TestPrograms;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import FourierTransformation.FourierTransform;

public class MicAnalysis {
	private static final int sampleRate = 48000;
	private static final int sampleSize = 2;
	private static final int N = 1 << 15; // 32768

	public static void main(String args[]) {
		TargetDataLine line;
		byte[] b1 = new byte[(int) (sampleSize * N)];
		byte[] b2 = new byte[(int) (sampleSize * N)];
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			System.err.println("ERROR: Das Audioformat " + format.toString() + "\nwird nicht unterstützt.");
			return;
		}
		try {
			line = AudioSystem.getTargetDataLine(format);
			line.open(format);
			line.start();

		} catch (LineUnavailableException ex) {
			System.err.println("line nicht verfügbar: " + ex.getMessage());
			return;
		}
		line.read(b1, 0, b1.length);
		for (int i = 0; i < 100; ++i) {
			Thread reader = new ReadTargetDataLine(line, b2);
			reader.start();
			System.out.println("Loudest Freq: " + getLoudestFrequency(b1, format));
			try {
				reader.join();
			} catch (InterruptedException e) {
			}
			reader = new ReadTargetDataLine(line, b1);
			reader.start();
			System.out.println("Loudest Freq: " + getLoudestFrequency(b2, format));
			try {
				reader.join();
			} catch (InterruptedException e) {
			}
		}
		line.close();
	}

	public static int getLoudestFrequency(byte b[], AudioFormat format) {
		int sampleRate = (int) format.getSampleRate();
		float real[];
		float imag[];
		if (format.getSampleSizeInBits() == 16 && format.getChannels() == 1) {
			real = new float[N];
			imag = new float[N];
			for (int i = 0; i < b.length; i += 2) {
				real[i / 2] = (float)(b[i] | (b[i + 1] << 8));
				imag[i / 2] = 0f;
			}
		} else {
			System.err.println("Format unknown!");
			return -1;
		}
		FourierTransform.FFT(real, imag);
		float max = 0;
		int freq = -1;
		for (int i = 1; i < N; ++i) {
			if (real[i] > max) {
				max = real[i];
				freq = i;
			}
		}
		System.out.println("Highest Ampltiude: " + max / (sampleRate / 2));
		return freq * sampleRate / N;

		
	}

	private static class ReadTargetDataLine extends Thread {
		private TargetDataLine line;
		private byte[] b;

		public ReadTargetDataLine(TargetDataLine line, byte[] b) {
			this.line = line;
			this.b = b;
		}

		public void run() {
			line.read(b, 0, b.length);
		}
	}
}
