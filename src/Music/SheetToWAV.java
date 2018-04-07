package Music;

import java.util.ArrayList;

import AudioFile.WAVWriter;
import Tools.ArrayConversion;

abstract class SheetToWAV {

	//steps in which the volume goes down when the note ends
	private static float volStep = 0.01f;
	
	/*
	 * this is the lowest accuracy between each iteration in writeSheetToWAV. given
	 * in 1/64
	 */
	private static final int stepTime = 4;

	// how long the note plays if it's stacatto, plain or legato in parts of the
	// full length
	private static final float[] stopArticulation = { 0.92f, 0.1f, 0.99f }; // PLAIN = 0.95, STACCATO = 0.1, LEGATO
																			// = 0.99

	private static short DCOffset = 0; // TODO: Check when implementing audio

	public static void WriteSheetToWAV(MusicSheet sheet, String fileName) throws Exception {
		ArrayList<Note> notes = sheet.getNotes();
		if (notes.isEmpty())
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
				/ sheet.getTempo()]; //
		byte[] bArray = new byte[sArray.length * 2];

		try {
			writer.open();
			// currentNoteDurations has same size as currentPlayedNotes, the idx of a note
			// corresponds to the duration left
			ArrayList<NoteDurationPair> currentPlayedNotes = new ArrayList<NoteDurationPair>();
			// steps in 1/16, 1 time means 1/64
			Note lastNote = notes.get(notes.size() - 1);
			final int endTime = lastNote.getTime() + lastNote.getDuration();
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
				while (timeIdx < notes.size() && notes.get(timeIdx).getTime() < nextTime) {
					currentPlayedNotes.add(
							new NoteDurationPair(notes.get(timeIdx), notes.get(timeIdx).getDuration()));
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
						sArray[x] += sVal;
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

		// i has to be even
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
			float volume = 1f;
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
		
		@Override
		public String toString() {
			return note.toString();
		}
	}

}