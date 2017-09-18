package GUI;

import javax.swing.JFrame;
import java.awt.event.*;
import javax.swing.JLabel;

public class MicAnalysisWindow extends JFrame {
	public MicAnalysisWindow() {
		super("Mikrophon Audioanalyse");
		this.add(new JLabel("Beispiel Label"));
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		this.setSize(200, 200);
		this.setVisible(true);
	}
	
}
