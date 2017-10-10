package FourierTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.lang.Math;
import java.nio.file.*;

public class FourierTesting {

	public static int N = 32;
	public static final int MaxN = 1048576; // 1 >> 20

	public static void main(String args[]) {
		FunctionTesting();
		//SpeedTesting();
	}

	@SuppressWarnings("unused")
	private static void SpeedTesting() {
		final int backupN = N;
		float valueList[];
		float imaginaryVals[];
		for (int n = N; n <= MaxN; n <<= 1) {
			System.out.println("\n\n===\nTesting speed for " + n + " values...\n===\n\n");
			System.out.println("Creating time domain...");
			N = n;
			valueList = analyse_FrequencySpectrum();
			imaginaryVals = new float[n];
			for (int i = 0; i < n; ++i)
				imaginaryVals[i] = 0;
			float CosVals[] = new float[N];
			float SinVals[] = new float[N];
			
			System.out.print("Testing Complex DFT... ");
			long startTime = System.currentTimeMillis();
			FourierTransform.ComplexDFT(valueList, imaginaryVals, CosVals, SinVals);
			System.out.printf("Finished in %.3f seconds.\n", (System.currentTimeMillis() - startTime) / 1000f);
			System.out.print("Testing Inverse Complex FFT... ");
			startTime = System.currentTimeMillis();
			FourierTransform.InverseComplexDFT(valueList, imaginaryVals, CosVals, SinVals);
			System.out.printf("Finished in %.3f seconds.\n", (System.currentTimeMillis() - startTime) / 1000f);
			
			System.out.print("\nTesting FFT Cooley-Tukey algorithm... ");
			startTime = System.currentTimeMillis();
			FourierTransform.FFT(valueList,  imaginaryVals);
			System.out.printf("Finished in %.3f seconds.\n", (System.currentTimeMillis() - startTime) / 1000f);
			System.out.print("Testing Inverse FFT algorithm... ");
			startTime = System.currentTimeMillis();
			FourierTransform.InverseFFT(valueList,  imaginaryVals);
			System.out.printf("Finished in %.3f seconds.\n", (System.currentTimeMillis() - startTime) / 1000f);
		}
		N = backupN;
	}
	
	@SuppressWarnings("unused")
	private static void FunctionTesting() {
		float[] valueList = FourierTesting.analyse_FrequencySpectrum();
		System.out.println("Testing Fourier Transformation for wave 12 hz / 8hz ...");
		System.out.print("values: ");
		printArray(valueList);
		
		
		System.out.println("\nStarting Complex DFT for " + N + " values...");
		long startTime = System.currentTimeMillis();
		float imaginaryVals[] = new float[N];
		for (int i = 0; i < N; ++i)
				imaginaryVals[i] = 0f;
		float CosVals[] = new float[N];
		float SinVals[] = new float[N];
		FourierTransform.ComplexDFT(valueList, imaginaryVals, CosVals, SinVals);
		System.out.printf("Finished in %.3f seconds. Results: ", (System.currentTimeMillis() - startTime) / 1000f);
		printDomain(CosVals, SinVals, N);
		
		System.out.println("\nTransforming back...");
		startTime = System.currentTimeMillis();
		FourierTransform.InverseComplexDFT(CosVals, SinVals, valueList, imaginaryVals);
		System.out.printf("Finished in %.3f seconds. Values: \n", (System.currentTimeMillis() - startTime) / 1000f);
		printArray(valueList);
		printArray(imaginaryVals);
		
		System.out.println("\nStarting FFT for " + N + " values...");
		startTime = System.currentTimeMillis();
		FourierTransform.FFT(valueList, imaginaryVals);
		System.out.printf("Finished in %.3f seconds. Values: \n", (System.currentTimeMillis() - startTime) / 1000f);
		printDomain(valueList, imaginaryVals, N);
		
		System.out.println("\nTransforming back...");
		startTime = System.currentTimeMillis();
		FourierTransform.InverseFFT(valueList, imaginaryVals);
		System.out.printf("Finished in %.3f seconds. Values: \n", (System.currentTimeMillis() - startTime) / 1000f);
		printArray(valueList);
		printArray(imaginaryVals);
		/*
		valueList = analyse_FrequencySpectrum();
		System.out.println("\n\nStarting Real DFT for " + N + " values...");
		startTime = System.currentTimeMillis();
		FourierTransform.RealDFT(valueList, CosVals, SinVals);
		System.out.printf("Finished in %.3f seconds. Results: ", (System.currentTimeMillis() - startTime) / 1000f);
		printDomain(CosVals, SinVals, N/2 + 1);
		
		System.out.println("\nTransforming back...");
		startTime = System.currentTimeMillis();
		FourierTransform.InverseRealDFT(CosVals, SinVals, valueList, N);
		System.out.printf("Finished in %.3f seconds. Values: [\n", (System.currentTimeMillis() - startTime) / 1000f);
		printArray(valueList);
		*/
	}
	
	@SuppressWarnings("unused")
	private static ArrayList<Double> analyse_PDFSample() {
		List<Double> valueListasObjects = Arrays.asList(1.0000, 0.3804, 0.8090, 0.2351, 0.3090, -0.0000, -0.3090,
				-0.2351, -0.8090, -0.3804, -1.0000, -0.3804, -0.8090, -0.2351, -0.3090, 0.0000, 0.3090, 0.2351, 0.8090,
				0.3804);
		return new ArrayList<Double>(valueListasObjects);
	}

	@SuppressWarnings("unused")
	private static ArrayList<Double> analyse_AudioFile() {
		double valueSize = Math.pow(2d, 15) - 1;
		ArrayList<Double> valueListasObjects = new ArrayList<Double>(1000);
		try {
			BufferedReader file = Files.newBufferedReader(Paths.get("wavepattern_440hz.txt"));
			String s;
			char c;
			for (int i = 0; i < 1000; ++i) {
				s = "";
				while ((c = (char) file.read()) != '\t')
					s += c;
				valueListasObjects.add(Double.parseDouble(s) / valueSize);
				// skip value for scnd channel
				while (file.read() != '\t')
					;
				while (file.read() != '\t')
					;
			}
			file.close();
			return valueListasObjects;
		} catch (IOException e) {
			System.out.println("Failed to open File: " + e.getMessage());
			return null;
		}
	}

	private static float[] analyse_FrequencySpectrum() {
		// Frequenzspektrum analysieren
		float DC_Offset = 1f;
		float values[] = new float[N];
		int freq1 = 12, freq2 = 8, freq3 = 3, freq4 = 16, freq5 = 17, freq6 = 14;
		for (int x = 0; x < N; ++x) {
			values[x] = (float) (1 * Math.cos(freq1 * 2 * Math.PI * x / N)
					+ 0.8 * Math.cos(freq2 * 2 * Math.PI * x / N));
			values[x] += 0.5 * Math.sin(freq3 * 2 * Math.PI * x / N) + DC_Offset;
			values[x] += 1 * Math.cos(freq4 * 2 * Math.PI * x / N);
			values[x] += 0.75 * Math.cos(freq5 * 2 * Math.PI * x / N);
			values[x] += 0.25 * Math.cos(freq6 * 2 * Math.PI * x / N) + 0.25 * Math.sin(freq6 * 2 * Math.PI * x / N);
		}
		return values;
	}
	
	public static void printDomain(float real[], float img[], int len) {
		System.out.print("[");
		for (int i = 0; i < len; ++i) {
			if (i % 7 == 0)
				System.out.println("");
			System.out.printf("%3d : ( %7.4f; %7.4f)\t",i,  real[i], img[i]);

		}
		System.out.println("\n\t]");
	}
	
	public static void printArray(float array[]) {
		System.out.print("[");
		for (int i = 0; i < array.length; ++i) {
			if (i % 14 == 0)
				System.out.println("");
			System.out.printf("%8.4f\t", array[i]);
		}
		System.out.println("\n\t]");
	}

}
