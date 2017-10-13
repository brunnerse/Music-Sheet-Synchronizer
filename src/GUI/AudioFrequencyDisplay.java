package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JPanel;

import FourierTransformation.FourierTransform;

//support for AudioFormats 16-bit signed and 8-bit signed
@SuppressWarnings("serial")
public class AudioFrequencyDisplay extends JPanel {

	private volatile boolean isAnalysing;

	private TargetDataLine line = null;
	private AudioFormat format;
	private float[] amps;

	private int minFreq = 0;
	private int maxFreq = 20000;
	private int maxAmp = 100;

	private int dataSize;

	// precision value is the 'range' of one frequency; e.g. precision of 2 means
	// that a frequency of 400 is for all frequencies of the range between 399 and
	// 401 Hz.
	private float precision;

	private Color bgColor = new Color(240, 240, 240);
	private Color scaleColor = new Color(10, 10, 10);
	private Color graphColor = new Color(20, 255, 20);

	private final int scaleTextOffsetX = 20;
	private final int scaleTextOffsetY = 15;
	private final int scaleArrowOffset = 3;
	private int axisLenX, axisLenY;
	
	private AudioUpdater audioUpdaterThread;

	public AudioFrequencyDisplay(int dataSize, int minFrequency, int maxFrequency, int width, int height) {
		super();
		this.minFreq = minFrequency;
		this.maxFreq = maxFrequency;
		precision = 0;
		this.setDataSize(dataSize);
		amps = new float[dataSize];
		this.setBackground(bgColor);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(width, height));
		this.isAnalysing = false;
	}

	public AudioFrequencyDisplay(TargetDataLine line, int dataSize, int minFrequency, int maxFrequency, int width,	int height) {
		this(dataSize, minFrequency, maxFrequency, width, height);
		this.line = line;
	}

	public void startAnalysis() {
		isAnalysing = true;
		if (line == null) {
			this.setFormat(new AudioFormat(44100, 16, 1, true, false));
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				throw new IllegalArgumentException("ERROR: Das Audioformat " + format.toString()
						+ "\nwird von der TargetDataLine nicht unterstützt.");
			}
			try {
				this.audioUpdaterThread = new AudioUpdater(AudioSystem.getTargetDataLine(format), dataSize);
			} catch (LineUnavailableException e) {
				System.err.println("Line is currently unavailable");
				stopAnalysis();
			}
		} else {
			this.audioUpdaterThread = new AudioUpdater(line, dataSize);
		}
		this.audioUpdaterThread.start();
	}

	public void stopAnalysis() {
		isAnalysing = false;
		try { this.audioUpdaterThread.join(); } catch (InterruptedException e) {}
	}

	@Override
	public void paintComponent(Graphics g) {
		this.axisLenX = this.getWidth() - scaleTextOffsetX - scaleArrowOffset;
		this.axisLenY = this.getHeight() - scaleTextOffsetY - scaleArrowOffset;
		g.setColor(bgColor);
		//As with repaint() shit doesn't seem to work, I'm gonna clear the Panel manually 
		g.fillRect(0,  0,  this.getWidth(), this.getHeight());
		// Whose Graphics is that
		drawScale(g);
		if (isAnalysing) {
			drawGraph(g);
		}
	}

	private void drawScale(Graphics g) {
		final int dotsPerLetter = 6;
		g.setColor(scaleColor);
		int offset = this.getHeight() - scaleTextOffsetY;
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX, offset); // y-Line
		g.drawLine(scaleTextOffsetX, offset, this.getWidth() - scaleArrowOffset, offset); // x-Line
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX - scaleArrowOffset, scaleArrowOffset + 3); // Arrow Lines for
																									// y-Line
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX + scaleArrowOffset, scaleArrowOffset + 3);
		offset = this.getHeight() - scaleTextOffsetY;
		g.drawLine(getWidth() - scaleArrowOffset, offset, getWidth() - 3 - scaleArrowOffset, offset - 3); // Arrow Lines for
																								// x-Line
		g.drawLine(getWidth() - scaleArrowOffset, offset, getWidth() - 3 - scaleArrowOffset, offset + 3);
		g.setFont(new Font(null, Font.BOLD, 12));
		g.drawString("Hz", this.getWidth() - 3 - 14, getHeight() - 2);
		g.drawString("Amp", scaleTextOffsetX + 7, 15);
		g.setFont(new Font(null, Font.PLAIN, 11));
		// draw Frequencies on X-Axis
		int offsetPerText = dotsPerLetter * String.valueOf(maxFreq).length() + 8;
		int numSegments = axisLenX / offsetPerText;
		for (int i = 1; i < numSegments; ++i) {
			String freq = String.valueOf(i * (maxFreq - minFreq) / numSegments + minFreq);
			g.drawString(freq, scaleTextOffsetX + offsetPerText * i - freq.length() * dotsPerLetter / 2,
					this.getHeight() - 2);
			g.drawLine(scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY + 3,
					scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY - 3);
		}
		// draw Amplitudes on Y-Axis
		offsetPerText = 20;
		numSegments = axisLenY / offsetPerText;
		for (int i = 1; i < numSegments; ++i) {
			String amp = String.valueOf(i * maxAmp / numSegments);
			int yPosition = this.getHeight() - scaleTextOffsetY - i * offsetPerText;
			g.drawString(amp, 3, yPosition + 5);
			g.drawLine(scaleTextOffsetX - 3, yPosition, scaleTextOffsetX + 3, yPosition);
		}
	}

	private void drawGraph(Graphics g) {
		g.setColor(graphColor);
		if (format.getSampleSizeInBits() == 16) {
			// Don't display value 0(The DC Offset)
			for (int i = 1; i < Math.min(amps.length, maxFreq) - minFreq; ++i) {
				int currentYVal = axisLenY * (int) amps[i + minFreq] / maxAmp;
				g.drawRect((int)(i * axisLenX * precision)/ (maxFreq - minFreq) + scaleTextOffsetX, this.getHeight() - scaleTextOffsetY - currentYVal,
						(int)(axisLenX * precision) / (maxFreq - minFreq) , currentYVal);
			}
		}
	}

	
	private class AudioUpdater extends Thread {
		private float[] fReal, fImag;
		private byte[] bData1, bData2, bData;
		private TargetDataLineReader tRead;
		private TargetDataLine line;

		public AudioUpdater(TargetDataLine line, int numData) {
			this.line = line;
			try {
				this.line.open(format);
			} catch (LineUnavailableException ex) {
				System.err.println("line nicht verfügbar: " + ex.getMessage());
				return;
			}
			fReal = new float[numData];
			fImag = new float[numData];
			bData1 = new byte[numData * 2];
			bData2 = new byte[numData * 2];
			try {Thread.sleep(500);} catch (Exception e) {}
		}

		@Override
		public void run() {
			//bData always points to the array which is supposed to be processed, the other array is being read into
			bData = bData1;
			line.start();
			tRead = new TargetDataLineReader(line, bData2);
			tRead.start();
			while (isAnalysing) {
				try {
					tRead.join();
				} catch (InterruptedException e) {}
				if (bData == bData1) {
					tRead = new TargetDataLineReader(line, bData1);
					tRead.start();
					bData = bData2;
				} else {
					tRead = new TargetDataLineReader(line, bData2);
					tRead.start();
					bData = bData1;
				}
				for (int i = 0; i < fReal.length; ++i) {
					fReal[i] = (float)(bData[2*i] | (bData[2*i + 1] << 8));
					fImag[i] = 0f;
				}
				FourierTransform.FFT(fReal,  fImag);
				FourierTransform.GetAmplitudes(fReal,  fReal, amps);
				repaint();
			}
			try {
				tRead.join();
			} catch (InterruptedException e) {}
			line.stop();
			line.close();
		}
	}

	private class TargetDataLineReader extends Thread {
		TargetDataLine line;
		byte[] data;

		public TargetDataLineReader(TargetDataLine line, byte[] data) {
			this.line = line;
			this.data = data;
		}

		@Override
		public void run() {
			line.read(data, 0, data.length);
		}
	}
	

	public float getLoudestFreq() {
		int MaxFreq = 1;
		for (int i = 2; i < amps.length; ++i) {
			if (amps[i] > amps[MaxFreq])
				MaxFreq = i;
		}
		return MaxFreq * precision;
	}

	public void setDataSize(int dataSize) {
		int levels = 31 - Integer.numberOfLeadingZeros(dataSize); // Equal to floor(log2(n))
		if (1 << levels != dataSize)
			throw new IllegalArgumentException("ERROR: Length is not a power of 2");
		this.dataSize = dataSize;
	}

	public void setDarkColorTheme() {
		this.bgColor = new Color(0, 0, 0);
		this.scaleColor = new Color(240, 240, 240);
		this.graphColor = new Color(0, 255, 0);
		this.setBackground(bgColor);
		repaint();
	}

	public void setBrightColorTheme() {
		this.bgColor = new Color(240, 240, 240);
		this.scaleColor = new Color(0, 0, 0);
		this.graphColor = new Color(50, 255, 50);
		this.setBackground(bgColor);
		repaint();
	}

	private void calcPrecision() {
		this.precision = this.format.getSampleRate() / amps.length;
	}

	public float getPrecision() {
		return this.precision;
	}

	public int getMaxFrequency() {
		return this.maxFreq;
	}

	public int getMinFrequency() {
		return this.minFreq;
	}

	public int getMaxAmplitude() {
		return this.maxAmp;
	}

	public void setMaxFrequency(int freq) {
		this.maxFreq = freq;
	}

	public void setMinFrequency(int freq) {
		this.minFreq = freq;
	}

	public void setMaxAmplitude(int amp) {
		this.maxAmp = amp;
	}
	
	public boolean isAnalysing() {
		return isAnalysing;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
		calcPrecision();
	}
}
