package TestPrograms;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainEntryPoint {

	public static void main(String[] args) throws Exception {
		recordSomeTimes(2);
	}

	public static void recordNotes() throws Exception {
		final float secs = 1.486f;
		final String instrument = "Piano";
		String[] notes = { "c1", "c2", "d1", "d2", "e1", "e2", "f1", "f2", "g1", "g2", "a1", "a2", "b1", "b2", "f#1", "f#2" };
		String[] kinds = { " - held" };
		for (int notesIdx = 0; notesIdx < notes.length; ++notesIdx) {
			for (String kind : kinds) {
				String s = instrument + " - " + notes[notesIdx] + kind;
				MicToWAVFile.openFile(s + ".wav", s, "TheGadgeteer");
				for (int i = 3; i > 0; i--) {
					System.out.print("\rRecording " + s + " in " + i + " seconds...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				System.out.println("\nRecording for " + secs + " seconds...");
				MicToWAVFile.recordTime(secs, false);
				System.out.println("Recording finished.");
				MicToWAVFile.closeFile();
			}
		}
	}

	public static void recordSomeTimes(int times) throws Exception {
		MicToWAVFile.openFile("testing.wav");
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		MicToWAVFile.startRecording();
		MicToWAVFile.stopRecording();
		for (int i = 0; i < times; ++i) {
			System.out.println("enter to record, enter to stop recording");
			b.readLine();
			System.out.println("Recording...");
			MicToWAVFile.startRecording();
			b.readLine();
			MicToWAVFile.stopRecording();
			System.out.println("Recording stopped.");
		}
		MicToWAVFile.closeFile();
	}
}
