package testing_and_notes;

import Music.Note;
import Music.MusicSheet;
import Music.Pitch;


public class MusicSheetTesting {
	public static void main(String []args) {
		MusicSheet sheet = new MusicSheet(null);
		sheet.setTempo(60);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 64));
		//ForElise(sheet);
		try {
			sheet.playSheet();
		} catch (Exception e) {
			System.err.println("something went wrong: " + e.getMessage());
		}
	}
	
	private static void ForElise(MusicSheet sheet) {
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2), 1f, 8, Pitch.Vorzeichen.SHARP), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2), 1f, 8, Pitch.Vorzeichen.SHARP), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('h', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('c', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 32), true);
	}
}
