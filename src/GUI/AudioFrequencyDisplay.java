package GUI;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import javax.sound.sampled.AudioFormat;

//support for AudioFormats 16-bit signed and 8-bit signed
@SuppressWarnings("serial")
public class AudioFrequencyDisplay extends Canvas {
	
	private boolean stop;
	
	private byte[] bSignal = null;
	private short[] sSignal = null;
	private AudioFormat format;
	
	private int minFreq = 0;
	private int maxFreq = 20000;
	
	public AudioFrequencyDisplay(int minFrequency, int maxFrequency, int length, int width) {
		super();
		this.minFreq = minFrequency;
		this.maxFreq = maxFrequency;
		this.setSize(length, width);
		
		this.setBackground(new Color(220, 220, 220));
		this.setVisible(true);
	}
	
	public AudioFrequencyDisplay(byte[] signal, AudioFormat format, int minFrequency, int maxFrequency, int length, int width) {
		this(minFrequency, maxFrequency, length, width);
		if (format.getSampleSizeInBits() != 8) {
			throw new IllegalArgumentException("A 8-bit signal was given, however the AudioFormat isn't 8 bits");
		}
		bSignal = signal;
		this.format = format;
		
		this.startAnalysis();
	}
	
	public AudioFrequencyDisplay(short[] signal, AudioFormat format, int minFrequency, int maxFrequency, int length, int width) {
		this(minFrequency, maxFrequency, length, width);
		if (format.getSampleSizeInBits() != 16) {
			throw new IllegalArgumentException("A 16-bit signal was given, however the AudioFormat isn't 16 bits");
		}
		sSignal = signal;
		this.format = format;
		
		this.startAnalysis();
	}
	
	private void drawScale() {
		
	}
	
	public void startAnalysis() {
		stop = false;
		new Thread(new Analysis());
	}
	
	public void stopAnalysis() {
		stop = true;
	}

	private class Analysis implements Runnable {
		@Override
		public void run() {
			
			while(!stop) {
				repaint();
			}
			
		}
	}	
	
	@Override
	public void paint(Graphics g) {
		//Whose Graphics is that
		System.out.println(g + "\n" + g.equals(this.getGraphics()) + "\n" + getGraphics());
		try { Thread.sleep(1000); } catch (Exception e) {}
	}
	
	public boolean isAnalysing() {
		return !stop;
	}

	
}
