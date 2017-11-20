package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MusicSheet {

	
	private ArrayList<Bar> bars = new ArrayList<>();
	private int tempo = 60; //tempo in quarters per minute
	private int numParts = 1;
	
	public MusicSheet(BufferedImage[] images) {
		this.scanSheet(images);
	}
	
	public void scanSheet(BufferedImage[] images) {
		
	}
	
	public void playSheet() {
		
	}
	
	private class Bar {
		barMetre metre;
		ArrayList<Note>[] parts;
		
		public Bar() {
			parts = new ArrayList[numParts];
			for (int i = 0; i < numParts; ++i)
				parts[i] = new ArrayList<>();
		}
		
	}
	
	private class Note {
		
	}
	
	public enum barMetre {FIVE_QUARTER, FOUR_QUARTER, THREE_QUARTER, TWO_QUARTER, ONE_QUARTER};
	
}
