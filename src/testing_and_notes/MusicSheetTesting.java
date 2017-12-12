package testing_and_notes;

import Music.Note;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import Music.MusicSheet;
import Music.Pitch;
import Music.Pitch.Vorzeichen;
import TestPrograms.GUI.MainEntryPoint;


public class MusicSheetTesting {
	public static void main(String []args) {
		testAnalyse();
		
		//testPlay();
	}
	
	private static void testAnalyse() {
		BufferedImage[] images = MainEntryPoint.startImageScroller();
		Graphics2D g = images[0].createGraphics();
		g.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.clearRect(50,  50,  50,  50);
		MusicSheet sheet = new MusicSheet(images);
		sheet.analyseSheet();
		
	}
	
	private static void testPlay() {
		MusicSheet sheet = new MusicSheet(null);
		sheet.setTempo(120);
		ForElise(sheet);
		try {
			sheet.playSheet();
		} catch (Exception e) {
			System.err.println("something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void ForElise(MusicSheet sheet) {
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2, Vorzeichen.SHARP), 0.5f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2, Vorzeichen.SHARP), 1f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 2), 1f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('h', 1), 1f, 8, Note.Articulation.STACCATO), true);
		sheet.addNote(new Note(Pitch.getPitch('d', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('c', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 24), true);
		sheet.addNote(new Note(Pitch.getPitch('c', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('h', 1), 1f, 24), true);
		sheet.addNote(new Note(Pitch.getPitch('e', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('c', 2), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('h', 1), 1f, 8), true);
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 1f, 32), true);

		//notes that play parallel
		sheet.addNote(new Note(Pitch.getPitch('e', 1), 0.5f, 32, 0));
		sheet.addNote(new Note(Pitch.getPitch('a', 0), 0.5f, 8, 64));
		sheet.addNote(new Note(Pitch.getPitch('e', 1), 0.5f, 8,72));
		sheet.addNote(new Note(Pitch.getPitch('a', 1), 0.5f, 8, Note.Articulation.LEGATO, 80));
		sheet.addNote(new Note(Pitch.getPitch('h', 0), 0.5f, 24, 112));
		sheet.addNote(new Note(Pitch.getPitch('a', -1), 1f, 32, 160));
		sheet.addNote(new Note(Pitch.getPitch('a', 0), 1f, 32, 160));
		sheet.addNote(new Note(Pitch.getPitch('a', -2), 1f, 32, 160));
	}
}
