package testing_and_notes;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import AudioFile.WAVWriter;

public class MicToWAV {
	public static void main(String[] s) throws IOException {
		AudioFormat f = new AudioFormat(44100f, 16, 2, true, false);
		WAVWriter w = new WAVWriter("testfile1.wav", f, "Rule The World", "Take That");
		w.open();
		w.write(new byte[] {1,  2,  3, 4, 5}, 0, 5);
		w.close();
	}
}
