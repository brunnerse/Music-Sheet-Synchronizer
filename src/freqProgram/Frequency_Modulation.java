package freqProgram;

import java.awt.*;
import javax.sound.sampled.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class Frequency_Modulation extends Frame
implements ActionListener, AdjustmentListener, ItemListener {
	private Button switchButton;
	private Scrollbar leftChannel, rightChannel;
	private Checkbox channelCheckbox[];
	private TextField freqTextField[];
	private TextField ampTextField[];
	private Label leftLabel, rightLabel;
	private SourceDataLine dataLine;
	private byte[] sampleList;
	private final int sampleRate = 96000; //Vorbild andere WAV- Datei
	private final int sampleSizeinBits = 16;
	private final int sampleSize = (int)Math.ceil(sampleSizeinBits / 8);
	private final int playMS = 5000;
	private final int channels = 2;
	private final int numWaves = 6;
	
	public Frequency_Modulation() {
		super("Frequenzmodulation : Sinuswellenüberlagerung");
		AudioFormat af = new AudioFormat(sampleRate, sampleSizeinBits, channels, true, false);
		sampleList = new byte[sampleRate * sampleSize * channels / 1000 * playMS];
		System.out.println("Audioformat: " + af.toString());
		try {
			dataLine = AudioSystem.getSourceDataLine(af);
			dataLine.open(af, sampleRate * 5);
		} catch (LineUnavailableException e1) {
			System.err.println("Couldn't create SourceDataLine: " + e1.getMessage());
			System.exit(1);
		}
		dataLine.start();
		

		
		leftChannel = new Scrollbar(Scrollbar.VERTICAL, 50, 1, 0, 101);
		rightChannel = new Scrollbar(Scrollbar.VERTICAL, 50, 1, 0, 101);
		

		Panel settings = new Panel();
		settings.setLayout(new GridLayout(numWaves + 1, 4, 10, 10));
		for (String s : new String[] {"Channel Nr.", "", "Frequency [Hz]", "Amplitude [1:100]"}) {
			System.out.println(s);
			settings.add(new Label(s));
		}
		channelCheckbox = new Checkbox[numWaves];
		freqTextField = new TextField[numWaves];
		ampTextField = new TextField[numWaves];
		for (int i = 0; i < numWaves; ++i) {
			settings.add(new Label("Wave " + i));
			channelCheckbox[i] = new Checkbox("use");
			settings.add(channelCheckbox[i]);
			freqTextField[i] = new TextField(10);
			settings.add(freqTextField[i]);
			ampTextField[i] = new TextField(10);
			settings.add(ampTextField[i]);
			
			channelCheckbox[i].addItemListener(this);
			freqTextField[i].addActionListener(this);
			ampTextField[i].addActionListener(this);
		}
		
		Panel North  = new Panel();
		North.setLayout(new GridLayout(1, 4, 15, 0));
		leftLabel = new Label();
		North.add(leftLabel);
		switchButton = new Button("On/Off");
		
		North.add(switchButton, BorderLayout.NORTH);
		Button reset = new Button("Reset");
		
		North.add(reset);
		rightLabel = new Label();
		North.add(rightLabel);
		
		((BorderLayout)this.getLayout()).setHgap(15);
		this.add(North, BorderLayout.NORTH);
		this.add(leftChannel, BorderLayout.WEST);
		this.add(settings, BorderLayout.CENTER);
		this.add(rightChannel, BorderLayout.EAST);
		
		leftChannel.addAdjustmentListener(this);
		rightChannel.addAdjustmentListener(this);
		switchButton.addActionListener(this);
		reset.addActionListener(this);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dataLine.close();
				dispose();
			}
		});
		
		resetValues();
		this.setBackground(new Color(230, 230, 230));
		this.pack();
		this.setLocation(300, 300);
		this.setVisible(true);
		
	}
	
	
	private void resetValues() {
		channelCheckbox[0].setState(true);
		freqTextField[0].setText("440");
		ampTextField[0].setText("50");
		for (int i = 1; i < 6; ++i) {
			channelCheckbox[i].setState(false);
			freqTextField[i].setText(" ");
			ampTextField[i].setText(" ");
		}
		leftChannel.setValue(50);
		rightChannel.setValue(50);
		updateLabels();
	}
	
	private int getScrollValue(boolean left) {
		if (left)
			return 100 - leftChannel.getValue();
		return 100 - rightChannel.getValue();
	}
	
	private void updateLabels() {
			leftLabel.setText("Left Speaker Amp: " + getScrollValue(true));
			rightLabel.setText("Right Speaker Amp: " + getScrollValue(false));
	}
	
	private void updateSampleList() {
		System.out.println("Updating Sample List...");
		WaveManager[] ch = fetchData();
		double sin;
		final int valueRange = (int)(Math.pow(2,  sampleSizeinBits - 1) - 1);
		int sinVal;
		for (int i = 0; i < sampleList.length; i += sampleSize * channels) {
			sin = 0;
			int curSample = i / (sampleSize * channels);
			for(int idx = 0; idx < ch.length; ++idx) {
				sin += Math.sin(2 * Math.PI * curSample / ch[idx].samplesPerPeriod) * ch[idx].amplitude;
			}
			//Left Sample
			sinVal = limitVal((int)(sin * getScrollValue(true) / 100 * valueRange), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
			//Right Sample
			sinVal = limitVal((int)(sin * getScrollValue(false) / 100 * valueRange), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + 2 + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
		}
		boolean restart = dataLine.isRunning();
		dataLine.stop();
		dataLine.flush();
		if (restart) {
			dataLine.start();
			new PlaySound().start();
		}
	}
	
	private void playSound() {
		dataLine.start();
		new PlaySound().start();
	}
	private void stopSound() {
		dataLine.stop();
		dataLine.flush();
	}
	

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
			updateLabels();
			System.out.println("Updating Sample List...");
			updateSampleList();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("On/Off")) {
			updateSampleList();
			if(dataLine.isRunning())
				stopSound();
			else
				playSound();
		} else if (e.getActionCommand().equals("Reset")) {
			this.resetValues();
			stopSound();
		} else if (e.getSource().getClass().equals(TextField.class)) {
				updateSampleList();
		}
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		updateSampleList();
	}
	
	//Threading class
	private class PlaySound extends Thread {
		public void run() {
				dataLine.write(sampleList,  0, sampleList.length);
		}
	}
	
	private class WaveManager {
		@SuppressWarnings("unused")
		public double frequency;
		public double amplitude;
		public double samplesPerPeriod;
		
		public WaveManager(double frequency, double amplitude, double sampleRate) {
			this.frequency = frequency;
			this.amplitude = amplitude;
			this.samplesPerPeriod = sampleRate / frequency;
		}
	}
	
	public WaveManager[] fetchData() {
		int numActiveWaves = 0;
		for (int x = 0; x < numWaves; ++x) {
			if (channelCheckbox[x].getState())
				numActiveWaves += 1;
		}
		int idx = 0;
		double freq, amp;
		WaveManager[] wm = new WaveManager[numActiveWaves];
		for (int i = 0; i < numWaves; ++i) {
			if (!channelCheckbox[i].getState())
				continue;
			try {
				freq = Double.parseDouble(freqTextField[i].getText());
				amp = Double.parseDouble(ampTextField[i].getText()) / 100d;
			} catch (NumberFormatException e) {
				freq = 0d;
				amp = 0d;
			}
			wm[idx++] = new WaveManager(freq, amp, sampleRate);
		}
		return wm;
	}
	
	
	private int limitVal(int val, int min, int max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}
	//main Function so it starts itself
	public static void main(String[] args) {
		new Frequency_Modulation();
	}

}
