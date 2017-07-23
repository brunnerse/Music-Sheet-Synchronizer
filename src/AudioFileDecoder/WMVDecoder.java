package AudioFileDecoder;

import java.io.*;
import java.nio.file.*;

import javax.sound.sampled.*;

public class WMVDecoder {

	public static boolean setTimeStamp = false;
	public static double startSecond = 5;
	public static void main(String[] args) {
		final String fileName = "all about you.wav", outFileName;
		System.out.println(ClassLoader.getSystemResource(fileName));
		try {
			AudioSystem.getAudioInputStream(ClassLoader.getSystemResource(fileName));
		} catch (UnsupportedAudioFileException | IOException e) {
			System.err.println(e.getMessage());
		}

		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(ClassLoader.getSystemResource("Rule the world.wav"));
			System.out.println("AudioFormat: " + audioInputStream.getFormat());
			outFileName = fileName + " - decoded.txt";
			//decode_freq_amp(audioInputStream, outFileName, 60, 5);
			System.out.println("Finished decoding Amplitude and Frequency, decoding raw values now...");
			decode_values(audioInputStream, outFileName, startSecond, 2);
			System.out.println("Writing process finished, saved in file " + outFileName);
		} catch (Exception e) {
			System.err.println("Decoding failed: " + e.getMessage());
		}

	}

	private static void decode_values(AudioInputStream stream, String filename, double startSec, double durationSecs)
			throws IOException {
		OutputStream outFile = Files.newOutputStream(new File(filename).toPath(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		AudioFormat af = stream.getFormat();
		final int bufSize = 1024;
		final int bytesPerSample = (int)Math.ceil(af.getSampleSizeInBits() / 8d);
		byte[] sampleList = new byte[bufSize];
		int numSkipSamples = (int) Math.ceil(af.getSampleRate() * startSec);
		int numSamples = (int) Math.ceil(af.getSampleRate() * durationSecs);
		int readBytes = 1;
		double sekStamp = 0.0;
		double sekStampStep = 0.25;
		System.out.println("Skipping " + numSkipSamples + " Samples and writing " + numSamples + " Samples");
		// Zur eigentlich zu lesenden Stelle springen
		for (numSkipSamples *= bytesPerSample; numSkipSamples > 0 && readBytes > 0; numSkipSamples -= bufSize)
			readBytes = stream.read(sampleList, 0, bufSize);
		//Nach und nach Stücke des Streams einlesen und abarbeiten
		for (numSamples *= bytesPerSample; numSamples  > 0 && readBytes > 0; numSamples -= readBytes) {
			readBytes = stream.read(sampleList, 0, bufSize);
			if (af.getSampleSizeInBits() == 8 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
				for (int i = 0; i < sampleList.length; i += 2) {
					if (i % af.getFrameSize() == 0)
						outFile.write("\t".getBytes());
					if (i % 32 == 0)
						outFile.write("\n".getBytes());
					outFile.write((String.format("%+4d\t", sampleList[i])).getBytes());
				}
			} else if (af.getSampleSizeInBits() == 16 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
				for (int i = 0; i < sampleList.length; i += 2) {
					short val = (short) sampleList[i];
					val += ((short) sampleList[i + 1] << 8); //Little Endian struktur
					if (i % af.getFrameSize() == 0)
						outFile.write("\t".getBytes());
					if (i % 32 == 0)
						outFile.write("\n".getBytes());
					outFile.write((String.format("%+6d\t", val)).getBytes());
				}
			} else {
				System.err.println("ERROR: AudioFormat not supported");
				break;
			}
			if (setTimeStamp && numSamples % (af.getSampleRate() * sekStampStep * bytesPerSample) < bufSize) {
				sekStamp += sekStampStep;
				System.out.println(sekStamp);
				outFile.write(String.format("\n%.2f sec", sekStamp).getBytes());
			}
		}

		outFile.close();
	}

	@SuppressWarnings("unused")
	private static void decode_freq_amp(AudioInputStream stream, String filename, double startSec, double durationSecs) throws IOException {
			OutputStream outFile = Files.newOutputStream(new File(filename).toPath(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
			AudioFormat af = stream.getFormat();
			final int bufSize = 1024;
			byte[] sampleList = new byte[bufSize];
			int numSkipSamples = (int) Math.ceil(af.getSampleRate() * startSec);
			int numSamples = (int) Math.ceil(af.getSampleRate() * durationSecs);
			int readBytes = 1;
			int minVal = 0, maxVal = 0, curVal = Integer.MAX_VALUE;
			System.out.println("Skipping " + numSkipSamples + " Samples and writing " + numSamples + " Samples");
			// Zur eigentlich zu lesenden Stelle springen
			for (; numSkipSamples > 0 && readBytes > 0; numSkipSamples -= bufSize)
				readBytes = stream.read(sampleList, 0, bufSize);
			//Nach und nach Stücke des Streams einlesen und abarbeiten
			for (; numSamples > 0 && readBytes > 0; numSamples -= readBytes) {
				readBytes = stream.read(sampleList, 0, bufSize);
				if (af.getSampleSizeInBits() == 8 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
					for (int i = 0; i < sampleList.length; i += 2) {
						if (i % af.getFrameSize() == 0)
							outFile.write("\t".getBytes());
						if (i % 32 == 0)
							outFile.write("\n".getBytes());
						outFile.write((String.format("%+4d\t", sampleList[i])).getBytes());
					}
				} else if (af.getSampleSizeInBits() == 16 && af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && !af.isBigEndian()) {
					for (int i = 0; i < sampleList.length; i += 2) {
						short val = (short) sampleList[i];
						val += ((short) sampleList[i + 1] << 8); //Little Endian struktur
						//TODO evtl: vllt zu kompliziert
						//Neue Periode messen
						if (curVal == Integer.MAX_VALUE) {
							curVal = val;
							minVal = val;
							maxVal = val;
						} else if (val < minVal) {
							minVal = val;
						} else if (val > maxVal) {
							maxVal = val;
						}
						
					}
				} else {
					System.err.println("ERROR: AudioFormat not supported");
				}
			}

			outFile.close();
	}
}
