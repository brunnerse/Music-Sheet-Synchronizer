package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.sound.sampled.AudioFormat;

import FourierTransformation.FourierTransform;

//support for AudioFormats 16-bit signed and 8-bit signed
@SuppressWarnings("serial")
public abstract class AbstractFrequencyDisplay<T> extends FrequencyDisplay {

	protected volatile boolean isAnalysing = false;

	protected T line = null;
	protected AudioFormat format;
	protected float[] amps;

	protected int minFreq = 0;
	protected int maxFreq = 20000;
	protected float maxAmp = 0.1f;
	protected int maxAmpFactor = 1000; //Depending on maxAmp is, the numbers of the Amplitude scale will be scaled by a 

	// precision value is the 'range' of one frequency; e.g. precision of 2 means
	// that a frequency of 400 is for all frequencies of the range between 399 and
	// 401 Hz.
	protected float precision;

	protected Color bgColor, scaleColor, graphColor;

	private int scaleTextOffsetX;
	private int scaleTextOffsetY;
	private final int scaleArrowOffset = 3;
	private final int dotsPerLetter = 6;
	private final int fontSize = 11;
	private int maxAmplitudeLetters;  //The Number of letters maxAmp has
	private int axisLenX, axisLenY;
	
	protected AudioUpdater audioUpdaterThread;

	public AbstractFrequencyDisplay(float precision, int minFrequency, int maxFrequency, int width, int height) {
		super();
		this.minFreq = minFrequency;
		this.maxFreq = maxFrequency;
		this.setBackground(bgColor);
		this.setPrecision(precision);
		this.setBrightColorTheme();
		this.setPreferredSize(new Dimension(width, height));
		
		MouseDragManager m = new MouseDragManager();
		this.addMouseMotionListener(m);
		this.addMouseListener(m);
		this.setVisible(true);
	}

	//abstract functions
	//This function is suppoed to fill the data array from the data of T
	protected abstract void readLine(byte[] data);
	protected abstract void openLine() throws Exception;
	protected abstract void closeLine();
	//This method is supposed to initialise T this.line and AudioFormat this.format
	protected abstract void setupLineAndFormat() throws Exception;
	
	public final void startAnalysis() {
		try {
			setupLineAndFormat();
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
			return;
		}
		if (line == null || format == null)
			throw new RuntimeException("setupLineAndFormat() doesn't initialise the format and the line");
		int dataSize = getDataSizeByPrecision(this.precision);
		this.precision = this.format.getSampleRate() / dataSize;
		amps = new float[dataSize / 2 + 1];
		this.audioUpdaterThread = new AudioUpdater(dataSize);
		this.audioUpdaterThread.start();
		isAnalysing = true;
	}
	
	
	public final void stopAnalysis() {
		isAnalysing = false;
		try { this.audioUpdaterThread.join(); } catch (InterruptedException e) {}
		this.line = null;
	}

	@Override
	public final void paintComponent(Graphics g) {
		g.setColor(bgColor);
		//As with repaint() shit doesn't seem to work, I'm gonna clear the Panel manually 
		g.fillRect(0,  0,  this.getWidth(), this.getHeight());
		
		this.axisLenX = this.getWidth() - scaleTextOffsetX - scaleArrowOffset;
		this.axisLenY = this.getHeight() - scaleTextOffsetY - scaleArrowOffset;
		
		//try to make the Amp Axis always show two digits
		final int switchVal = 25;
		if (maxAmp * maxAmpFactor >= switchVal * 10)
			maxAmpFactor /= 10;
		else if (maxAmp * maxAmpFactor < switchVal)
			maxAmpFactor *= 10;
		this.maxAmplitudeLetters = String.valueOf((int)(maxAmp * maxAmpFactor)).length();
		
		
		this.scaleTextOffsetY = fontSize + 4;
		this.scaleTextOffsetX = dotsPerLetter * maxAmplitudeLetters + 9;	
		
		if (amps != null)
			drawGraph(g);
		drawScale(g);
	}

	protected final void drawScale(Graphics g) {
		g.setColor(scaleColor);
		int offset = this.getHeight() - scaleTextOffsetY;

		g.drawLine(scaleTextOffsetX, offset, this.getWidth() - scaleArrowOffset, offset); // x-Line
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX, offset); // y-Line
		
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX - scaleArrowOffset, scaleArrowOffset + 3); // Arrow Lines for y-Line
		g.drawLine(scaleTextOffsetX, scaleArrowOffset, scaleTextOffsetX + scaleArrowOffset, scaleArrowOffset + 3);
		offset = this.getHeight() - scaleTextOffsetY;
		g.drawLine(getWidth() - scaleArrowOffset, offset, getWidth() - 3 - scaleArrowOffset, offset - 3); // Arrow Lines for x-Line
		g.drawLine(getWidth() - scaleArrowOffset, offset, getWidth() - 3 - scaleArrowOffset, offset + 3);
		
		g.setFont(new Font(null, Font.BOLD, fontSize + 1));
		g.drawString("Hz", this.getWidth() - 3 - 14, getHeight() - 2);
		g.drawString("Amp * " + this.maxAmpFactor, scaleTextOffsetX + 7, 15);
		
		g.setFont(new Font(null, Font.PLAIN, fontSize));
		// draw Frequencies on X-Axis
		int offsetPerText = dotsPerLetter * String.valueOf(maxFreq).length() + 8;
		float numSegments = (float)axisLenX / offsetPerText;
		for (int i = 1; i < numSegments - 1; ++i) {
			String freq = String.valueOf((int)(i * (maxFreq - minFreq) / numSegments + minFreq));
			g.drawString(freq, scaleTextOffsetX + offsetPerText * i - freq.length() * dotsPerLetter / 2,
					this.getHeight() - 2);
			g.drawLine(scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY + 3,
					scaleTextOffsetX + offsetPerText * i, this.getHeight() - scaleTextOffsetY - 3);
		}
		// draw Amplitudes on Y-Axis
		offsetPerText = 20;
		numSegments = (float)axisLenY / offsetPerText;
		for (int i = 1; i < numSegments - 1; ++i) {
			String amp = String.format("%" + maxAmplitudeLetters + "d",(int)(i * maxAmp * maxAmpFactor / numSegments));
			int yPosition = (int) (this.getHeight() - scaleTextOffsetY - i * axisLenY / numSegments);
			g.drawString(amp, 5, yPosition + 5);
			g.drawLine(scaleTextOffsetX - 3, yPosition, scaleTextOffsetX + 3, yPosition);
		}
	}

	protected final void drawGraph(Graphics g) {
		g.setColor(graphColor);
		if (format.getSampleSizeInBits() == 16) {
			// Don't display value 0(The DC Offset)
			int startIdx = Math.max(Math.round(minFreq / precision), 1); //take index corresponding to minFreq, but skip at least the DC Offset (amps[0])
			int endIdx = Math.round(maxFreq / precision);
			int IdxDiff = endIdx - startIdx + 1; //careful: one-by-off - both endIdx and startIdx count
			float dotsPerAmp = (float)axisLenX / IdxDiff;
			//i goes from the start index of amp[] to the end index of amp[]
			for (int i = startIdx; i < Math.min(amps.length, endIdx + 1); ++i) {
				int currentYVal = (int)(axisLenY  * amps[i] / (amps.length - 1) / maxAmp); //The (real) Amplitude is amps[i] / N/2, N/2 = amps.length - 1 . axisLenY / maxAmp = space per unit.
				if (dotsPerAmp < 2.1) //If the size of one bar is too small, draw instead of fill it(fill doesn't work under a certain value)
					g.drawRect((int)((i - startIdx) * dotsPerAmp) + scaleTextOffsetX, this.getHeight() - scaleTextOffsetY - currentYVal,
							(int)dotsPerAmp , currentYVal);
				else
					g.fillRect((int)((i - startIdx) * dotsPerAmp) + scaleTextOffsetX, this.getHeight() - scaleTextOffsetY - currentYVal,
							(int)dotsPerAmp , currentYVal);
			}
		}
	}
	
	private class AudioUpdater extends Thread {
		protected float[] fReal, fImag;
		protected byte[] bData1, bData2, bData;
		protected LineReader tRead;

		public AudioUpdater(int numData) {
			fReal = new float[numData];
			fImag = new float[numData];
			bData1 = new byte[numData * 2];
			bData2 = new byte[numData * 2];
		}

		@Override
		public void run() {
			try {
				openLine();
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage());
				stopAnalysis();
				return;
			}
			
			//bData always points to the array which is supposed to be processed,
			//the other array is being read into
			bData = bData1;
			tRead = new LineReader(bData2);
			tRead.run();
			while (isAnalysing) {
				tRead = new LineReader(bData);
				tRead.start();
				if (bData == bData1) {
					bData = bData2;
				} else {
					bData = bData1;
				}
				for (int i = 0; i < fReal.length; ++i) {
					fReal[i] = (float)(bData[2*i] | (bData[2*i + 1] << 8));
					fImag[i] = 0f;
				}
				FourierTransform.FFT(fReal,  fImag);
				FourierTransform.GetAmplitudes(fReal,  fReal, amps);
				repaint();
				try {
					tRead.join();
				} catch (InterruptedException e) {}
			}
			try {
				tRead.join();
			} catch (InterruptedException e) {}
			closeLine();
		}
	}

	protected class LineReader extends Thread {
		byte[] data;

		public LineReader(byte[] data) {
			this.data = data;
		}

		@Override
		public void run() {
			readLine(data);
		}

	}
	

	public float getLoudestFreq() {
		if (amps == null)
			return 0f;
		int LoudestFreqIdx = 10;
		for (int i = LoudestFreqIdx + 1; i < amps.length; ++i) {
			if (amps[i] > amps[LoudestFreqIdx])
				LoudestFreqIdx = i;
		}
		return LoudestFreqIdx * precision;
	}
	
	public float getAmpFromFreq(float freq) {
		if (amps == null)
			return 0f;
		int N = (amps.length - 1) * 2;
		int idx = (int) Math.round(freq * N / this.format.getFrameRate());
		if (idx >= amps.length)
			return 0f;
		
		if (freq == 0 || idx == amps.length - 1)
			return amps[idx] / N;
		return amps[idx] / (N / 2);
	}

	public void clearGraph() {
		for (int i = 0; i < amps.length; ++i)
			amps[i] = 0f;
		repaint();
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
		this.graphColor = new Color(10, 150, 10);
		this.setBackground(bgColor);
		repaint();
	}

	protected int getDataSizeByPrecision(float precision) {
		//Calculate dataSize(which must be 2^n)
		int dataSize = (int)(this.format.getSampleRate() / precision);
		int power = 31 - Integer.numberOfLeadingZeros(dataSize); //floor(log2(dataSize))
		if (dataSize - (1 << power) < ((1 << (power + 1)) - dataSize)) { //test which power of 2 is closer to dataSize
			dataSize = 1 << power;
		} else {
			dataSize = 1 << (power + 1);
		}
		return dataSize;
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

	public float getMaxAmplitude() {
		return this.maxAmp;
	}
	
	public AudioFormat getFormat() {
		return format;
	}

	public void setMaxFrequency(int freq) {
		if (freq <= minFreq)
			throw new IllegalArgumentException("Max Frequency must be bigger than the Min Frequency");
		this.maxFreq = freq;
		repaint();
	}

	public void setMinFrequency(int freq) {
		if (freq < 0 || freq >= this.maxFreq)
			throw new IllegalArgumentException("The Min Frequency must be bigger or equal to zero and lower than the Max Frequency");
		this.minFreq = freq;
		repaint();
	}

	public void setMaxAmplitude(float amp) {
		if (amp <= 0 || amp > 1)
			throw new IllegalArgumentException("Max Amplitude must be a value between 0 and 1");
		this.maxAmp = amp;
		//TODO: set maxAmpFactor so that most numbers between 0 and maxAmp have 2 decimals
		//this.maxAmpFactor = (int)Math.pow(10, String.valueOf((int)(maxAmp * 2000)).length()); //350 was chosen empirically
		repaint();
	}
				
	public void setFormat(AudioFormat format) {
		this.format = format;
		if (isAnalysing) {
			this.stopAnalysis();
			this.startAnalysis();
		}
	}

	public void setPrecision(float precision) {
		if (precision <= 0)
			throw new IllegalArgumentException("precision must be higher than 0");
		this.precision = precision;
		if (isAnalysing) {
			this.stopAnalysis();
			this.startAnalysis();
		}
	}
	
	public boolean isAnalysing() {
		return isAnalysing;
	}
	
	private class MouseDragManager implements MouseMotionListener, MouseListener {

		private int oldXPos, oldYPos;
		private float oldMaxAmp; 
		private int oldMinFreq, oldMaxFreq;
		private boolean dragFreq;
		@Override
		public void mousePressed(MouseEvent e) {
			oldXPos = e.getX();
			oldYPos = e.getY();
			oldMaxAmp = getMaxAmplitude();
			oldMinFreq = getMinFrequency();
			oldMaxFreq = getMaxFrequency();
			dragFreq = oldYPos > getHeight() - 30;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				float newAmp = oldMaxAmp * (axisLenY - oldYPos) / (axisLenY - e.getY());
				int newMaxFreq = (oldMaxFreq - oldMinFreq) * oldXPos / e.getX() + oldMinFreq;
				
				//Two modes: Scaling and Dragging. if click is under Y=30pts: Drag mode
				//Drag mode: "Scroll" through the Frequency without changing Amplitude
				//Scale mode: Change Max Frequency and Amplitude based on Mouse Movement
				setMaxFrequency(newMaxFreq);
				if (dragFreq) {
					setMinFrequency(oldMinFreq + getMaxFrequency() - oldMaxFreq);
				} else {
					setMaxAmplitude(newAmp);
				}
			} catch(ArithmeticException x) {
			} catch (IllegalArgumentException x) {}
		}
		
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent arg0) {}
		public void mouseMoved(MouseEvent e) {}
	}

}
