package GUI;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import AudioFile.WAVReader;

@SuppressWarnings("serial")
public class WAVFrequencyDisplay extends AbstractFrequencyDisplay<WAVReader> {

	private String file;
	private boolean play;
	private AudioFormat playBackAudioFormat;
	private SourceDataLine playBackLine;

	private static final int startMinFrequency = 100, startMaxFrequency = 2000;
	private static final float startPrecision = 3f;

	// boolean play: Set whether the file should be played during the analyis or not
	public WAVFrequencyDisplay(String file, boolean play, float precision, int minFrequency, int maxFrequency,
			int width, int height) {
		super(precision, minFrequency, maxFrequency, width, height);
		setFile(file);
		this.play = play;
	}

	public WAVFrequencyDisplay(String file, boolean play, int width, int height) {
		this(file, play, startPrecision, startMinFrequency, startMaxFrequency, width, height);
	}

	public void setFile(String file) {
		this.file = file;
		if (isAnalysing)
			this.stopAnalysis();
	}

	@Override
	protected void setupLineAndFormat() throws IOException {
		this.line = new WAVReader(file);
		this.line.open();
		this.setFormat(line.getFormat());
		this.playMusic(this.play);
	}

	@Override
	protected void readLine(byte[] data) {

		long time, end, start = System.currentTimeMillis();
		int channels = format.getChannels();
		int sampleSize = format.getSampleSizeInBits() / 8;
		byte []buffer = new byte[(channels - 1) * sampleSize];
		//if WAV has more than one channel, you need to skip every channel but the first 
		//as data should only contain data of one channel
		try {
			int offset = 0;
			for (int i = 0; i < data.length; i += sampleSize) {
				if (line.read(data, offset, sampleSize) < sampleSize) // If file reached end, reset file to start
					line.resetToStart();
				offset += sampleSize;
				if (buffer.length > 0) {
					line.read(buffer, 0, buffer.length);
				}
			}
		} catch (IOException e) {
			return;
		}
		if (play) {
			this.playBackLine.write(data, 0, data.length);
		}
		
		 //*Note: This block shouldn't be needed when using playBackLine.drain();
		// Artificially slow down the reading process so the data match the time
		time = (long) (data.length * 1000 / (sampleSize * format.getSampleRate()));
		end = System.currentTimeMillis();
		time -= end - start;
		if (time > 0) {
			try {
				Thread.sleep(time - 2);
			} catch (InterruptedException e) {
			}
		}
		

	}

	public void playMusic(boolean play) {
		if (this.format == null) {
			System.err.println("Error: Audioformat is not yet set");
			return;
		}
		this.play = play;
		if (play) {
			
			try {
				this.playBackAudioFormat = new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 1, true, format.isBigEndian());
				this.playBackLine = AudioSystem.getSourceDataLine(playBackAudioFormat);
			} catch (LineUnavailableException e) {
				System.err.println("ERROR: SourceDataLine is unavailable");
				play = false;
				return;
			}
		}
		if (isAnalysing) {
			this.stopAnalysis();
			this.startAnalysis();
		}
		
	}

	@Override
	protected void openLine() throws Exception {
		// this.line.resetToStart();
		if (play) {
			this.playBackLine.open();
			this.playBackLine.start();
		}
		
	}

	@Override
	protected void closeLine() {
		try {
			this.line.close();
			if (play) {
				this.playBackLine.stop();
				this.playBackLine.close();
			}
		} catch (IOException e) {
			this.line = null;
		}
	}

}
