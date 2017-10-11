package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


@SuppressWarnings("serial")
public class MicAnalysisWindow extends JFrame implements ActionListener {
	
	private AudioFrequencyDisplay  signalDisplay;
	private JButton toggleButton;
	private JMenuItem mDark, mBright;
	
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		this.add(new JLabel("Zeichenfläche:"), BorderLayout.NORTH);
		signalDisplay = new AudioFrequencyDisplay(0, 20000, 600, 600);
		
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
			}
		});
		this.pack();
		this.setVisible(true);
		signalDisplay.startAnalysis();
		
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
		}
	}
	
}
