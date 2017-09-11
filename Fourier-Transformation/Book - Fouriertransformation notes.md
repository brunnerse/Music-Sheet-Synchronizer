    # Chapter 8 - The Discrete Fourier Transform

        Any discrete(and continuous) signal can be represented as the sum of properly chosen sinusoidal waves. - Pierre Simon de Laplace(1749 - 1827)

    ## Family of Fourier Transform
        Fourier Transform - for Aperiodic-Continuous signals
        Fourier Series - for Periodic-Continuous signals
        Discrete Time Fourier Transform - for Aperoidic-Discrete signals
        Discrete Fourier Transform - for Periodic-Discrete signals

        all signals extend to positive and negative infinity, finite data will be made to look like they're infinite by adding copies(periodic) or zero values(aperiodic) to the ends.
        To synthesize an aperiodic signal, you need an infinite number of sinusoids.

        Because a computer can't work with infinity or continuity, the only option viable for Computers is the DFT (Discrete Fourier Transform)
    ![](family of fourier transformation.png)

        Each of the four Fourier Transforms has a real and a complex version.

        a transform is a function that allows multiple values for input and output, e.g. convert 100 input samples into 200 output samples or convert discrete into continuous data.

    ## Notation and Format of the Real DFT
    ![](time-frequency-domain.png)
        Input Signal:
            Time Domain x[], N points (time domain bcs data are regularly taken over time), goes from x[0] to x[N - 1]
        Output:
            Frequency Domain
                Real part Re X[], N/2 + 1 points, goes from ReX[0] to ReX[N/2]
                Imaginary part Im X[], N/2 + 1 points, goes from ImX[0] to ImX[N/2]
        Re X[] contains the amplitudes of the cosine waves(scaled 1/2)
        Im X[] contains the amplitudes of the sine waves(scaled 1/2)

        The frequency domain contains the exact same data as the time domain, but in a different form. If you know one, you can calculate the other.
        time domain -> frequency domain: forward DFT, decomposition, analysis
        frequency domain -> time doamin: inverse DFT, synthesis

        N(number of samples) is typically 2^^k(a power of two) and between 32 and 4096.

        Generally, you chosse a lowercase letter for time domain and an uppercase letter for frequency domain.

    ## The Frequency Domain's Independent Variable
        Example x[] 128 samples, Re X[] 65 samples
        the horizontal axis of the frequency domain can be referred to in three different ways:
                labeled from 0 to N/2 (corresponding to the sample index)
                    ReX[k] to ReX[k]    with k from 0 to 64
                labeled as a fraction of the sampling rate N( from 0 to 0.5):
                    ReX[f] with f from 0 to 0.5
                        f = k/N
                labeled as fraction of sample rate N * 2 * Pi
                    ReX[w] with w from 0 to Pi
                    w = f * 2 * Pi
                    w : natural frequency, unit[radians]
                    used to make equations shorter, e.g. k: c[n] = cos(2Pi*k*n/N) = cos(2Pi*f*n) = cos(w*n)
                labeled specifically for one application
                        not suitable for general DFT equations
    ## DFT Basis Functions
        refers to the sine and cosine waves with unity amplitude.
    ![](DFT basis functions.png)
        A 32 points DFT has 17 discrete cosine waves and 17 discrete sine waves for its basis functions.
        DC Offset: The average value of all the points in the time domain signal.
        As there are N samples entering the DFT and N + 2 samples exiting, the values at Im X[0] and Im X[N/2] have a value of zero: They contain no information.
    ## Synthesis, Calculating the Inverse DFT
        synthesis equation:
    ![](synthesis equation.png)
        i is the position in the time domain,
        k is the frequency
        The arrays Im X[i] and Re X[i] (with line over the letter) are slightly different from the frequency domain: The amplitudes are scaled back to the original form.
        conversion:
    ![](scaling factor conversion.png)
        The scaling difference occurs because of Spectral density.
