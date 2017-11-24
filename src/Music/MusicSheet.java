package Music;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import AudioFile.WAVWriter;
import AudioFile.WAVPlayer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class MusicSheet {

	private ArrayList<Bar> bars = new ArrayList<Bar>();
	private ArrayList<Note> notes = new ArrayList<Note>();
	// tempo in Quarters per minute
	private int tempo = 60;

	public MusicSheet(BufferedImage[] images) {
		this.scanSheet(images);
	}

	public void scanSheet(BufferedImage[] images) {

	}

	// Creates a WAV file and writes into it.
	public void playSheet() throws Exception {
		if (notes.isEmpty())
			return;
		sortNotes(); //TODO: Avoid this if possible
		final int sampleRate = 44100;
		final String fileName = "temp - sheetfile.wav";
		WAVWriter writer = new WAVWriter(fileName, new AudioFormat(sampleRate, 16, 1, true, false));
		// enough space for 1/16 of time
		short[] sArray = new short[sampleRate * 60 / tempo / 4]; // tempo / 60 = quarters per second,
																	// 1/4 = 1/16 per quarter
		byte[] bArray = new byte[sArray.length * 2];
		
		try {
			writer.open();
			ArrayList<Note> currentPlayedNotes = new ArrayList<Note>();
			// steps in 1/16, 1 time means 1/64
			Note lastNote = notes.get(notes.size() - 1);
			int endTime = lastNote.getTime() + lastNote.getDuration();
			int nextTime;
			int timeIdx = 0;
			SourceDataLine line = AudioSystem.getSourceDataLine(new AudioFormat(sampleRate, 16, 1, true, false));
			line.open();
			line.start();
			for (int time = 0; time < endTime; time = nextTime) { //TODO
				nextTime = time + 4;
				//Remove the notes from the last iteration
				for (int x = 0; x < currentPlayedNotes.size(); ++x) {
					int noteTime = currentPlayedNotes.get(x).getTime() - 4;
					if (noteTime <= 0)
						currentPlayedNotes.remove(x);
					else
						currentPlayedNotes.get(x).setTime(noteTime);
				}
				
				while (notes.get(timeIdx).getTime() < nextTime) {
					currentPlayedNotes.add(notes.get(timeIdx));
					timeIdx++;
				}
				
				for (int i = 0; i < sArray.length; ++i)
					sArray[i] = 0;
				for (Note n : currentPlayedNotes) {
					FrequencyAnalyzer.getFrequencyPitch(n.getPitch()).read(bArray, bArray.length, n); 
					/*
					 * TODO: Idea: Instead of every note having its own pitch, you link each Note one pitch,
					 * so you dont have to search for the right pitch everytime.
					 */
					line.write(bArray,  0,  bArray.length);
					for (int x = 0; x < sArray.length; ++x) {
						sArray[x] += (short)(n.getVolume() * (bArray[x * 2] | (bArray[x * 2 + 1] << 8)));
					}
				}
				
				for (int i = 0; i < sArray.length; ++i) {
					bArray[i * 2] = (byte) sArray[i];
					bArray[i * 2 + 1] = (byte) (sArray[i] >> 8);
				}
				writer.write(bArray, 0, bArray.length);
			}
			line.stop();
			line.close();
			writer.close();
			System.out.println("playing..");
			WAVPlayer.play(fileName);
		} catch (IOException e) {
			throw new Exception(((Exception) e).getMessage());
		} finally {
			writer.close();
		}
		
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
	 * sorts the Notes in the Note Array by time
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

	public static class Bar {
		private int startIdx;
		private Beat takt;

		/**
		 * @param t:
		 *            Beat of the Bar
		 * @param startIdx:
		 *            Idx of the first note of the Bar in the note-Array
		 */
		public Bar(Beat t, int startIdx) {
			this.takt = t;
			this.startIdx = startIdx;
		}

		public Beat getBeat() {
			return takt;
		}

		/**
		 * @return the index of the first note in the Bar in the note-Array
		 */
		public int getStartIdx() {
			return startIdx;
		}
	}

	public static class Beat {
		int first, scnd;
		float beatsPerBar;

		public Beat(int n, int divisor) {
			this.first = n;
			this.scnd = divisor;
			this.beatsPerBar = first / scnd;
		}

		/**
		 * @return the number of beats in one bar as Ones (e.g. 4/4 returns 1, 3/4
		 *         return 0.75)
		 */
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
