package FourierTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.lang.Math;
import java.nio.file.*;


public class FourierTesting {
	public static void main(String args[]) {
		ArrayList<Double> valueListasObjects = FourierTesting.analyse_FrequencySpectrum();
		double[] valueList = new double[valueListasObjects.size()];
		for (int i = 0; i < valueListasObjects.size(); ++i) {
			valueList[i] = valueListasObjects.get(i).doubleValue();
		}
		System.out.println("Testing Fourier Transformation for wave 880 hz / 440hz ...");
		System.out.println("values: [");
		for (Double i : valueListasObjects) {
			System.out.print( i + ", ");
		}
		System.out.println("\b\b ]");
		System.out.println("Starting calculation for " + valueList.length + " values...");
		ComplexPoint[] resultList = fourier_transform(valueList);
		System.out.println("Finished. Results: [");
		for (int i = 0; i < resultList.length; ++i) {
			System.out.printf("%3d : ( %7.4f; %7.4f)\t",i,  resultList[i].real, resultList[i].complex);
			if (i % 10 == 9)
				System.out.println("");
		}
		System.out.println("]");
	}
	
	//@SuppressWarnings("unused")
	public static ComplexPoint[] fourier_transform(double[] v) {
		double n = v.length;
		ComplexPoint[] resultList = new ComplexPoint[(int) (n / 2 + 1)];
		double sum, complexSum;
		for (int k = 0; k <= n / 2; ++k) {
			sum = 0;
			complexSum = 0;
			for (int j = 0; j < n; ++j) {
				sum += v[j] * Math.cos(2 * Math.PI * k * j / n);
				complexSum += v[j] * (-1) * Math.sin(2 * Math.PI * k * j / n);
			}
			resultList[k] = new ComplexPoint(sum / n, complexSum / n);
		}
		return resultList;
	}
	
	@SuppressWarnings("unused")
	private static ArrayList<Double> analyse_PDFSample() {
		List<Double> valueListasObjects = Arrays.asList(1.0000 ,  0.3804 ,  0.8090  ,  0.2351 ,  0.3090 ,
				-0.0000 , -0.3090 , -0.2351 , -0.8090 , -0.3804 ,-1.0000 , -0.3804 , -0.8090 , -0.2351 , -0.3090 , 
				0.0000 ,  0.3090 ,  0.2351  ,  0.8090 ,  0.3804 );
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
			for(int i = 0; i < 1000; ++i) {
				s = "";
				while ((c = (char) file.read()) != '\t')
					s += c;
				valueListasObjects.add(Double.parseDouble(s) / valueSize);
				//skip value for scnd channel
				while (file.read() != '\t');
				while (file.read() != '\t');
			}
			file.close();
			return valueListasObjects;
		} catch (IOException e) {
			System.out.println("Failed to open File: " + e.getMessage());
			return null;
		}
	}
	
	private static ArrayList<Double> analyse_FrequencySpectrum() {
		//Frequenzspektrum analysieren
		ArrayList<Double> valueListasObjects = new ArrayList<Double>();
		double val;
		for (double t = 0; t <= 1; t += 0.001) {
			val = 0.7 * Math.cos(440 * 2 * Math.PI * t) + 0.3 * Math.cos(485 * 2 * Math.PI * t) + Math.cos(243 * 2 * Math.PI * t);
			valueListasObjects.add(val);
		}
		return valueListasObjects;
	}
	
	public static class ComplexPoint {
		public double real;
		public double complex;
		public ComplexPoint(double real, double complex) {
			this.real = real;
			this.complex = complex;
		}
		
	}
}


