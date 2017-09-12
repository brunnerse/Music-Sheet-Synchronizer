package FourierTransformation;

public class FourierTransform {
	
	//executes the DFT on the timeDomain and writes the frequency Domain in the arrays ReX and ImX
	public static void RealDFT(float timeDomain[], float out_ReX[], float out_ImX[]) {
		int N = timeDomain.length;
		if (out_ReX.length < N / 2 + 1) {
			System.out.println("ReX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		if (out_ImX.length < N / 2 + 1) {
			System.out.println("ImX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		for(int i = 0; i <= N / 2; ++i) {
			out_ReX[i] = 0f;
			out_ImX[i] = 0f;
			double step = 2 * Math.PI * i / N;
			for(int x = 0; x < N; ++x) {
				out_ReX[i] += timeDomain[x] * Math.cos(step * x); //2 * Pi * i * x / N
				out_ImX[i] -= timeDomain[x] * Math.sin(step * x);
			}
		}
	}
	
	public static void ComplexDFT(float in_Rex[], float in_Imx[], float out_ReX[], float out_ImX[]) {
		int N = in_Rex.length;
		if (in_Imx.length != N || out_ReX.length < N || out_ImX.length < N) {
			System.err.println("ERROR: The array don't have the same size.");
			return;
		}
		for (int i = 0; i < N; ++i) {
			out_ReX[i] = 0f;
			out_ImX[i] = 0f;
			double step = 2 * Math.PI * i / N;
			for (int x = 0; x < N; ++x) {
				double cosVal = Math.cos(step * x);
				double sinVal = - Math.sin(step * x);
				out_ReX[i] += in_Rex[x] * cosVal - in_Imx[x] * sinVal;
				out_ImX[i] += in_Rex[x] * sinVal + in_Imx[x] * cosVal;
			}
		}
		
	}
	
	public static void InverseRealDFT(float in_ReX[], float in_ImX[], float out_timeDomain[], int N) {
		int x;
		//N is the length of the time Domain, K the length of the Frequency Domain arrays.
		if (in_ImX.length <= N/2 || in_ReX.length <= N/2 || out_timeDomain.length < N) {
			System.err.println("ERROR: The arrays don't have a proper size.");
			return;
		}
		for (int i = 0; i < N; ++i) {
			out_timeDomain[i] = in_ReX[0] / N;
			for (x = 1; x < N/2; ++x) {
				out_timeDomain[i] += in_ReX[x] / (N/2) * Math.cos(2 * Math.PI * x * i / N);
				out_timeDomain[i] -= in_ImX[x] / (N/2) * Math.sin(2 * Math.PI * x * i / N);
			}
			out_timeDomain[i] += in_ReX[x] / N * Math.cos(2 * Math.PI * x * i / N);
			out_timeDomain[i] -= in_ImX[x] / N * Math.sin(2 * Math.PI * x * i / N);
		}
		
	}
	
	public static void InverseComplexDFT(float in_ReX[], float in_ImX[], float out_Rex[], float out_Imx[]) {
		//convert the amplitudes into the right scale, then perform a Complex DFT.
		int N = in_ReX.length;
		if (in_ImX.length != N || out_Rex.length < N || out_Imx.length < N) {
			System.err.println("ERROR: Arrays got an invalid size.");
			return;
		}

		for (int i = 0; i < N; ++i) {
			out_Rex[i] = 0f;
			out_Imx[i] = 0f;
			double step = 2 * Math.PI * i / N;
			for (int x = 0; x < N; ++x) {
				double cosVal = Math.cos(step * x);
				double sinVal = - Math.sin(step * x);
				out_Rex[i] += in_ReX[x] * cosVal + in_ImX[x] * sinVal;
				out_Imx[i] += in_ReX[x] * sinVal - in_ImX[x] * cosVal;
			}
			out_Rex[i] = out_Rex[i] / N;
			out_Imx[i] = out_Imx[i] / -N;
		}
	}
	
	//Converts time domain data from inout_Rex and inout_Imx into frequency domain data and writes them into inout_Rex and inout_Imx
	public static void FFT(float inout_Rex[], float inout_Imx[]) {
		int N = inout_Rex.length;
		float M = (float) (Math.log(N) / Math.log(2));
		if (Math.abs(M - (int)M) > 0.0001) {
			System.err.println("ERROR: length of arrays must be like\tlength = 2^x");
			return;
		}
		if (N != inout_Imx.length) {
			System.err.println("ERROR:length of arrays doesn't match.");
			return;
		}
		
		
	}
	
	//Transforms the time domain data of inout_Rex[] into frequency domain data with
	//real part inout_Rex[] and out_Imx[]. out_Imx[] needs a length of at least N/2 + 1.
	public static void RealFFT(float inout_Rex[], float out_ImX[]) {
		int N = inout_Rex.length;
		if (out_ImX.length <= N / 2) {
			System.out.println("out_ImX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		//ImaginaryPart: All values are zero
		//out_ReX and out_ImX :  idx 0 - N/2
		
	}
	
	public static void InverseFFT(float inout_Rex[], float inout_Imx[]) {
		int N = inout_Rex.length;
		if (inout_Imx.length != N) {
			System.err.println("ERROR: size of the arrays doesn't match.");
			return;
		}
		for (int i = 0; i < N; ++i)
			inout_Imx[i] = - inout_Imx[i];
		
		FFT(inout_Rex, inout_Imx);
		
		for(int i = 0; i < N; ++i) {
			inout_Rex[i] = inout_Rex[i] / N;
			inout_Imx[i] = inout_Imx[i] / -N;
		}
	}

	public static void InverseRealFFT(float in_ReX[], float in_ImX[], float out_timeDomain[]) {
		
	}
}
