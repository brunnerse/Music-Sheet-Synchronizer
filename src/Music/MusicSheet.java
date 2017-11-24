package Music;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import AudioFile.WAVWriter;
import AudioFile.WAVPlayer;
import javax.sound.sampled.AudioFormat;

public class MusicSheet {

	private ArrayList<Bar> bars = new ArrayList<>();
	//tempo in Quarters per minute
	private int tempo = 60; 
	
	public MusicSheet(BufferedImage[] images) {
		this.scanSheet(images);
	}
	
	public void scanSheet(BufferedImage[] images) {
		
	}
	
	//Creates a WAV file and writes into it.
	public void playSheet() throws Exception{
		final int sampleRate = 44100;
		final String fileName = "temp - sheetfile.wav";
		WAVWriter writer = new WAVWriter(fileName, new AudioFormat(sampleRate, 16, 1, true, false));
		//enough space for 1/16 of time
		short []sArray = new short[sampleRate  * 60 / tempo / 4]; //tempo / 60 = quarters per second,
																// 1/4 = 1/16 per quarter
		byte []bArray = new byte[sArray.length * 2];
		try {
			writer.open();
			ArrayList<Note> currentPlayedNotes;
			for (Bar bar : bars) {
				//notes in bar are assumed to be sorted
				//steps in 1/16, 1 time means 1/64
				for (int time = 0; time < bar.getBeat().beatsPerBar * 64; time += 4) {
					for (int i = 0; i < sArray.length; ++i)
						sArray[i] = 0;
					
					
					
					for (int i = 0; i < sArray.length; ++i) {
						bArray[i * 2] = (byte)sArray[i];
						bArray[i * 2 + 1] = (byte)(sArray[i] >> 8);
					}
					writer.write(bArray, 0, bArray.length);
				}
				
			}
			
			writer.close();
			WAVPlayer.play(fileName);
		} catch (IOException e) {
			throw new Exception(((Exception)e).getMessage());
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
	 * @param tempo in quarters per minute
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public ArrayList<Bar> getBars() {
		return bars;
	}

	public static class Bar {
		private ArrayList<Note> notes;
		private Beat takt;
		
		public Bar(Beat t, ArrayList<Note> notes) {
				this.takt = t;
				this.notes = notes;
		}
		
		public Bar(Beat t) {
			this(t, new ArrayList<Note>());
	}
		
		public ArrayList<Note> getNotes() {
			return notes;
		}

		public Beat getBeat() {
			return takt;
		}
		
		/**
		 * sorts the Notes in the Note Array by time
		 */
		public void sortNotes() {
			//using insertion sort
			Note n;
			for (int i = 1; i < notes.size(); ++i) {
				for (int k = i; k > 0; --k) {
					n = notes.get(k - 1);
					if (notes.get(k).getTime() < n.getTime()) {
						notes.set(k - 1, notes.get(k));
						notes.set(k, n);
					} else {
						break;
					}
				}
			}
		}
	}
	
	public static class Beat {
		int first, scnd;
		float beatsPerBar;
		public Beat(int n, int divisor) {
			this.first = n;
			this.scnd = divisor;
			this.beatsPerBar = first/scnd;
		}
		
		/**
		 * @return the number of beats in one bar as Ones (e.g. 4/4 returns 1, 3/4 return 0.75)
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
