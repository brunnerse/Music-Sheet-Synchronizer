package GUI;

public class GUIStarter {

	public static void main(String[] args) {    
		new AudioAnalysisWindow(new WAVFrequencyDisplay("rule the world.wav", true, 800, 600));
		//new AudioAnalysisWindow();
	}

}
