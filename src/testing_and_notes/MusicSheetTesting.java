package testing_and_notes;

import Music.Note;
import Music.MusicSheet;
import Music.Pitch;
import Music.Pitch.Vorzeichen;


public class MusicSheetTesting {
	public static void main(String []args) {
		MusicSheet sheet = new MusicSheet(null);
		sheet.setTempo(90);
		//sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 64), true);
		ForElise(sheet);
		try {
			sheet.playSheet();
		} catch (Exception e) {
			System.err.println("something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void ForElise(MusicSheet sheet) {
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 0.5f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2, Vorzeichen.SHARP), 0.5f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 0.3f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2, Vorzeichen.SHARP), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 0.5f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('h', 1), 0.5f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('c', 2), 0.5f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 32), true);
		
		//notes that play parallel
		sheet.addNote(new Note(Pitch.getPitch('e', 1), 0.5f, 32, 0));
		sheet.addNote(new Note(Pitch.getPitch('h', 0), 0.5f, 16, 48));
	}
}
