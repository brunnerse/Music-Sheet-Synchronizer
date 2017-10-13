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
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;


@SuppressWarnings("serial")
public class MicAnalysisWindow extends JFrame implements ActionListener {
	
	private AudioFrequencyDisplay  signalDisplay;
	private JButton toggleButton;
	private JRadioButtonMenuItem mDark, mBright;
	private JLabel lPrecision, lMaxFreq;
	
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		lPrecision = new JLabel("Precision: -");
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 3));
		p.add(lPrecision);
		p.add(new JLabel(" "));
		lMaxFreq = new JLabel("Current Loudest Frequency: -");
		p.add(lMaxFreq);
		this.add(p, BorderLayout.NORTH);
		signalDisplay = new AudioFrequencyDisplay(1 << 16, 100, 500, 1000, 600);
		signalDisplay.setDarkColorTheme();
		this.add(signalDisplay, BorderLayout.CENTER);
		
		
		toggleButton = new JButton("Start Analysis");
		toggleButton.setSize(150, 50);
		toggleButton.addActionListener(this);
		this.add(toggleButton, BorderLayout.EAST);
		
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
		this.pack();
		
		this.setVisible(true);
	}

	private class UpdateData extends Thread {
		@Override
		public void run() {
			while(signalDisplay.isAnalysing()) {
				lPrecision.setText(String.format("Precision: %.1f Hz", signalDisplay.getPrecision()));
				lMaxFreq.setText(String.format("Current Loudest Frequency: %.1f Hz", signalDisplay.getLoudestFreq()));
				try { Thread.sleep(300); } catch(InterruptedException e) {}
			}
		}
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
				new Thread(new UpdateData()).start();
			}
		} else if (e.getSource().equals(mDark)) {
			signalDisplay.setDarkColorTheme();
		} else if (e.getSource().equals(mBright)) {
			signalDisplay.setBrightColorTheme();
		}
	}
	
}
