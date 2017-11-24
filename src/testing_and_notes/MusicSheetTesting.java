package testing_and_notes;

import Music.Note;
import Music.MusicSheet;
import Music.Pitch;


public class MusicSheetTesting {
	public static void main(String []args) {
		MusicSheet sheet = new MusicSheet(null);
		sheet.setTempo(60);
		ForElise(sheet);
		try {
			sheet.playSheet();
		} catch (Exception e) {
		}
	}
	
	private static void ForElise(MusicSheet sheet) {
		sheet.addNote(new Note(new Pitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('d', 2), 1f, 8, Pitch.Vorzeichen.SHARP), true);
		sheet.addNote(new Note(new Pitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('d', 2), 1f, 8, Pitch.Vorzeichen.SHARP), true);
		sheet.addNote(new Note(new Pitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('h', 1), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('d', 2), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('c', 2), 1f, 8), true);
		sheet.addNote(new Note(new Pitch('a', 1), 1f, 32), true);
	}
}
