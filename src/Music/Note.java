package Music;

public class Note {
	private final byte volume; //0 corresponding to lowest volume, 127 highest volume
	private final byte duration; //should be multiples of 1/64 (which is seen as the lowest duration possible)
	private final byte time; //should be a value between 0 and 127, showing which duration (in 1/64) has passed until the note is played
	
	private final Pitch p;
	private final byte articulation; //is in format from Articulation
	
	public Note(Pitch p, byte volume, byte duration, byte articulation, byte time) {
		this.p = p;
		this.volume = volume;
		this.duration = duration;
		this.articulation = articulation;
		this.time = time;
	}
	
	public Note(Pitch p, float volume, byte duration, byte articulation, byte time) {
		this(p, (byte)(volume * Byte.MAX_VALUE), duration, articulation, time);
	}
	
	public float getVolume() {
		return volume;
	}

	public byte getDuration() {
		return duration;
	}

	public Pitch getPitch() {
		return p;
	}

	public byte getArticulation() {
		return articulation;
	}

	public byte getTime() {
		return time;
	}

	
	//not using enumerations as they take too much memory
	public static abstract class Articulation {
		public static final byte PLAIN = 0, STACCATO = 1, LEGATO = 2;
	};
	
	
	public static float getVolumefromNotation(String vol) throws IllegalArgumentException {
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
