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
		if (in_Imx.length < N) {
			System.out.println("Im x[] array doesn't fit the Re x[] array. Size of Re x[]: " + N);
			return;
		}
		if (out_ReX.length < N) {
			System.out.println("ReX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		if (out_ImX.length < N) {
			System.out.println("ImX[] array too small. It needs " + (N / 2 + 1) + " values.");
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
	
	public static void FFT(float in_Rex[], float in_Imx[], float out_ReX[], float out_ImX[]) {
		int N = in_Rex.length;
		if (in_Imx.length < N) {
			System.out.println("Im x[] array doesn't fit the Re x[] array. Size of Re x[]: " + N);
			return;
		}
		if (out_ReX.length < N / 2 + 1) {
			System.out.println("ReX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		if (out_ImX.length < N / 2 + 1) {
			System.out.println("ImX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		
	}
	
	public static void RealFFT(float timeDomain[], float out_ReX[], float out_ImX[]) {
		int N = timeDomain.length;
		if (out_ReX.length < N / 2 + 1) {
			System.out.println("ReX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		if (out_ImX.length < N / 2 + 1) {
			System.out.println("ImX[] array too small. It needs " + (N / 2 + 1) + " values.");
			return;
		}
		//ImaginaryPart: All values are zero
		//out_ReX and out_ImX :  idx 0 - N/2
		
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
		int N = in_ReX.length;
		if (in_ImX.length != N || out_Rex.length < N || out_Imx.length < N) {
			System.err.println("ERROR: Arrays got an invalid size.");
			return;
		}
		for (int i = 0; i < N; ++i)
			in_ImX[i] = - in_ImX[i];
		ComplexDFT(in_ReX, in_ImX, out_Rex, out_Imx);
		for(int i = 0; i < N; ++i) {
			out_Rex[i] = out_Rex[i] / N;
			out_Imx[i] = out_Imx[i] / -N;
		}
	}
}
