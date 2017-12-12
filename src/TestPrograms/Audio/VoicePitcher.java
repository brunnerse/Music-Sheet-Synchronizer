package TestPrograms.Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import Tools.ArrayConversion;
import FourierTransformation.FourierTransform;

public class VoicePitcher {
	public static void main(String args[]) {
		AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
		System.out.println("Recording...");
		byte[] b = record(12, format);
		System.out.println("Recording finished.");
		higherPitch(100, b, format);
		System.out.println("Playing...");
		play(b, format);
		System.out.println("Playing finished.");
	}
	
	
	public static byte[] record(float secs, AudioFormat format) {
		try {
			TargetDataLine line = AudioSystem.getTargetDataLine(format);
			byte[] b = new byte[(int)(format.getSampleRate() * 2 * secs)];
			
			line.open();
			line.start();
			
			line.read(b, 0, b.length);
			line.stop();
			line.close();
			return b;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void play(byte[] b, AudioFormat format) {
		try {
			SourceDataLine line = AudioSystem.getSourceDataLine(format);
			
			line.open();
			line.start();
			
			line.write(b, 0, b.length);
			line.stop();
			line.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static void higherPitch(float freqShift, byte[] b, AudioFormat format) {
		int e = 1;
		while (1 << e <= b.length / 2)
			++e;
		--e;
		int length = 1 << e;
		float[] real = new float[length], imag = new float[length];
		
		for (int i = 0; i < real.length; ++i) {
			short s = ArrayConversion.reinterpByteToShort(b[i * 2], b[i * 2 + 1]);
			real[i] = (float)s;
			imag[i] = 0;
		}
		
		FourierTransform.FFT(real, imag);
		
		int idxShift = (int)(freqShift * real.length / format.getSampleRate());
		
		for (int i = real.length - 1 - idxShift; i >= 0; --i) {
			real[i + idxShift] = real[i];
			imag[i + idxShift] = imag[i];
		}
		for (int i = 0; i < idxShift; ++i) {
			real[i] = 0f;
			imag[i] = 0f;
		}
		
		FourierTransform.InverseFFT(real,  imag);
		for (int i = 0; i < real.length; ++i) {
			short s = (short)real[i];
			ArrayConversion.reinterpShortToByte(s, b, i * 2);
		}
	}
	
}
