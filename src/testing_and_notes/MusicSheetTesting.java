package testing_and_notes;
import java.util.ArrayList;

import Music.*;
import Music.MusicSheet.*;


public class MusicSheetTesting {
	public static void main(String []args) {
		MusicSheet sheet = new MusicSheet(null);
		ArrayList<Bar> bars = sheet.getBars();
		sheet.setTempo(60);
		for (int i = 0; i < 5; ++i) {
			bars.add(new Bar(new Beat(4, 4)));
		}
		
		try {
			sheet.playSheet();
		} catch (Exception e) {
		}
	}
}
