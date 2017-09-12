package FourierTransformation;

public class FourierTransform {

	// executes the DFT on the timeDomain and writes the frequency Domain in the
	// arrays ReX and ImX
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
		for (int i = 0; i <= N / 2; ++i) {
			out_ReX[i] = 0f;
			out_ImX[i] = 0f;
			double step = 2 * Math.PI * i / N;
			for (int x = 0; x < N; ++x) {
				out_ReX[i] += timeDomain[x] * Math.cos(step * x); // 2 * Pi * i * x / N
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
				double sinVal = -Math.sin(step * x);
				out_ReX[i] += in_Rex[x] * cosVal - in_Imx[x] * sinVal;
				out_ImX[i] += in_Rex[x] * sinVal + in_Imx[x] * cosVal;
			}
		}

	}

	public static void InverseRealDFT(float in_ReX[], float in_ImX[], float out_timeDomain[], int N) {
		int x;
		// N is the length of the time Domain, K the length of the Frequency Domain
		// arrays.
		if (in_ImX.length <= N / 2 || in_ReX.length <= N / 2 || out_timeDomain.length < N) {
			System.err.println("ERROR: The arrays don't have a proper size.");
			return;
		}
		for (int i = 0; i < N; ++i) {
			out_timeDomain[i] = in_ReX[0] / N;
			for (x = 1; x < N / 2; ++x) {
				out_timeDomain[i] += in_ReX[x] / (N / 2) * Math.cos(2 * Math.PI * x * i / N);
				out_timeDomain[i] -= in_ImX[x] / (N / 2) * Math.sin(2 * Math.PI * x * i / N);
			}
			out_timeDomain[i] += in_ReX[x] / N * Math.cos(2 * Math.PI * x * i / N);
			out_timeDomain[i] -= in_ImX[x] / N * Math.sin(2 * Math.PI * x * i / N);
		}

	}

	public static void InverseComplexDFT(float in_ReX[], float in_ImX[], float out_Rex[], float out_Imx[]) {
		// convert the amplitudes into the right scale, then perform a Complex DFT.
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
				double sinVal = -Math.sin(step * x);
				out_Rex[i] += in_ReX[x] * cosVal + in_ImX[x] * sinVal;
				out_Imx[i] += in_ReX[x] * sinVal - in_ImX[x] * cosVal;
			}
			out_Rex[i] = out_Rex[i] / N;
			out_Imx[i] = out_Imx[i] / -N;
		}
	}

	// CREDIT: https://www.nayuki.io/page/free-small-fft-in-multiple-languages
	// This FFT is using the Cooley-Tukey algorithm
	public static void FFT(float[] real, float[] imag) {
		// Length variables
		int n = real.length;
		if (n != imag.length)
			throw new IllegalArgumentException("Mismatched lengths");
		int levels = 31 - Integer.numberOfLeadingZeros(n); // Equal to floor(log2(n))
		if (1 << levels != n)
			throw new IllegalArgumentException("Length is not a power of 2");

		// Trigonometric tables
		double[] cosTable = new double[n / 2];
		double[] sinTable = new double[n / 2];
		for (int i = 0; i < n / 2; i++) {
			cosTable[i] = Math.cos(2 * Math.PI * i / n);
			sinTable[i] = Math.sin(2 * Math.PI * i / n);
		}

		// Bit-reversed addressing permutation
		for (int i = 0; i < n; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				double temp = real[i];
				real[i] = real[j];
				real[j] = (float) temp;
				temp = imag[i];
				imag[i] = imag[j];
				imag[j] = (float) temp;
			}
		}

		// Cooley-Tukey decimation-in-time radix-2 FFT
		for (int size = 2; size <= n; size *= 2) {
			int halfsize = size / 2;
			int tablestep = n / size;
			for (int i = 0; i < n; i += size) {
				for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
					int l = j + halfsize;
					double tpre = real[l] * cosTable[k] + imag[l] * sinTable[k];
					double tpim = -real[l] * sinTable[k] + imag[l] * cosTable[k];
					real[l] = (float) (real[j] - tpre);
					imag[l] = (float) (imag[j] - tpim);
					real[j] += tpre;
					imag[j] += tpim;
				}
			}
			if (size == n) // Prevent overflow in 'size *= 2'
				break;
		}
	}

	// Transforms the time domain data of inout_Rex[] into frequency domain data
	// with
	// real part inout_Rex[] and out_Imx[]. out_Imx[] needs a length of at least N/2
	// + 1.
	public static void RealFFT(float inout_real[], float out_imag[]) {
		// ImaginaryPart: All values are zero
		// out_ReX and out_ImX : idx 0 - N/2
		// Length variables
		int n = inout_real.length;
		if (out_imag.length < n / 2 + 1)
			throw new IllegalArgumentException("Out_Imag is too small!");
		int levels = 31 - Integer.numberOfLeadingZeros(n); // Equal to floor(log2(n))
		if (1 << levels != n)
			throw new IllegalArgumentException("Length is not a power of 2");

		// Bit-reversed addressing permutation
		for (int i = 0; i < n; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				double temp = inout_real[i];
				inout_real[i] = inout_real[j];
				inout_real[j] = (float) temp;
				temp = out_imag[i];
				out_imag[i] = out_imag[j];
				out_imag[j] = (float) temp;
			}
		}

		// Cooley-Tukey decimation-in-time radix-2 FFT
		for (int size = 2; size <= n; size *= 2) {
			int halfsize = size / 2;
			int tablestep = n / size;
			double factor = 2 * Math.PI / n;
			for (int i = 0; i < n; i += size) {
				for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {

					double cosVal = Math.cos(factor * k);
					double sinVal = Math.sin(factor * k);

					int l = j + halfsize;
					double tpre = inout_real[l] * cosVal + out_imag[l] * sinVal;
					double tpim = -inout_real[l] * sinVal + out_imag[l] * cosVal;
					inout_real[l] = (float) (inout_real[j] - tpre);
					out_imag[l] = (float) (out_imag[j] - tpim);
					inout_real[j] += tpre;
					out_imag[j] += tpim;
				}
			}
			if (size == n) // Prevent overflow in 'size *= 2'
				break;
		}
	}

	public static void InverseFFT(float inout_Rex[], float inout_Imx[]) {
		int N = inout_Rex.length;
		if (inout_Imx.length != N) {
			System.err.println("ERROR: size of the arrays doesn't match.");
			return;
		}
		for (int i = 0; i < N; ++i)
			inout_Imx[i] = -inout_Imx[i];

		FFT(inout_Rex, inout_Imx);

		for (int i = 0; i < N; ++i) {
			inout_Rex[i] = inout_Rex[i] / N;
			inout_Imx[i] = inout_Imx[i] / -N;
		}
	}

	public static void InverseRealFFT(float inout_real[], float in_imag[]) {
		int n = inout_real.length;
		if (in_imag.length != n / 2 + 1)
			throw new IllegalArgumentException("in_Imag is too small!");
		int levels = 31 - Integer.numberOfLeadingZeros(n); // Equal to floor(log2(n))
		if (1 << levels != n)
			throw new IllegalArgumentException("Length is not a power of 2");
		
	}

}
