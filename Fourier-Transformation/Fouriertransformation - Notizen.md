# FOURIER-TRANSFORMATION
## Einleitung
    Rechentechnik zur Analyse von periodischen Vorgängen von
        JEAN BAPTISTE JOSEPH FOURIER (1768-1830)
    „Jede periodische Funktion kann durch Summen von Sinus- und  Kosinusschwingungen dargestellt werden.“ - Fourier

    Fouriertransformation ermöglicht Überführung von Signalen der Darstellung (Zeitpunkt, Abtastwert) in Signale der Darstellung (Frequenzanteil, Amplitude und/oder Phase).
    Ermöglicht u.a. Untersuchung natürlicher Töne und Signale bzw. mit der inversen Transformation, Signale zu erzeugen.
## Grundidee einer Transformation
    Transformation: Vektor von beliebig vielen Zahlen über eine Funktion auf einen neuen Vektor abbilden
    Manchmal ist es schneller, um eine Operation auf Werte anzuwenden, diese Werte umzuwandeln, die Operation in einem anderen Raum durchzuführen, und die Werte wieder rückzuwandeln, als die Operation direkt auszuführen. Bsp:
        Multiplikation von reellen Zahlen:
            a * b   ==   log-1 ( log(a) + log(b) )  (Nachschlagen von Logarithmus-Werten in Tabelle, dann einfache Addition)
## Das Samplingtheorem
    Sampling: Abtasten einer Funktion
>   "Die Abtastfrequenz sollte größer dem doppelten der maximal auftretenden Frequenz sein"
>   f >= 2 * fmax   fmax - Nyquistfrequenz      f - Nyquistrate
bsp:
    menschliches Gehör: 20 Hz bis 22
    kHzAudiosignale in CD-Qualität: 44 kHz
Dabei muss man beachten, dass die verwendete Samplerate höher sein sollte, als die Nyquist-Rate, da Quantisierungsfehler beim Samplingprozess das Ergebnis zusätzlich beeinflussen.
Als Quantisieren bezeichnet man den Vorgang, bei dem derWertebereich in Intervalle unterteilt wird (Wertdiskretisierung).

## Die Diskrete Fouriertransformation (DFT)
    Dient zur Transformation von diskreten, periodischen Signalen
    Eingangswerte:
        Time Domain Signal(N Werte, die in zeitlicher Abfolge gegeben sind ) x[]
        N wird i.d.R. so gewählt, dass 2^k = N      (Nötig für Fast Fourier Transformation(FFT))
    Ausgangswerte:
        Frequency Domain Signal X[]:
            Realer Teil ReX[]:
                N/2 + 1 Werte, die die (skalierten) Amplituden der Cosinuswellen enthalten
            Imaginärer Teil ImX[]:
                N/2 + 1 Werte, die die (skalierteen) Amplituden der Sinuswellen enthalten

    In der digitalen Signalverarbeitung  wird das Ausgangssignal in den Raum der komplexen Zahlen überführt und als Frequenzspektrum mit realem und imaginären Anteil betrachtet und analysiert.
![Formel Fourier-Transformation](C:\Users\Severin\Git_Repositories\Music Sheet Synchroniser\Fourier-Transformation\formel.png "Formel Fourier-Transformation")

    Die einzelnen Werte des Ergebnisvektors heißen Fourierkoeffizienten.
    Bie reellen Funktionen sind die Fourierkoeffizienten symmetrisch.
    Die Amplitude ergibt sich dann zu
        yk = 2 * |ck | (Amplitudenspektrum)
    Die Phase ist:
        PhiK = arctan(Im(ck) / Re(ck))  (Phasenspektrum)

    Der Ergebniswerte enthält im Prinzip nur Amplitudenwerte / 2;
    der Index, an dem sich der (halbe) Amplitudenwert befindet, entspricht der Frequenz, die die Amplitude besitzt.
    Zur Filterung bestimmter Frequenzen werden einfach die Werte an den Indizes, die den Frequenzen entsprechen, auf 0 gesetzt und anschließend rücktransformiert. Zur (verlustbehafteten) Komprimierung können auch Werte nahe 0 auf 0 gesetzt werden, da sie kaum zum Gesamtspektrum beitragen.
## Die Inverse Diskrete Fouriertransformation (IDFT)
    Wandelt die Daten vom Frequenzbereich zurück in den Zeitbereich
![Formel Inverse Fourier-Transformation](C:\Users\Severin\Git_Repositories\Music Sheet Synchroniser\Fourier-Transformation\inverse formel.png "Formel Inverse Fourier-Transformation")

## Die Schnelle Fourier-Transformation (FFT)
