package freqProgram;

import java.awt.*;
import javax.sound.sampled.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class Frequency_Scrollbar extends Frame implements ActionListener, AdjustmentListener {
	private Button switchButton;
	private Scrollbar frequency, amplitude;
	private Label freqLabel, amplLabel;
	private SourceDataLine dataLine;
	private byte[] sampleList;
	private final int sampleRate = 16 * 1024;
	private final int sampleSizeinBits = 8;
	private final int playMS = 5000;
	
	public Frequency_Scrollbar() {
		super("Frequenz und Amplitude einstellen");
		AudioFormat af = new AudioFormat(sampleRate, sampleSizeinBits, 1, true, false);
		sampleList = new byte[sampleRate * playMS / 1000];
		try {
			dataLine = AudioSystem.getSourceDataLine(af);
			dataLine.open(af, sampleRate);
		} catch (LineUnavailableException e1) {
			System.err.println("Couldn't create SourceDataLine: " + e1.getMessage());
			System.exit(1);
		}
		dataLine.start();
		
		switchButton = new Button("On");
		switchButton.addActionListener(this);
		frequency = new Scrollbar(Scrollbar.HORIZONTAL, 440, 1, 1, 20001);
		amplitude = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 0 , 101);
		frequency.addAdjustmentListener(this);
		amplitude.addAdjustmentListener(this);
		freqLabel = new Label();
		amplLabel = new Label();
		updateLabels();
		this.setLayout(new GridLayout(5,1, 10, 10));
		this.add(switchButton);
		this.add(amplitude);
		this.add(amplLabel);
		this.add(freqLabel);
		this.add(frequency);
		
		this.add(amplitude);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dataLine.close();
				dispose();
			}
		});
		
		this.setBackground(new Color(200, 200, 200));
		this.setSize(1000, 200);
		this.setLocation(300, 300);
		this.setVisible(true);
		
	}

	
	private void updateLabels() {
		freqLabel.setText("Frequency: " + frequency.getValue() + " Hz");
		amplLabel.setText("Amplitude: " + amplitude.getValue());
	}
	
	private void updateSampleList(int frequency, int amplitude) {
		double period = (double)sampleRate / frequency;
		double amplifier = (double)amplitude / 100d;
		double sin;
		final int valueRange = (int)(Math.pow(2,  sampleSizeinBits - 1) - 1);
		int sinVal;
		final int sampleSizeinBytes = sampleSizeinBits / 8;
		for (int i = 0; i < sampleList.length / sampleSizeinBytes; i += sampleSizeinBytes) {
			sin = Math.sin(2 * Math.PI * i / period);
			sin *= amplifier;
			sinVal = (int)(sin * valueRange);
			for (int x = 0; x < sampleSizeinBytes; ++x) {
				sampleList[i + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
			
		}
		dataLine.stop();
		dataLine.flush();
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		updateLabels();
		updateSampleList(frequency.getValue(), amplitude.getValue());
		playSound();
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
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("On")) {
			updateSampleList(frequency.getValue(), amplitude.getValue());
			playSound();
			switchButton.setLabel("Off");
		} else if (e.getActionCommand().equals("Off")) {
			stopSound();
			switchButton.setLabel("On");
		}
	}
	
	
	//Threading class
	private class PlaySound extends Thread {
		public void run() {
				dataLine.write(sampleList,  0, sampleList.length);
		}
	}
	
	//main Function so it starts itself
	public static void main(String[] args) {
		new Frequency_Scrollbar();
	}
	
}
