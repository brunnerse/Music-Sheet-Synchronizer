package Music;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import AudioFile.WAVReader;

/**
 * 
 * @author Severin
 *
 */
//Objekt, das eine bestimmte Tonhöhe repräsentiert
public class Pitch {
	private final char note;
	private final byte octave; // lowest is -2
	private final byte vorzeichen;

	private static final int numOctaves = 7;
	private static final int notesPerOctave = 12;
	
	private static final Pitch[] pitches = new Pitch[numOctaves * notesPerOctave];
	private static boolean isInitialised = false;
	
	private final AudioPitch audio;

	private Pitch(char note, int octave, byte vorzeichen, AudioPitch audio) {
		this.note = note;
		this.octave = (byte)octave;
		this.vorzeichen = vorzeichen;
		this.audio = audio;
	}
	
	/**
	 * Convention to simplify things: 
	 * if vorzeichen is B, it gets reduced to the lower note with vorzeichen sharp
	 * Pitches can be only created in this class, so KEEP the constraint TODO
	 */

	public static Pitch getPitch(char note, int octave, byte vorzeichen) {
		if (!isInitialised)
			init();
		note = Character.toLowerCase(note);
		if (vorzeichen == Vorzeichen.SHARP) {
			if (note == 'e') {
				vorzeichen = Vorzeichen.PLAIN;
				note = 'f';
			} else if (note == 'h') {
				vorzeichen = Vorzeichen.PLAIN;
				note = 'c';
				++octave;
			}
		} else if (vorzeichen == Vorzeichen.B) {
			if (note == 'c') {
				vorzeichen = Vorzeichen.PLAIN;
				note = 'h';
				--octave;
			} else if (note == 'f') {
				vorzeichen = Vorzeichen.PLAIN;
				note = 'e';
			} else {
				vorzeichen = Vorzeichen.SHARP;
				note--;
			}
		}
		//search to find the correct pitch
		int idx = notesPerOctave * (octave + 2); //start with the 'c' of the octave, +2 as lowest octave is -2
		while (idx < pitches.length) {
			if (pitches[idx].note == note && pitches[idx].vorzeichen == vorzeichen)
				return pitches[idx];
			++idx;
		}
		//if codes reaches this point, the corresponding pitch was not found
		System.err.println("ERROR: The Pitch " + new Pitch(note, octave, vorzeichen, null) + 
				" doesn't exist.");
		return null;
	}
	
	public static Pitch getPitch(char note, int octave) {
		return Pitch.getPitch(note,  octave, Vorzeichen.PLAIN);
	}
	
	public static AudioFormat getAudioFormat() {
		return AudioPitch.format;
	}

	private static void init() {
		float []baseFrequencies = {261.63f, 277.18f, 293.66f, 311.13f, 329.63f, 349.23f, 370f, 392f, 415.3f,
				440f, 466.16f, 493.88f}; //c1, c#1, d1, d#1, e1, f1, f#1, g1, g#1, a1, a#1, h1
		char [] notes = {'d', 'e', 'f', 'g', 'a', 'h'};
		//initiate array with base frequencies and notes, then calc the other frequencies from the values
		//initiate lowest note
		int pitchIdx = 0;
		for (byte octave = -2; octave <= 4; ++octave) {
			int freqIdx = 0;
			float powerOf2 = (float)Math.pow(2d,  (octave - 1));
			pitches[pitchIdx++] = new Pitch('c', octave, Pitch.Vorzeichen.PLAIN, 
					new AudioPitch(baseFrequencies[freqIdx++] * powerOf2));
			pitches[pitchIdx++] = (new Pitch('c', octave, Pitch.Vorzeichen.SHARP,
					new AudioPitch(baseFrequencies[freqIdx++] * powerOf2)));
			for (char c : notes) {
				pitches[pitchIdx++] = new Pitch(c, octave, Pitch.Vorzeichen.PLAIN,
						new AudioPitch(baseFrequencies[freqIdx++] * powerOf2));
				if (c != 'e' && c != 'h')
					pitches[pitchIdx++] = new Pitch(c, octave, Pitch.Vorzeichen.SHARP,
							new AudioPitch(baseFrequencies[freqIdx++] * powerOf2));
			}
		
		}
		
		isInitialised = true;
	}
	
	//Algorithm: starts in the middle of the list, moves into one direction until
	//the margin becomes bigger, meaning the element closest to freq was found
	public static Pitch getPitchFromFreq(float freq) {
		if (!isInitialised)
			Pitch.init();
		int idx = pitches.length / 2;
		float margin, lastMargin;
		int step = 1;
		if (freq < pitches[idx].getFrequency())
			step = -1;
		
		lastMargin = Math.abs(pitches[idx].getFrequency() - freq);
		idx += step;
		for (; idx >= 0 && idx < pitches.length; idx += step) {
			margin = Math.abs(pitches[idx].getFrequency() - freq);
			if (margin > lastMargin)
				break;
			lastMargin = margin;
		}
		return pitches[idx - step];
	}
	
	public static void close() {
		for (Pitch p : pitches)
			p.audio.close();
	}
	
	public char getNote() {
		return note;
	}

	public byte getOctave() {
		return octave;
	}

	public byte getVorzeichen() {
		return vorzeichen;
	}
	
	@Override
	public String toString() {
		String s = "" + (octave >= 0 ? note : Character.toUpperCase(note));
		if (vorzeichen == Vorzeichen.B) {
			s += "b";
		}
		else if (vorzeichen == Vorzeichen.SHARP)
			s += "#";
		

		if (this.octave >= 1) {
			s += octave;
		} else if (octave == -2){
			s +=  + '\'';
		}
		return s;
	}
	
	//Operations for Audio
	public void read(byte[] b, int length, Note n) {
		this.audio.read(b, length, n);
	}
	
	public float getFrequency() {
		return this.audio.frequency;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass())
			return false;
		return this.equals((Pitch)o);	
	}
	
	public boolean equals(Pitch p) {
		return p.note == this.note && p.vorzeichen == this.vorzeichen && p.octave == this.octave;
	}

	//not using enumerations as they take too much memory
	public static abstract class Vorzeichen { 
		public static final byte PLAIN = 0, B = 1, SHARP = -1;
	};
	
	
	private static class AudioPitch {
		public static boolean onlyRawData = true;
		private WAVReader reader;
		private final String fileName;
		
		public final float frequency;
		private int curIdx;
		private Note lastNote = null;
		
		public static final float sampleRate = 44100f;
		public static final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		
		/**
		 * @param freq
		 * @param fileName	the WAV-File that contains the sound to be played when the Pitch occurs
		 */
		public AudioPitch(float freq, String fileName) {
			this.frequency = freq;
			this.fileName = fileName;
		}
		
		public AudioPitch(float freq) {
			this(freq, null);
			onlyRawData = true;
		}
		
		
		//TODO: dont forget to take Staccato and Legato into consideration!
		public void read(byte[] b, int length, Note n) {
			if (n != lastNote) {
				curIdx = 0;
				lastNote = n;
			}
			if (onlyRawData) {
				readRaw(b, length);
			} else {
				if (reader == null) {
					reader = new WAVReader(fileName);
				}
			}
		}
		
		//function used for raw signal
		private void readRaw(byte[] b, int length) {
			//System.out.println(frequency);
			int numVals = length / 2;
			double amplitude = numVals / 4;
			int idx;
			for (idx = 0; idx < numVals; ++idx) {
				short val = (short)(amplitude * Math.cos(2 *  (idx + curIdx) * frequency * Math.PI / sampleRate));
				b[idx * 2] = (byte)val;
				b[idx * 2 + 1] = (byte)(val >> 8);
			}
			curIdx += idx;
		}

		public void close() {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {}
			reader = null;
		}
	}
}
