package GUI;

public class GUIStarter {

	public static void main(String[] args) {    
		new AudioAnalysisWindow(new WAVFrequencyDisplay("Piano - a1 - held.wav", true, 800, 600));
		//new AudioAnalysisWindow();
	}

}
