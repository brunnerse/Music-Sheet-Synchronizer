package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MusicSheet {

	private ArrayList<Bar> bars = new ArrayList<>();
	private int tempo = 60; //tempo in quarters per minute
	
	public MusicSheet(BufferedImage[] images) {
		this.scanSheet(images);
	}
	
	public void scanSheet(BufferedImage[] images) {
		
	}
	
	public void playSheet() {
		
	}
	
	public int getTempo() {
		return tempo;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public ArrayList<Bar> getBars() {
		return bars;
	}

	public class Bar {
		
		//There's no way to make the warning go away, and it doesnt make error, so its alright
		private ArrayList<Note> notes = new ArrayList<Note>();
		Takt takt;
		
		public Bar(Takt t, ArrayList<Note> notes) {
				
		}
		
		public ArrayList<Note> getNotes() {
			return notes;
		}

		//function to sort the Note Array by the time the notes come
		//needed to work with the Bar
		public void sortNotes() {
			//using insertion sort
			Note n;
			for (int i = 1; i < notes.size(); ++i) {
				for (int k = i; k > 0; --k) {
					n = notes.get(k - 1);
					if (notes.get(k).getTime() < n.getTime()) {
						notes.set(k - 1, notes.get(k));
						notes.set(k, n);
					} else {
						break;
					}
				}
			}
		}
	}
	
	//class to represent 4/4 Takt or sth like that
	public static class Takt {
		int first, scnd;
		float beatsPerBar;
		public Takt(int n, int divisor) {
			this.first = n;
			this.scnd = divisor;
			this.beatsPerBar = first/scnd;
		}
		
		public float getBeatsPerBar() {
			return beatsPerBar;
		}
		
		public int getFirst() {
			return first;
		}
		
		public int getScnd() {
			return scnd;
		}
		
		@Override
		public String toString() {
			return first + "/" + scnd;
		}
	}
	
}
