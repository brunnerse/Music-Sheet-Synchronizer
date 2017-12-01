package Music;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import AudioFile.WAVWriter;
import AudioFile.WAVPlayer;
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
		final String fileName = "temp - sheetfile.wav";
		WAVWriter writer = new WAVWriter(fileName, Pitch.getAudioFormat());
		SourceDataLine line = AudioSystem.getSourceDataLine(Pitch.getAudioFormat());
		
		//number of 1/64 between each iteration, 4 means 4/64 = 1/16
		//is the lowest accuracy, for perfect results set to 1
		final int stepTime = 8;
		
		// enough space for stepTime/64 time
		//tempo * 64 / 60 / 4  1/64 per sec =>> t = stepTime * 60 * 4 / tempo / 64 
		//number of samples = sampleRate * t = sampleRate * stepTime * 60 * 4 / 64 / temp0
		short[] sArray = new short[(int)Pitch.getAudioFormat().getSampleRate() * stepTime * 60 * 4 / 64 / tempo]; // 
		byte[] bArray = new byte[sArray.length * 2];
		
		try {
			writer.open();
			ArrayList<Note> currentPlayedNotes = new ArrayList<Note>();
			// steps in 1/16, 1 time means 1/64
			Note lastNote = notes.get(notes.size() - 1);
			final int endTime = lastNote.getTime() + lastNote.getDuration();
			int nextTime;
			//current idx in the notelist where the note starts at the current time
			int timeIdx = 0;
			
			line.open();
			line.start();
			for (int time = 0; time < endTime; time = nextTime) {
				nextTime = time + stepTime;
				//Remove the notes from the last iteration
				System.out.print(currentPlayedNotes.size() + " -\t");
				for (int x = 0; x < currentPlayedNotes.size(); ++x) {
					int noteDuration = currentPlayedNotes.get(x).getDuration() - stepTime;
					System.out.println("\t" + currentPlayedNotes.get(x) + "\t" + noteDuration);
					if (noteDuration <= 0)
						currentPlayedNotes.remove(x--); //x-- because the next element has index x
					else
						currentPlayedNotes.get(x).setDuration(noteDuration);
				}
				//add new Notes that start at the current time
				while (timeIdx < notes.size() && notes.get(timeIdx).getTime() < nextTime) {
					currentPlayedNotes.add(notes.get(timeIdx));
					timeIdx++;
				}
				for (int i = 0; i < sArray.length; ++i)
					sArray[i] = 0;
				
				for (Note n : currentPlayedNotes) {
					n.getPitch().read(bArray, bArray.length, n); 
					//line.write(bArray,  0,  bArray.length);
					for (int x = 0; x < sArray.length; ++x) {
						int i = (bArray[x * 2 + 1] << 8) & 0xff00;
						i = i | ((int)bArray[x * 2] & 0xff);
						short sVal = (short)i;
						sVal = (short)(sVal * n.getVolume());
						sArray[x] +=  sVal; //TODO: to make Volume and  multiple notes possible, you have to take the DC OFFSET into consideration
						//System.out.printf("%x - %x, %x\n",sVal , bArray[x * 2], bArray[x * 2 + 1]);
						//TODO: Make ugly jump go away, see: Note_Frequency_Playback.java
					}
				}
				
				//convert the short Array into the byte Array
				for (int i = 0; i < sArray.length; ++i) {
					bArray[i * 2] = (byte) sArray[i];
					bArray[i * 2 + 1] = (byte) (sArray[i] >> 8);
				}
				writer.write(bArray, 0, bArray.length);
			}
			
			int sampleRate = 44100;
	        byte[] b = new byte[sampleRate * 4];
	        short[] s = new short[sampleRate * 2];
	        
	        int freq1 = 440, freq2 = 220;
	        
	        for (int i = 0; i < sampleRate; ++i) {
	        	s[i] = (short)(10000 * Math.cos(2 * Math.PI * freq1 * i / sampleRate));
	        }
	        for (int i = 0; i < sampleRate; ++i) {
	        	s[i + sampleRate] = (short)(10000 * Math.cos(2 * Math.PI * i * freq2 / sampleRate));
	        }

	        for (int i = 0; i < s.length; ++i) {
	        	b[2 * i] = (byte)s[i];
	        	b[2 * i + 1] = (byte)(s[i] >> 8);
	        }
	        
	        writer.write(b, 0, b.length / 2);
	        writer.write(b, b.length / 2, b.length / 2);
		} finally {
			line.stop();
			line.close();
			writer.close();
		}
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
	 * @param n: Note to be added to the note-Array
	 * CAREFUL: The correct time in the note must already be set.
	 * If the function should set it automatically to the next position, 
	 * call MusicSheet.addNote(Note n, true);
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

}
