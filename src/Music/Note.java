package Music;

public class Note {
	private float volume; //should be value between 0 and 1
	private byte duration; //should be multiples of 1/64 (which is seen as the lowest duration possible) 
	
	private Pitch p;
	private Articulation a;
	
	
	public static class Pitch {
		char note;
		private byte octave; //lowest is 0 (Big)
		private Vorzeichen v;
		
		
		@Override
		public String toString() {
			return "";
		}
	}
	
	public static enum Articulation {PLAIN, STACCATO, LEGATO};
	public static enum Vorzeichen { PLAIN, B, SHARP };
	
	public static float getVolume(String vol) throws IllegalArgumentException {
		if (vol.compareToIgnoreCase("ff") == 0) {
			return 1f;
		} else  if (vol.compareToIgnoreCase("f") == 0) {
			return 0.8f;
		} else  if (vol.compareToIgnoreCase("mf") == 0) {
			return 0.6f;
		} else  if (vol.compareToIgnoreCase("mp") == 0) {
			return 0.4f;
		} else  if (vol.compareToIgnoreCase("p") == 0) {
			return 0.2f;
		} else  if (vol.compareToIgnoreCase("pp") == 0) {
			return 0.1f;
		} else {
			throw new IllegalArgumentException("The Volume " + vol + " isnt a denotation for volume.");
		}
	}
	
}
