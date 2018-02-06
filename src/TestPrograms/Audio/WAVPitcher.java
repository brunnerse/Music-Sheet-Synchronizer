package TestPrograms.Audio;

import java.io.IOException;

import AudioFile.WAVReader;
import AudioFile.WAVWriter;
import FourierTransformation.FourierTransform;
import Tools.ArrayConversion;

public class WAVPitcher {
	static float pitchFactor = 1.2f;

	public static void main(String args[]) {
		try {
			//WAVReader r = new WAVReader("temp - sheetfile.wav");
			WAVReader r = new WAVReader("brain mire.wav");
			r.open();
			System.out.println(r.getFormat());
			WAVWriter w = new WAVWriter("brain mire - pitched.wav", r.getFormat());
			w.open();
			int samples = 1 << 18;
			int numChannels = r.getFormat().getChannels();
			int sampleSize = r.getFormat().getSampleSizeInBits() / 8;
			byte b[] = new byte[numChannels * sampleSize * samples];
			float real[] = new float[samples];
			float img[] = new float[samples];

			int n = r.read(b, 0, b.length);
			do {
				if (n != b.length)
					break;
				long startTime;
				int readSamples = n / numChannels / sampleSize;
				System.out.println(b.length + "\t" + n + "\t" + readSamples);
				for (int channel = 0; channel < r.getFormat().getChannels(); ++channel) {
					// Read bytes into float array
					for (int i = 0; i < readSamples; ++i) {
						if (sampleSize == 2)
							real[i] = ArrayConversion.reinterpByteToShort(b[2 * (numChannels * i + channel)],
									b[2 * (numChannels * i + channel) + 1]);
						else if (sampleSize == 1)
							real[i] = b[numChannels * i + channel];
						else {
							System.err.println("ERROR: sample Size not supported.");
							return;
						}
						img[i] = 0f;
					}
					
					startTime = System.nanoTime();
					float[] real_out = null, img_out = null;
					// Convert them into Frequency domain
					if (readSamples == samples) // Can use FFT only if readSamples == 2^s
						FourierTransform.FFT(real, img);
					else {
						real_out = new float[readSamples];
						img_out = new float[readSamples];
						FourierTransform.ComplexDFT(real, img, real_out, img_out, readSamples);
						//swap real and real_out
						float [] f;
						f = real;
						real = real_out;
						real_out = f;
						f = img;
						img = img_out;
						img_out = f;
					}
					System.out.print("Fourier Transformation completed in " + (System.nanoTime() - startTime) / 1000000 + "ms\t");
					
					if (pitchFactor > 1) {
						for (int i = readSamples - 1; i > 0; --i) {
							int targetIdx = (int) Math.floor(i * pitchFactor);
							if (targetIdx == i)
								continue;
							if (targetIdx < readSamples) {
								real[targetIdx] += real[i];
								img[targetIdx] += img[i];
							}
							real[i] = 0f; //set Value to 0, as real[i] and img[i] will be overwritten later by another targetIdx
							img[i] = 0f;
						}
					} else {
						for (int i = 1; i < readSamples; ++i) {
							int targetIdx = (int) Math.floor(i * pitchFactor);
							if (targetIdx == i)
								continue;
							if (targetIdx > 0) { //dont overwrite 0 as its the DC Offset
								real[targetIdx] += real[i];
								img[targetIdx] += img[i];
							}
							real[i] = 0f; //set Value to 0, as real[i] and img[i] will be overwritten later by another targetIdx
							img[i] = 0f;
						}
					}
					
					startTime = System.nanoTime();
					if (readSamples == samples)
						FourierTransform.InverseFFT(real,  img);
					else {		
						FourierTransform.InverseComplexDFT(real,img, real_out, img_out, readSamples);
						real = real_out;
						img = img_out;
					}
					System.out.println("Inverse Fourier Transformation completed in " + (System.nanoTime() - startTime) / 1000000 + "ms");
					//Transfer data back into array
					for (int i = 0; i < readSamples; ++i) {
						if (sampleSize == 2)
							ArrayConversion.reinterpShortToByte((short)real[i], b, 2 * (numChannels * i + channel));
						else if (sampleSize == 1)
							b[numChannels * i + channel] = (byte)real[i];
						else {
							System.err.println("ERROR: sample Size not supported.");
							return;
						}
					}
				}
				w.write(b, 0, n);
				n = r.read(b, 0, b.length);
			} while (n > 0);
			System.out.println("Terminating...");
			r.close();
			w.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
}
