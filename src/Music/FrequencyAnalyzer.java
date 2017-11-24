package Music;

import java.io.IOException;

import AudioFile.WAVReader;

public abstract class FrequencyAnalyzer {
	
	private static final int numOctaves = 7;
	private static final int notesPerOctave = 12;
	
	private static FrequencyPitch[] pitches;
	private static boolean isInitiated = false;
	
	//fills the array noteFreq; calculates all frequencies from the base octave
	private static void initiatePitches() {
		pitches = new FrequencyPitch[numOctaves * notesPerOctave]; //7 octaves, 7 notes per octave, 3 vorzeichen per note
		
		float []baseFrequencies = {261.63f, 277.18f, 293.66f, 311.13f, 329.63f, 349.23f, 370f, 392f, 415.3f,
				440f, 466.16f, 493.88f}; //c1, c#1, d1, d#1, e1, f1, f#1, g1, g#1, a1, a#1, h1
		char [] notes = {'d', 'e', 'f', 'g', 'a', 'h'};
		//initiate array with base frequencies and notes, then calc the other frequencies from the values
		//initiate lowest note
		int pitchIdx = 0;
		for (byte octave = -2; octave <= 4; ++octave) {
			int freqIdx = 0;
			float powerOf2 = (float)Math.pow(2d,  (octave - 1));
			pitches[pitchIdx++] = new FrequencyPitch(new Pitch('c', octave, Pitch.Vorzeichen.PLAIN), baseFrequencies[freqIdx++] * powerOf2);
			pitches[pitchIdx++] = new FrequencyPitch(new Pitch('c', octave, Pitch.Vorzeichen.SHARP), baseFrequencies[freqIdx++] * powerOf2);
			for (char c : notes) {
				pitches[pitchIdx++] = new FrequencyPitch(new Pitch(c, octave, Pitch.Vorzeichen.PLAIN), baseFrequencies[freqIdx++] * powerOf2);
				if (c != 'e' && c != 'h')
					pitches[pitchIdx++] = new FrequencyPitch(new Pitch(c, octave, Pitch.Vorzeichen.SHARP), baseFrequencies[freqIdx++] * powerOf2);
			}
		
		}
		
		isInitiated = true;
	}
	
	//returns the Note whose Frequency is the closest to the argument freq.
	public static Pitch getPitchFromFreq(float freq) {
		if (!isInitiated)
			initiatePitches();
		int idx = 0;
		int minIdx = 0, maxIdx = pitches.length - 1;
		float margin, nextMargin;
		while (maxIdx > minIdx) {
			idx = (maxIdx - minIdx) / 2 + minIdx;
			margin = Math.abs(freq - pitches[idx].frequency);
			if (idx > minIdx)
				nextMargin = Math.abs(freq - pitches[idx - 1].frequency);
			else
				break;
			if (nextMargin < margin)
				maxIdx = idx;
			else
				minIdx = idx;
		}
		return pitches[idx].pitch;
	}
	
	public static FrequencyPitch getFrequencyPitch(Pitch p) {
		if (!isInitiated)
			initiatePitches();
		int idx = 0;
		int minIdx = 0, maxIdx = pitches.length - 1;
		while (maxIdx > minIdx) {
			idx = (maxIdx - minIdx) / 2 + minIdx;
			Pitch p2 = pitches[idx].pitch;
			//lexikographische Ordnung: Octave - Note - Vorzeichen
			if (p.getOctave() > p2.getOctave() || 
					(p.getOctave() == p2.getOctave() && (p.getNote() > p2.getNote() || (
							p.getNote() == p2.getNote() && (p.getVorzeichen() == p2.getVorzeichen()))))) {
				minIdx = idx + 1; //p > p2
			} else if (p.equals(p2)) //p == p2, gefunden
				break;
			else //p < p2
				maxIdx = idx - 1;
		}
		return pitches[idx];
	}
	
	public static float getFrequencyFromPitch(Pitch p) {
		return getFrequencyPitch(p).frequency;
	}
	
	public static void closePitches() {
		if (pitches == null)
			return;
		for (FrequencyPitch p : pitches) {
			p.close();
		}
	}
	
	public static class FrequencyPitch {
		public final float frequency;
		public final Pitch pitch;
		private int curIdx;
		private WAVReader reader;
		private String fileName;
		private Note lastNote = null;
		
		public FrequencyPitch(Pitch p, float freq) {
			this.pitch = p;
			this.frequency = freq;
		}
		
		//TODO: dont forget to take Staccato and Legato into consideration!
		public void read(byte[] b, int length, Note n) {
			readRaw(b, length, n);
			int a;
			if ((a = 1) == 1)
				return;
			if (reader == null) {
				reader = new WAVReader(fileName);
			}
		}
		//function used for raw signal
		private void readRaw(byte[] b, int length, Note n) {
			int numVals = length / 2;
			int amplitude = numVals / 2;
			int frameRate = 44100;
			if (n != lastNote) {
				curIdx = 0;
				lastNote = n;
			}
			for (; curIdx < numVals; ++curIdx) {
				short val = (short)(amplitude * Math.cos(2 *  curIdx * frequency * Math.PI / frameRate));
				b[curIdx * 2] = (byte)val;
				b[curIdx * 2 + 1] = (byte)(val >> 8);
			}
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
