package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import AudioFile.WAVWriter;
import AudioFile.WAVPlayer;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.SourceDataLine;
import Tools.ArrayConversion;

public class MusicSheet {

	private ArrayList<Bar> bars = new ArrayList<Bar>();
	private ArrayList<Note> notes = new ArrayList<Note>();
	// tempo in Quarters per minute
	private int tempo = 60;

	public MusicSheet(BufferedImage[] images) {
		//this.analyseSheet(images);
	}

	public void analyseSheet(BufferedImage[] images) {

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

	private static abstract class SheetToWAV {

		// the slope value at which the volume is decreasing at the end of the note
		private static final short step = 200;

		/*
		 * this is the lowest accuracy between each iteration in writeSheetToWAV. given
		 * in 1/64
		 */
		private static final int stepTime = 4;

		// how long the note plays if it's stacatto, plain or legato in parts of the
		// full length
		private static final float[] stopArticulation = { 0.95f, 0.1f, 0.99f }; // PLAIN = 0.95, STACCATO = 0.1, LEGATO
																				// = 0.99

		private static short DCOffset = 0; // TODO: Check when implementing audio

		public static void WriteSheetToWAV(MusicSheet sheet, String fileName) throws Exception {

			if (sheet.notes.isEmpty())
				return;
			sheet.sortNotes(); // TODO: Avoid this if possible
			WAVWriter writer = new WAVWriter(fileName, Pitch.getAudioFormat());
			// SourceDataLine line = AudioSystem.getSourceDataLine(Pitch.getAudioFormat());

			// number of 1/64 between each iteration, 4 means 4/64 = 1/16
			// is the lowest accuracy, for perfect results set to 1
			final int stepTime = 4;

			// enough space for stepTime/64 time
			// tempo * 64 / 60 / 4 1/64 per sec =>> t = stepTime * 60 * 4 / tempo / 64
			// number of samples = sampleRate * t = sampleRate * stepTime * 60 * 4 / 64 /
			// temp0
			short[] sArray = new short[(int) Pitch.getAudioFormat().getSampleRate() * stepTime * 60 * 4 / 64
					/ sheet.tempo]; //
			byte[] bArray = new byte[sArray.length * 2];

			try {
				writer.open();
				// currentNoteDurations has same size as currentPlayedNotes, the idx of a note
				// corresponds to the duration left
				ArrayList<NoteDurationPair> currentPlayedNotes = new ArrayList<NoteDurationPair>();
				// steps in 1/16, 1 time means 1/64
				Note lastNote = sheet.notes.get(sheet.notes.size() - 1);
				final int endTime = lastNote.getTime() + lastNote.getDuration();
				System.out.println(lastNote + "\t" + endTime);
				int nextTime;
				// current idx in the notelist where the note starts at the current time
				int timeIdx = 0;

				// line.open();
				// line.start();
				for (int time = 0; time < endTime; time = nextTime) {
					nextTime = time + stepTime;
					// Remove the notes from the last iteration
					//System.out.print(currentPlayedNotes.size() + " -\t");
					for (int x = 0; x < currentPlayedNotes.size(); ++x) {
						int noteDuration = currentPlayedNotes.get(x).duration - stepTime;
						//System.out.println("\t" + currentPlayedNotes.get(x) + "\t" + noteDuration);
						if (noteDuration <= 0)
							currentPlayedNotes.remove(x--); // x-- because the next element has index x
						else
							currentPlayedNotes.get(x).duration = noteDuration;
					}
					// add new Notes that start in the current period (between time and nextTime)
					while (timeIdx < sheet.notes.size() && sheet.notes.get(timeIdx).getTime() < nextTime) {
						currentPlayedNotes.add(
								new NoteDurationPair(sheet.notes.get(timeIdx), sheet.notes.get(timeIdx).getDuration()));
						timeIdx++;
					}
					
					for (int i = 0; i < sArray.length; ++i)
						sArray[i] = 0;

					for (NoteDurationPair pair : currentPlayedNotes) {
						Note n = pair.note;
						n.getPitch().read(bArray, bArray.length, n);
						applyArticulation(bArray, pair);
						// line.write(bArray, 0, bArray.length);
						for (int x = 0; x < sArray.length; ++x) {
							short sVal = ArrayConversion.reinterpByteToShort(bArray[x * 2], bArray[x * 2 + 1]);
							sVal = (short) (sVal * n.getVolume());
							sArray[x] += sVal; // TODO: to make Volume and multiple notes possible, you have to take the
												// DC OFFSET into consideration
							// TODO: Make ugly jump go away, see: Note_Frequency_Playback.java
						}
					}

					// convert the short Array into the byte Array
					ArrayConversion.reinterpShortToByte(sArray, bArray);
					writer.write(bArray, 0, bArray.length);
				}
			} finally {
				// line.stop();
				// line.close();
				writer.close();
			}
		}

		/**
		 * Function to be used by playSheet
		 */
		private static void applyArticulation(byte[] b, NoteDurationPair p) {
			// modify audio data to make the note legato or staccato
			float f = stopArticulation[p.note.getArticulation()];
			
			// i is the index at which the notelen ends
			int i = (int)((p.note.getDuration() * (f - 1f) + p.duration) * b.length / stepTime); 
			/* Explanation: the step at which the note ends step =  p.note.getDuration() * f
			 * the note length already played is p.note.getDuration() - p.duration
			 * the Index at which the note ends = step * b.length / stepTime 	because b.length / stepTime is the length of the byte array per step
			 */

			// i muss gerade sein
			i -= i % 2;

			if (i > b.length) { // the point at where the note ends comes in a later cicle
				return;
			} else if (i < -100) { // the point at where the note ends already passed
				byte []bDCOff = new byte[2];
				ArrayConversion.reinterpShortToByte(DCOffset, bDCOff);
				for (int idx = 0; idx < b.length; idx += 2) {
					b[idx] = bDCOff[0];
					b[idx + 1] = bDCOff[1];
				}
			} else {
				float volume = 1f, volStep = 0.01f;
				boolean reachedZero = false;
				short s;
				for (; i < b.length; i += 2) {
					if (reachedZero) {
						s = DCOffset;
					} else {
						s = ArrayConversion.reinterpByteToShort(b[i], b[i + 1]);
						volume -= volStep;
						s = (short)(volume * s);
						if (volume <= 0f)
							reachedZero = true;
					}
					ArrayConversion.reinterpShortToByte(s, b, i);
				}
			}
		}

		private static class NoteDurationPair {
			public Note note;
			public int duration;

			public NoteDurationPair(Note note, int duration) {
				this.note = note;
				this.duration = duration;
			}
		}

	}

	private static abstract class SheetAnalyser {
		
	}
}
