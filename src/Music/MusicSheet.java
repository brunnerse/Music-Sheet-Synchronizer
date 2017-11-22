package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class MusicSheet {

	
	private ArrayList<Bar> bars = new ArrayList<>();
	private Voice[] voices;
	private int tempo = 60; //tempo in quarters per minute
	private int numParts = 1;
	
	public MusicSheet(BufferedImage[] images) {
		this.scanSheet(images);
	}
	
	public void scanSheet(BufferedImage[] images) {
		
		this.voices = new Voice[numParts];
		for (int i = 0; i < numParts; ++i) {
			ArrayList<Note> DynNotes = new ArrayList<Note>();
		
			
			
			this.voices[i].notes = DynNotes.toArray(this.voices[i].notes);
		}
	}
	
	public void playSheet() {
		
	}
	
	private class Bar {
		
		//There's no way to make the warning go away, and it doesnt make error, so its alright
		ListIterator<Note> []positions;
		Takt takt;
		
		@SuppressWarnings("unchecked")
		public Bar(Takt t) {
			positions = (ListIterator<Note>[]) new Object[numParts];
			this.takt = t;	
		}
		
		public void setPosition(ListIterator<Note> iterator, int voice) {
			this.positions[voice] = iterator;
		}

	}

	
	//class to represent 4/4 Takt or sth like that
	private static class Takt {
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
	
	
	private static class Voice {
		public Note[] notes;
	}
	
}
