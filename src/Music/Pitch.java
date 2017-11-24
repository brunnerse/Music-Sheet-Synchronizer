package Music;

//Objekt, das eine bestimmte Tonhöhe repräsentiert
public class Pitch {
	private final char note;
	private final byte octave; // lowest is -2
	private final byte vorzeichen;

	public static final char[] notes = {'c', 'd', 'e', 'f', 'g', 'a', 'h'} ;
	
	public Pitch(char note, int octave) {
		this(note, octave, Vorzeichen.PLAIN);
	}
	
	//Convention to simplify things: if vorzeichen is B, it gets reduced to the lower note with vorzeichen sharp
	public Pitch(char note, int octave, byte vorzeichen) {
		note = Character.toLowerCase(note);
		/*
		 * if (note < 'a' || note > 'h') throw new
		 * IllegalArgumentException("Note is in wrong format");
		 */
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
		
		this.note = note;
		this.octave = (byte)octave;
		this.vorzeichen = vorzeichen;
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

	//not using enumerations as they take too much memory
	public static abstract class Vorzeichen { 
		public static final byte PLAIN = 0, B = 1, SHARP = -1;
	};
	
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
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass())
			return false;
		return this.equals((Pitch)o);	
	}
	
	public boolean equals(Pitch p) {
		return p.note == this.note && p.vorzeichen == this.vorzeichen && p.octave == this.octave;
	}
}
