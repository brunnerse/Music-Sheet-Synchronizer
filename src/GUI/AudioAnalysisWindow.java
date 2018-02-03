package GUI;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import Music.Pitch;

@SuppressWarnings("serial")
public class AudioAnalysisWindow extends JFrame implements ActionListener, KeyListener {
	
	private FrequencyDisplay signalDisplay;
	private JButton toggleButton, clearButton;
	private JRadioButtonMenuItem mDark, mBright;
	private JLabel lPrecision, lMaxFreq;
	JTextField inMaxFreq, inMinFreq, inMaxAmp, inPrecision;
	
	private static final int startMinFrequency = 100, startMaxFrequency = 2000;
	private static final float startPrecision = 3f, startAmplitude = 0.1f;

	
	public AudioAnalysisWindow () {
		this(new MicFrequencyDisplay(startPrecision, startMinFrequency, startMaxFrequency, 800, 600));
	}
	
	public AudioAnalysisWindow(FrequencyDisplay display) {
		super("Mikrophon Audioanalyse");
		this.signalDisplay = display;
		signalDisplay.setMaxAmplitude(0.1f);
		signalDisplay.setDarkColorTheme();
		this.add(signalDisplay, BorderLayout.CENTER);
		
		lPrecision = new JLabel("");
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));
		p.add(lPrecision);
		lMaxFreq = new JLabel("");
		p.add(lMaxFreq);
		this.add(p, BorderLayout.NORTH);
		
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
		
		
		this.addKeyListener(this);

		this.setFocusable(true);
		this.requestFocus();
		this.signalDisplay.addKeyListener(this);
		for (Component c : this.getComponents()) {
			
			c.addKeyListener(this);
		}
		
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
								freq, Pitch.getPitchFromFreq(freq), signalDisplay.getAmpFromFreq(freq)));
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
	
	
	private void createWarningDialog(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	public FrequencyDisplay getFrequencyDisplay() {
		return this.signalDisplay;
	}
	
	public void setFullScreen(boolean b) {
		dispose();
		if (b) {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			this.setUndecorated(true);
		} else {
			this.setUndecorated(false);
			this.setExtendedState(JFrame.NORMAL);
		}
		this.setVisible(true);
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
	
	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.setFullScreen(false);
		} else if (e.getKeyCode() == KeyEvent.VK_F11) {
			this.setFullScreen(true);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
}
