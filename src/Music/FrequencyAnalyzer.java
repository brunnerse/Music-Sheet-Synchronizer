package Music;

public abstract class FrequencyAnalyzer {
	
	private static final int numOctaves = 6;
	private static final int notesPerOctave = 12;
	
	private static FrequencyNotePair noteFreq[];
	private static boolean isInitiated = false;
	
	//fills the array noteFreq; calculates all frequencies from the base octave
	private static void initiateNotePair() {
		noteFreq = new FrequencyNotePair[notesPerOctave * numOctaves];
		float []baseFrequencies = {261.63f, 277.18f, 293.66f, 311.13f, 329.63f, 349.23f, 370f, 392f, 415.3f,
				440f, 466.16f, 493.88f};
		String[] baseNotes = {"c1", "c#1/db1", "d1", "d#1/eb1", "e1", "f1","f#1/gb1", "g1", "g#1/ab1",
				"a1", "a#1/hb1", "h1"};
		//initiate array with base frequencies and notes, then calc the other frequencies from the values
		for (int i = 0; i < notesPerOctave; ++i) {
			//initiate base octave(c1, d1 etc)
			noteFreq[notesPerOctave * 2 + i] = new FrequencyNotePair(baseNotes[i], baseFrequencies[i]);
			//initiate small octave(c, d etc)
			String lowerNote = baseNotes[i].replace("1", "");
			noteFreq[notesPerOctave + i] = new FrequencyNotePair(lowerNote, baseFrequencies[i] / 2);
			//initiate big octave(C, D etc)
			lowerNote = lowerNote.toUpperCase();
			lowerNote = lowerNote.replace('B',  'b');
			noteFreq[i] = new FrequencyNotePair(lowerNote, baseFrequencies[i] / 4);
			//initiate all Octaves higher then the base octave
			for (int x = 0; x < numOctaves - 3; ++x) {
				noteFreq[notesPerOctave * (3 + x) + i] = 
						new FrequencyNotePair(baseNotes[i].replace("1", String.valueOf(numOctaves - 4 + x)),
								baseFrequencies[i] * (1 << x));
			}
		}
		isInitiated = true;
	}
	
	//returns the Note whose Frequency is the closest to the argument freq.
	public static String getNoteFromFreq(float freq) {
		if (!isInitiated)
			initiateNotePair();
		int idx = noteFreq.length / 2, previousIdx = idx;
		float margin, prevMargin = Integer.MAX_VALUE;
		//Get Closer to the Freq until the Margin becomes bigger again; then 
		while ((margin = Math.abs(noteFreq[idx].getFreq() - freq)) < prevMargin) {
			prevMargin = margin;
			previousIdx = idx;
			if (noteFreq[idx].getFreq() < freq) {
				idx++;
				if (idx >= noteFreq.length - 1)
					return noteFreq[noteFreq.length - 1].getNote();
			} else {
				idx--;
				if (idx <= 0)
					return noteFreq[0].getNote();
			}
		}
		return noteFreq[previousIdx].getNote();
	}
	
	private static class FrequencyNotePair {
		private String note;
		private float freq;
		public FrequencyNotePair(String note, float freq) {
			this.note = note;
			this.freq = freq;
		}
		public String getNote() {
			return note;
		}
		public float getFreq() {
			return freq;
		}
		@Override
		public String toString() {
			return "(" + note + " : " + freq + ")"; 
		}
	}
}
