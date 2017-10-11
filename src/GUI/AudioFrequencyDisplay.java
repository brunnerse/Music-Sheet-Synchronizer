package GUI;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.sound.sampled.AudioFormat;

//support for AudioFormats 16-bit signed and 8-bit signed
@SuppressWarnings("serial")
public class AudioFrequencyDisplay extends Canvas {
	
	private boolean isAnalysing;
	
	private byte[] bSignal = null;
	private short[] sSignal = null;
	private AudioFormat format;
	
	private int minFreq = 0;
	private int maxFreq = 20000;
	private int maxAmp = 100;
	
	//precision value is the 'range' of one frequency; e.g. precision of 2 means
	//that a frequency of 400 is for all frequencies of the range between 399 and 401 Hz.
	private int precision;
	
	private Color bgColor = new Color(240, 240, 240);
	private Color scaleColor = new Color(10, 10, 10);
	private Color graphColor = new Color(20, 255, 20);
	
	private int scaleTextOffsetX = 20;
	private final int scaleTextOffsetY = 15;
	
	public AudioFrequencyDisplay(int minFrequency, int maxFrequency, int length, int width) {
		super();
		this.minFreq = minFrequency;
		this.maxFreq = maxFrequency;
		this.setSize(length, width);
		
		this.setBackground(bgColor);
		this.setVisible(true);
	}
	
	public AudioFrequencyDisplay(byte[] signal, AudioFormat format, int minFrequency, int maxFrequency, int length, int width) {
		this(minFrequency, maxFrequency, length, width);
		if (format.getSampleSizeInBits() != 8) {
			throw new IllegalArgumentException("A 8-bit signal was given, however the AudioFormat isn't 8 bits");
		}
		bSignal = signal;
		this.setFormat(format);
		this.precision = (int) (format.getSampleRate()) / signal.length;
		this.startAnalysis();
	}
	
	public AudioFrequencyDisplay(short[] signal, AudioFormat format, int minFrequency, int maxFrequency, int length, int width) {
		this(minFrequency, maxFrequency, length, width);
		if (format.getSampleSizeInBits() != 16) {
			throw new IllegalArgumentException("A 16-bit signal was given, however the AudioFormat isn't 16 bits");
		}
		sSignal = signal;
		this.setFormat(format);
		
		this.startAnalysis();
	}
	
	public void updateGraph() {
		repaint();
	}
	
	public void startAnalysis() {
		isAnalysing = true;
		updateGraph();
	}
	
	public void stopAnalysis() {
		isAnalysing = false;
		updateGraph();
	}
	
	
	@Override
	public void paint(Graphics g) {
		//Whose Graphics is that
		drawScale(g);
		if (isAnalysing) {
			drawGraph(g);
		}
	}
	
	private void drawScale(Graphics g) {
		final int dotsPerLetter = 6;
		int arrowOffset = 3;
		g.setColor(scaleColor);
		int offset = this.getHeight() - scaleTextOffsetY;
		g.drawLine(scaleTextOffsetX, arrowOffset, scaleTextOffsetX, offset); //y-Line
		g.drawLine(scaleTextOffsetX, offset, this.getWidth() - arrowOffset, offset); //x-Line
		g.drawLine(scaleTextOffsetX, arrowOffset, scaleTextOffsetX - arrowOffset, arrowOffset + 3); //Arrow Lines for y-Line
		g.drawLine(scaleTextOffsetX, arrowOffset, scaleTextOffsetX + arrowOffset, arrowOffset + 3);
		offset = this.getHeight() - scaleTextOffsetY;
		g.drawLine(getWidth() - arrowOffset, offset, getWidth() - 3 - arrowOffset, offset - 3); //Arrow Lines for x-Line
		g.drawLine(getWidth() - arrowOffset, offset, getWidth() - 3 - arrowOffset, offset + 3);
		g.setFont(new Font(null, Font.BOLD, 12));
		g.drawString("Hz",  this.getWidth() - 3 - 14, getHeight() - 2);
		g.drawString("Amp",  scaleTextOffsetX + 7,  15);
		g.setFont(new Font(null, Font.PLAIN, 11));
		//draw Frequencies on X-Axis
		int offsetPerText = dotsPerLetter * String.valueOf(maxFreq).length() + 8;
		int axisLen = this.getWidth() - scaleTextOffsetX - arrowOffset;
		int numSegments = axisLen / offsetPerText;
		for(int i = 1; i < numSegments; ++i) {
			String freq = String.valueOf(i * (maxFreq - minFreq) / numSegments + minFreq);
			g.drawString(freq, scaleTextOffsetX + offsetPerText * i - freq.length() * dotsPerLetter / 2, this.getHeight() - 2);
			g.drawLine(scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY + 3, scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY - 3);
		}
		//draw Amplitudes on Y-Axis
		offsetPerText = 20;
		axisLen = this.getHeight() - scaleTextOffsetY - arrowOffset;
		numSegments = axisLen / offsetPerText;
		for(int i = 1; i < numSegments; ++i) {
			String amp = String.valueOf(i *  maxAmp / numSegments);
			int yPosition = this.getHeight() - scaleTextOffsetY - i * offsetPerText;
			g.drawString(amp, 2, yPosition + 5);
			g.drawLine(scaleTextOffsetX - 3, yPosition , scaleTextOffsetX + 3, yPosition );
		}
	}
	
	private void drawGraph(Graphics g) {
		int axisLen = this.getWidth() - scaleTextOffsetX - 3;
		g.setColor(graphColor);
		if (format.getSampleSizeInBits() == 16) {
			//Don't display value 0(The DC Offset)
			for(int i = 1; i < sSignal.length; ++i) {
				g.fillRect(i * sSignal.length / axisLen, scaleTextOffsetY + sSignal[i], axisLen / sSignal.length, sSignal[i]);
			}
		}
	}

	public boolean isAnalysing() {
		return isAnalysing;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public void setDarkColorTheme() {
		this.bgColor = new Color(0, 0, 0);
		this.scaleColor = new Color(240, 240, 240);
		this.graphColor = new Color(0, 255, 0);
		this.setBackground(bgColor);
	}
	
	public void setBrightColorTheme() {
		this.bgColor = new Color(240, 240, 240);
		this.scaleColor = new Color(0, 0, 0);
		this.graphColor = new Color(50, 255, 50);
		this.setBackground(bgColor);
	}
	
}
