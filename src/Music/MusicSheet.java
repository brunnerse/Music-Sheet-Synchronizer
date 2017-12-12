package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import AudioFile.WAVPlayer;

public class MusicSheet {

	private ArrayList<Bar> bars = new ArrayList<Bar>();
	private ArrayList<Note> notes = new ArrayList<Note>();
	// tempo in Quarters per minute
	private int tempo = 60;
	private BufferedImage[] images;

	public MusicSheet(BufferedImage[] images) {
		this.images = images;
		//this.analyseSheet(images);
	}

	public void analyseSheet() {
		SheetAnalyser.analyse(this);
	}
	
	public void analyseSheet(BufferedImage[] images) {
		this.images = images;
		analyseSheet();
	}

	// Creates a WAV file and writes into it.
	public void playSheet() throws Exception {
		String fileName = "temp - sheetfile.wav";
		SheetToWAV.WriteSheetToWAV(this, fileName);

		System.out.println("playing..");
		WAVPlayer.play(fileName);
		System.out.println("stopped playing.");
	}

	/**
	 * @return tempo which is saved in quarters per minute
	 */
	public int getTempo() {
		return tempo;
	}

	/**
	 * @param tempo
	 *            in quarters per minute
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public ArrayList<Bar> getBars() {
		return bars;
	}

	public ArrayList<Note> getNotes() {
		return notes;
	}

	public BufferedImage[] getImages() {
		return this.images;
	}
	/**
	 * 
	 * @param n:
	 *            Note to be added to the note-Array
	 * @param setTime:
	 *            if true, Note.time will be set when the previous note ends,
	 *            otherwise, Note.time will stay at its current value
	 */
	public void addNote(Note n, boolean setTime) {
		if (setTime) {
			if (notes.isEmpty())
				n.setTime(0);
			else {
				Note prevNote = notes.get(notes.size() - 1);
				n.setTime(prevNote.getTime() + prevNote.getDuration());
			}
		}
		notes.add(n);
	}

	/**
	 * 
	 * @param n:
	 *            Note to be added to the note-Array CAREFUL: The correct time in
	 *            the note must already be set. If the function should set it
	 *            automatically to the next position, call MusicSheet.addNote(Note
	 *            n, true);
	 */
	public void addNote(Note n) {
		this.addNote(n, false);
	}

	/**
	 * sorts the Notes in the Note Array by time and duration
	 */
	public void sortNotes() {
		// using insertion sort
		Note n;
		for (int i = 1; i < notes.size(); ++i) {
			for (int k = i; k > 0; --k) {
				n = notes.get(k - 1);
				if (notes.get(k).getTime() <= n.getTime()) {
					if (notes.get(k).getTime() < n.getTime() || notes.get(k).getDuration() < n.getDuration()) {
						notes.set(k - 1, notes.get(k));
						notes.set(k, n);
					}
				} else {
					break;
				}
			}
		}
	}

}
