package GUI;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import Music.FrequencyAnalyzer;

@SuppressWarnings("serial")
public class MicAnalysisWindow extends JFrame implements ActionListener {
	
	private MicFrequencyDisplay  signalDisplay;
	private JButton toggleButton, clearButton;
	private JRadioButtonMenuItem mDark, mBright;
	private JLabel lPrecision, lMaxFreq;
	JTextField inMaxFreq, inMinFreq, inMaxAmp, inPrecision;
	private final int startMinFrequency = 100, startMaxFrequency = 2000;
	private final float startPrecision = 3f, startAmplitude = 0.1f;
	
	
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		lPrecision = new JLabel("  Precision: -");
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));
		p.add(lPrecision);
		lMaxFreq = new JLabel("Current Loudest Frequency: -");
		p.add(lMaxFreq);
		this.add(p, BorderLayout.NORTH);
		signalDisplay = new MicFrequencyDisplay(startPrecision, startMinFrequency, startMaxFrequency, 800, 600);
		signalDisplay.setMaxAmplitude(0.1f);
		signalDisplay.setDarkColorTheme();
		this.add(signalDisplay, BorderLayout.CENTER);
		
		JPanel controlButtons = new JPanel();
		controlButtons.setLayout(new GridLayout(6, 1));
		toggleButton = new JButton("Start Analysis");
		toggleButton.addActionListener(this);
		controlButtons.add(toggleButton);
		clearButton = new JButton("Clear Graph");
		clearButton.addActionListener(this);
		controlButtons.add(clearButton);
		inMinFreq = new JTextField(); inMinFreq.addActionListener(this); controlButtons.add(inMinFreq);
		inMaxFreq = new JTextField(); inMaxFreq.addActionListener(this); controlButtons.add(inMaxFreq);
		inMaxAmp = new JTextField(); inMaxAmp.addActionListener(this); controlButtons.add(inMaxAmp);
		inPrecision = new JTextField(); inPrecision.addActionListener(this); controlButtons.add(inPrecision);
		updateControlButtonText();
		this.add(controlButtons, BorderLayout.EAST);
		
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		JMenu m = new JMenu("Color theme");
		mb.add(m);
		mDark = new JRadioButtonMenuItem("Dark theme");
		mBright = new JRadioButtonMenuItem("Bright theme");
		m.add(mDark);
		m.add(mBright);
		mDark.addActionListener(this);
		mBright.addActionListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(mDark);
		group.add(mBright);
		mDark.setSelected(true);
		
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		new UpdateData().start();
		
		this.pack();
		this.setVisible(true);
	}

	private class UpdateData extends Thread {
		float prevPrecision = startPrecision;
		float prevFreq = 0, freq;
		float prevAmp = startAmplitude;
		int prevMinFreq = startMinFrequency, prevMaxFreq = startMaxFrequency;
		@Override
		public void run() {
			while(true) {
				if (!signalDisplay.isAnalysing()) {
						try { Thread.sleep(500); } catch (InterruptedException e) {}
				} else {
					if (signalDisplay.getPrecision() != prevPrecision) {
						lPrecision.setText(String.format("  Precision: %.2f Hz", signalDisplay.getPrecision()));
						prevPrecision = signalDisplay.getPrecision();
					}
					if (prevFreq != (freq = signalDisplay.getLoudestFreq())) {
						lMaxFreq.setText(String.format("\t\tLoudest Frequency: %.1f Hz (Note: %s), Amp %.4f",
								freq, FrequencyAnalyzer.getNoteFromFreq(freq), signalDisplay.getAmpFromFreq(freq)));
					}
					
					
					try { Thread.sleep(50); } catch (InterruptedException e) {}
				}
				if (prevAmp != signalDisplay.getMaxAmplitude() || prevMinFreq != signalDisplay.getMinFrequency() ||
						prevMaxFreq != signalDisplay.getMaxFrequency()) {
					updateControlButtonText();
					prevAmp = signalDisplay.getMaxAmplitude();
					prevMinFreq = signalDisplay.getMinFrequency();
					prevMaxFreq = signalDisplay.getMaxFrequency();
				}
			}
		}
	}
	
	private void updateControlButtonText() {
		inMaxFreq.setText("Max: " + this.signalDisplay.getMaxFrequency() + " Hz");
		inMinFreq.setText("Min: " + this.signalDisplay.getMinFrequency() + " Hz");
		inMaxAmp.setText("Max Amp: " + this.signalDisplay.getMaxAmplitude() + "");
		inPrecision.setText(String.format("Precision: %.2f Hz", this.signalDisplay.getPrecision()));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(toggleButton)) {
			if (signalDisplay.isAnalysing()) {
				signalDisplay.stopAnalysis();
				toggleButton.setText("Start Analysis");
				repaint();
			} else {
				signalDisplay.startAnalysis();
				toggleButton.setText("Stop Analysis");
			}
		} else if (e.getSource().equals(mDark)) {
			signalDisplay.setDarkColorTheme();
		} else if (e.getSource().equals(mBright)) {
			signalDisplay.setBrightColorTheme();
		} else if (e.getSource().equals(clearButton)) {
			signalDisplay.clearGraph();
		} else if (e.getSource().equals(inMaxFreq)) {
			try {
				this.signalDisplay.setMaxFrequency(Integer.valueOf(inMaxFreq.getText())); 
			} catch (IllegalArgumentException ex) {
				createWarningDialog("False Input: " + ex.getMessage());
			}
		} else if (e.getSource().equals(inMinFreq)) {
			try {
				this.signalDisplay.setMinFrequency(Integer.valueOf(inMinFreq.getText()));
			} catch (IllegalArgumentException ex) {
				createWarningDialog("False Input: " + ex.getMessage());
			}
		} else if (e.getSource().equals(inMaxAmp)) {
			try {
				this.signalDisplay.setMaxAmplitude(Float.valueOf(inMaxAmp.getText()));
			} catch (IllegalArgumentException ex) {
				createWarningDialog("False Input: " + ex.getMessage());
			}
		} else if (e.getSource().equals(inPrecision)) {
			try {
				signalDisplay.setPrecision(Float.valueOf(inPrecision.getText()));
			} catch (IllegalArgumentException ex) {
				createWarningDialog("False Input: " + ex.getMessage());
			}
		}
		updateControlButtonText();
	}
	
	private void createWarningDialog(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
}
