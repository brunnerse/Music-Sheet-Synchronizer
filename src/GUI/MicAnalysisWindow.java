package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MicAnalysisWindow extends JFrame implements ActionListener {
	
	private AudioFrequencyDisplay  signalDisplay;
	private JButton toggleButton;
	private JMenuItem mDark, mBright;
	private JLabel lPrecision, lMaxFreq;
	
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		lPrecision = new JLabel(" ");
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 3));
		p.add(lPrecision);
		p.add(new JLabel(" "));
		lMaxFreq = new JLabel(" ");
		p.add(lMaxFreq);
		this.add(p, BorderLayout.NORTH);
		signalDisplay = new AudioFrequencyDisplay(1 << 13, 100, 2000, 600, 600);
		
		this.add(signalDisplay, BorderLayout.CENTER);
		
		toggleButton = new JButton("Start Analysis");
		toggleButton.setSize(150, 50);
		toggleButton.addActionListener(this);
		this.add(toggleButton, BorderLayout.EAST);
		
		JMenuBar mb = new JMenuBar();
		this.setJMenuBar(mb);
		JMenu m = new JMenu("Color theme");
		mb.add(m);
		mDark = new JMenuItem("Dark theme");
		mBright = new JMenuItem("Bright theme");
		m.add(mDark);
		m.add(mBright);
		mDark.addActionListener(this);
		mBright.addActionListener(this);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		new Thread(new UpdateData()).start();
		this.pack();
		this.setVisible(true);
	}

	private class UpdateData extends Thread {
		public void run() {
			while(signalDisplay.isAnalysing()) {
				lPrecision.setText(String.format("Precision: %.1f Hz", signalDisplay.getPrecision()));
				lMaxFreq.setText(String.format("Current Loudest Frequency At %.1f Hz", signalDisplay.getLoudestFreq()));
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
