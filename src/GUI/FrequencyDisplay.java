package GUI;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

//interface to allow external classes access to the FrequencyDisplay functions
@SuppressWarnings("serial")
public abstract class FrequencyDisplay extends JPanel {
	
	public abstract void startAnalysis();
	public abstract void stopAnalysis();
	public abstract boolean isAnalysing();
	
	public abstract float getLoudestFreq();
	public abstract float getAmpFromFreq(float freq);
	
	public abstract void clearGraph();
	
	public abstract void setDarkColorTheme();
	public abstract void setBrightColorTheme();
	
	public abstract float getPrecision();
	
	public abstract int getMaxFrequency();
	public abstract int getMinFrequency();
	public abstract float getMaxAmplitude();
	
	public abstract  AudioFormat getFormat();
	
	public abstract void setFormat(AudioFormat format);
	public abstract void setPrecision(float precision);
	public abstract void setMaxAmplitude(float amp);
	public abstract void setMaxFrequency(int freq);
	public abstract void setMinFrequency(int freq);
}
