package AudioFile;

import java.io.*;
import java.nio.file.*;

import javax.sound.sampled.*;

public class WavePattern_Decoder {

	public static boolean setTimeStamp = false;
	private static byte[] sampleList;
	private static int sampleSizeinBits = 16;
	private static int sampleSize = 2;
	private static int channels = 2;
	private static float sampleRate = 44100;
	private static int seconds = 2;
	
	public static void main(String[] args) {
		sampleList = new byte[(int) (sampleRate * seconds)];
		AudioFormat af = new AudioFormat(sampleRate, sampleSizeinBits, channels, true, false);
		System.out.println("Creating samples...");
		createSampleList();
		System.out.println("Finished. Writing to file now...");
		try {
			decode_values(sampleList, af, "decoded_wavepattern.txt", 0, seconds);
		} catch (IOException e) {
			System.err.println("ERROR while writing to File");
		}
		System.out.println("Finished.");

	}

	private static void decode_values(byte[] bStream, AudioFormat af, String filename, double startSec, double durationSecs)
			throws IOException {
		OutputStream outFile = Files.newOutputStream(new File(filename).toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		final int bufSize = 1024;
		final int bytesPerSample = (int)Math.ceil(af.getSampleSizeInBits() / 8d);
		int numSamples = (int) Math.ceil(af.getSampleRate() * durationSecs);
		double sekStamp = 0.0;
		double sekStampStep = 0.25;
			if (af.getSampleSizeInBits() == 8 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
				for (int i = 0; i < bStream.length; i += 2) {
					if (i % af.getFrameSize() == 0)
						outFile.write("\t".getBytes());
					if (i % 32 == 0)
						outFile.write("\n".getBytes());
					outFile.write((String.format("%+4d\t", bStream[i])).getBytes());
				}
			} else if (af.getSampleSizeInBits() == 16 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
				for (int i = 0; i < bStream.length; i += 2) {
					short val = (short) bStream[i];
					val += ((short) bStream[i + 1] << 8); //Little Endian struktur
					if (i % af.getFrameSize() == 0)
						outFile.write("\t".getBytes());
					if (i % 32 == 0)
						outFile.write("\n".getBytes());
					outFile.write((String.format("%+6d\t", val)).getBytes());
				}
			} else {
				System.err.println("ERROR: AudioFormat not supported");
				return;
			}
			if (setTimeStamp && numSamples % (af.getSampleRate() * sekStampStep * bytesPerSample) < bufSize) {
				sekStamp += sekStampStep;
				System.out.println(sekStamp);
				outFile.write(String.format("\n%.2f sec", sekStamp).getBytes());
			}

		outFile.close();
	}

	private static void createSampleList() {
		double sin;
		WaveManager wm[] = new WaveManager[2];
		final double amplitude = 0.5;
		wm[0] = new WaveManager(440, amplitude, sampleRate);
		wm[1] = new WaveManager(880, amplitude, sampleRate);
		//wm[2] = new WaveManager(1760, amplitude, sampleRate);
		final int valueRange = (int)(Math.pow(2,  sampleSizeinBits - 1) - 1);
		int sinVal;
		for (int i = 0; i < sampleList.length; i += sampleSize * channels) {
			sin = 0;
			int curSample = i / (sampleSize * channels);
			for(int idx = 0; idx < wm.length; ++idx) {
				sin += Math.sin(2 * Math.PI * curSample / wm[idx].samplesPerPeriod) * wm[idx].amplitude;
			}
			//Left Sample
			sinVal = limitVal((int)(sin * valueRange), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
			//Right Sample
			sinVal = limitVal((int)(sin * valueRange * 1), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + 2 + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
		}
		
	}
	
	private static int limitVal(int val, int min, int max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}
	
	public static class WaveManager {
		@SuppressWarnings("unused")
		public double frequency;
		public double amplitude;
		public double samplesPerPeriod;
		
		public WaveManager(double frequency, double amplitude, double sampleRate) {
			this.frequency = frequency;
			this.amplitude = amplitude;
			this.samplesPerPeriod = sampleRate / frequency;
		}
	}
}
