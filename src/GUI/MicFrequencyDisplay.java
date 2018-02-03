package GUI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

//support for AudioFormats 16-bit signed and 8-bit signed
@SuppressWarnings("serial")
public class MicFrequencyDisplay extends AbstractFrequencyDisplay<TargetDataLine> {

	//if Constructor is called without giving a line, it will use the TargetDataLine of the AudioSystem
	public MicFrequencyDisplay(float precision, int minFrequency, int maxFrequency, int width, int height) {
		super(precision, minFrequency, maxFrequency, AbstractFrequencyDisplay.INTERVAL.DEFAULT, width, height);
	}

	@Override
	protected void setupLineAndFormat() throws RuntimeException {
		System.out.println("Finding supported audio format...");
		boolean formatFound = false;
		AudioFormat format;
		TargetDataLine line;
		int[] possibleSampleRates = {48000, 44100, 96000, 192000};
		for (int sampleRate : possibleSampleRates) {
			format = new AudioFormat(sampleRate, 16, 1, true, false);
			line = testAudioFormat(format);
			if(line != null) {
				System.out.println("Using AudioFormat " + format);
				formatFound = true;
				setLine(line);
				break;
			} else {
				System.out.println("The AudioFormat " + format + "isn't supported by the System.");
			}
		}
		if (!formatFound)
			throw new RuntimeException("The system doesn't support any known AudioFormat");
	}
	
	private TargetDataLine testAudioFormat(AudioFormat format) {
		TargetDataLine dLine;
		try {
			dLine = AudioSystem.getTargetDataLine(format);
			dLine.open();
			dLine.start();
			dLine.stop();
			dLine.close();
			//If all Operations suceeded, get the same line as new
			dLine = AudioSystem.getTargetDataLine(format);
		} catch(Exception e) {
			return null;
		}
		return dLine;
	}
	
	
	public void setLine(TargetDataLine line) {
		this.line = line;
		this.setFormat(line.getFormat());
		if (isAnalysing) {
			this.stopAnalysis();
			this.startAnalysis();
		}
	}

	@Override
	protected void readLine(byte[] data, int off, int len) {
		this.line.read(data, off, len);
	}

	@Override
	protected void openLine() throws Exception {
		try {
			this.line.open();
			this.line.start();
		} catch (LineUnavailableException e) {
			throw new Exception("Line unavailable: " + e.getMessage());
		}
	}

	@Override
	protected void closeLine() {
		this.line.stop();
		this.line.close();
	}

}
