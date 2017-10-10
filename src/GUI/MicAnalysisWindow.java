package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class MicAnalysisWindow extends JFrame implements ActionListener {
	
	private AudioFrequencyDisplay  signalDisplay;
	private JButton toggleButton;
	
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		this.add(new JLabel("Zeichenfläche:"), BorderLayout.NORTH);
		signalDisplay = new AudioFrequencyDisplay(0, 20000, 300, 300);
		
		this.add(signalDisplay, BorderLayout.CENTER);
		
		toggleButton = new JButton("Start Analysis");
		toggleButton.addActionListener(this);
		this.add(toggleButton, BorderLayout.EAST);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		this.pack();
		this.setVisible(true);
		this.getGraphics().setColor(new Color(255, 0, 0));
		this.getGraphics().drawRect(30,  30,  50,  100);
		update(this.getGraphics());
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
		}
	}
	
}
